package com.antiaction.common.classmapper;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

public class FieldData extends TypeData {

	protected FieldDataFactoryAbstract<FieldData> fieldDataFactory;

	public Field field;

	/** Field name. */
	public String fieldName;

	/** InstanceType annotation class type parameter. */
	public Class<?> instanceClazz;

	//public Class<?> fieldType = null;

	//public String fieldTypeName;

	protected FieldData() {
	}

	@Override
	public FieldData cloneObj() {
		FieldData fieldData = new FieldData();
		return copyToObj(fieldData);
	}

	public FieldData copyToObj(FieldData fieldData) {
		fieldData.fieldName = fieldName;
		fieldData.instanceClazz = instanceClazz;
		//fieldData.fieldType = fieldType;
		//fieldData.fieldTypeName = fieldTypeName;
		super.copyToObj(fieldData);
		return fieldData;
	}

	public static FieldData replaceTypeVarNames(FieldData oldFieldData, int[] typeVarNameIds, TypeData[] typeVarParamTypes) {
		TypeData[] oldParameterTypes;
		TypeData[] newParameterTypes;
		FieldData newFieldData;
		if (oldFieldData.bTypeVariable) {
			newFieldData = oldFieldData.cloneObj();
			throw new IllegalStateException("WIP(topTypeData.bTypeVariable)");
		}
		// topTypeData.bParameterizedType
		oldParameterTypes = oldFieldData.parameterTypes;
		if (oldParameterTypes.length > 0) {
			newFieldData = oldFieldData.cloneObj();
			newParameterTypes = new TypeData[oldParameterTypes.length];
			newFieldData.parameterTypes = newParameterTypes;
			replaceTypeVarNames(oldParameterTypes, newParameterTypes, typeVarNameIds, typeVarParamTypes);
			return newFieldData;
		}
		else {
			return oldFieldData;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Field: ");
		sb.append(fieldName);
		sb.append(" Type: ");
		typeToString(sb);
		return sb.toString();
	}

	public static String toString(FieldData[] fieldDataArr) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<fieldDataArr.length; ++i) {
			toString(i, fieldDataArr[i], sb);
		}
		return sb.toString();
	}

	public static String toString(List<FieldData> fieldDataList) {
		StringBuilder sb = new StringBuilder();
		int idx = 0;
		Iterator<FieldData> iterator = fieldDataList.iterator();
		while (iterator.hasNext()) {
			toString(idx++, iterator.next(), sb);
		}
		return sb.toString();
	}

	public static void toString(int idx, FieldData fieldData, StringBuilder sb) {
		sb.append("  [");
		sb.append(idx + 1);
		sb.append("]: ");
		sb.append("fieldName: ");
		sb.append(fieldData.fieldName);
		sb.append("\n");

		sb.append("  [");
		sb.append(idx + 1);
		sb.append("]: ");
		sb.append("     Type: ");
		fieldData.typeToString(sb);
		if (fieldData.bUnresolved) {
			sb.append("  (*unresolved*)");
		}
		sb.append("\n");
	}

}
