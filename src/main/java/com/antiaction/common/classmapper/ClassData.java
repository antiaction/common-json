package com.antiaction.common.classmapper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import java.util.List;

public class ClassData {

	/** Class the actual data is gathered from. */
	public Class<?> clazz;

	/** Class level annotations. */
	public Annotations annotations;

	/** Class modifiers. */
	public int modifiers;

	/** Generic variable(s) of this class, if any. */
	public TypeVariable<?>[] typeParameters;

	public int[] typeVarNameIds;

	//public Class<?>[] clazzArguments;

	/** Extends another class or null. */
	public Type superType;

	public TypeData superTypeData;

	/** Extends another class with parameters. */
	//public ParameterizedType parameterizedType;

	/** Types or parameters in extends. */
	//public Type[] argumentTypes;

	/** Extends a simple class. */
	//public Class<?> superClazz;

	/** Array of <code>ClassData</code> objects for the a class and all its extended classes. */
	public ClassData[] classDataArr;

	public Field[] declaredFields;

	public FieldData[] fieldsClass;

	public int unresolvedFields;

	public FieldData[] fieldsInherited;

	public ClassData cloneObj() {
		ClassData classData = new ClassData();
		return copyToObj(classData);
	}

	public ClassData copyToObj(ClassData classData) {
		classData.clazz = clazz;
		classData.annotations = annotations;
		classData.modifiers = modifiers;
		classData.typeParameters = typeParameters;
		classData.typeVarNameIds = typeVarNameIds;
		classData.superType = superType;
		classData.superTypeData = superTypeData;
		classData.classDataArr = classDataArr;
		classData.declaredFields = declaredFields;
		classData.fieldsClass = fieldsClass;
		classData.unresolvedFields = unresolvedFields;
		classData.fieldsInherited = fieldsInherited;
		return classData;
	}

	public String toString() {
		return toString(classDataArr);
	}

	public static String toString(ClassData[] classDataArr) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<classDataArr.length; ++i) {
			toString(i + 1, classDataArr[i], sb);
		}
		return sb.toString();
	}

	public static String toString(List<ClassData> classDataList) {
		StringBuilder sb = new StringBuilder();
		int idx = 1;
		Iterator<ClassData> iterator = classDataList.iterator();
		while (iterator.hasNext()) {
			toString(idx, iterator.next(), sb);
			++idx;
		}
		return sb.toString();
	}

	public static void toString(int idx, ClassData classData, StringBuilder sb) {
		/*
		sb.append("[");
		sb.append(idx);
		sb.append("]: ");
		sb.append(classData.clazz.getClass().getTypeName());
		sb.append("\n");
		*/
		sb.append("[");
		sb.append(idx);
		sb.append("]: ");
		sb.append(classData.clazz.getTypeName());
		if (classData.typeParameters.length > 0) {
			sb.append("<");
			for (int i=0; i<classData.typeParameters.length; ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(classData.typeParameters[i].getName());
			}
			sb.append(">");
		}
		sb.append(" extends ");
		classData.superTypeData.typeToString(sb);
		sb.append("\n");

		sb.append("  typeVariables: ");
		sb.append(classData.typeParameters.length);
		sb.append("\n");

		if (classData.typeParameters.length != 0) {
			for (int i=0; i<classData.typeParameters.length; ++i) {
				sb.append("    [");
				sb.append(i + 1);
				sb.append("]: ");
				sb.append(classData.typeParameters[i].getName());
				sb.append("\n");
			}
		}
		/*
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
		*/
		/*
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
		*/

		sb.append("  fields(class): ");
		sb.append(classData.fieldsClass.length);
		sb.append(" of ");
		sb.append(classData.declaredFields.length);
		sb.append("\n");

		for (int i=0; i<classData.fieldsClass.length; ++i) {
			FieldData.toString(i, classData.fieldsClass[i], sb);
		}

		sb.append("  fields(inherited): ");
		sb.append(classData.fieldsInherited.length);
		sb.append("\n");

		for (int i=0; i<classData.fieldsInherited.length; ++i) {
			FieldData.toString(i, classData.fieldsInherited[i], sb);
		}
	}

	public String toGenericString() {
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getTypeName());
		if (typeParameters != null && typeParameters.length > 0) {
			sb.append("<");
			for (int i=0; i<typeParameters.length; ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(typeParameters[i].getName());
			}
			sb.append(">");
		}
		sb.append(" extends ");
		superTypeData.typeToString(sb);
		return sb.toString();
	}

}
