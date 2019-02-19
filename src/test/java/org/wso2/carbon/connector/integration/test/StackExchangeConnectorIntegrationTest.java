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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.StackExchangeCommonWrapper.WrapperType;
import org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.FilterIncludedFieldsKey;
import org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.QuestionIdKey;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.carbon.connector.integration.test.CommonTestUtil.TestType;
import static org.wso2.carbon.connector.integration.test.CommonTestUtil.getConnectorName;
import static org.wso2.carbon.connector.integration.test.CommonTestUtil.getFilenameOfPayload;
import static org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.*;
import static org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.getStackExchangeObjectKey;

/**
 * StackExchange connector integration test
 */
@Listeners(TestNgExecutionListener.class)
public class StackExchangeConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private static final Log LOG = LogFactory.getLog(StackExchangeConnectorIntegrationTest.class);

    public static final String STACKEXCHANGE_PRIVILEGES = "stackexchange.privileges";
    public static final String STACKEXCHANGE_PRIVILEGES_SEPARATOR = ";";

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
        String filterName = connectorProperties.getProperty("filterName");
        String apiVersion = connectorProperties.getProperty("apiVersion");
        String accessToken = connectorProperties.getProperty("accessToken");
        String placeHolderQId = connectorProperties.getProperty("placeHolderQId");
        String placeHolderAId = connectorProperties.getProperty("placeHolderAId");

        StackExchangeUrl filterUrl =
                new StackExchangeUrl.Builder(apiVersion, "/filters/" + filterName).build();
        FilterIncludedFieldsKey filterIncludedFieldsKey =
                getStackExchangeObjectKey(filterUrl, FilterIncludedFieldsKey.class);
        stackExchangeCommonWrapper = new StackExchangeCommonWrapper(filterIncludedFieldsKey);

        StackExchangeUrl questionUrl =
                new StackExchangeUrl.Builder(apiVersion, "/search/advanced")
                        .queryParam("site", site)
                        .queryParam("accepted", "False")
                        .queryParam("answers", "1").build();
        QuestionIdKey questionIdKey = getStackExchangeObjectKey(questionUrl, QuestionIdKey.class);
        while (Integer.parseInt(placeHolderQId) == questionIdKey.getKey()) {
            questionIdKey = getStackExchangeObjectKey(questionUrl, QuestionIdKey.class);
        }
        connectorProperties.setProperty("questionId", String.valueOf(questionIdKey.getKey()));

        if (StringUtils.isEmpty(placeHolderAId)) {
            StackExchangeUrl answerUrl =
                    new StackExchangeUrl.Builder(apiVersion, "/questions/" + questionIdKey.getKey() +"/answers")
                            .queryParam("site", site).build();
            AnswerIdKey answerIdKey = getStackExchangeObjectKey(answerUrl, AnswerIdKey.class);
            connectorProperties.setProperty("placeHolderAId", String.valueOf(answerIdKey.getKey()));
        }

        StackExchangeUrl privilegeUrl =
                new StackExchangeUrl.Builder(apiVersion, "/me/privileges/")
                        .queryParam("site", site)
                        .queryParam("key", key)
                        .queryParam("access_token", accessToken).build();
        List<PrivilegeShortDescriptionKey> privilegeShortDescriptionKeyList =
                getStackExchangeObjectKeyList(privilegeUrl, PrivilegeShortDescriptionKey.class);
        setPrivilegesInSystemProperty(privilegeShortDescriptionKeyList);
    }

    private void setPrivilegesInSystemProperty(List<PrivilegeShortDescriptionKey> privileges) {
        StringBuilder privilegeBuilder = new StringBuilder();
        for (PrivilegeShortDescriptionKey key : privileges) {
            privilegeBuilder.append(key.getKey()).append(STACKEXCHANGE_PRIVILEGES_SEPARATOR);
        }
        if (privilegeBuilder.length() > 0) {
            privilegeBuilder.setLength(privilegeBuilder.length() - 1);
        }
        System.setProperty(STACKEXCHANGE_PRIVILEGES, privilegeBuilder.toString());
    }

    /* ======================================= getMe ======================================= */

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetMeWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getMe", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetMeWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getMe", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= addQuestion ======================================= */

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAddQuestionWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("addQuestion", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAddQuestionWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("addQuestion", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= deleteQuestionById ======================================= */

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testDeleteQuestionByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("deleteQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testDeleteQuestionByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("deleteQuestionById", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= downvoteQuestionById ======================================= */

    @StackExchange(privilege = "vote down")
    @Test(groups = {"wso2.ei"})
    public void testDownvoteQuestionByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("downvoteQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange(privilege = "vote down")
    @Test(groups = {"wso2.ei"})
    public void testDownvoteQuestionByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("downvoteQuestionById", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= editQuestionById ======================================= */

    @StackExchange(skipPrivilegeCheck = true, privilege = "edit questions and answers")
    @Test(groups = {"wso2.ei"})
    public void testEditQuestionByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("editQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true, privilege = "edit questions and answers")
    @Test(groups = {"wso2.ei"})
    public void testEditQuestionByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("editQuestionById", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= upvoteQuestionById ======================================= */

    @StackExchange(privilege = "vote up")
    @Test(groups = {"wso2.ei"})
    public void testUpvoteQuestionByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("upvoteQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange(privilege = "vote up")
    @Test(groups = {"wso2.ei"})
    public void testUpvoteQuestionByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("upvoteQuestionById", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= getQuestionsByUserIds ======================================= */

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsByUserIdsWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getQuestionsByUserIds", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsByUserIdsWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getQuestionsByUserIds", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsByUserIdsWithPaging() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getQuestionsByUserIds", TestType.PAGING);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= getQuestionsWithNoAnswers ======================================= */

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsWithNoAnswersWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getQuestionsWithNoAnswers", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsWithNoAnswersWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getQuestionsWithNoAnswers", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsWithNoAnswersWithOptional() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getQuestionsWithNoAnswers", TestType.OPTIONAL);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsWithNoAnswersWithPaging() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("getQuestionsWithNoAnswers", TestType.PAGING);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= acceptAnswerById ======================================= */

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAcceptAnswerByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("acceptAnswerById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(r.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.ERROR);
    }

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAcceptAnswerByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> r = sendJsonPostReqToEi("acceptAnswerById", TestType.MANDATORY);

        Assert.assertEquals(r.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(r.getBody()), WrapperType.NO_ERROR);
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