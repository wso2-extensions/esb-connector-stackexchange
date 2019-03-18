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

/**
 * Build the StackExchange URL precisely.
 */
public class StackExchangeUrl {

    /**
     * StackExchange API domain.
     */
    private static final String API_DOMAIN = "api.stackexchange.com";
    /**
     * StackExchange URL.
     */
    private final URL url;

    /**
     * StackExchange URL builder.
     */
    public static class Builder {

        /**
         * Path of the URL.
         */
        private final String path;
        /**
         * API version.
         */
        private final String version;
        /**
         * Query parameters.
         */
        private final StringBuilder queryParamBuilder;

        /**
         * Return an Instance of {@code Builder}.
         *
         * @param version the StackExchange API version.
         * @param path    the path to the API route.
         */
        public Builder(String version, String path) {

            this.version = version;
            /* Paths should always start with a '/'. But if it isn't the case
               we should append '/' ourselves. */
            if (!path.isEmpty()) {
                this.path = (path.charAt(0) != '/') ? "/" + path : path;
            } else {
                this.path = "/";
            }
            queryParamBuilder = new StringBuilder();
        }

        /**
         * Add query parameters to {@code queryParamBuilder} and Return the {@code Builder}.
         *
         * @param key the name of the parameter.
         * @param val the value of the parameter.
         * @return the {@code Builder}.
         */
        public Builder queryParam(String key, String val) {

            queryParamBuilder.append("&").append(key).append("=").append(val);
            return this;
        }

        /**
         * Return an instance of {@code StackExchangeUrl}.
         *
         * @return an instance of {@code StackExchangeUrl}.
         * @throws MalformedURLException If URL string is broken.
         */
        public StackExchangeUrl build() throws MalformedURLException {

            return new StackExchangeUrl(this);
        }
    }

    /**
     * Return an instance of {@code StackExchangeUrl}.
     *
     * @param builder the StackExchange URL builder.
     * @throws MalformedURLException If URL string is broken.
     */
    private StackExchangeUrl(Builder builder) throws MalformedURLException {

        String queryParams = builder.queryParamBuilder.toString();
        StringBuilder urlBuilder = new StringBuilder().append("https://").append(API_DOMAIN).append("/")
                .append(builder.version).append(builder.path);
        if (!queryParams.isEmpty()) {
            urlBuilder.append("?").append(queryParams);
        }
        url = new URL(urlBuilder.toString());
    }

    /**
     * Open an connection using URL instance and return it as a {@code HttpsURLConnection}.
     *
     * @return an instance of {@code HttpsURLConnection}.
     * @throws IOException if URL instance is invalid.
     */
    public HttpsURLConnection openConnection() throws IOException {

        URLConnection connection = url.openConnection();
        return (HttpsURLConnection) connection;
    }
}
