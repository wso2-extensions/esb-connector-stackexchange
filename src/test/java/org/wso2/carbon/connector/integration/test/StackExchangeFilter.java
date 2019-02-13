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

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class StackExchangeFilter {
    private static final String WRAPPER_ROOT = "items";
    private static final String DATA_ROOT = "included_fields";

    private final Set<String> commonKeys;

    public StackExchangeFilter(JSONObject filter) throws JSONException {
        JSONArray keys = filter
                .getJSONArray(WRAPPER_ROOT)
                .getJSONObject(0)
                .getJSONArray(DATA_ROOT);
        commonKeys = getCommonKeys(keys);
    }

    private static Set<String> getCommonKeys(JSONArray keys) throws JSONException {
        Set<String> commonKeys = new HashSet<>();
        for (int i = 0; i < keys.length(); i++) {
            String k = keys.getString(i);
            if (isCommonKey(k)) {
                commonKeys.add(getCommonKeyAsKey(k));
            }
        }
        return commonKeys;
    }

    private static boolean isCommonKey(String key) {
        return StringUtils.isNotEmpty(key) && key.charAt(0) == '.';
    }

    private static String getCommonKeyAsKey(String commonKey) {
        return commonKey.substring(1);
    }

    public WrapperType fetchWrapperType(JSONObject json) {
        for (Iterator<String> i = json.keys(); i.hasNext();) {
            String key = i.next();
            if (!commonKeys.contains(key)) {
                return WrapperType.UNKNOWN;
            }
            if (key.contains("error")) {
                return WrapperType.ERROR;
            }
        }
        return WrapperType.NO_ERROR;
    }

    public enum WrapperType {
        UNKNOWN, ERROR, NO_ERROR
    }
}
