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
import org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.StackExchangeCommonWrapper.WrapperType;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.wso2.carbon.connector.integration.test.CommonTestUtil.TestType;
import static org.wso2.carbon.connector.integration.test.CommonTestUtil.getConnectorName;
import static org.wso2.carbon.connector.integration.test.CommonTestUtil.getFilenameOfPayload;
import static org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.StackExchangeCommonWrapper;
import static org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.StackExchangeItems;
import static org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.getStackExchangeCommonWrapper;
import static org.wso2.carbon.connector.integration.test.StackExchangeTestUtil.getStackExchangeItems;

/*
 * StackExchange connector integration test
 */
@Listeners(TestNgExecutionListener.class)
public class StackExchangeConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private static final Log LOG = LogFactory.getLog(StackExchangeConnectorIntegrationTest.class);

    public static final Properties stackExchangeProperties = new Properties();

    /**
     * Common property keys for saving StackExchange specific data.
     */
    public static final String STACKEXCHANGE_HAS_QUESTION = "stackexchange.has_question";
    public static final String STACKEXCHANGE_HAS_QUESTION_WITH_ANSWERS = "stackexchange.has_question_with_answers";
    public static final String STACKEXCHANGE_HAS_ANSWER = "stackexchange.has_answer";
    public static final String STACKEXCHANGE_IS_UNACCEPTED_ANSWER = "stackexchange.is_unaccepted_answer";
    public static final String STACKEXCHANGE_PRIVILEGES = "stackexchange.privileges";

    /**
     * Connector property keys for saving StackExchange specific data.
     */

    /* To store a valid question id */
    private static final String PROP_KEY_QUESTION_ID = "questionId";
    /* To store a valid answer id */
    private static final String PROP_KEY_ANSWER_ID = "answerId";
    /* To store an unaccepted answer id */
    private static final String PROP_KEY_UNACCEPTED_ANSWER_ID = "unacceptedAnswerId";
    /* To store a semicolon delimited set of answer ids */
    private static final String PROP_KEY_ANSWER_ID_LIST = "answerIdList";
    /* To store a semicolon delimited set of site tags */
    private static final String PROP_KEY_SITE_TAGS = "siteTags";

    /**
     * Keys needed to extract data from an StackExchange API response.
     */
    private static final String SE_RES_KEY_NAME = "name";
    private static final String SE_RES_KEY_INCLUDED_FIELDS = "included_fields";
    private static final String SE_RES_KEY_QUESTION_ID = "question_id";
    private static final String SE_RES_KEY_ANSWER_ID = "answer_id";
    private static final String SE_RES_KEY_SHORT_DESCRIPTION = "short_description";
    private static final String SE_RES_KEY_IS_ACCEPTED = "is_accepted";

    /**
     * Common API properties
     */
    private String key;
    private String site;
    private String apiVersion;

    private StackExchangeCommonWrapper stackExchangeCommonWrapper;

    /**
     * EI headers map.
     */
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
        String placeHolderASite = connectorProperties.getProperty("placeHolderASite");
        String placeHolderQSite = connectorProperties.getProperty("placeHolderQSite");

        this.key = key;
        this.site = site;
        this.apiVersion = apiVersion;

        /* Save few tags specific to the site */
        StackExchangeItems tagItems = getStackExchangeTagItems();
        String[] tagNameArray = tagItems.getAll(SE_RES_KEY_NAME, String.class);
        setListLikePropertyInConnector(PROP_KEY_SITE_TAGS, tagNameArray);

        /* Create a common wrapper using filter route */
        StackExchangeItems filterItems = getStackExchangeFilterItems(filterName);
        String[] includedFields = filterItems.getRandom(SE_RES_KEY_INCLUDED_FIELDS, String[].class);
        stackExchangeCommonWrapper = getStackExchangeCommonWrapper(includedFields);

        /* Save a random id of a question */
        StackExchangeItems questionItems = getStackExchangeQuestionItems();
        Integer questionId = questionItems.getRandom(SE_RES_KEY_QUESTION_ID, Integer.class);
        /* Save availability of Placeholder question id */
        if (StringUtils.isEmpty(placeHolderQId)) {
            stackExchangeProperties.setProperty(STACKEXCHANGE_HAS_QUESTION, String.valueOf(false));
            stackExchangeProperties.setProperty(STACKEXCHANGE_HAS_QUESTION_WITH_ANSWERS, String.valueOf(false));
        } else {
            stackExchangeProperties.setProperty(STACKEXCHANGE_HAS_QUESTION, String.valueOf(true));
            StackExchangeItems placeHolderQuestionAnswerItems = getStackExchangeAnswerItems(
                    Integer.parseInt(placeHolderQId), placeHolderQSite);
            if (placeHolderQuestionAnswerItems.isEmpty()) {
                stackExchangeProperties.setProperty(STACKEXCHANGE_HAS_QUESTION_WITH_ANSWERS, String.valueOf(false));
            } else {
                boolean hasAcceptedAnswer = false;
                for (int i = 0; i < placeHolderQuestionAnswerItems.length(); i++) {
                    boolean b = placeHolderQuestionAnswerItems.getAt(SE_RES_KEY_IS_ACCEPTED, Boolean.class,0);
                    if (b) {
                        hasAcceptedAnswer = true;
                        break;
                    }
                }
                stackExchangeProperties.setProperty(
                        STACKEXCHANGE_HAS_QUESTION_WITH_ANSWERS, String.valueOf(hasAcceptedAnswer));
                if (!hasAcceptedAnswer) {
                    Integer unacceptedAnswerId = placeHolderQuestionAnswerItems.getRandom(
                            SE_RES_KEY_ANSWER_ID, Integer.class);
                    connectorProperties.setProperty(PROP_KEY_UNACCEPTED_ANSWER_ID, String.valueOf(unacceptedAnswerId));
                }
            }
            /* API routes like 'voting up a question' does not allow
               user to up vote his/her own post. In those cases we should
               avoid using placeHolderQId as the questionId. Hence looping
               to guarantee it never happens. Length check is necessary to
               avoid infinite looping. */
            if (questionItems.length() > 1) {
                int qid = Integer.parseInt(placeHolderQId);
                while (questionId.equals(qid)) {
                    questionId = questionItems.getRandom(SE_RES_KEY_QUESTION_ID, Integer.class);
                }
            }
        }
        connectorProperties.setProperty(PROP_KEY_QUESTION_ID, String.valueOf(questionId));

        /* Save a random id of an answer */
        StackExchangeItems answerItems = getStackExchangeAnswerItems(questionId);
        Integer answerId = answerItems.getRandom(SE_RES_KEY_ANSWER_ID, Integer.class);
        Integer[] answerIdArray = answerItems.getAll(SE_RES_KEY_ANSWER_ID, Integer.class);
        connectorProperties.setProperty(PROP_KEY_ANSWER_ID, String.valueOf(answerId));
        setListLikePropertyInConnector(PROP_KEY_ANSWER_ID_LIST, answerIdArray);

        /* Save availability of Placeholder answer id */
        if (StringUtils.isEmpty(placeHolderAId)) {
            stackExchangeProperties.setProperty(STACKEXCHANGE_HAS_ANSWER, String.valueOf(false));
        } else {
            stackExchangeProperties.setProperty(STACKEXCHANGE_HAS_ANSWER, String.valueOf(true));
            /* TODO: Check placeHolderAId and answer id collide. */
            if (hasAnswerAccepted(Integer.parseInt(placeHolderAId), placeHolderASite)) {
                stackExchangeProperties.setProperty(STACKEXCHANGE_IS_UNACCEPTED_ANSWER, String.valueOf(false));
            } else {
                stackExchangeProperties.setProperty(STACKEXCHANGE_IS_UNACCEPTED_ANSWER, String.valueOf(true));
            }
        }

        /* Check credential availability to avoid unnecessary failures */
        if (StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(key)) {
            /* Save privilege wordings belong to given credentials */
            StackExchangeItems privilegeItems = getStackExchangePrivilegeItems(accessToken);
            String[] privilegeShortDescriptionArray = privilegeItems.getAll(SE_RES_KEY_SHORT_DESCRIPTION, String.class);
            setListLikePropertyInSystem(STACKEXCHANGE_PRIVILEGES, privilegeShortDescriptionArray);
        } else {
            /* Set defaults */
            stackExchangeProperties.setProperty(STACKEXCHANGE_PRIVILEGES, StackExchange.PRIVILEGE_DEFAULT);
        }
    }

    /**
     * Return whether the answer given by answer id is accepted.
     *
     * @param answerId the id the answer.
     * @param site the site answer id belong to.
     * @return whether the answer given by answer id is accepted.
     */
    private boolean hasAnswerAccepted(int answerId, String site) throws Exception {
        StackExchangeUrl answerUrl =
                new StackExchangeUrl.Builder(apiVersion, "/answers/" + answerId)
                        .queryParam("site", site).build();
        StackExchangeItems answerItems = getStackExchangeItems( answerUrl);
        return answerItems.getAt(SE_RES_KEY_IS_ACCEPTED, Boolean.class, 0);
    }

    /**
     * Following are set of custom API routes we have defined for convenience. These routes will help gathering
     * data to make sure that we have enough data to run a corresponding test. So based on the data we execute
     * or skip a test. Remember we only have one method ({@code StackExchangeTestUtil.getStackExchangeItems})
     * to call the API and these are only wrappers to that method. We have categorized them based on the object
     * they contain in the response.
     *
    /* ============================================= Tag routes ============================================= */

    /**
     * @return the Tag items belong to the {@code site}
     */
    private StackExchangeItems getStackExchangeTagItems() throws Exception {

        StackExchangeUrl tagUrl =
                new StackExchangeUrl.Builder(apiVersion, "/tags")
                        .queryParam("site", site)
                        .queryParam("pagesize", "3").build();
        return getStackExchangeItems(tagUrl);
    }

    /* ============================================= Filter routes ============================================= */

    /**
     * Return filter items to the given filter.
     *
     * @param filterName the name of the filter
     * @return the filter items to the given filter.
     */
    private StackExchangeItems getStackExchangeFilterItems(String filterName) throws Exception {

        StackExchangeUrl filterUrl =
                new StackExchangeUrl.Builder(apiVersion, "/filters/" + filterName).build();
        return getStackExchangeItems(filterUrl);
    }

    /* ============================================= Question routes ============================================= */

    /**
     *
     * @return
     * @throws Exception
     */
    private StackExchangeItems getStackExchangeQuestionItems() throws Exception {

        StackExchangeUrl questionUrl =
                new StackExchangeUrl.Builder(apiVersion, "/search/advanced")
                        .queryParam("site", site)
                        .queryParam("accepted", "False")
                        .queryParam("answers", "1").build();
        return getStackExchangeItems(questionUrl);
    }

    /* ============================================= Answer routes ============================================= */

    private StackExchangeItems getStackExchangeAnswerItems(int questionId, String site) throws Exception {

        StackExchangeUrl answerUrl =
                new StackExchangeUrl.Builder(apiVersion, "/questions/" + questionId + "/answers")
                        .queryParam("site", site).build();
        return getStackExchangeItems(answerUrl);
    }

    private StackExchangeItems getStackExchangeAnswerItems(int questionId) throws Exception {

        StackExchangeUrl answerUrl =
                new StackExchangeUrl.Builder(apiVersion, "/questions/" + questionId + "/answers")
                        .queryParam("site", site).build();
        return getStackExchangeItems(answerUrl);
    }

    /* ============================================= Privilege routes ============================================= */

    private StackExchangeItems getStackExchangePrivilegeItems(String accessToken) throws Exception {

        StackExchangeUrl privilegeUrl =
                new StackExchangeUrl.Builder(apiVersion, "/me/privileges/")
                        .queryParam("site", site)
                        .queryParam("key", key)
                        .queryParam("access_token", accessToken).build();
        return getStackExchangeItems(privilegeUrl);
    }

    private <T> void setListLikePropertyInSystem(String propertyName, T[] array) {

        stackExchangeProperties.setProperty(propertyName, getTArrayAsSemicolonDelimitedString(array));
    }

    private <T> void setListLikePropertyInConnector(String propertyName, T[] array) {

        connectorProperties.setProperty(propertyName, getTArrayAsSemicolonDelimitedString(array));
    }

    private <T> String getTArrayAsSemicolonDelimitedString(T[] array) {

        StringBuilder builder = new StringBuilder();
        for (T a : array) {
            builder.append(a).append(";");
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    /* ======================================= getUsersByIds ======================================= */

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testUsersByIdsWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getUsersByIds", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetUsersByIdsWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getUsersByIds", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= addQuestion ======================================= */

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAddQuestionWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("addQuestion", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAddQuestionWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("addQuestion", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= deleteQuestionById ======================================= */

    @StackExchange(needMyQuestion = true)
    @Test(groups = {"wso2.ei"})
    public void testDeleteQuestionByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("deleteQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(needMyQuestion = true)
    @Test(groups = {"wso2.ei"})
    public void testDeleteQuestionByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("deleteQuestionById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= downvoteQuestionById ======================================= */

    @StackExchange(privilege = "vote down")
    @Test(groups = {"wso2.ei"})
    public void testDownvoteQuestionByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("downvoteQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(privilege = "vote down")
    @Test(groups = {"wso2.ei"})
    public void testDownvoteQuestionByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("downvoteQuestionById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= editQuestionById ======================================= */

    @StackExchange(needMyQuestion = true, skipPrivilegeCheck = true, privilege = "edit questions and answers")
    @Test(groups = {"wso2.ei"})
    public void testEditQuestionByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("editQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(needMyQuestion = true, skipPrivilegeCheck = true, privilege = "edit questions and answers")
    @Test(groups = {"wso2.ei"})
    public void testEditQuestionByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("editQuestionById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= upvoteQuestionById ======================================= */

    @StackExchange(privilege = "vote up")
    @Test(groups = {"wso2.ei"})
    public void testUpvoteQuestionByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("upvoteQuestionById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(privilege = "vote up")
    @Test(groups = {"wso2.ei"})
    public void testUpvoteQuestionByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("upvoteQuestionById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= getQuestionsByUserIds ======================================= */

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsByUserIdsWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getQuestionsByUserIds", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsByUserIdsWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getQuestionsByUserIds", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsByUserIdsWithPaging() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getQuestionsByUserIds", TestType.PAGING);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= getQuestionsWithNoAnswers ======================================= */

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsWithNoAnswersWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getQuestionsWithNoAnswers", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsWithNoAnswersWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getQuestionsWithNoAnswers", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsWithNoAnswersWithOptional() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getQuestionsWithNoAnswers", TestType.OPTIONAL);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetQuestionsWithNoAnswersWithPaging() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getQuestionsWithNoAnswers", TestType.PAGING);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= acceptAnswerById ======================================= */

    @StackExchange(needMyQuestionWithAnswers = true)
    @Test(groups = {"wso2.ei"})
    public void testAcceptAnswerByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("acceptAnswerById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(needMyQuestionWithAnswers = true)
    @Test(groups = {"wso2.ei"})
    public void testAcceptAnswerByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("acceptAnswerById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= addAnswer ======================================= */

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAddAnswerWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("addAnswer", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange
    @Test(groups = {"wso2.ei"})
    public void testAddAnswerWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("addAnswer", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= deleteAnswerById ======================================= */

    @StackExchange(needMyAnswer = true, needUnacceptedAnswer = true)
    @Test(groups = {"wso2.ei"})
    public void testDeleteAnswerByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("deleteAnswerById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(needMyAnswer = true, needUnacceptedAnswer = true)
    @Test(groups = {"wso2.ei"})
    public void testDeleteAnswerByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("deleteAnswerById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= editAnswerById ======================================= */

    @StackExchange(needMyAnswer = true)
    @Test(groups = {"wso2.ei"})
    public void testEditAnswerByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("editAnswerById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(needMyAnswer = true)
    @Test(groups = {"wso2.ei"})
    public void testEditAnswerByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("editAnswerById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= downvoteAnswerById ======================================= */

    @StackExchange(privilege = "vote down")
    @Test(groups = {"wso2.ei"})
    public void testDownvoteAnswerByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("downvoteAnswerById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(privilege = "vote down")
    @Test(groups = {"wso2.ei"})
    public void testDownvoteAnswerByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("downvoteAnswerById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= upvoteAnswerById ======================================= */

    @StackExchange(privilege = "vote up")
    @Test(groups = {"wso2.ei"})
    public void testUpvoteAnswerByIdWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("upvoteAnswerById", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(privilege = "vote up")
    @Test(groups = {"wso2.ei"})
    public void testUpvoteAnswerByIdWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("upvoteAnswerById", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    /* ======================================= getAnswersByIds ======================================= */

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetAnswersByIdsWithInvalid() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getAnswersByIds", TestType.INVALID, "missingParameter");

        Assert.assertEquals(response.getHttpStatusCode(), 400);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetAnswersByIdsWithMandatory() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getAnswersByIds", TestType.MANDATORY);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
    }

    @StackExchange(skipPrivilegeCheck = true)
    @Test(groups = {"wso2.ei"})
    public void testGetAnswersByIdsWithPaging() throws IOException, JSONException {

        RestResponse<JSONObject> response = sendJsonPostReqToEi("getAnswersByIds", TestType.PAGING);

        Assert.assertEquals(response.getHttpStatusCode(), 200);
        Assert.assertEquals(stackExchangeCommonWrapper.fetchWrapperType(response.getBody()), WrapperType.NO_ERROR);
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
