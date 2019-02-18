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

    private static final int LOWEST_POSSIBLE_REPUTATION = 1;

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

        if (!iInvokedMethod.isTestMethod()) {
            return;
        }
        String property = System.getProperty(StackExchangeConnectorIntegrationTest.STACKEXCHANGE_PRIVILEGES);
        if (property == null) {
            return;
        }
        Set<String> privilegeSet = new HashSet<>(Arrays.asList(property.split(
                StackExchangeConnectorIntegrationTest.STACKEXCHANGE_PRIVILEGES_SEPARATOR)));
        try {
            StackExchange stackExchange = StackExchangeConnectorIntegrationTest.class.getMethod(
                    iInvokedMethod.getTestMethod().getMethodName()).getAnnotation(StackExchange.class);
            if (stackExchange == null || stackExchange.skipPrivilegeCheck()) {
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
