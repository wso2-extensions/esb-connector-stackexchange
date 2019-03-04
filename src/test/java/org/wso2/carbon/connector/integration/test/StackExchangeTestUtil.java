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

/*
 * API specific helper structures and methods to be used in tests.
 */
public class StackExchangeTestUtil {

    private static final Log LOG = LogFactory.getLog(StackExchangeTestUtil.class);

    /*
     * getStackExchangeItems method's creates an StackExchangeItems instance based on the URL. The method fails on
     * following two conditions.
     * 1). Status code is not valid.
     * 2). Response body does not contain a valid JSON response.
     * Any of above failures will occur due to an invalid URL.
     */
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

    /*
     * In all API responses data is wrapped in a field called 'items'. This class provides necessary methods to extract
     * response data which is inside the 'items' field. For the data extraction every method in the class expect name of
     * the key and type of the key as necessary arguments.
     *
     * JSON types vs Java classes
     * ==========================
     * a string: String.class
     * a number: Integer.class, Double.class
     * a boolean: Boolean.class
     * an object: JSONObject.class
     * an array: T[].class (T should be one of above types)
     */
    public static class StackExchangeItems {

        /* Items returned from API */
        private final JSONArray items;

        /* For randomly picking values from Item array */
        private final Random random;

        private StackExchangeItems(JSONObject data) throws JSONException {

            this.items = data.getJSONArray("items");
            random = new Random();
        }

        public int length() {

            return items.length();
        }

        public boolean isEmpty() {

            return items.length() == 0;
        }

        private void fail(String key) throws JSONException {

            throw new JSONException(String.format(
                    "Could not extract requested inner value '%s' from an empty item list.", key));
        }

        /*
         * The getRandom method picks a random index from the item array and returns
         * JSON value for the index based on the key and type.
         * This method will fail due to following reasons.
         * 1). If item array is empty.
         * 2). If an item doesn't contain the key.
         * 3). If type is incorrect.
         */
        public <T> T getRandom(String key, Class<T> type) throws JSONException {

            if (isEmpty()) {
                fail(key);
            }
            int i = random.nextInt(items.length());
            return getAt(key, type, i);
        }

        /*
         * The getAt method picks the given index from the item array and return
         * JSON value for the index based on the key and type.
         * This method will fail due to following reasons.
         * 1). If item array is empty.
         * 2). If an item doesn't contain the key.
         * 3). If type is incorrect.
         */
        public <T> T getAt(String key, Class<T> type, int i) throws JSONException {

            return getValue(items.getJSONObject(i), key, type);
        }

        /*
         * The getAll method returns JSON values for all the items in the item array
         * based on the key and type.
         * This method will fail due to following reasons.
         * 1). If item array is empty.
         * 2). If an item doesn't contain the key.
         * 3). If type is incorrect.
         */
        public <T> T[] getAll(String key, Class<T> type) throws JSONException {

            if (isEmpty()) {
                fail(key);
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

    /*
     * The getStackExchangeCommonWrapper method creates an StackExchangeItems instance. You must always get parameter
     * includedFields By calling to the /filter/{filter_name} route. Among these fields there are set of fields common
     * to every API response. In the filter route's response their names start with a dot. For instantiating
     * StackExchangeCommonWrapper, we only care about these fields. This method will cause to undefined behaviours if
     * you do not give the includedFields parameter by calling to the filter route.
     */
    public static StackExchangeCommonWrapper getStackExchangeCommonWrapper(String[] includedFields) {

        Set<String> commonKeySet = new HashSet<>();
        for (String field : includedFields) {
            if (StackExchangeCommonWrapper.isCommonKey(field)) {
                commonKeySet.add(StackExchangeCommonWrapper.getCommonKeyAsKey(field));
            }
        }
        return new StackExchangeCommonWrapper(commonKeySet);
    }

    /*
     * All responses in the StackExchange API share a common format. So all API responses return one Common Wrapper
     * Object (some fields could be absent. e.g. error fields will be absent if response does not have an error).
     * Any user can filter out these fields in the way they want by creating a new filter in the site. For checking
     * EI responses in tests we define following logic in the class based on above facts.
     *
     * case1: If response contain non of the fields it is an UNKNOWN response. This could possibly happen due to
     *        decompression issues.
     * case2: If response contains any field like 'error_*' it is an ERROR response.
     * case3: If non of the above is true it is a NO_ERROR response.
     */
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
