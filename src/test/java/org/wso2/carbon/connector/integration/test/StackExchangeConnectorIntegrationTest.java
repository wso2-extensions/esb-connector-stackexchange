/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample integration test
 */
public class StackExchangeConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private static final Log LOG = LogFactory.getLog(StackExchangeConnectorIntegrationTest.class);

    private Map<String, String> eiRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        StringBuilder connectorName = new StringBuilder()
                .append(System.getProperty("connector_name"))
                .append("-")
                .append("connector")
                .append("-")
                .append(System.getProperty("connector_version"))
                .append(".zip");

        init(connectorName.toString());

        getApiConfigProperties();

        eiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        eiRequestHeadersMap.put("Content-Type", "application/json");
    }

    @Test(groups = {"wso2.ei"})
    public void testGetMeWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonRestRequest(
                proxyUrl,
                HttpVerb.POST,
                eiRequestHeadersMap,
                mandatoryFilename("getMe"));
        Assert.assertEquals(response.getHttpStatusCode(), 200);
    }

    private String mandatoryFilename(String method) {
        return String.format("%s_mandatory.json", method);
    }

    private String optionalFilename(String method) {
        return String.format("%s_optional.json", method);
    }

    private static class HttpVerb {
        static final String GET = "GET";
        static final String POST = "POST";
    }
}