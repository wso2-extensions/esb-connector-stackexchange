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
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;

/**
 * API specific helper structures and methods to be used in tests.
 */
public class StackExchangeTestUtil {

    private static final Log LOG = LogFactory.getLog(StackExchangeTestUtil.class);

    /**
     * Return an instance of {@code StackExchangeItems} created using StackExchange API response.
     *
     * @param url the StackExchange URL to make the request.
     * @return an instance of {@code StackExchangeItems}.
     * @throws IOException   if response is invalid.
     * @throws JSONException if API response does not contain a valid JSON object.
     */
    public static StackExchangeItems getStackExchangeItems(StackExchangeUrl url) throws IOException, JSONException {

        HttpsURLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept-Encoding", "gzip");

        if (connection.getResponseCode() != 200) {
            String error = String.format("Object extraction failed due to invalid statusCode: (code = %s)",
                    connection.getResponseCode());
            throw new IOException(error);
        }
        return new StackExchangeItems(new JSONObject(
                IOUtils.toString(new GZIPInputStream(connection.getInputStream()), "UTF-8")));
    }

    /**
     * In all API responses data is wrapped in a field called 'items'. This class provides necessary methods to
     * extract response data which is inside the 'items' field. For the data extraction every method in the class
     * expect name of the key and type of the key as necessary arguments.
     *
     * JSON types vs Java classes
     * ==========================
     * a string: String.class
     * a number: Integer.class, Double.class
     * a boolean: Boolean.class
     * an object: JSONObject.class
     * an array: T[].class (T should be one of the above types)
     */
    public static class StackExchangeItems {

        /**
         * Items returned from API
         */
        private final JSONArray items;
        /**
         * An Random to pick values from items array
         */
        private final Random random;

        private StackExchangeItems(JSONObject data) throws JSONException {

            this.items = data.getJSONArray("items");
            random = new Random();
        }

        /**
         * Return the number of items in the items array.
         *
         * @return the number of items in the items array.
         */
        public int length() {

            return items.length();
        }

        /**
         * Return whether the items array is empty.
         *
         * @return whether the items array is empty.
         */
        public boolean isEmpty() {

            return items.length() == 0;
        }

        private void fail(String key) throws JSONException {

            throw new JSONException(String.format(
                    "Could not extract requested inner value '%s' from an empty item list.", key));
        }

        /**
         * This method pick a random item from the items array and Return the value specific to key and type given.
         *
         * @param key  the field name which should contain in an item.
         * @param type the Class instance of the field specified by key.
         * @param <T>  the type of the field specified by key.
         * @return the value specific to key and type given.
         * @throws JSONException if the items array is empty or
         *                       if the key is incorrect or
         *                       if the type is incorrect.
         */
        public <T> T getRandom(String key, Class<T> type) throws JSONException {

            if (isEmpty()) {
                fail(key);
            }
            int i = random.nextInt(items.length());
            return getAt(key, type, i);
        }

        /**
         * This method pick an item for a given index from the items array and Return the value specific to
         * the key and type given.
         *
         * @param key  the field name which should contain in an item.
         * @param type the Class instance of the field specified by key.
         * @param <T>  the type of the field specified by key.
         * @param i    the index to pick an item from items array.
         * @return the value specific to key and type given
         * @throws JSONException if the items array is empty or
         *                       if the key is incorrect or
         *                       if the type is incorrect.
         */
        public <T> T getAt(String key, Class<T> type, int i) throws JSONException {

            return getValue(items.getJSONObject(i), key, type);
        }

        /**
         * Return all the values specific to the key and type given in the items array.
         *
         * @param key  the field name which should contain in a item.
         * @param type the the Class instance of the field specified by key.
         * @param <T>  the type of the field specified by key.
         * @return the value specific to key and type given
         * @throws JSONException if the items array is empty or
         *                       if the key is incorrect or
         *                       if the type is incorrect.
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

    /**
     * The {@code getStackExchangeCommonWrapper} method creates an instance of {@code StackExchangeCommonWrapper}.
     * You must always get parameter {@code includedFields} By calling to the /filter/default route. Among these
     * fields there are set of fields common to every API response. In the filter route response their names start
     * with a dot. For instantiating the {@code StackExchangeCommonWrapper}, we only care about these fields. This
     * method will cause to undefined behaviours if you do not give the {@code includedFields} is not from filter
     * route response.
     *
     * @param includedFields the field 'included_fields' in the filter route response .
     * @return an instance of {@code StackExchangeCommonWrapper}.
     */
    public static StackExchangeCommonWrapper getStackExchangeCommonWrapper(String[] includedFields) {

        Set<String> commonKeySet = new HashSet<>();
        for (String field : includedFields) {
            if (StackExchangeCommonWrapper.isCommonField(field)) {
                commonKeySet.add(StackExchangeCommonWrapper.unHideCommonField(field));
            }
        }
        return new StackExchangeCommonWrapper(commonKeySet);
    }

    /**
     * All responses in the StackExchange API share a common format. So all API responses return one Common Wrapper
     * Object (some fields could be absent. e.g. error fields will be absent if response does not have an error).
     * Any user can filter out these fields in the way they want by creating a new filter in the site. For checking
     * EI responses in tests we define following logic in the class based on above facts.
     *
     * case1: If response contain non of the fields then it is an UNKNOWN response. This could possibly happen
     *        due to decompression issues.
     * case2: If response contains any field like 'error_*' it is an ERROR response.
     * case3: If non of the above is true it is a NO_ERROR response.
     */
    public static class StackExchangeCommonWrapper {

        /**
         * Fields containing following text should indicates an error in response.
         */
        private static final String ERROR_STRING = "error";
        /**
         * Fields should be stored in a Set Data Structure for better performance.
         */
        private final Set<String> commonKeySet;

        private StackExchangeCommonWrapper(Set<String> commonKeySet) {

            this.commonKeySet = commonKeySet;
        }

        /**
         * Return whether field belong to the common wrapper.
         *
         * @param key the name of the field.
         * @return whether field belong to the common wrapper.
         */
        public static boolean isCommonField(String key) {

            return StringUtils.isNotEmpty(key) && key.charAt(0) == '.';
        }

        /**
         * Convert common field from the filter response to match with a regular response.
         *
         * @param commonKey the name of the common field.
         * @return the converted common field.
         */
        public static String unHideCommonField(String commonKey) {

            return commonKey.substring(1);
        }

        /**
         * Return the {@code WrapperType} according to the below logic.
         *
         * case1: If response contain non of the fields then it is an UNKNOWN response. This could possibly happen
         *        due to decompression issues.
         * case2: If response contains any field like 'error_*' it is an ERROR response.
         * case3: If non of the above is true it is a NO_ERROR response.
         *
         * @param json the response body from the API response.
         * @return the {@code WrapperType}.
         */
        public WrapperType fetchWrapperType(JSONObject json) {

            for (Iterator<String> i = json.keys(); i.hasNext(); ) {
                String key = i.next();
                if (!commonKeySet.contains(key)) {
                    return WrapperType.UNKNOWN;
                }
                if (key.contains(ERROR_STRING)) {
                    return WrapperType.ERROR;
                }
            }
            return WrapperType.NO_ERROR;
        }

        /**
         * Type of the wrapper.
         *
         * {@code UNKNOWN}: the unknown response.
         * {@code ERROR}: the error response.
         * {@code NO_ERROR}: the correct response.
         */
        public enum WrapperType {
            UNKNOWN, ERROR, NO_ERROR
        }
    }
}
