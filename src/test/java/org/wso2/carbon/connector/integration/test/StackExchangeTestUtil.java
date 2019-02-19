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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StackExchangeTestUtil {

    private static final Log LOG = LogFactory.getLog(StackExchangeTestUtil.class);

    public static <T extends StackExchangeObjectKey> List<T> getStackExchangeObjectKeyList(
            StackExchangeUrl url, Class<T> tClass) throws Exception {

        StackExchangeUrlConnection connection = new StackExchangeUrlConnection(url.openConnection());
        if (connection.getResponseCode() != 200) {
            String error = String.format("Object extraction failed due to invalid statusCode: (code = %s)",
                    connection.getResponseCode());
            throw new IOException(error);
        }
        JSONObject body = new JSONObject(IOUtils.toString(connection.getInputStream(), "UTF-8"));
        return new ObjectItemKey(body).asList(tClass);
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

    public static class ObjectItemKey extends StackExchangeObjectKey<JSONArray> {

        protected ObjectItemKey(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected JSONArray extract(JSONObject item) throws JSONException {
            return item.getJSONArray("items");
        }

        private <T extends StackExchangeObjectKey> List<T> asList(Class<T> tClass)
                throws ReflectiveOperationException, JSONException {

            List<T> itemList = new ArrayList<>();

            for (int i = 0; i < super.key.length(); i++) {
                JSONObject item = super.key.getJSONObject(i);
                Constructor<T> constructor = tClass.getDeclaredConstructor(JSONObject.class);
                constructor.setAccessible(true);
                itemList.add(constructor.newInstance(item));
            }
            return itemList;
        }
    }

    public static class FilterIncludedFieldsKey extends StackExchangeObjectKey<List<String>> {

        protected FilterIncludedFieldsKey(JSONObject item) throws JSONException {
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
