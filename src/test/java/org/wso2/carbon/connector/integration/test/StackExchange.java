package org.wso2.carbon.connector.integration.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StackExchange {
    boolean self() default false;
    int minReputation() default 1;
    String privilegeWording() default "create posts";
}
