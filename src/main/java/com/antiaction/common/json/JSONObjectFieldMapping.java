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

import java.lang.reflect.Field;

/**
 * A JSON objects field mapping description.
 *
 * @author Nicholas
 * Created on 23/11/2012
 */
public class JSONObjectFieldMapping {

	/** Field name. */
	public String fieldName;

	/** JSON name. */
	public String jsonName;

	/** JSON Java type identifier. */
	public int type;

	/** JSON Java array type identifier. */
	public int arrayType;

	/** Field class type name. */
	public String className;

	/** Field class. */
	public Class<?> clazz;

	/** JSONInstanceType annotation class type parameter. */
	public Class<?> instanceClazz;

	/** Field object mapping, if object type. */
	public JSONObjectMapping objectMapping;

	/** Type of generic class parameters. */
	public Integer[] parametrizedObjectTypes;

	/** Parameterized class object mappings for generic class parameters. */
	public JSONObjectMapping[] parametrizedObjectMappings;

	/** Reflection field. */
	public Field field;

	/** Is field nullable. */
	public boolean nullable;

	/** Allow null values in array. */
	public boolean nullValues;

	/** Name of desired field data converter. */
	public String converterName;

	/** Id of desired field data converter. */
	public int converterId = -1;

	public JSONObjectFieldMapping() {
	}

	public JSONObjectFieldMapping(JSONFieldData fieldData) {
		field = fieldData.field;
		field.setAccessible( true );
		fieldName = fieldData.fieldName;
		type = fieldData.type;
		arrayType = fieldData.arrayType;
		className = fieldData.fieldTypeName;
		clazz = fieldData.fieldType;
		instanceClazz = fieldData.fieldTypeInstance;
		objectMapping = fieldData.fieldObjectMapping;
		parametrizedObjectTypes = fieldData.parametrizedObjectTypes;
		parametrizedObjectMappings = fieldData.parametrizedObjectMappings;
		if ( instanceClazz == null ) {
			instanceClazz = clazz;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString( sb );
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		sb.append( "    fieldName/jsonName: " );
		sb.append( fieldName );
		sb.append( "/" );
		sb.append( jsonName );
		sb.append( " - nullable: " );
		sb.append( nullable );
		sb.append( " - nullValues: " );
		sb.append( nullValues );
		sb.append( " - converterName: " );
		sb.append( converterName );
		sb.append( " - converterId: " );
		sb.append( converterId );
		sb.append( "\n" );
		sb.append( "    type: " );
		sb.append( JSONObjectMappingConstants.typeString( type ) );
		sb.append( "(" );
		sb.append( type );
		sb.append( ")\n" );
		sb.append( "    arrayType: " );
		sb.append( JSONObjectMappingConstants.typeString( arrayType ) );
		sb.append( "(" );
		sb.append( arrayType );
		sb.append( ")\n" );
		sb.append( "    className: " );
		sb.append( className );
		sb.append( "\n" );
		sb.append( "    clazz: " );
		sb.append( clazz );
		sb.append( "\n" );
		sb.append( "    objectMapping: " );
		if ( objectMapping != null ) {
			sb.append( objectMapping.className );
		}
		else {
			sb.append( "null" );
		}
		sb.append( "\n" );
		sb.append( "    parametrizedObjectTypes[]: " );
		if ( parametrizedObjectTypes != null ) {
			sb.append( "<" );
			for ( int i=0; i<parametrizedObjectTypes.length; ++i ) {
				if ( i > 0 ) {
					sb.append( ", " );
				}
				sb.append( JSONObjectMappingConstants.typeString( parametrizedObjectTypes[ i ] ) );
				sb.append( "(" );
				sb.append( parametrizedObjectTypes[ i ] );
				sb.append( ")" );
			}
			sb.append( ">" );
		}
		else {
			sb.append( "null" );
		}
		sb.append( "\n" );
		sb.append( "    parametrizedObjectMappings[]: " );
		if ( parametrizedObjectMappings != null ) {
			sb.append( "<" );
			for ( int i=0; i<parametrizedObjectMappings.length; ++i ) {
				if ( i > 0 ) {
					sb.append( ", " );
				}
				JSONObjectMapping objectMapping = parametrizedObjectMappings[ i ];
				if ( objectMapping != null ) {
					sb.append( objectMapping.className );
				}
				else {
					sb.append( "null" );
				}
			}
			sb.append( ">" );
		}
		else {
			sb.append( "null" );
		}
		sb.append( "\n" );
	}

	/*
	public void toString(StringBuilder sb) {
		sb.append( "    fieldName: " );
		sb.append( fieldName );
		sb.append( "\n" );
		sb.append( "    jsonName: " );
		sb.append( jsonName );
		sb.append( "\n" );
		sb.append( "    type: " );
		sb.append( JSONObjectMappingConstants.typeString( type ) );
		sb.append( "(" );
		sb.append( type );
		sb.append( ")\n" );
		sb.append( "    arrayType: " );
		sb.append( JSONObjectMappingConstants.typeString( arrayType ) );
		sb.append( "(" );
		sb.append( arrayType );
		sb.append( ")\n" );
		sb.append( "    className: " );
		sb.append( className );
		sb.append( "\n" );
		sb.append( "    clazz: " );
		sb.append( clazz );
		sb.append( "\n" );
		sb.append( "    objectMapping: " );
		if ( objectMapping != null ) {
			sb.append( objectMapping.className );
		}
		else {
			sb.append( "null" );
		}
		sb.append( "\n" );
		sb.append( "    parametrizedObjectTypes[]: " );
		if ( parametrizedObjectTypes != null ) {
			sb.append( "<" );
			for ( int i=0; i<parametrizedObjectTypes.length; ++i ) {
				if ( i > 0 ) {
					sb.append( ", " );
				}
				sb.append( JSONObjectMappingConstants.typeString( parametrizedObjectTypes[ i ] ) );
				sb.append( "(" );
				sb.append( parametrizedObjectTypes[ i ] );
				sb.append( ")" );
			}
			sb.append( ">" );
		}
		else {
			sb.append( "null" );
		}
		sb.append( "\n" );
		sb.append( "    parametrizedObjectMappings[]: " );
		if ( parametrizedObjectMappings != null ) {
			sb.append( "<" );
			for ( int i=0; i<parametrizedObjectMappings.length; ++i ) {
				if ( i > 0 ) {
					sb.append( ", " );
				}
				JSONObjectMapping objectMapping = parametrizedObjectMappings[ i ];
				if ( objectMapping != null ) {
					sb.append( objectMapping.className );
				}
				else {
					sb.append( "null" );
				}
			}
			sb.append( ">" );
		}
		else {
			sb.append( "null" );
		}
		sb.append( "\n" );
		sb.append( "    nullable: " );
		sb.append( nullable );
		sb.append( "\n" );
		sb.append( "    nullValues: " );
		sb.append( nullValues );
		sb.append( "\n" );
		sb.append( "    converterName: " );
		sb.append( converterName );
		sb.append( "\n" );
		sb.append( "    converterId: " );
		sb.append( converterId );
		sb.append( "\n" );
	}
	*/

}
