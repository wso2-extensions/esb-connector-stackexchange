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
import java.util.Random;
import java.util.Set;

public class StackExchangeTestUtil {

    private static final Log LOG = LogFactory.getLog(StackExchangeTestUtil.class);

    public static <T extends StackExchangeObjectField> T getStackExchangeObjectField(
            StackExchangeUrl url, Class<T> tClass) throws Exception {

        return new ObjectItemsField(getResponse(url)).getRandomItem(tClass);
    }

    public static <T extends StackExchangeObjectField> List<T> getStackExchangeObjectFieldList(
            StackExchangeUrl url, Class<T> tClass) throws Exception {

        return new ObjectItemsField(getResponse(url)).asList(tClass);
    }

    private static JSONObject getResponse(StackExchangeUrl url) throws IOException, JSONException {
        StackExchangeUrlConnection connection = new StackExchangeUrlConnection(url.openConnection());

        if (connection.getResponseCode() != 200) {
            String error = String.format("Object extraction failed due to invalid statusCode: (code = %s)",
                    connection.getResponseCode());
            throw new IOException(error);
        }
        return new JSONObject(IOUtils.toString(connection.getInputStream(), "UTF-8"));
    }

    public static abstract class StackExchangeObjectField<T> {

        private final T value;

        protected StackExchangeObjectField(JSONObject item) throws JSONException {
            value = extract(item);
        }

        protected abstract T extract(JSONObject item) throws JSONException;

        public T getValue() {
            return value;
        }
    }

    public static class ObjectItemsField extends StackExchangeObjectField<JSONArray> {

        protected ObjectItemsField(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected JSONArray extract(JSONObject item) throws JSONException {
            return item.getJSONArray("items");
        }

        private <T extends StackExchangeObjectField> T newInstance(JSONObject item, Class<T> tClass)
                throws ReflectiveOperationException {
            Constructor<T> constructor = tClass.getDeclaredConstructor(JSONObject.class);

            constructor.setAccessible(true);
            return constructor.newInstance(item);
        }

        private <T extends StackExchangeObjectField> T getRandomItem(Class<T> tClass)
                throws ReflectiveOperationException, JSONException {

            int i = new Random().nextInt(super.value.length());
            JSONObject item = super.value.getJSONObject(i);
            return newInstance(item, tClass);
        }

        private <T extends StackExchangeObjectField> List<T> asList(Class<T> tClass)
                throws ReflectiveOperationException, JSONException {

            List<T> tList = new ArrayList<>();

            for (int i = 0; i < super.value.length(); i++) {
                JSONObject item = super.value.getJSONObject(i);
                tList.add(newInstance(item, tClass));
            }
            return tList;
        }
    }

    public static class FilterIncludedFieldsField extends StackExchangeObjectField<List<String>> {

        protected FilterIncludedFieldsField(JSONObject item) throws JSONException {
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
            for (String keyInKey : super.value) {
                if (StackExchangeCommonWrapper.isCommonKey(keyInKey)) {
                    commonKeySet.add(StackExchangeCommonWrapper.getCommonKeyAsKey(keyInKey));
                }
            }
            return commonKeySet;
        }
    }

    public static class QuestionIdField extends StackExchangeObjectField<Integer> {

        protected QuestionIdField(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected Integer extract(JSONObject item) throws JSONException {
            return item.getInt("question_id");
        }
    }

    public static class AnswerIdField extends StackExchangeObjectField<Integer> {

        protected AnswerIdField(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected Integer extract(JSONObject item) throws JSONException {
            return item.getInt("answer_id");
        }
    }

    public static class PrivilegeShortDescriptionField extends StackExchangeObjectField<String> {

        protected PrivilegeShortDescriptionField(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected String extract(JSONObject item) throws JSONException {
            return item.getString("short_description");
        }
    }

    public static class TagNameField extends StackExchangeObjectField<String> {

        protected TagNameField(JSONObject item) throws JSONException {
            super(item);
        }

        @Override
        protected String extract(JSONObject item) throws JSONException {
            return item.getString("name");
        }
    }
}
