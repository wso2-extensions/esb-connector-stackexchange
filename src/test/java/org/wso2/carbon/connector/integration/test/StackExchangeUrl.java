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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;

public class StackExchangeUrl {
    private static final String API_DOMAIN = "api.stackexchange.com";
    private final URL url;

    public static class Builder {
        private final String path;
        private final StringBuilder queryParamBuilder;

        public Builder(String version, String path) {
            this.path = path;
            queryParamBuilder = new StringBuilder();
        }

        public Builder queryParam(String key, String val) {
            queryParamBuilder
                    .append("&")
                    .append(key)
                    .append("=")
                    .append(val);
            return this;
        }

        public StackExchangeUrl build() throws MalformedURLException {
            return new StackExchangeUrl(this);
        }
    }

    private StackExchangeUrl(Builder builder) throws MalformedURLException {
        String queryParams = builder.queryParamBuilder.toString();
        StringBuilder urlBuilder = new StringBuilder()
                .append("https://")
                .append(API_DOMAIN)
                .append(builder.path);
        if (!queryParams.isEmpty()) {
            urlBuilder.append("?").append(queryParams);
        }
        url = new URL(urlBuilder.toString());
    }
    
    public HttpsURLConnection openConnection() throws IOException {
        URLConnection connection = url.openConnection();
        return (HttpsURLConnection) connection;
    }
}
