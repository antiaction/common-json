package com.antiaction.common.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONClassData {

	/** Class the actual data is gathered from. */
	public Class<?> clazz;

	/** Field names to ignore when mapping objects. */
	public List<String> ignore;

	/** Field names which can be null. */
	public List<String> nullable;

	/** Field names which can have null values. */
	public List<String> nullValues;

	/** Variables this class has. */
	public TypeVariable<?>[] typeParameters;

	/** Extends another class or null. */
	public Type superType;

	/** Extends another class with parameters. */
	public ParameterizedType parameterizedType;

	/** Types or parameters in extends. */
	public Type[] argumentTypes;

	public Class<?>[] clazzArguments;

	/** Extends a simple class. */
	public Class<?> superClazz;

	public List<JSONFieldData> fieldDataList;

	/**
	 * Construct new object.
	 */
	protected JSONClassData() {
		ignore = new ArrayList<>(16);
		nullable = new ArrayList<>(16);
		nullValues = new ArrayList<>(16);
		fieldDataList = new ArrayList<>(32);
	}

	public static String toString(List<JSONClassData> classDataList) {
		StringBuilder sb = new StringBuilder();
		toString(classDataList, sb);
		return sb.toString();
	}

	public static void toString(List<JSONClassData> classDataList, StringBuilder sb) {
		JSONClassData classData;
		if (classDataList != null) {
			Iterator<JSONClassData> iter = classDataList.iterator();
			int idx = 0;
			while (iter.hasNext()) {
				classData = iter.next();
				++idx;
				sb.append("[");
				sb.append(idx);
				sb.append("]: ");
				sb.append(classData.clazz.getClass().getTypeName());
				sb.append("\n");
				sb.append("[");
				sb.append(idx);
				sb.append("]: ");
				sb.append(classData.clazz.getTypeName());
				sb.append("\n");
				sb.append("  typeVariables: ");
				sb.append(classData.typeParameters.length);
				sb.append("\n");
				if (classData.typeParameters.length != 0) {
					for (int i=0; i<classData.typeParameters.length; ++i) {
						sb.append("    [");
						sb.append(i);
						sb.append("]: ");
						sb.append(classData.typeParameters[i].getName());
						sb.append("\n");
					}
				}
				sb.append("  clazzArguments: ");
				sb.append(classData.clazzArguments.length);
				sb.append("\n");
				if (classData.clazzArguments.length > 0) {
					for (int i=0; i<classData.clazzArguments.length; ++i) {
						sb.append("    [");
						sb.append(i);
						sb.append("]: ");
						// .getName()
						sb.append(classData.clazzArguments[i]);
						sb.append("\n");
					}
				}
				if (classData.argumentTypes != null) {
					// debug
					sb.append("  argumentTypes: ");
					sb.append(classData.argumentTypes.length);
					sb.append("\n");
					if (classData.argumentTypes.length > 0) {
						for (int i=0; i<classData.argumentTypes.length; ++i) {
							sb.append("    [");
							sb.append(i);
							sb.append("]: ");
							sb.append(classData.argumentTypes[i].getClass().getTypeName());
							sb.append("\n");
							sb.append("    [");
							sb.append(i);
							sb.append("]: ");
							sb.append(classData.argumentTypes[i]);
							sb.append("\n");
						}
					}
				}
				if (classData.superClazz != null) {
					sb.append("  superClass: ");
					sb.append(classData.superClazz.getClass().getTypeName());
					sb.append("\n");
					sb.append("  superClass: ");
					sb.append(classData.superClazz.getTypeName());
					sb.append("\n");
				}
			}
		}
	}

	/*
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}
	*/

}
