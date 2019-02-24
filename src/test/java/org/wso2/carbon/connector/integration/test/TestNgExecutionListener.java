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

import static org.wso2.carbon.connector.integration.test.StackExchangeConnectorIntegrationTest.STACKEXCHANGE_HAS_ANSWER;
import static org.wso2.carbon.connector.integration.test.StackExchangeConnectorIntegrationTest.STACKEXCHANGE_HAS_QUESTION;
import static org.wso2.carbon.connector.integration.test.StackExchangeConnectorIntegrationTest.STACKEXCHANGE_PRIVILEGES;

public class TestNgExecutionListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

        if (!iInvokedMethod.isTestMethod()) {
            return;
        }
        try {
            StackExchange stackExchange = StackExchangeConnectorIntegrationTest.class.getMethod(
                    iInvokedMethod.getTestMethod().getMethodName()).getAnnotation(StackExchange.class);

            if (stackExchange == null) {
                return;
            }

            if (stackExchange.needMyAnswer()) {
                String hasA = System.getProperty(STACKEXCHANGE_HAS_ANSWER);
                if (!Boolean.parseBoolean(hasA)) {
                    iTestResult.setStatus(TestResult.SKIP);
                    throw new SkipException("Cannot execute this test due to lack of data hence skipping.");
                }
            }
            if (stackExchange.needMyQuestion()) {
                String hasQ = System.getProperty(STACKEXCHANGE_HAS_QUESTION);
                if (!Boolean.parseBoolean(hasQ)) {
                    iTestResult.setStatus(TestResult.SKIP);
                    throw new SkipException("Cannot execute this test due to lack of data hence skipping.");
                }
            }
            if (!stackExchange.skipPrivilegeCheck()) {
                String privileges = System.getProperty(STACKEXCHANGE_PRIVILEGES);
                if (privileges != null && privileges.toLowerCase().contains(stackExchange.privilege().trim().toLowerCase())) {
                    return;
                }
                iTestResult.setStatus(TestResult.SKIP);
                throw new SkipException(String.format(
                        "Cannot execute this test due to lack of privileges hence skipping. (missing: %s)",
                        stackExchange.privilege()));
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Error while extracting the test method", e);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }
}
