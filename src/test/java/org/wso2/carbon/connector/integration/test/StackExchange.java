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