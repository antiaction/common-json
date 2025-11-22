package com.antiaction.common.classmapper;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.TreeSet;

public class Annotations {

	public Set<String> ignoreSet = new TreeSet<>();

	public Set<String> nullableSet = new TreeSet<>();

	public Set<String> nullValuesSet = new TreeSet<>();

	public void processClass(Class<?> cls) {
	}

	public boolean ignore(String className, Field field) {
		return false;
	}

	public Class<?> instanceClazz(Field field) throws Exception {
		return null;
	}

}
