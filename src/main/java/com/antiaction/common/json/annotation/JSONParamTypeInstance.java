package com.antiaction.common.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(JSONParamTypeInstances.class)
public @interface JSONParamTypeInstance {

	int index() default 0;

	Class<?> clazz();

}
