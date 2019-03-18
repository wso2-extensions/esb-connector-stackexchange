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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StackExchange {

    /**
     * Store lowest privilege as the default privilege.
     */
    String PRIVILEGE_DEFAULT = "create posts";

    /**
     * @return whether the privileges checking should be skipped.
     */
    boolean skipPrivilegeCheck() default false;

    /**
     * @return whether the specific method needs an answer id created by user.
     */
    boolean needMyAnswer() default false;

    /**
     * @return whether the specific method needs an question id created by user.
     */
    boolean needMyQuestion() default false;

    /**
     * @return whether the specific method needs an question id with at least one answer created by user.
     */
    boolean needMyQuestionWithAnswers() default false;
    /**
     * @return whether the specific method needs an unaccepted answer id created by user.
     */
    boolean needUnacceptedAnswer() default false;

    /**
     * @return the privilege need to execute the test method successfully.
     */
    String privilege() default PRIVILEGE_DEFAULT;
}
