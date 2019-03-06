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

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.internal.TestResult;

import static org.wso2.carbon.connector.integration.test.StackExchangeConnectorIntegrationTest.*;
import static org.wso2.carbon.connector.integration.test.StackExchangeConnectorIntegrationTest.STACKEXCHANGE_HAS_ANSWER;
import static org.wso2.carbon.connector.integration.test.StackExchangeConnectorIntegrationTest.STACKEXCHANGE_HAS_QUESTION;
import static org.wso2.carbon.connector.integration.test.StackExchangeConnectorIntegrationTest.STACKEXCHANGE_PRIVILEGES;

public class TestNgExecutionListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

        /* All non test methods should be avoided */
        if (!iInvokedMethod.isTestMethod()) {
            return;
        }
        try {
            StackExchange stackExchange = StackExchangeConnectorIntegrationTest.class.getMethod(
                    iInvokedMethod.getTestMethod().getMethodName()).getAnnotation(StackExchange.class);

            /* If no annotation is present no need to process further */
            if (stackExchange == null) {
                return;
            }

            /* If needMyAnswer is true then the test method needs an id for an user owned answer.
               If we have one we should let the test proceed or skip otherwise */
            if (stackExchange.needMyAnswer()) {
                String hasA = stackExchangeProperties.getProperty(STACKEXCHANGE_HAS_ANSWER);
                if (!Boolean.parseBoolean(hasA)) {
                    iTestResult.setStatus(TestResult.SKIP);
                    throw new SkipException("Cannot execute this test due to lack of data hence skipping.");
                }
            }
            /* If needMyQuestion is true then the test method needs an id for an user owned question.
               If we have one we should let the test proceed or skip otherwise */
            if (stackExchange.needMyQuestion()) {
                String hasQ = stackExchangeProperties.getProperty(STACKEXCHANGE_HAS_QUESTION);
                if (!Boolean.parseBoolean(hasQ)) {
                    iTestResult.setStatus(TestResult.SKIP);
                    throw new SkipException("Cannot execute this test due to lack of data hence skipping.");
                }
            }
            /* Test method has stored the privilege which user should have gained to run the test method.
               If we find this privilege inside STACKEXCHANGE_PRIVILEGES property which is created using response
               of user's privilege route (/me/privilege) we should let the test proceed or skip otherwise. */
            if (!stackExchange.skipPrivilegeCheck()) {
                String privileges = stackExchangeProperties.getProperty(STACKEXCHANGE_PRIVILEGES);
                /* Lowercase and trim subroutines are called to guarantee the overall safety of the logic. */
                if (privileges != null &&
                        privileges.toLowerCase().contains(stackExchange.privilege().trim().toLowerCase())) {
                    return;
                }
                iTestResult.setStatus(TestResult.SKIP);
                throw new SkipException(String.format(
                        "Cannot execute this test due to lack of privileges hence skipping. (missing: %s)",
                        stackExchange.privilege()));
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find the test method", e);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }
}
