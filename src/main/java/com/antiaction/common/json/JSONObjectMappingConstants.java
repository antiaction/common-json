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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 24/07/2013
 */
public class JSONObjectMappingConstants {

	/**
	 * Prohibit external construction.
	 */
	protected JSONObjectMappingConstants() {
	}

	/*
	 * Type.
	 */

	public static final int T_PRIMITIVE_BOOLEAN = 1;
	public static final int T_PRIMITIVE_CHAR = 2;
	public static final int T_PRIMITIVE_BYTE = 3;
	public static final int T_PRIMITIVE_SHORT = 4;
	public static final int T_PRIMITIVE_INTEGER = 5;
	public static final int T_PRIMITIVE_LONG = 6;
	public static final int T_PRIMITIVE_FLOAT = 7;
	public static final int T_PRIMITIVE_DOUBLE = 8;
	public static final int T_OBJECT = 100;
	public static final int T_BOOLEAN = 101;
	public static final int T_CHARACTER = 102;
	public static final int T_BYTE = 103;
	public static final int T_SHORT = 104;
	public static final int T_INTEGER = 105;
	public static final int T_LONG = 106;
	public static final int T_FLOAT = 107;
	public static final int T_DOUBLE = 108;
	public static final int T_DATE = 109;
	public static final int T_TIMESTAMP = 110;
	public static final int T_BIGINTEGER = 111;
	public static final int T_BIGDECIMAL = 112;
	public static final int T_STRING = 113;
	public static final int T_BYTEARRAY = 114;
	public static final int T_ARRAY = 200;
	public static final int T_LIST = 201;
	public static final int T_MAP = 202;
	public static final int T_SET = 203;

	public static Map<String, Integer> primitiveTypeMappings = new TreeMap<String, Integer>();

	public static Map<String, Integer> arrayPrimitiveTypeMappings = new TreeMap<String, Integer>();

	static {
		primitiveTypeMappings.put( boolean.class.getName(), T_PRIMITIVE_BOOLEAN );
		primitiveTypeMappings.put( char.class.getName(), T_PRIMITIVE_CHAR );
		primitiveTypeMappings.put( byte.class.getName(), T_PRIMITIVE_BYTE );
		primitiveTypeMappings.put( short.class.getName(), T_PRIMITIVE_SHORT );
		primitiveTypeMappings.put( int.class.getName(), T_PRIMITIVE_INTEGER );
		primitiveTypeMappings.put( long.class.getName(), T_PRIMITIVE_LONG );
		primitiveTypeMappings.put( float.class.getName(), T_PRIMITIVE_FLOAT );
		primitiveTypeMappings.put( double.class.getName(), T_PRIMITIVE_DOUBLE );
		primitiveTypeMappings.put( Boolean.class.getName(), T_BOOLEAN );
		primitiveTypeMappings.put( Character.class.getName(), T_CHARACTER );
		primitiveTypeMappings.put( Byte.class.getName(), T_BYTE );
		primitiveTypeMappings.put( Short.class.getName(), T_SHORT );
		primitiveTypeMappings.put( Integer.class.getName(), T_INTEGER );
		primitiveTypeMappings.put( Long.class.getName(), T_LONG );
		primitiveTypeMappings.put( Float.class.getName(), T_FLOAT );
		primitiveTypeMappings.put( Double.class.getName(), T_DOUBLE );
		primitiveTypeMappings.put( Date.class.getName(), T_DATE );
		primitiveTypeMappings.put( Timestamp.class.getName(), T_TIMESTAMP );
		primitiveTypeMappings.put( BigInteger.class.getName(), T_BIGINTEGER );
		primitiveTypeMappings.put( BigDecimal.class.getName(), T_BIGDECIMAL );
		primitiveTypeMappings.put( String.class.getName(), T_STRING );
		primitiveTypeMappings.put( byte[].class.getName(), T_BYTEARRAY );

		arrayPrimitiveTypeMappings.put( boolean[].class.getName(), T_PRIMITIVE_BOOLEAN );
		//arrayPrimitiveTypeMappings.put( char[].class.getName(), T_PRIMITIVE_CHAR );
		//arrayPrimitiveTypeMappings.put( byte[].class.getName(), T_PRIMITIVE_BYTE );
		arrayPrimitiveTypeMappings.put( int[].class.getName(), T_PRIMITIVE_INTEGER );
		arrayPrimitiveTypeMappings.put( long[].class.getName(), T_PRIMITIVE_LONG );
		arrayPrimitiveTypeMappings.put( float[].class.getName(), T_PRIMITIVE_FLOAT );
		arrayPrimitiveTypeMappings.put( double[].class.getName(), T_PRIMITIVE_DOUBLE );
		arrayPrimitiveTypeMappings.put( Boolean[].class.getName(), T_BOOLEAN );
		//arrayPrimitiveTypeMappings.put( Character[].class.getName(), T_CHARACTER );
		//arrayPrimitiveTypeMappings.put( Byte[].class.getName(), T_BYTE );
		arrayPrimitiveTypeMappings.put( Integer[].class.getName(), T_INTEGER );
		arrayPrimitiveTypeMappings.put( Long[].class.getName(), T_LONG );
		arrayPrimitiveTypeMappings.put( Float[].class.getName(), T_FLOAT );
		arrayPrimitiveTypeMappings.put( Double[].class.getName(), T_DOUBLE );
		arrayPrimitiveTypeMappings.put( Date[].class.getName(), T_DATE );
		arrayPrimitiveTypeMappings.put( Timestamp[].class.getName(), T_TIMESTAMP );
		arrayPrimitiveTypeMappings.put( BigInteger[].class.getName(), T_BIGINTEGER );
		arrayPrimitiveTypeMappings.put( BigDecimal[].class.getName(), T_BIGDECIMAL );
		arrayPrimitiveTypeMappings.put( String[].class.getName(), T_STRING );
	}

