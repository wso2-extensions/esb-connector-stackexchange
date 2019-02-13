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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;

import static org.wso2.carbon.connector.integration.test.StackExchangeCommonWrapper.getCommonKeyAsKey;
import static org.wso2.carbon.connector.integration.test.StackExchangeCommonWrapper.isCommonKey;

public class StackExchangeTestUtil {

    public static Filter getFilter(String apiUrl, String filterPath, String filterName)
            throws IOException, JSONException {

        URL url = new URL(apiUrl + filterPath + "/" + filterName);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestProperty("Accept-Encoding", "gzip");
        InputStream response = new GZIPInputStream(connection.getInputStream());

        return new Filter(new JSONObject(IOUtils.toString(response, "UTF-8")), connection.getResponseCode());
    }

    public static class Filter {
        private static final String OUTER_KEY = "items";
        private static final String INNER_KEY = "included_fields";

        private final JSONObject body;
        private final int code;

        private Filter(JSONObject body, int code) {
            this.body = body;
            this.code = code;
        }

        public boolean hasErrorKeys() {
            return code != 200;
        }

        public boolean hasData() {
            return body.has(OUTER_KEY);
        }

        public Set<String> getCommonKeySet() throws JSONException {
            if (hasErrorKeys() || !hasData()) {
                return null;
            }
            JSONArray keys = body.getJSONArray(OUTER_KEY).getJSONObject(0).getJSONArray(INNER_KEY);
            Set<String> commonKeys = new HashSet<>();
            for (int i = 0; i < keys.length(); i++) {
                String k = keys.getString(i);
                if (isCommonKey(k)) {
                    commonKeys.add(getCommonKeyAsKey(k));
                }
            }
            return commonKeys;
        }

    }
}
