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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import static org.wso2.carbon.connector.integration.test.StackExchangeCommonWrapper.getCommonKeyAsKey;
import static org.wso2.carbon.connector.integration.test.StackExchangeCommonWrapper.isCommonKey;

public class StackExchangeTestUtil {
    private static final String API_DOMAIN = "api.stackexchange.com";
    private static final String FILTER_PATH = "/filters";
    private static final String QUESTION_PATH = "/questions";
    private static final String ME_PATH = "/me";
    private static final String OUTER_KEY = "items";

    private static final Log LOG = LogFactory.getLog(StackExchangeTestUtil.class);

    public static URL getApiUrl(String version, String path) throws MalformedURLException {
        return getApiUrl(version, path, null);
    }

    public static URL getApiUrl(String version, String path, Map<String, String> queryParams) throws MalformedURLException {
        if (queryParams == null || queryParams.isEmpty()) {
             return new URL(String.format("https://%s/%s%s", API_DOMAIN, version, path));
        }
        StringBuilder queryParamBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry: queryParams.entrySet()) {
            queryParamBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            queryParamBuilder.append("&");
        }
        queryParamBuilder.setLength(queryParamBuilder.length() - 1);
        return new URL(String.format("https://%s/%s%s?%s", API_DOMAIN, version, path, queryParamBuilder.toString()));
    }

    public static int getUserReputation(String apiVersion, String site, String accessToken, String key) 
            throws IOException, JSONException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("key", key);
        queryParams.put("site", site);
        queryParams.put("access_token", accessToken);
        StackExchangeApiConnection connection = new StackExchangeApiConnection(
                (HttpsURLConnection) getApiUrl(apiVersion, ME_PATH, queryParams).openConnection());
        JSONObject body = connection.body();
        return body.getJSONArray(OUTER_KEY).getJSONObject(0).getInt("reputation");
    }

    public static FilterKeyIterator getFilterKeyIterator(String apiVersion, String filterName)
            throws Exception {
        StackExchangeApiConnection connection = new StackExchangeApiConnection(
                (HttpsURLConnection) getApiUrl(apiVersion, FILTER_PATH + "/" + filterName).openConnection());
        return new FilterKeyIterator(connection.body(), connection.responseCode());
    }

    public static QuestionIdIterator getQuestionIdIterator(String apiVersion, String site, int placeHolderId)
            throws Exception {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("site", site);
        StackExchangeApiConnection connection = new StackExchangeApiConnection(
                (HttpsURLConnection) getApiUrl(apiVersion, QUESTION_PATH, queryParams).openConnection());
        return new QuestionIdIterator(connection.body(), connection.responseCode(), placeHolderId);
    }

    public static class FilterKeyIterator implements Iterator<String> {
        private static final String INNER_KEY = "included_fields";

        private final Iterator<String> keyIterator;

        private FilterKeyIterator(JSONObject body, int code) throws Exception {
            if (code != 200) {
                throw new Exception(
                        String.format("Cannot create a %s from filter route response. status code: %s",
                                FilterKeyIterator.class.getName(), code));
            }
            JSONObject outer = body.getJSONArray(OUTER_KEY).getJSONObject(0);
            try {
                JSONArray data = outer.getJSONArray(INNER_KEY);
                List<String> keyList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    keyList.add(data.getString(i));
                }
                this.keyIterator = keyList.iterator();
            } catch (JSONException e) {
                throw new JSONException(
                        String.format("Cannot create a %s from filter route response. " +
                                "If you override filter name make sure it exists in backend.",
                                FilterKeyIterator.class.getName()));
            }
        }

        public Set<String> getCommonKeySet() {
            Set<String> commonKeys = new HashSet<>();
            while (hasNext()) {
                String key = next();
                if (isCommonKey(key)) {
                    commonKeys.add(getCommonKeyAsKey(key));
                }
            }
            return commonKeys;
        }

        @Override
        public boolean hasNext() {
            return keyIterator.hasNext();
        }

        @Override
        public String next() {
            return keyIterator.next();
        }
    }

    public static class QuestionIdIterator implements Iterator<Integer> {
        private static final String INNER_KEY = "question_id";

        private final int placeHolder;
        private final Iterator<Integer> idIterator;

        private QuestionIdIterator(JSONObject body, int code, int placeHolder) throws Exception {
            if (code != 200) {
                throw new Exception(
                        String.format("Cannot create a %s from question route response. status code: %s",
                                QuestionIdIterator.class.getName(), code));
            }
            JSONArray outer = body.getJSONArray(OUTER_KEY);
            List<Integer> idList = new ArrayList<>();
            for (int i = 0; i < outer.length(); i++) {
                int id = outer.getJSONObject(i).getInt(INNER_KEY);
                if (isUsable(id)) {
                    idList.add(id);
                }
            }
            this.idIterator = idList.iterator();
            this.placeHolder = placeHolder;
        }

        public boolean isUsable(int id) {
            return id != placeHolder;
        }

        @Override
        public boolean hasNext() {
            return idIterator.hasNext();
        }

        @Override
        public Integer next() {
            return idIterator.next();
        }
    }
}
