/*
 * JSON library.
 * Copyright 2012-2013 Antiaction (http://antiaction.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antiaction.common.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.antiaction.common.classmapper.ClassData;
import com.antiaction.common.classmapper.ClassMapperException;
import com.antiaction.common.classmapper.GenericClassMapper;
import com.antiaction.common.json.annotation.JSONConverter;
import com.antiaction.common.json.annotation.JSONName;
import com.antiaction.common.json.annotation.JSONNullValues;
import com.antiaction.common.json.annotation.JSONNullable;
import com.antiaction.common.json.representation.JSONStructureMarshaller;
import com.antiaction.common.json.representation.JSONStructureUnmarshaller;
import com.antiaction.common.json.representation.JSONTextMarshaller;
import com.antiaction.common.json.representation.JSONTextUnmarshaller;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 13/11/2012
 */
public class JSONObjectMappings {

	protected final Class<?>[] zeroArgsParameterTypes = new Class[ 0 ];

	public GenericClassMapper genericClassMapper;

	public final Map<String, JSONObjectMapping> classMappings = new TreeMap<String, JSONObjectMapping>();

	protected final Map<String, Integer> converterNameIdMap = new TreeMap<String, Integer>();

	protected int converterIds = 0;

	public Map<String, Set<String>> overrideIgnoreMapSet = new HashMap<String, Set<String>>();

	public Map<String, Set<String>> forceNullableMapSet = new HashMap<String, Set<String>>();

	protected final JSONTextMarshaller textMarshaller;

	protected final JSONTextUnmarshaller textUnmarshaller;

	protected final JSONStructureMarshaller structureMarshaller;

	protected final JSONStructureUnmarshaller structureUnmarshaller;

	protected final JSONStreamMarshaller streamMarshaller;

	protected final JSONStreamUnmarshaller streamUnmarshaller;

	public JSONObjectMappings() {
		textMarshaller = new JSONTextMarshaller();
		textUnmarshaller = new JSONTextUnmarshaller();
		structureMarshaller = new JSONStructureMarshaller( this );
		structureUnmarshaller = new JSONStructureUnmarshaller( this );
		streamMarshaller = new JSONStreamMarshaller( this );
		streamUnmarshaller = new JSONStreamUnmarshaller( this );
		genericClassMapper = new GenericClassMapper();
	}

	public JSONTextMarshaller getTextMarshaller() {
		return textMarshaller;
	}

	public JSONTextUnmarshaller getTextUnmarshaller() {
		return textUnmarshaller;
	}

	public JSONStructureMarshaller getStructureMarshaller() {
		return structureMarshaller;
	}

	public JSONStructureUnmarshaller getStructureUnmarshaller() {
		return structureUnmarshaller;
	}

	public JSONStreamMarshaller getStreamMarshaller() {
		return streamMarshaller;
	}

	public JSONStreamUnmarshaller getStreamUnmarshaller() {
		return streamUnmarshaller;
	}

	public int getConverterNameId(String converterName) throws JSONException {
		Integer id =  converterNameIdMap.get( converterName );
		if ( id == null ) {
			throw new JSONException( "Unknown conveter name: " + converterName );
		}
		return id;
	}

	public int getConverters() {
		return converterIds;
	}

	/*
	public JSONObjectMapping register(Class<?> clazz) throws JSONException {
		return register(clazz, false);
	}
	*/

	// TODO , boolean bExtended
	public JSONObjectMapping register(Class<?> clazz) throws JSONException {
		/*
		 * Check if we mapped this class definition already.
		 */
		JSONObjectMapping objectMapping = classMappings.get( clazz.getName() );
		if ( objectMapping == null ) {
			/*
			 * Check for class type modifier that we do not accept.
			 */
			int classTypeMask = ClassTypeModifiers.getClassTypeModifiersMask( clazz );
			if ( (classTypeMask & JSONObjectMappingConstants.CLASS_INVALID_TYPE_MODIFIERS_MASK) != 0 ) {
				throw new JSONException( "Unsupported class type." );
			}
			/*
			 * Check for supported class and array type modifiers.
			 */
			classTypeMask &= JSONObjectMappingConstants.CLASS_VALID_TYPE_MODIFIERS_MASK;
			if ( (classTypeMask == JSONObjectMappingConstants.VALID_CLASS) || (classTypeMask == JSONObjectMappingConstants.VALID_MEMBER_CLASS) ) {
				objectMapping = mapClass( clazz, JSONClassAndExtendsData.ZERO_CLAZZ_ARGUMENTS );
			}
			/*
			else if (classTypeMask == JSONObjectMappingConstants.ABSTRACT_CLASS) {
				if (!bExtended) {
					throw new JSONException( "Abstract class can not be registered directly." );
				}
				objectMapping = mapClass( clazz );
			}
			*/
			else if ( classTypeMask == JSONObjectMappingConstants.VALID_ARRAY_CLASS ) {
				objectMapping = mapArray( clazz );
			}
			else  {
				// debug
				//System.out.println(ClassTypeModifiers.toString(classTypeMask));
				throw new JSONException( "Unsupported class type." );
			}
		}
		return objectMapping;
	}

