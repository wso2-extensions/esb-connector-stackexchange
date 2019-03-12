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

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Here we are defining set of helper utils for convenience.
 */
public class CommonTestUtil {

    /**
     * Return the name of the connector.
     *
     * @return the name of the connector.
     */
    public static String getConnectorName() {

        return System.getProperty("connector_name") +
                "-connector-" +
                System.getProperty("connector_version") +
                ".zip";
    }

    /**
     * Return the filename for a given method.
     *
     * @param method the method name.
     * @param type   the type of the test.
     * @param suffix the extra latter part of the filename.
     * @return the filename.
     */
    public static String getFilenameOfPayload(String method, TestType type, String suffix) {

        if (StringUtils.isEmpty(suffix)) {
            return String.format("%s_%s.json", method, type.value);
        }
        return String.format("%s_%s_%s.json", method, type.value, suffix);
    }

    /**
     * Test types.
     */
    public enum TestType {
        MANDATORY("mandatory"), OPTIONAL("optional"), INVALID("invalid"), PAGING("paging");

        private final String value;

        TestType(String value) {

            this.value = value;
        }
    }
}