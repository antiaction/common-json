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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A JSON object mapping description.
 *
 * @author Nicholas
 * Created on 22/11/2012
 */
public class JSONObjectMapping {

	/** Object mapping type. */
	public static final int OMT_OBJECT = 1;
	/** Array mapping type. */
	public static final int OMT_ARRAY = 2;

	/** Mapping type, object or array. */
	public int type;

	/*
	 * Object.
	 */

	/** Class and parent classes used for this object mapping. */
	public List<JSONClassData> classDataList;

	/** Field names to ignore when mapping objects. */
	public Set<String> ignore;

	/** Field names which can be null. */
	public Set<String> nullable;

	/** Field names which can have null values. */
	public Set<String> nullValues;

	/** Map of mapped fields. */
	public Map<String, JSONObjectFieldMapping> fieldMappingsMap;

	/** List of mapped fields. */
	public List<JSONObjectFieldMapping> fieldMappingsList;

	/** Array of mapped fields. */
	public JSONObjectFieldMapping[] fieldMappingsArr;

	/** Boolean indicating if one or more field mapping(s) requires a converter. */
	public boolean converters;

	/*
	 * Array.
	 */

	/** JSON Java array type identifier. */
	public int arrayType;

	/** Field class type name. */
	public String className;

	/** Field class. */
	public Class<?> clazz;

	/** Field object mapping. */
	public JSONObjectFieldMapping fieldMapping;

	/** Object mapping, if array is an object type. */
	public JSONObjectMapping objectMapping;

	private JSONObjectMapping() {
	}

	public static JSONObjectMapping getObjectMapping() {
		JSONObjectMapping om = new JSONObjectMapping();
		om.type = OMT_OBJECT;
		om.ignore = new TreeSet<String>();
		om.nullable = new TreeSet<String>();
		om.nullValues = new TreeSet<String>();
		om.fieldMappingsMap = new TreeMap<String, JSONObjectFieldMapping>();
		om.fieldMappingsList = new LinkedList<JSONObjectFieldMapping>();
		return om;
	}

	public static JSONObjectMapping getArrayMapping() {
		JSONObjectMapping om = new JSONObjectMapping();
		om.type = OMT_ARRAY;
		return om;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString( sb );
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		switch ( type ) {
		case OMT_OBJECT:
			sb.append( "  type: Object(" );
			sb.append( type );
			sb.append( ")\n" );
			break;
		case OMT_ARRAY:
			sb.append( "  type: Array(" );
			sb.append( type );
			sb.append( ")\n" );
			sb.append( "  arrayType: " );
			sb.append( JSONObjectMappingConstants.typeString( arrayType ) );
			sb.append( "(" );
			sb.append( arrayType );
			sb.append( ")\n" );
			break;
		}
		sb.append( "  className: " );
		sb.append( className );
		sb.append( "\n" );
		sb.append( "  clazz: " );
		sb.append( clazz );
		sb.append( "\n" );

		sb.append( "  ignore: " );
		toString( ignore, sb );
		sb.append( "\n" );
		sb.append( "  nullableSet: " );
		toString( nullable, sb );
		sb.append( "\n" );
		sb.append( "  nullValuesSet: " );
		toString( nullValues, sb );
		sb.append( "\n" );
		sb.append( "  fieldMapping: " );
		if ( fieldMapping != null ) {
			sb.append( "\n" );
			fieldMapping.toString( sb );
		}
		else {
			sb.append( "null" );
			sb.append( "\n" );
		}
		sb.append( "  objectMapping: " );
		if ( objectMapping != null ) {
			sb.append( objectMapping.className );
		}
		else {
			sb.append( "null" );
		}
		sb.append( "\n" );
		sb.append( "  fieldMappingsArr[]: " );
		if ( fieldMappingsArr != null ) {
			sb.append( "\n" );
			for ( int i=0; i<fieldMappingsArr.length; ++i ) {
				sb.append( "  [" );
				sb.append( i );
				sb.append( "]" );
				sb.append( "\n" );
				fieldMappingsArr[ i ].toString( sb );
			}
		}
		else {
			sb.append( "null" );
			sb.append( "\n" );
		}
	}

	public static void toString(Set<String> set, StringBuilder sb) {
		if ( set != null ) {
			sb.append( "[" );
			Iterator<String> iter = set.iterator();
			String str = null;
			while ( iter.hasNext() ) {
				if ( str != null ) {
					sb.append( ", " );
				}
				str = iter.next();
				sb.append( str );
			}
			sb.append( "]" );
		}
		else {
			sb.append( "null" );
		}
	}

	public void classDataStringAppend(StringBuilder sb) {
		JSONClassData.toString(classDataList, sb);
	}

}
