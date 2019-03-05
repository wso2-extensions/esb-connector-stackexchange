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
     * Return whether the privileges checking should be skipped.
     * @return whether the privileges checking should be skipped.
     */
    boolean skipPrivilegeCheck() default false;

    /**
     * Return whether the specific method needs an answer id created by user.
     * @return whether the specific method needs an answer id created by user.
     */
    boolean needMyAnswer() default false;

    /**
     * Return whether the specific method needs an question id created by user.
     * @return whether the specific method needs an question id created by user.
     */
    boolean needMyQuestion() default false;

    /**
     * Return the privilege need to execute the test method successfully.
     * @return the privilege need to execute the test method successfully.
     */
    String privilege() default PRIVILEGE_DEFAULT;
}