	protected JSONObjectMapping mapArray(Class<?> clazz) throws JSONException {
		int level;
		Class<?> fieldType = null;
		JSONObjectMapping fieldObjectMapping = null;

		JSONObjectMapping objectMapping = JSONObjectMapping.getArrayMapping();
		classMappings.put( clazz.getName(), objectMapping );

		try {
			String arrayTypeName = clazz.getName();
			// debug
			//System.out.println( typeName );
			Integer arrayType = JSONObjectMappingConstants.arrayPrimitiveTypeMappings.get( arrayTypeName );
			// debug
			//System.out.println( arrayType );
			if ( arrayType == null ) {
				level = 0;
				while ( level < arrayTypeName.length() && arrayTypeName.charAt( level ) == '[' ) {
					++level;
				}
				// [L<class>;
				if ( level == 1 && level < arrayTypeName.length() && arrayTypeName.charAt( level ) == 'L' && arrayTypeName.endsWith( ";" ) ) {
					arrayType = JSONObjectMappingConstants.T_OBJECT;
					arrayTypeName = arrayTypeName.substring( level + 1, arrayTypeName.length() - 1 );
					fieldType = Class.forName( arrayTypeName, true, clazz.getClassLoader() );
					// Cache
					fieldObjectMapping = classMappings.get( arrayTypeName );
					if ( fieldObjectMapping == null ) {
						fieldObjectMapping = mapClass( fieldType, JSONClassAndExtendsData.ZERO_CLAZZ_ARGUMENTS );
					}
				}
				else {
					throw new JSONException( "Unsupported array type '" + arrayTypeName + "'." );
				}
			}
			objectMapping.arrayType = arrayType;
			objectMapping.className = arrayTypeName;
			objectMapping.clazz = fieldType;
			objectMapping.objectMapping = fieldObjectMapping;
			// TODO FieldMapping need to work for top level arrays.
			objectMapping.fieldMapping = new JSONObjectFieldMapping();
			// Try to support Collection field type. Caused regression error.
			objectMapping.fieldMapping.type = JSONObjectMappingConstants.T_ARRAY;
			objectMapping.fieldMapping.arrayType = arrayType;
			objectMapping.fieldMapping.className = arrayTypeName;
			objectMapping.fieldMapping.clazz = fieldType;
		}
		catch (ClassNotFoundException e) {
			new JSONException( e );
		}
		return objectMapping;
	}

