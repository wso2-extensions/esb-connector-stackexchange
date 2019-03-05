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
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;

/**
 * Decorate the HttpsURLConnection instance to force the decompression.
 */
public class StackExchangeUrlConnection {

    /**
     * HttpsURLConnection instance.
     */
    private final HttpsURLConnection connection;

    public StackExchangeUrlConnection(HttpsURLConnection connection) {

        this.connection = connection;
        this.connection.setRequestProperty("Accept-Encoding", "gzip");
    }

    /**
     * Return the response code.
     *
     * @return the response code.
     * @throws IOException If response is invalid.
     */
    public int getResponseCode() throws IOException {

        return connection.getResponseCode();
    }

    /**
     * Return the decompressed API response.
     *
     * @return the decompressed API response.
     * @throws IOException If response is invalid.
     */
    public InputStream getInputStream() throws IOException {

        return new GZIPInputStream(connection.getInputStream());
    }
}
