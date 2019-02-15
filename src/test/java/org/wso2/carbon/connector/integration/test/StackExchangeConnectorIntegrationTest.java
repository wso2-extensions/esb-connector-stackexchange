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
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.StackExchangeCommonWrapper.WrapperType;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.wso2.carbon.connector.integration.test.CommonTestUtil.*;

/**
 * StackExchange connector integration test
 */
@Listeners(TestNgExecutionListener.class)
public class StackExchangeConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    public static final String STACKEXCHANGE_REPUTATION = "stackexchange.reputation";
    private static final Log LOG = LogFactory.getLog(StackExchangeConnectorIntegrationTest.class);

    private StackExchangeCommonWrapper stackExchangeCommonWrapper;
    private Map<String, String> eiRequestHeadersMap = new HashMap<>();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        eiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        eiRequestHeadersMap.put("Content-Type", "application/json");

        init(getConnectorName());
        getApiConfigProperties();

        String key = connectorProperties.getProperty("key");
        String site = connectorProperties.getProperty("site");
        String apiVersion = connectorProperties.getProperty("apiVersion");
        String accessToken = connectorProperties.getProperty("accessToken");
        String filterName = connectorProperties.getProperty("filterName");
        int placeHolderId = Integer.parseInt(connectorProperties.getProperty("placeHolderId"));

        stackExchangeCommonWrapper = new StackExchangeCommonWrapper(
                StackExchangeTestUtil.getFilterKeyIterator(apiVersion, filterName));

        int stackExchangeQuestionId = StackExchangeTestUtil.getQuestionIdIterator(
                apiVersion, site, placeHolderId).next();
        connectorProperties.setProperty("questionId", String.valueOf(stackExchangeQuestionId));

        int reputation = StackExchangeTestUtil.getUserReputation(apiVersion, site, accessToken, key);
        System.setProperty(STACKEXCHANGE_REPUTATION, String.valueOf(reputation));
    }

    /* ======================================= getMe ======================================= */

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testGetMeWithMandatory() throws IOException, JSONException {
        RestResponse<JSONObject> r = sendJsonPostReqToEi("getMe", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testGetMeWithInvalid() throws IOException, JSONException {
        RestResponse<JSONObject> r = sendJsonPostReqToEi("getMe", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    /* ======================================= addQuestion ======================================= */

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAddQuestionWithMandatory() throws IOException, JSONException {
        RestResponse<JSONObject> r = sendJsonPostReqToEi("addQuestion", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAddQuestionWithInvalid() throws IOException, JSONException {
        RestResponse<JSONObject> r = sendJsonPostReqToEi("addQuestion", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    /* ======================================= deleteQuestionById ======================================= */

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testDeleteQuestionByIdWithMandatory() throws IOException, JSONException {
        RestResponse<JSONObject> r = sendJsonPostReqToEi("deleteQuestionById", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testDeleteQuestionByIdWithInvalid() throws IOException, JSONException {
        RestResponse<JSONObject> r = sendJsonPostReqToEi("deleteQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    /* ======================================= downvoteQuestion ======================================= */

    @StackExchange(minReputation = 125, privilegeWording = "vote down")
    @Test(groups = {"wso2.ei"})
    public void testDownvoteQuestionByIdWithMandatory() throws IOException, JSONException {
        RestResponse<JSONObject> r = sendJsonPostReqToEi("downvoteQuestionById", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange(minReputation = 125, privilegeWording = "vote down")
    @Test(groups = {"wso2.ei"})
    public void testDownvoteQuestionByIdWithInvalid() throws IOException, JSONException {
        RestResponse<JSONObject> r = sendJsonPostReqToEi("downvoteQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    /* ======================================= Helpers  ======================================= */

    private RestResponse<JSONObject> sendJsonPostReqToEi(String method, TestType type)
            throws IOException, JSONException {
        return sendJsonPostReqToEi(method, type, null);
    }

    private RestResponse<JSONObject> sendJsonPostReqToEi(String method, TestType type, String suffix)
            throws IOException, JSONException {
        eiRequestHeadersMap.put("Action", String.format("urn:%s", method));
        return sendJsonRestRequest(proxyUrl,
                "POST",
                eiRequestHeadersMap,
                getFilenameOfPayload(method, type, suffix));
    }
}