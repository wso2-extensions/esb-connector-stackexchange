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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestNgExecutionListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

        if (!iInvokedMethod.isTestMethod()) {
            return;
        }
        String hasAnswerProp = System.getProperty(StackExchangeConnectorIntegrationTest.STACKEXCHANGE_HAS_ANSWER);
        String privilegeProp = System.getProperty(StackExchangeConnectorIntegrationTest.STACKEXCHANGE_PRIVILEGES);
        if (privilegeProp == null || hasAnswerProp == null) {
            return;
        }

        Set<String> privilegeSet = new HashSet<>(Arrays.asList(privilegeProp.split(
                StackExchangeConnectorIntegrationTest.STACKEXCHANGE_PRIVILEGES_SEPARATOR)));
        try {
            StackExchange stackExchange = StackExchangeConnectorIntegrationTest.class.getMethod(
                    iInvokedMethod.getTestMethod().getMethodName()).getAnnotation(StackExchange.class);

            if (stackExchange == null) {
                return;
            }
            if (!Boolean.parseBoolean(hasAnswerProp) && stackExchange.needMyAnswer()) {
                iTestResult.setStatus(TestResult.SKIP);
                throw new SkipException("Cannot process this test due to lack of data hence skipping.");
            }
            if (stackExchange.skipPrivilegeCheck()) {
                return;
            }
            if (!privilegeSet.contains(stackExchange.privilege().trim())) {
                iTestResult.setStatus(TestResult.SKIP);
                throw new SkipException(String.format(
                        "Skip test due to low reputation: missing privilege: '%s'", stackExchange.privilege()));
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }
}
