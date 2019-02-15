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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;

public class StackExchangeApiConnection {
    private final HttpsURLConnection connection;

    public StackExchangeApiConnection(HttpsURLConnection connection) {
        this.connection = connection;
    }

    public JSONObject body() throws IOException, JSONException {
        connection.setRequestProperty("Accept-Encoding", "gzip");
        InputStream stream = new GZIPInputStream(connection.getInputStream());
        return new JSONObject(IOUtils.toString(stream, "UTF-8"));
    }

    public int responseCode() throws IOException {
        return connection.getResponseCode();
    }
}
