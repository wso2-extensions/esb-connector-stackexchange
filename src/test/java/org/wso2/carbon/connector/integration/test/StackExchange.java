package org.wso2.carbon.connector.integration.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StackExchange {
    String PRIVILEGE_DEFAULT = "create posts";

    boolean skipPrivilegeCheck() default false;

    boolean needMyAnswer() default false;

    boolean needMyQuestion() default false;

    String privilege() default PRIVILEGE_DEFAULT;
}