	public static String primitivTypesToString() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = primitiveTypeMappings.keySet().iterator();
		while (iter.hasNext()) {
			sb.append(iter.next());
			sb.append("\n");
		}
		return sb.toString();
	}

	public static String arrayPrimitiveTypesToString() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = arrayPrimitiveTypeMappings.keySet().iterator();
		while (iter.hasNext()) {
			sb.append(iter.next());
			sb.append("\n");
		}
		return sb.toString();
	}

	/*
	 * Class.
	 */

	public static final int CLASS_INVALID_TYPE_MODIFIERS_MASK = ClassTypeModifiers.CT_ANNOTATION
			| ClassTypeModifiers.CT_ANONYMOUSCLASS
			| ClassTypeModifiers.CT_ENUM
			| ClassTypeModifiers.CT_INTERFACE
			| ClassTypeModifiers.CT_LOCALCLASS
			| ClassTypeModifiers.CT_PRIMITIVE
			| ClassTypeModifiers.CM_NATIVE;

	public static final int CLASS_VALID_TYPE_MODIFIERS_MASK = ClassTypeModifiers.CT_ARRAY
			| ClassTypeModifiers.CT_MEMBERCLASS
			| ClassTypeModifiers.CT_CLASS
			| ClassTypeModifiers.CM_ABSTRACT
			| ClassTypeModifiers.CM_FINAL
			| ClassTypeModifiers.CM_STATIC;

	public static final int VALID_CLASS = ClassTypeModifiers.CT_CLASS;

	public static final int VALID_MEMBER_CLASS = ClassTypeModifiers.CT_MEMBERCLASS | ClassTypeModifiers.CM_STATIC;

	public static final int ABSTRACT_CLASS = ClassTypeModifiers.CT_CLASS | ClassTypeModifiers.CM_ABSTRACT;

	public static final int VALID_ARRAY_CLASS = ClassTypeModifiers.CT_ARRAY | ClassTypeModifiers.CM_ABSTRACT | ClassTypeModifiers.CM_FINAL;

	/*
	 * Field.
	 */

	public static final int FIELD_IGNORE_TYPE_MODIFIER = ClassTypeModifiers.CM_STATIC
			| ClassTypeModifiers.CM_TRANSIENT;

	// TODO support List/Set interface but required impl class annotation.
	// TODO byte array mapping
	public static final int FIELD_INVALID_TYPE_MODIFIERS_MASK = ClassTypeModifiers.CT_ANNOTATION
			| ClassTypeModifiers.CT_ANONYMOUSCLASS
			| ClassTypeModifiers.CT_ENUM
			| ClassTypeModifiers.CT_INTERFACE
			| ClassTypeModifiers.CT_LOCALCLASS
			| ClassTypeModifiers.CT_PRIMITIVE;

	public static final int FIELD_VALID_TYPE_MODIFIERS_MASK = ClassTypeModifiers.CT_ARRAY
			| ClassTypeModifiers.CT_MEMBERCLASS
			| ClassTypeModifiers.CT_CLASS
			| ClassTypeModifiers.CM_ABSTRACT
			| ClassTypeModifiers.CM_FINAL
			| ClassTypeModifiers.CM_STATIC;

	public static String typeString(Integer type)  {
		if ( type != null ) {
			switch ( type ) {
			case T_PRIMITIVE_BOOLEAN:
				return "T_PRIMITIVE_BOOLEAN";
			case T_PRIMITIVE_BYTE:
				return "T_PRIMITIVE_BYTE";
			case T_PRIMITIVE_CHAR:
				return "T_PRIMITIVE_CHAR";
			case T_PRIMITIVE_INTEGER:
				return "T_PRIMITIVE_INTEGER";
			case T_PRIMITIVE_LONG:
				return "T_PRIMITIVE_LONG";
			case T_PRIMITIVE_FLOAT:
				return "T_PRIMITIVE_FLOAT";
			case T_PRIMITIVE_DOUBLE:
				return "T_PRIMITIVE_DOUBLE";
			case T_OBJECT:
				return "T_OBJECT";
			case T_BOOLEAN:
				return "T_BOOLEAN";
			case T_BYTE:
				return "T_BYTE";
			case T_CHARACTER:
				return "T_CHARACTER";
			case T_INTEGER:
				return "T_INTEGER";
			case T_LONG:
				return "T_LONG";
			case T_FLOAT:
				return "T_FLOAT";
			case T_DOUBLE:
				return "T_DOUBLE";
			case T_DATE:
				return "T_DATE";
			case T_TIMESTAMP:
				return "T_TIMESTAMP";
			case T_BIGINTEGER:
				return "T_BIGINTEGER";
			case T_BIGDECIMAL:
				return "T_BIGDECIMAL";
			case T_STRING:
				return "T_STRING";
			case T_BYTEARRAY:
				return "T_BYTEARRAY";
			case T_ARRAY:
				return "T_ARRAY";
			case T_LIST:
				return "T_LIST";
			case T_MAP:
				return "T_MAP";
			case T_SET:
				return "T_SET";
			default:
				return "Unknown(" + type + ")";
			}
		}
		else {
			return "null";
		}
	}

}
