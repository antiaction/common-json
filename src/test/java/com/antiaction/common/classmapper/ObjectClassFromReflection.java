package com.antiaction.common.classmapper;

public class ObjectClassFromReflection<T> {

	private T obj;

	public ObjectClassFromReflection(T obj) {
		this.obj = obj;
	}

	public Class<?> getClazz() {
		return this.obj.getClass();
	}

}
