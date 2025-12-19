package com.antiaction.common.json.deprecated;

import java.lang.reflect.Field;

import com.antiaction.common.json.JSONObjectMapping;

@Deprecated
public class JSONFieldData {

	public Field field;

	/** Field name. */
	public String fieldName;

	public Class<?> fieldType = null;

	public String fieldTypeName;

	public Integer type;

	/** JSON Java array type identifier. */
	public Integer arrayType;

	public Class<?> fieldTypeInstance;

	public JSONObjectMapping fieldObjectMapping;

	public Integer[] parametrizedObjectTypes; 

	public JSONObjectMapping[] parametrizedObjectMappings;

}