	protected JSONObjectMapping mapClass(Class<?> clazz, Class<?>[] clazzArguments) throws JSONException {
		JSONObjectFieldMapping json_fm;

		/*
		System.out.println(clazz.getCanonicalName());
		System.out.println(clazz.getName());
		System.out.println(clazz.getSimpleName());
		System.out.println(clazz.getTypeName());
		*/

		/*
		 * Check if we mapped this class already.
		 */

		JSONObjectMapping objectMapping = classMappings.get(clazz.getName());
		if (objectMapping != null) {
			return objectMapping;
		}

		/*
		 * Prepare new ObjectMapping instance.
		 */

		objectMapping = JSONObjectMapping.getObjectMapping();
		classMappings.put( clazz.getName(), objectMapping );
		// Try to set to support List in stream unmarshall.
		objectMapping.className = clazz.getName();
		objectMapping.clazz = clazz;

		/*
		 * Zero argument constructor required.
		 */

		Constructor<?> constructor = null;
		try {
			constructor = clazz.getConstructor( zeroArgsParameterTypes );
		}
		catch (NoSuchMethodException e) {
		}
		if ( constructor == null ) {
			throw new JSONException( clazz.getName() + " does not have a zero argument constructor!" );
		}

		JSONClassAndExtendsData clazzAndExtendsData = new JSONClassAndExtendsData();
		clazzAndExtendsData.ignore = objectMapping.ignore;
		clazzAndExtendsData.nullable = objectMapping.nullable;
		clazzAndExtendsData.nullValues = objectMapping.nullValues;
		clazzAndExtendsData.prepClassAndExtends(this, clazz, clazzArguments, overrideIgnoreMapSet);
		objectMapping.classDataList = clazzAndExtendsData.clazzDataList;

		try {
			ClassData classData = genericClassMapper.mapClass(clazz);
			System.out.println(classData.toString());
			//objectMapping.classDataList = classData.classDataArr;
		}
		catch (ClassMapperException e1) {
			e1.printStackTrace();
		}
	
		// debug
		//System.out.println(JSONClassData.toString(classDataList));

		//Field[] fields = clazz.getDeclaredFields();
		List<JSONFieldData> fieldDataList = clazzAndExtendsData.fieldDataList;
		JSONFieldData fieldData;
		Field field;

		JSONNullable nullable;
		boolean bNullable;
		JSONNullValues nullValues;
		boolean bNullValues;
		JSONConverter converter;
		JSONName jsonName;

		try {
			for ( int i=0; i<fieldDataList.size(); ++i ) {
				fieldData = fieldDataList.get( i );
				field = fieldDataList.get( i ).field;
				// debug
				//System.out.println( field.getName() );
				json_fm = new JSONObjectFieldMapping(fieldData);

				objectMapping.fieldMapping = json_fm;

				bNullable = objectMapping.nullable.contains( json_fm.fieldName );
				if ( !bNullable ) {
					nullable = field.getAnnotation( JSONNullable.class );
					if ( nullable != null ) {
						bNullable = nullable.value();
					}
				}
				Set<String> forceNullableSet = forceNullableMapSet.get( objectMapping.className );
				if ( !bNullable ) {
					if ( forceNullableSet != null && forceNullableSet.contains( field.getName() ) ) {
						bNullable = true;
					}
				}
				if ( bNullable ) {
					if ( json_fm.type < JSONObjectMappingConstants.T_OBJECT ) {
						throw new JSONException( "Primitive types can not be nullable." );
					}
					json_fm.nullable = true;
				}
				bNullValues = objectMapping.nullValues.contains( json_fm.fieldName );
				if ( !bNullValues) {
					nullValues = field.getAnnotation( JSONNullValues.class );
					if ( nullValues != null ) {
						bNullValues = nullValues.value();
					}
				}
				if ( bNullValues ) {
					if ( json_fm.type >= JSONObjectMappingConstants.T_ARRAY && json_fm.arrayType < JSONObjectMappingConstants.T_OBJECT ) {
						throw new JSONException( "Array of primitive type can not have null values." );
					}
					json_fm.nullValues = true;
				}
				converter = field.getAnnotation( JSONConverter.class );
				if ( converter != null ) {
					json_fm.converterName = converter.name();
					Integer converterId = converterNameIdMap.get( json_fm.converterName );
					if ( converterId == null ) {
						converterId = converterIds++;
						converterNameIdMap.put( json_fm.converterName, converterId );
					}
					json_fm.converterId = converterId;
					objectMapping.converters = true;
				}
				jsonName = field.getAnnotation( JSONName.class );
				if ( jsonName != null ) {
					json_fm.jsonName = jsonName.value();
				}
				else {
					json_fm.jsonName = json_fm.fieldName;
				}

				objectMapping.fieldMappingsMap.put( json_fm.jsonName, json_fm );
				objectMapping.fieldMappingsList.add( json_fm );
			}
			objectMapping.fieldMappingsArr = objectMapping.fieldMappingsList.toArray( new JSONObjectFieldMapping[ 0 ] );
		}
		/*
		catch (ClassNotFoundException e) {
			throw new JSONException( e );
		}
		*/
		/*
		catch (NoSuchFieldException e) {
			throw new JSONException( e );
		}
		*/
		catch (SecurityException e) {
			throw new JSONException( e );
		}
		return objectMapping;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, JSONObjectMapping>> iter = classMappings.entrySet().iterator();
		Entry<String, JSONObjectMapping> entry;
		JSONObjectMapping objectMapping;
		while ( iter.hasNext() ) {
			entry = iter.next();
			sb.append( "class: " );
			sb.append( entry.getKey() );
			sb.append( "\n" );
			objectMapping = entry.getValue();
			objectMapping.toString( sb );
		}
		return sb.toString();
	}

}
