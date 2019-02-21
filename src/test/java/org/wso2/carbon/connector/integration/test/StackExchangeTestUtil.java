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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class StackExchangeTestUtil {

    private static final Log LOG = LogFactory.getLog(StackExchangeTestUtil.class);

    public static StackExchangeItems getStackExchangeItems(StackExchangeUrl url) throws IOException, JSONException {
        StackExchangeUrlConnection connection = new StackExchangeUrlConnection(url.openConnection());

        if (connection.getResponseCode() != 200) {
            String error = String.format("Object extraction failed due to invalid statusCode: (code = %s)",
                    connection.getResponseCode());
            throw new IOException(error);
        }
        return new StackExchangeItems(new JSONObject(
                IOUtils.toString(connection.getInputStream(), "UTF-8")));
    }

    public static class StackExchangeItems {
        private final JSONArray items;

        private StackExchangeItems(JSONObject data) throws JSONException {
            this.items = data.getJSONArray("items");
        }

        public boolean isEmpty() {
            return items.length() == 0;
        }

        private void failForGood(String key) throws JSONException {
            throw new JSONException(String.format("Could not extract '%s' requested from an empty item list.", key));
        }

        public <T> T getRandom(String key, Class<T> type) throws JSONException {
            if (isEmpty()) {
                failForGood(key);
            }
            int i = new Random().nextInt(items.length());
            return getItemAt(key, type, i);
        }

        private <T> T getItemAt(String key, Class<T> type, int i) throws JSONException {
            return getValue(items.getJSONObject(i), key, type);
        }

        public <T> T[] getAll(String key, Class<T> type) throws JSONException {
            if (isEmpty()) {
                failForGood(key);
            }
            T[] all = (T[]) Array.newInstance(type, items.length());
            for (int i = 0; i < items.length(); i++) {
                all[i] = getValue(items.getJSONObject(i), key, type);
            }
            return all;
        }

        private <T> T getValue(JSONObject json, String key, Class<T> type) throws JSONException {
            Object value = json.get(key);
            if (type.isArray()) {
                Class<?> componentType = type.getComponentType();
                if (!componentType.isInstance(json.getJSONArray(key).get(0))) {
                    throw new JSONException("Given type is not supported.");
                }
                return (T) getValueArray(json.getJSONArray(key), componentType);
            }
            if (!type.isInstance(value)) {
                throw new JSONException("Given type is not supported.");
            }
            return type.cast(value);
        }

        private <T> T[] getValueArray(JSONArray array, Class<T> type) throws JSONException {
            T[] valueArray = (T[]) Array.newInstance(type, array.length());
            for (int i = 0; i < array.length(); i++) {
                valueArray[i] = type.cast(array.get(i));
            }
            return valueArray;
        }
    }

    public static StackExchangeCommonWrapper getStackExchangeCommonWrapper(String[] includedFields) {
        Set<String> commonKeySet = new HashSet<>();
        for (String field : includedFields) {
            if (StackExchangeCommonWrapper.isCommonKey(field)) {
                commonKeySet.add(StackExchangeCommonWrapper.getCommonKeyAsKey(field));
            }
        }
        return new StackExchangeCommonWrapper(commonKeySet);
    }

    public static class StackExchangeCommonWrapper {

        private static final String ERROR_STRING = "error";

        private final Set<String> commonKeySet;
        private boolean errorKeysExist = false;

        private StackExchangeCommonWrapper(Set<String> commonKeySet) {

            this.commonKeySet = commonKeySet;
            for (String key : commonKeySet) {
                if (key.contains(ERROR_STRING)) {
                    errorKeysExist = true;
                    break;
                }
            }
        }

        public static boolean isCommonKey(String key) {

            return StringUtils.isNotEmpty(key) && key.charAt(0) == '.';
        }

        public static String getCommonKeyAsKey(String commonKey) {

            return commonKey.substring(1);
        }

        public WrapperType fetchWrapperType(JSONObject json) {

            for (Iterator<String> i = json.keys(); i.hasNext(); ) {
                String key = i.next();
                if (!commonKeySet.contains(key)) {
                    return WrapperType.UNKNOWN;
                }
                if (errorKeysExist && key.contains(ERROR_STRING)) {
                    return WrapperType.ERROR;
                }
            }
            return WrapperType.NO_ERROR;
        }

        public enum WrapperType {
            UNKNOWN, ERROR, NO_ERROR
        }
    }
}
