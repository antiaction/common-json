package com.antiaction.common.json;

import java.lang.reflect.Field;

import com.antiaction.common.classmapper.Annotations;
import com.antiaction.common.json.annotation.JSON;
import com.antiaction.common.json.annotation.JSONIgnore;
import com.antiaction.common.json.annotation.JSONTypeInstance;

public class JSONAnnotations extends Annotations {

	@Override
	public void processClass(Class<?> clazz) {
		JSON json = clazz.getAnnotation(JSON.class);
		if ( json != null ) {
			String[] ignoreArr = json.ignore();
			for (int i=0; i<ignoreArr.length; ++i) {
				ignoreSet.add(ignoreArr[i]);
			}
			String [] nullableArr = json.nullable();
			for (int i=0; i<nullableArr.length; ++i) {
				nullableSet.add(nullableArr[i]);
			}
			String[] nullValuesArr = json.nullValues();
			for (int i=0; i<nullValuesArr.length; ++i) {
				nullValuesSet.add(nullValuesArr[i]);
			}
		}
	}

	@Override
	public boolean ignore(String className, Field field) {
		String fieldName = field.getName();
		boolean bIgnore = ignoreSet.contains(fieldName);
		JSONIgnore jsonIgnore = field.getAnnotation(JSONIgnore.class);
		if (jsonIgnore != null) {
			bIgnore = true;
		}
		return bIgnore;
	}

	@Override
	public Class<?> instanceClazz(Field field) throws Exception {
		JSONTypeInstance jsonTypeInstance = field.getAnnotation( JSONTypeInstance.class );
		Class<?> instanceClazz;
		if ( jsonTypeInstance != null ) {
			instanceClazz = jsonTypeInstance.value();
			if ( instanceClazz == null ) {
				throw new JSONException( "JSONTypeInstance annotation with null value is not allowed." );
			}
			int typeInstanceMask = ClassTypeModifiers.getClassTypeModifiersMask( instanceClazz );
			if ( (typeInstanceMask & JSONObjectMappingConstants.FIELD_INVALID_TYPE_MODIFIERS_MASK) != 0 ) {
				throw new JSONException( "Unsupported field instance type modifier(s) [" + ClassTypeModifiers.toString( typeInstanceMask ) + "] for class: " + instanceClazz.getName() );
			}
		}
		else {
			instanceClazz =  null;
		}
		return instanceClazz;
	}

}
