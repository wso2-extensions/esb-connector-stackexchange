/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.integration.test;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StackExchangeTestUtil {

    private static final Log LOG = LogFactory.getLog(StackExchangeTestUtil.class);

    public static <T> List<T> getStackExchangeObjectKeyList(StackExchangeUrl url, Class<T> type) throws Exception {
        StackExchangeUrlConnection connection =
                new StackExchangeUrlConnection(url.openConnection());
        int code = connection.getResponseCode();
        if (code != 200) {
            throw new IOException(
                    String.format("Object extraction failed due to invalid statusCode: (code = %s)", code));
        }
        InputStream stream = connection.getInputStream();
        JSONArray itemArray = new JSONObject(IOUtils.toString(stream, "UTF-8")).getJSONArray("items");
        List<T> keys = new ArrayList<>();

        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject item = itemArray.getJSONObject(i);
            // T t = type.getConstructor(JSONObject.class).newInstance(item);
            Constructor<T> constructor = type.getDeclaredConstructor(JSONObject.class);
            constructor.setAccessible(true);
            T t = constructor.newInstance(item);
            keys.add(t);
        }
        return keys;
    }

    public static abstract class StackExchangeObjectKey<T> {

        private final T key;

        protected StackExchangeObjectKey(JSONObject item) throws JSONException {
            key = extract(item);
        }

        protected abstract T extract(JSONObject item) throws JSONException;

        public T getKey() {
            return key;
        }
    }

    public static class FilterShortDescriptionKey extends StackExchangeObjectKey<List<String>> {

        protected FilterShortDescriptionKey(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected List<String> extract(JSONObject item) throws JSONException {
            JSONArray includedFields = item.getJSONArray("included_fields");
            List<String> keys = new ArrayList<>();
            for (int i = 0; i < includedFields.length(); i++) {
                keys.add(includedFields.getString(i));
            }
            return keys;
        }

        public Set<String> getCommonKeySet() {

            Set<String> commonKeySet = new HashSet<>();
            for (String keyInKey : super.key) {
                if (StackExchangeCommonWrapper.isCommonKey(keyInKey)) {
                    commonKeySet.add(StackExchangeCommonWrapper.getCommonKeyAsKey(keyInKey));
                }
            }
            return commonKeySet;
        }
    }

    public static class QuestionIdKey extends StackExchangeObjectKey<Integer> {

        protected QuestionIdKey(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected Integer extract(JSONObject item) throws JSONException {
            return item.getInt("question_id");
        }
    }

    public static class PrivilegeShortDescriptionKey extends StackExchangeObjectKey<String> {

        protected PrivilegeShortDescriptionKey(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected String extract(JSONObject item) throws JSONException {
            return item.getString("short_description");
        }
    }
}
