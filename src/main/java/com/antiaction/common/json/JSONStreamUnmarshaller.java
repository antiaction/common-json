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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * De-serialize a JSON data stream into Java Object(s).
 * FIXME Test nullable for fields not present in stream but present in mapping.
 * FIXME Throws exception when an object or array is not mapped.
 *
 * @author Nicholas
 * Created on 17/10/2013
 */
public class JSONStreamUnmarshaller {

	private static final int S_START = 0;
	private static final int S_OBJECT_BEGIN = 1;
	private static final int S_OBJECT_END = 2;
	private static final int S_ARRAY_BEGIN = 3;
	private static final int S_ARRAY_END = 4;
	private static final int S_OBJECT = 5;
	private static final int S_OBJECT_NAME = 6;
	private static final int S_OBJECT_COLON = 7;
	private static final int S_OBJECT_VALUE = 8;
	private static final int S_OBJECT_VALUE_NEXT = 9;
	private static final int S_OBJECT_NAME_NEXT = 10;
	private static final int S_ARRAY = 11;
	private static final int S_ARRAY_VALUE = 12;
	private static final int S_ARRAY_NEXT = 13;
	private static final int S_LIST = 14;
	private static final int S_LIST_VALUE = 15;
	private static final int S_LIST_NEXT = 16;
	private static final int S_VALUE_START = 17;
	private static final int S_STRING = 18;
	private static final int S_STRING_UNESCAPE = 19;
	private static final int S_STRING_UNHEX = 20;
	private static final int S_CONSTANT = 21;
	private static final int S_NUMBER_MINUS = 22;
	private static final int S_NUMBER_ZERO = 23;
	private static final int S_NUMBER_INTEGER = 24;
	private static final int S_NUMBER_DECIMAL = 25;
	private static final int S_NUMBER_DECIMALS = 26;
	private static final int S_NUMBER_E = 27;
	private static final int S_NUMBER_EXPONENT = 28;
	private static final int S_NUMBER_EXPONENTS = 29;
	private static final int S_EOF = 30;

	private static final int VT_NULL = 1;
	private static final int VT_BOOLEAN = 2;
	private static final int VT_STRING = 3;
	private static final int VT_NUMBER = 4;
	private static final int VT_OBJECT = 5;
	private static final int VT_ARRAY = 6;

	protected JSONObjectMappings objectMappings;

	protected Map<String, JSONObjectMapping> classMappings;

	/** Temporary <code>StringBuilder</code> used to store JSON strings and values. */
	protected StringBuilder sbStr = new StringBuilder();

	private static final class StackEntry {
		int state;
		Object curObj;
		Map<String, JSONObjectFieldMapping> fieldMappingsMap;
		JSONObjectFieldMapping fieldMapping;
		int fieldMappingStep;
		String mapKey;
		Collection<?> curArr;
	}

	public JSONStreamUnmarshaller(JSONObjectMappings objectMappings) {
		this.objectMappings = objectMappings;
		this.classMappings = objectMappings.classMappings;
	}

	public <T> T toObject(InputStream in, JSONDecoder decoder, Class<T> clazz) throws IOException, JSONException {
		JSONDecoderContext decoderContext = new JSONDecoderContext( decoder, 8192 );
		decoderContext.init( in );
		return toObject( in, decoderContext, false, clazz, null );
	}

	public <T> T toObject(InputStream in, JSONDecoder decoder, Class<T> clazz, JSONConverterAbstract[] converters) throws IOException, JSONException {
		JSONDecoderContext decoderContext = new JSONDecoderContext( decoder, 8192 );
		decoderContext.init( in );
		return toObject( in, decoderContext,false, clazz, converters );
	}

	public <T> T toObject(InputStream in, JSONDecoder decoder, boolean bExitAfterEnd, Class<T> clazz) throws IOException, JSONException {
		JSONDecoderContext decoderContext = new JSONDecoderContext( decoder, 8192 );
		decoderContext.init( in );
		return toObject( in, decoderContext, bExitAfterEnd, clazz, null );
	}

	public <T> T toObject(InputStream in, JSONDecoder decoder, boolean bExitAfterEnd, Class<T> clazz, JSONConverterAbstract[] converters) throws IOException, JSONException {
		JSONDecoderContext decoderContext = new JSONDecoderContext( decoder, 8192 );
		decoderContext.init( in );
		return toObject( in, decoderContext, bExitAfterEnd, clazz, converters );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T toObject(InputStream in, JSONDecoderContext decoderContext, boolean bExitAfterEnd, Class<T> clazz, JSONConverterAbstract[] converters) throws IOException, JSONException {
		Boolean booleanVal = null;
		Byte byteVal = null;
		Character charVal = null;
		Integer intVal = null;
		Long longVal = null;
		Float floatVal = null;
		Double doubleVal = null;
		Date dateVal = null;
		Timestamp timestampVal = null;
		BigInteger bigIntegerVal = null;
		BigDecimal bigDecimalVal = null;
		String stringVal = null;
		byte[] byteArray = null;
		Object object = null;
		Collection array = null;

		boolean[] arrayOf_boolean;
		int[] arrayOf_int;
		long[] arrayOf_long;
		float[] arrayOf_float;
		double[] arrayOf_double;
		/*
		Boolean[] arrayOf_Boolean;
		Integer[] arrayOf_Integer;
		Long[] arrayOf_Long;
		Float[] arrayOf_Float;
		Double[] arrayOf_Double;
		BigInteger[] arrayOf_BigInteger;
		BigDecimal[] arrayOf_BigDecimal;
		String[] arrayOf_String;
		Object[] arrayOf_Object;
		*/

		JSONObjectMapping objectMapping = classMappings.get( clazz.getName() );
		if ( objectMapping == null ) {
			throw new JSONException( "Class '" + clazz.getName() + "' not registered." );
		}
		if ( objectMapping.converters == true && converters == null ) {
			throw new JSONException( "Class '" + clazz.getName() + "' may required converters!" );
		}

		T rootObj = null;

		LinkedList<StackEntry> stack = new LinkedList<StackEntry>();
		StackEntry stackEntry = null;

		char[] charArray = decoderContext.charArray;
		int pos = decoderContext.pos;
		int limit = decoderContext.limit;
		int y = decoderContext.y;
		int x = decoderContext.x;

		int hexValue = 0;
		int hexCount = 0;
		int i;
		String constant;

		int json_value_type = 0;

		Object curObj = null;
		Collection curArr = null;
		Map<String, JSONObjectFieldMapping> fieldMappingsMap = null;
		JSONObjectFieldMapping fieldMapping = null;
		int fieldMappingStep = 0;								// TODO Document.
		String mapKey = null;

		boolean bArrayToRootObj = false;

		int state = S_START;
		int rstate = -1;
		boolean bLoop = true;
		boolean bExit = false;
		char c;
		try {
			while ( bLoop ) {
				while ( pos < limit && !bExit ) {
					c = charArray[ pos ];
					// debug
					//System.out.println( "(" + y + ":" + x + ") " + stateStr.get( state ) + " (" + state + ")" );
					/*
					 * Parser state machine.
					 */
					switch ( state ) {
					/*
					 * Start state: Whitespace or the start of an object or array.
					 */
					case S_START:
						++x;
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							// Whitespace.
							break;
						case 0x0A:
							++y;
							x = 1;
							break;
						case '{':
							rootObj = clazz.newInstance();
							curObj = rootObj;
							fieldMappingsMap = objectMapping.fieldMappingsMap;
							state = S_OBJECT;
							break;
						case '[':
							curArr = new ArrayList();
							fieldMapping = objectMapping.fieldMapping;
							state = S_ARRAY;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						++pos;
						break;
						/*
						 * Parses all objects except a root one.
						 */
					case S_OBJECT_BEGIN:
						++pos;
						stackEntry = new StackEntry();
						stackEntry.state = rstate;
						stackEntry.curObj = curObj;
						stackEntry.fieldMappingsMap = fieldMappingsMap;
						stackEntry.fieldMapping = fieldMapping;
						stackEntry.fieldMappingStep = fieldMappingStep;
						stackEntry.mapKey = mapKey;
						stackEntry.curArr = curArr;
						stack.add( stackEntry );

						// To support collection of objects. "[{..."
						switch (fieldMapping.type) {
						case JSONObjectMappingConstants.T_OBJECT:
							curObj = fieldMapping.clazz.newInstance();
							objectMapping = classMappings.get( fieldMapping.clazz.getName() );
							fieldMappingStep = 0;
							break;
						case JSONObjectMappingConstants.T_ARRAY:
							curObj = fieldMapping.clazz.newInstance();
							objectMapping = classMappings.get( fieldMapping.clazz.getName() );
							fieldMappingStep = 0;
							break;
						case JSONObjectMappingConstants.T_LIST:
							curObj = fieldMapping.parametrizedObjectMappings[ 0 ].clazz.newInstance();
							objectMapping = classMappings.get( fieldMapping.parametrizedObjectMappings[ 0 ].clazz.getName() );
							fieldMappingStep = 0;
							break;
						case JSONObjectMappingConstants.T_SET:
							throw new UnsupportedOperationException( "Unexpected type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) );
						case JSONObjectMappingConstants.T_MAP:
							if ( fieldMappingStep == 0 ) {
								curObj = fieldMapping.instanceClazz.newInstance();
								objectMapping = classMappings.get( fieldMapping.instanceClazz.getName() );
								fieldMappingStep = 1;
							}
							else {
								curObj = fieldMapping.parametrizedObjectMappings[ 1 ].clazz.newInstance();
								objectMapping = classMappings.get( fieldMapping.parametrizedObjectMappings[ 1 ].clazz.getName() );
								--fieldMappingStep;
							}
							break;
						default:
							throw new UnsupportedOperationException( "Unexpected type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) );
						}
						if ( objectMapping != null ) {
							fieldMappingsMap = objectMapping.fieldMappingsMap;
							if ( objectMapping.converters == true && converters == null ) {
								throw new JSONException( "Class '" + fieldMapping.clazz.getName() + "' may required converters!" );
							}
						}
						else {
							if ( fieldMappingStep == 0 ) {
								throw new JSONException( "Class '" + fieldMapping.clazz.getName() + "' not registered." );
							}
						}
						state = S_OBJECT;
						break;
					case S_OBJECT_END:
						++pos;
						if ( stack.size() > 0 ) {
							json_value_type = VT_OBJECT;
							object = curObj;
							stackEntry = stack.removeLast();
							state = stackEntry.state;
							curObj = stackEntry.curObj;
							fieldMappingsMap = stackEntry.fieldMappingsMap;
							fieldMapping = stackEntry.fieldMapping;
							fieldMappingStep = stackEntry.fieldMappingStep;
							mapKey = stackEntry.mapKey;
							curArr = stackEntry.curArr;
						}
						else {
							state = S_EOF;
						}
						break;
					case S_ARRAY_BEGIN:
						++pos;
						stackEntry = new StackEntry();
						stackEntry.state = rstate;
						stackEntry.curObj = curObj;
						stackEntry.fieldMappingsMap = fieldMappingsMap;
						stackEntry.fieldMapping = fieldMapping;
						stackEntry.fieldMappingStep = fieldMappingStep;
						stackEntry.mapKey = mapKey;
						stackEntry.curArr = curArr;
						stack.add( stackEntry );

						switch (fieldMapping.type) {
						case JSONObjectMappingConstants.T_ARRAY:
							curArr = new ArrayList();
							//curArrType = fieldMapping.arrayType;
							state = S_ARRAY;
							break;
						case JSONObjectMappingConstants.T_LIST:
						case JSONObjectMappingConstants.T_SET:
							//curArr = (Collection)fieldMapping.clazz.newInstance();
							curArr = (Collection)fieldMapping.instanceClazz.newInstance();
							state = S_LIST;
							break;
						case JSONObjectMappingConstants.T_OBJECT:
						case JSONObjectMappingConstants.T_MAP:
						default:
							throw new UnsupportedOperationException( "Unexpected type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) );
						}
						objectMapping = classMappings.get( fieldMapping.clazz.getName() );
						fieldMappingStep = 0;
						break;
					case S_ARRAY_END:
						++pos;
						if ( stack.size() > 0 ) {
							// debug
							//System.out.println( fieldMapping.type );
							json_value_type = VT_ARRAY;
							array = curArr;
							stackEntry = stack.removeLast();
							state = stackEntry.state;
							curObj = stackEntry.curObj;
							fieldMappingsMap = stackEntry.fieldMappingsMap;
							fieldMapping = stackEntry.fieldMapping;
							fieldMappingStep = stackEntry.fieldMappingStep;
							mapKey = stackEntry.mapKey;
							curArr = stackEntry.curArr;
						}
						else {
							array = curArr;
							bArrayToRootObj = true;
							state = S_EOF;
						}
						break;
					case S_OBJECT:
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							++x;
							// Whitespace.
							++pos;
							break;
						case 0x0A:
							++y;
							x = 1;
							++pos;
							break;
						case '}':
							++x;
							state = S_OBJECT_END;
							break;
						case '"':
							++x;
							sbStr.setLength( 0 );
							state = S_STRING;
							rstate = S_OBJECT_NAME;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_OBJECT_NAME:
						// debug
						//System.out.println( stringVal + " - " + fieldMappingStep );
						// TODO Handle missing mapping for key/field in stream.
						if ( fieldMappingStep == 0 ) {
							fieldMapping = fieldMappingsMap.get( stringVal );
							if (fieldMapping == null) {
								throw new JSONException("No java field for \"" + stringVal + "\" at (" + y + ":" + x + ")!");
							}
						}
						else {
							mapKey = stringVal;
						}
						state = S_OBJECT_COLON;
					case S_OBJECT_COLON:
						++x;
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							// Whitespace.
							break;
						case 0x0A:
							++y;
							x = 1;
							break;
						case ':':
							state = S_VALUE_START;
							rstate = S_OBJECT_VALUE;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						++pos;
						break;
					case S_OBJECT_VALUE:
						// debug
						//System.out.println("json_value_type=" + json_value_type);
						switch ( json_value_type ) {
						case VT_NULL:
							if ( !fieldMapping.nullable ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "'/'" + fieldMapping.jsonName + "' is not nullable." );
							}
							break;
						case VT_BOOLEAN:
							switch ( fieldMapping.type ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								fieldMapping.field.setBoolean( curObj, booleanVal );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( booleanVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, booleanVal );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) );
							}
							break;
						case VT_STRING:
							switch ( fieldMapping.type ) {
							case JSONObjectMappingConstants.T_STRING:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//strVal = converters[ fieldMapping.converterId ].getString( fieldMapping.fieldName, json_string );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( stringVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, stringVal );
								break;
							case JSONObjectMappingConstants.T_BYTEARRAY:
								if ( fieldMapping.converterId == -1 ) {
									byteArray = stringVal.getBytes();
								}
								else {
									// TODO
									//byteArray = converters[ fieldMapping.converterId ].getBytes( fieldMapping.fieldName, json_string );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( byteArray == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, byteArray );
								break;
							case JSONObjectMappingConstants.T_MAP:
								// debug
								//System.out.println( "Map - " + fieldMappingStep + " - " + mapKey );
								if ( fieldMappingStep == 1 ) {
									((Map)curObj).put( mapKey, stringVal );
								}
								else {
									// Is this code ever reached?
									/*
									// From Object.
									if ( object != null ) {
										// TODO
										//object = toObject( object, fieldMapping.clazz, converters );
									}
									*/
									if ( stringVal == null && !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									fieldMapping.field.set( curObj, stringVal );
								}
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) + " at (" + y + ":" + x + ")!" );
							}
							break;
						case VT_OBJECT:
							switch ( fieldMapping.type ) {
							case JSONObjectMappingConstants.T_OBJECT:
								if ( object != null ) {
									// TODO
									//object = toObject( object, fieldMapping.clazz, converters );
								}
								if ( object == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, object );
								break;
							case JSONObjectMappingConstants.T_MAP:
								// debug
								//System.out.println( "Map - " + fieldMappingStep + " - " + mapKey );
								if ( fieldMappingStep == 1 ) {
									((Map)curObj).put( mapKey, object );
								}
								else {
									// Is this code ever reached?
									if ( object != null ) {
										// TODO
										//object = toObject( object, fieldMapping.clazz, converters );
									}
									if ( object == null && !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									fieldMapping.field.set( curObj, object );
								}
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) );
							}
							break;
						case VT_NUMBER:
							switch ( fieldMapping.type ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								if ( "1".equals( stringVal ) ) {
									booleanVal = true;
								}
								else if ( "0".equals( stringVal ) ) {
									booleanVal = false;
								}
								else {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is a boolean and can not be '" + stringVal + "'." );
								}
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									//booleanVal = null;
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								fieldMapping.field.setBoolean( curObj, booleanVal );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								if ( "1".equals( stringVal ) ) {
									booleanVal = true;
								}
								else if ( "0".equals( stringVal ) ) {
									booleanVal = false;
								}
								else {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is a boolean and can not be '" + stringVal + "'." );
								}
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									//booleanVal = null;
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( booleanVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, booleanVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_BYTE:
								if ( fieldMapping.converterId == -1 ) {
									byteVal = Byte.parseByte( stringVal );
								}
								else {
									// TODO
									//byteVal = converters[ fieldMapping.converterId ].getByte( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( byteVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								fieldMapping.field.setByte( curObj, byteVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_CHAR:
								if ( fieldMapping.converterId == -1 ) {
									charVal = (char)Integer.parseInt( stringVal );
								}
								else {
									// TODO
									//charVal = converters[ fieldMapping.converterId ].getChar( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( charVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								fieldMapping.field.setChar( curObj, charVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
								if ( fieldMapping.converterId == -1 ) {
									intVal = Integer.parseInt( stringVal );
								}
								else {
									// TODO
									//intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( intVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								fieldMapping.field.setInt( curObj, intVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
								if ( fieldMapping.converterId == -1 ) {
									longVal = Long.parseLong( stringVal );
								}
								else {
									// TODO
									//longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( longVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								fieldMapping.field.setLong( curObj, longVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
								if ( fieldMapping.converterId == -1 ) {
									floatVal = Float.parseFloat( stringVal );
								}
								else {
									// TODO
									//floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( floatVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								fieldMapping.field.setFloat( curObj, floatVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
								if ( fieldMapping.converterId == -1 ) {
									doubleVal = Double.parseDouble( stringVal );
								}
								else {
									// TODO
									//doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( doubleVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								fieldMapping.field.setDouble( curObj, doubleVal );
								break;
							case JSONObjectMappingConstants.T_BYTE:
								if ( fieldMapping.converterId == -1 ) {
									byteVal = Byte.parseByte( stringVal );
								}
								else {
									// TODO
									//byteVal = converters[ fieldMapping.converterId ].getByte( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( byteVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, byteVal );
								break;
							case JSONObjectMappingConstants.T_CHARACTER:
								if ( fieldMapping.converterId == -1 ) {
									charVal = (char)Integer.parseInt( stringVal );
								}
								else {
									// TODO
									//charVal = converters[ fieldMapping.converterId ].getChar( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( charVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, charVal );
								break;
							case JSONObjectMappingConstants.T_INTEGER:
								if ( fieldMapping.converterId == -1 ) {
									intVal = Integer.parseInt( stringVal );
								}
								else {
									// TODO
									//intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( intVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, intVal );
								break;
							case JSONObjectMappingConstants.T_LONG:
								if ( fieldMapping.converterId == -1 ) {
									longVal = Long.parseLong( stringVal );
								}
								else {
									// TODO
									//longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( longVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, longVal );
								break;
							case JSONObjectMappingConstants.T_FLOAT:
								if ( fieldMapping.converterId == -1 ) {
									floatVal = Float.parseFloat( stringVal );
								}
								else {
									// TODO
									//floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( floatVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, floatVal );
								break;
							case JSONObjectMappingConstants.T_DOUBLE:
								if ( fieldMapping.converterId == -1 ) {
									doubleVal = Double.parseDouble( stringVal );
								}
								else {
									// TODO
									//doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( doubleVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, doubleVal );
								break;
							case JSONObjectMappingConstants.T_DATE:
								if ( fieldMapping.converterId == -1 ) {
									dateVal = new Date( Long.parseLong( stringVal ) );
								}
								else {
									// TODO
									//dateVal = converters[ fieldMapping.converterId ].getDate( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
s								if ( dateVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, dateVal );
								break;
							case JSONObjectMappingConstants.T_TIMESTAMP:
								if ( fieldMapping.converterId == -1 ) {
									timestampVal = new Timestamp( Long.parseLong( stringVal ) );
								}
								else {
									// TODO
									//timestampVal = converters[ fieldMapping.converterId ].getTimestamp( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( timestampVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, timestampVal );
								break;
							case JSONObjectMappingConstants.T_BIGINTEGER:
								if ( fieldMapping.converterId == -1 ) {
									bigIntegerVal = new BigInteger( stringVal );
								}
								else {
									// TODO
									//bigIntegerVal = converters[ fieldMapping.converterId ].getBigInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( bigIntegerVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, bigIntegerVal );
								break;
							case JSONObjectMappingConstants.T_BIGDECIMAL:
								if ( fieldMapping.converterId == -1 ) {
									bigDecimalVal = new BigDecimal( stringVal );
								}
								else {
									// TODO
									//bigDecimalVal = converters[ fieldMapping.converterId ].getBigDecimal( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( bigDecimalVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								fieldMapping.field.set( curObj, bigDecimalVal );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) );
							}
							break;
						case VT_ARRAY:
							switch ( fieldMapping.type ) {
							case JSONObjectMappingConstants.T_ARRAY:
								// debug
								//System.out.println( fieldMapping.arrayType );
								int idx;
								switch ( fieldMapping.arrayType ) {
								case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
									arrayOf_boolean = new boolean[ array.size() ];
									Iterator<Boolean> booleanIter = array.iterator();
									idx = 0;
									while ( booleanIter.hasNext() ) {
										arrayOf_boolean[ idx++ ] = booleanIter.next();
									}
									fieldMapping.field.set( curObj, arrayOf_boolean );
									break;
								case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
									arrayOf_int = new int[ array.size() ];
									Iterator<Integer> intIter = array.iterator();
									idx = 0;
									while ( intIter.hasNext() ) {
										arrayOf_int[ idx++ ] = intIter.next();
									}
									fieldMapping.field.set( curObj, arrayOf_int );
									break;
								case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
									arrayOf_long = new long[ array.size() ];
									Iterator<Long> longIter = array.iterator();
									idx = 0;
									while ( longIter.hasNext() ) {
										arrayOf_long[ idx++ ] = longIter.next();
									}
									fieldMapping.field.set( curObj, arrayOf_long );
									break;
								case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
									arrayOf_float = new float[ array.size() ];
									Iterator<Float> floatIter = array.iterator();
									idx = 0;
									while ( floatIter.hasNext() ) {
										arrayOf_float[ idx++ ] = floatIter.next();
									}
									fieldMapping.field.set( curObj, arrayOf_float );
									break;
								case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
									arrayOf_double = new double[ array.size() ];
									Iterator<Double> doubleIter = array.iterator();
									idx = 0;
									while ( doubleIter.hasNext() ) {
										arrayOf_double[ idx++ ] = doubleIter.next();
									}
									fieldMapping.field.set( curObj, arrayOf_double );
									break;
								default:
									fieldMapping.field.set( curObj, array.toArray( (Object[]) Array.newInstance( fieldMapping.field.getType().getComponentType(), array.size() ) ) );
									break;
								}
								break;
							case JSONObjectMappingConstants.T_LIST:
							case JSONObjectMappingConstants.T_SET:
								fieldMapping.field.set( curObj, array );
								break;
							case JSONObjectMappingConstants.T_MAP:
								// TODO
								throw new UnsupportedOperationException();
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) );
							}
							break;
						default:
							throw new JSONException( "Unknown json value type: " + typeString( json_value_type ) );
						}
						// TODO
						//current.put( json_name, json_value );
						state = S_OBJECT_VALUE_NEXT;
						// debug
						//System.out.println( json_value.toString() );
					case S_OBJECT_VALUE_NEXT:
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							++x;
							// Whitespace.
							++pos;
							break;
						case 0x0A:
							++y;
							x = 1;
							++pos;
							break;
						case '}':
							++x;
							state = S_OBJECT_END;
							break;
						case ',':
							++x;
							state = S_OBJECT_NAME_NEXT;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_OBJECT_NAME_NEXT:
						++x;
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							// Whitespace.
							break;
						case 0x0A:
							++y;
							x = 1;
							break;
						case '"':
							sbStr.setLength( 0 );
							state = S_STRING;
							rstate = S_OBJECT_NAME;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						++pos;
						break;
					case S_ARRAY:
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							++x;
							// Whitespace.
							++pos;
							break;
						case 0x0A:
							++y;
							x = 1;
							++pos;
							break;
						case ']':
							++x;
							state = S_ARRAY_END;
							break;
						case '{':
							++x;
							state = S_OBJECT_BEGIN;
							rstate = S_ARRAY_VALUE;
							break;
						case '[':
							++x;
							state = S_ARRAY_BEGIN;
							rstate = S_ARRAY_VALUE;
							break;
						case '"':
							++x;
							sbStr.setLength( 0 );
							state = S_STRING;
							rstate = S_ARRAY_VALUE;
							++pos;
							break;
						case 'f':
						case 'n':
						case 't':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_CONSTANT;
							rstate = S_ARRAY_VALUE;
							++pos;
							break;
						case '-':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_MINUS;
							rstate = S_ARRAY_VALUE;
							++pos;
							break;
						case '0':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_ZERO;
							rstate = S_ARRAY_VALUE;
							++pos;
							break;
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_INTEGER;
							rstate = S_ARRAY_VALUE;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_ARRAY_VALUE:
						// TODO
						switch ( json_value_type ) {
						case VT_NULL:
							if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "'/'" + fieldMapping.jsonName + "' is not nullable." );
							}
							curArr.add( null );
							break;
						case VT_BOOLEAN:
							switch ( fieldMapping.arrayType ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								curArr.add( booleanVal );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( booleanVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								curArr.add( booleanVal );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
							}
							break;
						case VT_STRING:
							switch ( fieldMapping.arrayType ) {
							case JSONObjectMappingConstants.T_STRING:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//strVal = converters[ fieldMapping.converterId ].getString( fieldMapping.fieldName, json_string );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( stringVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								curArr.add( stringVal );
								break;
							case JSONObjectMappingConstants.T_BYTEARRAY:
								if ( fieldMapping.converterId == -1 ) {
									byteArray = stringVal.getBytes();
								}
								else {
									// TODO
									//byteArray = converters[ fieldMapping.converterId ].getBytes( fieldMapping.fieldName, json_string );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( byteArray == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								curArr.add( byteArray );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
							}
							break;
						case VT_OBJECT:
							switch ( fieldMapping.arrayType ) {
							case JSONObjectMappingConstants.T_OBJECT:
								if ( object != null ) {
									// TODO
									//object = toObject( object, fieldMapping.clazz, converters );
								}
								if ( object == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								curArr.add( object );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
							}
							break;
						case VT_NUMBER:
							switch ( fieldMapping.arrayType ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								if ( "1".equals( stringVal ) ) {
									booleanVal = true;
								}
								else if ( "0".equals( stringVal ) ) {
									booleanVal = false;
								}
								else {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is a boolean and can not be '" + stringVal + "'." );
								}
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									//booleanVal = null;
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( booleanVal );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								if ( "1".equals( stringVal ) ) {
									booleanVal = true;
								}
								else if ( "0".equals( stringVal ) ) {
									booleanVal = false;
								}
								else {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is a boolean and can not be '" + stringVal + "'." );
								}
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									//booleanVal = null;
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( booleanVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( booleanVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
								if ( fieldMapping.converterId == -1 ) {
									intVal = Integer.parseInt( stringVal );
								}
								else {
									// TODO
									//intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( intVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( intVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
								if ( fieldMapping.converterId == -1 ) {
									longVal = Long.parseLong( stringVal );
								}
								else {
									// TODO
									//longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( longVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( longVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
								if ( fieldMapping.converterId == -1 ) {
									floatVal = Float.parseFloat( stringVal );
								}
								else {
									// TODO
									//floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( floatVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( floatVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
								if ( fieldMapping.converterId == -1 ) {
									doubleVal = Double.parseDouble( stringVal );
								}
								else {
									// TODO
									//doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( doubleVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( doubleVal );
								break;
							case JSONObjectMappingConstants.T_INTEGER:
								if ( fieldMapping.converterId == -1 ) {
									intVal = Integer.parseInt( stringVal );
								}
								else {
									// TODO
									//intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( intVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( intVal );
								break;
							case JSONObjectMappingConstants.T_LONG:
								if ( fieldMapping.converterId == -1 ) {
									longVal = Long.parseLong( stringVal );
								}
								else {
									// TODO
									//longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( longVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( longVal );
								break;
							case JSONObjectMappingConstants.T_FLOAT:
								if ( fieldMapping.converterId == -1 ) {
									floatVal = Float.parseFloat( stringVal );
								}
								else {
									// TODO
									//floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( floatVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( floatVal );
								break;
							case JSONObjectMappingConstants.T_DOUBLE:
								if ( fieldMapping.converterId == -1 ) {
									doubleVal = Double.parseDouble( stringVal );
								}
								else {
									// TODO
									//doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( doubleVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( doubleVal );
								break;
							case JSONObjectMappingConstants.T_BIGINTEGER:
								if ( fieldMapping.converterId == -1 ) {
									bigIntegerVal = new BigInteger( stringVal );
								}
								else {
									// TODO
									//bigIntegerVal = converters[ fieldMapping.converterId ].getBigInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( bigIntegerVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( bigIntegerVal );
								break;
							case JSONObjectMappingConstants.T_BIGDECIMAL:
								if ( fieldMapping.converterId == -1 ) {
									bigDecimalVal = new BigDecimal( stringVal );
								}
								else {
									// TODO
									//bigDecimalVal = converters[ fieldMapping.converterId ].getBigDecimal( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( bigDecimalVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( bigDecimalVal );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
							}
							break;
						default:
							throw new JSONException( "Unknown json value type: " + typeString( json_value_type ) );
						}
						state = S_ARRAY_NEXT;
						// debug
						//System.out.println( json_value.toString() );
					case S_ARRAY_NEXT:
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							++x;
							// Whitespace.
							++pos;
							break;
						case 0x0A:
							++y;
							x = 1;
							++pos;
							break;
						case ']':
							++x;
							state = S_ARRAY_END;
							break;
						case ',':
							++x;
							state = S_VALUE_START;
							rstate = S_ARRAY_VALUE;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_LIST:
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							++x;
							// Whitespace.
							++pos;
							break;
						case 0x0A:
							++y;
							x = 1;
							++pos;
							break;
						case ']':
							++x;
							state = S_ARRAY_END;
							break;
						case '{':
							++x;
							state = S_OBJECT_BEGIN;
							rstate = S_LIST_VALUE;
							break;
						case '[':
							++x;
							state = S_ARRAY_BEGIN;
							rstate = S_LIST_VALUE;
							break;
						case '"':
							++x;
							sbStr.setLength( 0 );
							state = S_STRING;
							rstate = S_LIST_VALUE;
							++pos;
							break;
						case 'f':
						case 'n':
						case 't':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_CONSTANT;
							rstate = S_LIST_VALUE;
							++pos;
							break;
						case '-':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_MINUS;
							rstate = S_LIST_VALUE;
							++pos;
							break;
						case '0':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_ZERO;
							rstate = S_LIST_VALUE;
							++pos;
							break;
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_INTEGER;
							rstate = S_LIST_VALUE;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_LIST_VALUE:
						// TODO
						switch ( json_value_type ) {
						case VT_NULL:
							if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "'/'" + fieldMapping.jsonName + "' is not nullable." );
							}
							curArr.add( null );
							break;
						case VT_BOOLEAN:
							switch ( fieldMapping.parametrizedObjectTypes[ 0 ] ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								curArr.add( booleanVal );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( booleanVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								curArr.add( booleanVal );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
							}
							break;
						case VT_STRING:
							switch ( fieldMapping.parametrizedObjectTypes[ 0 ] ) {
							case JSONObjectMappingConstants.T_STRING:
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//strVal = converters[ fieldMapping.converterId ].getString( fieldMapping.fieldName, json_string );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( stringVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								curArr.add( stringVal );
								break;
							case JSONObjectMappingConstants.T_BYTEARRAY:
								if ( fieldMapping.converterId == -1 ) {
									byteArray = stringVal.getBytes();
								}
								else {
									// TODO
									//byteArray = converters[ fieldMapping.converterId ].getBytes( fieldMapping.fieldName, json_string );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								if ( byteArray == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								curArr.add( byteArray );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
							}
							break;
						case VT_OBJECT:
							switch ( fieldMapping.parametrizedObjectTypes[ 0 ] ) {
							case JSONObjectMappingConstants.T_OBJECT:
								if ( object != null ) {
									// TODO
									//object = toObject( object, fieldMapping.clazz, converters );
								}
								if ( object == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								curArr.add( object );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
							}
							break;
						case VT_NUMBER:
							switch ( fieldMapping.parametrizedObjectTypes[ 0 ] ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								if ( "1".equals( stringVal ) ) {
									booleanVal = true;
								}
								else if ( "0".equals( stringVal ) ) {
									booleanVal = false;
								}
								else {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is a boolean and can not be '" + stringVal + "'." );
								}
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									//booleanVal = null;
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( booleanVal );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								if ( "1".equals( stringVal ) ) {
									booleanVal = true;
								}
								else if ( "0".equals( stringVal ) ) {
									booleanVal = false;
								}
								else {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is a boolean and can not be '" + stringVal + "'." );
								}
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
									//booleanVal = null;
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( booleanVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( booleanVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
								if ( fieldMapping.converterId == -1 ) {
									intVal = Integer.parseInt( stringVal );
								}
								else {
									// TODO
									//intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( intVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( intVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
								if ( fieldMapping.converterId == -1 ) {
									longVal = Long.parseLong( stringVal );
								}
								else {
									// TODO
									//longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( longVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( longVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
								if ( fieldMapping.converterId == -1 ) {
									floatVal = Float.parseFloat( stringVal );
								}
								else {
									// TODO
									//floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( floatVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( floatVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
								if ( fieldMapping.converterId == -1 ) {
									doubleVal = Double.parseDouble( stringVal );
								}
								else {
									// TODO
									//doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( doubleVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								*/
								curArr.add( doubleVal );
								break;
							case JSONObjectMappingConstants.T_INTEGER:
								if ( fieldMapping.converterId == -1 ) {
									intVal = Integer.parseInt( stringVal );
								}
								else {
									// TODO
									//intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( intVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( intVal );
								break;
							case JSONObjectMappingConstants.T_LONG:
								if ( fieldMapping.converterId == -1 ) {
									longVal = Long.parseLong( stringVal );
								}
								else {
									// TODO
									//longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( longVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( longVal );
								break;
							case JSONObjectMappingConstants.T_FLOAT:
								if ( fieldMapping.converterId == -1 ) {
									floatVal = Float.parseFloat( stringVal );
								}
								else {
									// TODO
									//floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( floatVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( floatVal );
								break;
							case JSONObjectMappingConstants.T_DOUBLE:
								if ( fieldMapping.converterId == -1 ) {
									doubleVal = Double.parseDouble( stringVal );
								}
								else {
									// TODO
									//doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( doubleVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( doubleVal );
								break;
							case JSONObjectMappingConstants.T_BIGINTEGER:
								if ( fieldMapping.converterId == -1 ) {
									bigIntegerVal = new BigInteger( stringVal );
								}
								else {
									// TODO
									//bigIntegerVal = converters[ fieldMapping.converterId ].getBigInteger( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( bigIntegerVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( bigIntegerVal );
								break;
							case JSONObjectMappingConstants.T_BIGDECIMAL:
								if ( fieldMapping.converterId == -1 ) {
									bigDecimalVal = new BigDecimal( stringVal );
								}
								else {
									// TODO
									//bigDecimalVal = converters[ fieldMapping.converterId ].getBigDecimal( fieldMapping.fieldName, json_value );
									throw new JSONException( "Converter unsupported in stream unmarshaller." );
								}
								/*
								if ( bigDecimalVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								*/
								curArr.add( bigDecimalVal );
								break;
							default:
								throw new JSONException( "Wrong type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
							}
							break;
						default:
							throw new JSONException( "Unknown json value type: " + typeString( json_value_type ) );
						}
						state = S_LIST_NEXT;
						// debug
						//System.out.println( json_value.toString() );
					case S_LIST_NEXT:
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							++x;
							// Whitespace.
							++pos;
							break;
						case 0x0A:
							++y;
							x = 1;
							++pos;
							break;
						case ']':
							++x;
							state = S_ARRAY_END;
							break;
						case ',':
							++x;
							state = S_VALUE_START;
							rstate = S_LIST_VALUE;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_VALUE_START:
						// rstate should be set prior to this state.
						switch ( c ) {
						case 0x20:
						case 0x09:
						case 0x0D:
							++x;
							// Whitespace.
							++pos;
							break;
						case 0x0A:
							++y;
							x = 1;
							++pos;
							break;
						case '{':
							++x;
							state = S_OBJECT_BEGIN;
							break;
						case '[':
							++x;
							state = S_ARRAY_BEGIN;
							break;
						case '"':
							++x;
							sbStr.setLength( 0 );
							state = S_STRING;
							++pos;
							break;
						case 'f':
						case 'n':
						case 't':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_CONSTANT;
							++pos;
							break;
						case '-':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_MINUS;
							++pos;
							break;
						case '0':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_ZERO;
							++pos;
							break;
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							++x;
							sbStr.setLength( 0 );
							sbStr.append( c );
							state = S_NUMBER_INTEGER;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_STRING:
						++x;
						switch ( c ) {
						case '"':
							json_value_type = VT_STRING;
							stringVal = sbStr.toString();
							state = rstate;
							break;
						case '\\':
							state = S_STRING_UNESCAPE;
							break;
						default:
							if ( c < 32 ) {
								throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
							}
							sbStr.append( c );
							break;
						}
						++pos;
						break;
					case S_STRING_UNESCAPE:
						++x;
						switch ( c ) {
						case '"':
							sbStr.append( '"');
							state = S_STRING;
							break;
						case '/':
							sbStr.append( '/' );
							state = S_STRING;
							break;
						case '\\':
							sbStr.append( '\\' );
							state = S_STRING;
							break;
						case 'b':
							sbStr.append( (char)0x08 );
							state = S_STRING;
							break;
						case 't':
							sbStr.append( (char)0x09 );
							state = S_STRING;
							break;
						case 'n':
							sbStr.append( (char)0x0A );
							state = S_STRING;
							break;
						case 'f':
							sbStr.append( (char)0x0C );
							state = S_STRING;
							break;
						case 'r':
							sbStr.append( (char)0x0D );
							state = S_STRING;
							break;
						case 'u':
							hexValue = 0;
							hexCount = 4;
							state = S_STRING_UNHEX;
							break;
						default:
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						++pos;
						break;
					case S_STRING_UNHEX:
						++x;
						if ( c > 255 ) {
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						i = asciiHexTab[ c ];
						if ( i == -1 ) {
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						hexValue <<= 4;
						hexValue |= i;
						--hexCount;
						if (hexCount == 0) {
							sbStr.append( (char)hexValue );
							state = S_STRING;
						}
						++pos;
						break;
					case S_CONSTANT:
						switch ( c ) {
						case 'a':
						case 'l':
						case 's':
						case 'e':
						case 'r':
						case 'u':
							++x;
							sbStr.append( c );
							++pos;
							break;
						default:
							constant = sbStr.toString();
							if ( "false".equals( constant ) ) {
								json_value_type = VT_BOOLEAN;
								booleanVal = false;
							}
							else if ( "true".equals( constant ) ) {
								json_value_type = VT_BOOLEAN;
								booleanVal = true;
							}
							else if ( "null".equals( constant ) ) {
								json_value_type = VT_NULL;
							}
							else {
								throw new JSONException( "Invalid JSON constant: '" + constant + "' at (" + y + ":" + x + ")!" );
							}
							state = rstate;
							break;
						}
						break;
					case S_NUMBER_MINUS:
						++x;
						switch ( c ) {
						case '0':
							sbStr.append( c );
							state = S_NUMBER_ZERO;
							break;
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							sbStr.append( c );
							state = S_NUMBER_INTEGER;
							break;
						default:
							throw new JSONException( "Invalid JSON number structure at (" + y + ":" + x + ")!" );
						}
						++pos;
						break;
					case S_NUMBER_INTEGER:
						switch ( c ) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							++x;
							sbStr.append( c );
							++pos;
							break;
						case '.':
							++x;
							sbStr.append( c );
							state = S_NUMBER_DECIMAL;
							++pos;
							break;
						case 'e':
							++x;
							sbStr.append( c );
							state = S_NUMBER_E;
							++pos;
							break;
						case 'E':
							++x;
							sbStr.append( c );
							state = S_NUMBER_E;
							++pos;
							break;
						default:
							json_value_type = VT_NUMBER;
							stringVal = sbStr.toString();
							state = rstate;
							break;
						}
						break;
					case S_NUMBER_ZERO:
						switch ( c ) {
						case '.':
							++x;
							sbStr.append( c );
							state = S_NUMBER_DECIMAL;
							++pos;
							break;
						case 'e':
							++x;
							sbStr.append( c );
							state = S_NUMBER_E;
							++pos;
							break;
						case 'E':
							++x;
							sbStr.append( c );
							state = S_NUMBER_E;
							++pos;
							break;
						default:
							json_value_type = VT_NUMBER;
							stringVal = sbStr.toString();
							state = rstate;
							break;
						}
						break;
					case S_NUMBER_DECIMAL:
						switch ( c ) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							++x;
							sbStr.append( c );
							state = S_NUMBER_DECIMALS;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON number structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_NUMBER_DECIMALS:
						switch ( c ) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							++x;
							sbStr.append( c );
							++pos;
							break;
						case 'e':
							++x;
							sbStr.append( c );
							state = S_NUMBER_E;
							++pos;
							break;
						case 'E':
							++x;
							sbStr.append( c );
							state = S_NUMBER_E;
							++pos;
							break;
						default:
							json_value_type = VT_NUMBER;
							stringVal = sbStr.toString();
							state = rstate;
						}
						break;
					case S_NUMBER_E:
						++x;
						switch ( c ) {
						case '+':
							sbStr.append( c );
							state = S_NUMBER_EXPONENT;
							break;
						case '-':
							sbStr.append( c );
							state = S_NUMBER_EXPONENT;
							break;
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							sbStr.append( c );
							state = S_NUMBER_EXPONENTS;
							break;
						default:
							throw new JSONException( "Invalid JSON number structure at (" + y + ":" + x + ")!" );
						}
						++pos;
						break;
					case S_NUMBER_EXPONENT:
						switch ( c ) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							++x;
							sbStr.append( c );
							state = S_NUMBER_EXPONENTS;
							++pos;
							break;
						default:
							throw new JSONException( "Invalid JSON number structure at (" + y + ":" + x + ")!" );
						}
						break;
					case S_NUMBER_EXPONENTS:
						switch ( c ) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							++x;
							sbStr.append( c );
							++pos;
							break;
						default:
							json_value_type = VT_NUMBER;
							stringVal = sbStr.toString();
							state = rstate;
						}
						break;
					case S_EOF:
						if ( !bExitAfterEnd ) {
							++x;
							switch ( c ) {
							case 0x20:
							case 0x09:
							case 0x0D:
								// Whitespace.
								break;
							case 0x0A:
								++y;
								x = 1;
								break;
							default:
								throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
							}
							++pos;
						}
						else {
							bExit = true;
							bLoop = false;
						}
					}
				}
				decoderContext.position( pos, y, x );
				if ( !bExit ) {
					decoderContext.buffer();
					pos = decoderContext.pos;
					limit = decoderContext.limit;
					bLoop = !(pos == limit);
				}
			}
		}
		catch (InstantiationException e) {
			throw new JSONException( e );
		}
		catch (IllegalAccessException e) {
			throw new JSONException( e );
		}
		if ( bArrayToRootObj ) {
			int idx;
			switch ( fieldMapping.arrayType ) {
			case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
				arrayOf_boolean = new boolean[ array.size() ];
				Iterator<Boolean> booleanIter = array.iterator();
				idx = 0;
				while ( booleanIter.hasNext() ) {
					arrayOf_boolean[ idx++ ] = booleanIter.next();
				}
				rootObj = (T)arrayOf_boolean;
				break;
			case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
				arrayOf_int = new int[ array.size() ];
				Iterator<Integer> intIter = array.iterator();
				idx = 0;
				while ( intIter.hasNext() ) {
					arrayOf_int[ idx++ ] = intIter.next();
				}
				rootObj = (T)arrayOf_int;
				break;
			case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
				arrayOf_long = new long[ array.size() ];
				Iterator<Long> longIter = array.iterator();
				idx = 0;
				while ( longIter.hasNext() ) {
					arrayOf_long[ idx++ ] = longIter.next();
				}
				rootObj = (T)arrayOf_long;
				break;
			case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
				arrayOf_float = new float[ array.size() ];
				Iterator<Float> floatIter = array.iterator();
				idx = 0;
				while ( floatIter.hasNext() ) {
					arrayOf_float[ idx++ ] = floatIter.next();
				}
				rootObj = (T)arrayOf_float;
				break;
			case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
				arrayOf_double = new double[ array.size() ];
				Iterator<Double> doubleIter = array.iterator();
				idx = 0;
				while ( doubleIter.hasNext() ) {
					arrayOf_double[ idx++ ] = doubleIter.next();
				}
				rootObj = (T)arrayOf_double;
				break;
			default:
				rootObj = (T)array.toArray( (Object[]) Array.newInstance( fieldMapping.clazz, array.size() ) );
				break;
			}
		}
		if ( (!bExitAfterEnd && curObj == null && curArr == null) || stack.size() > 0 ) {
			throw new JSONException( "Invalid JSON structure!" );
		}
		return rootObj;
	}

	/** Integer to hex char conversion table. */
	//private static char[] hexTab = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/** Hex char to integer conversion table. */
	private static int[] asciiHexTab = new int[256];

	/*
	 * Initialize ASCII hex table.
	 */
	static {
		String hex = "0123456789abcdef";
		for (int i=0; i<asciiHexTab.length; ++i) {
			asciiHexTab[i] = hex.indexOf(i);
		}
		hex = hex.toUpperCase();
		for (int i=0; i<hex.length(); ++i) {
			asciiHexTab[hex.charAt(i)] = i;
		}
	}

	private static Map<Integer, String> stateStr = new TreeMap<Integer, String>();

	static {
		stateStr.put( S_START, "S_START" );
		stateStr.put( S_OBJECT_BEGIN, "S_OBJECT_START" );
		stateStr.put( S_OBJECT_END, "S_OBJECT_END" );
		stateStr.put( S_ARRAY_BEGIN, "S_ARRAY_START" );
		stateStr.put( S_ARRAY_END, "S_ARRAY_END" );
		stateStr.put( S_OBJECT, "S_OBJECT" );
		stateStr.put( S_OBJECT_NAME, "S_OBJECT_NAME" );
		stateStr.put( S_OBJECT_COLON, "S_OBJECT_COLON" );
		stateStr.put( S_OBJECT_VALUE, "S_OBJECT_VALUE" );
		stateStr.put( S_OBJECT_VALUE_NEXT, "S_OBJECT_VALUE_NEXT" );
		stateStr.put( S_OBJECT_NAME_NEXT, "S_OBJECT_NAME_NEXT" );
		stateStr.put( S_ARRAY, "S_ARRAY" );
		stateStr.put( S_ARRAY_VALUE, "S_ARRAY_VALUE" );
		stateStr.put( S_ARRAY_NEXT, "S_ARRAY_NEXT" );
		stateStr.put( S_VALUE_START, "S_VALUE_START" );
		stateStr.put( S_STRING, "S_STRING" );
		stateStr.put( S_STRING_UNESCAPE, "S_STRING_UNESCAPE" );
		stateStr.put( S_STRING_UNHEX, "S_STRING_UNHEX" );
		stateStr.put( S_CONSTANT, "S_CONSTANT" );
		stateStr.put( S_NUMBER_MINUS, "S_NUMBER_MINUS" );
		stateStr.put( S_NUMBER_ZERO, "S_NUMBER_ZERO" );
		stateStr.put( S_NUMBER_INTEGER, "S_NUMBER_INTEGER" );
		stateStr.put( S_NUMBER_DECIMAL, "S_NUMBER_DECIMAL" );
		stateStr.put( S_NUMBER_DECIMALS, "S_NUMBER_DECIMALS" );
		stateStr.put( S_NUMBER_E, "S_NUMBER_E" );
		stateStr.put( S_NUMBER_EXPONENT, "S_NUMBER_EXPONENT" );
		stateStr.put( S_NUMBER_EXPONENTS, "S_NUMBER_EXPONENTS" );
		stateStr.put( S_EOF, "S_EOF" );
	}

	public static String typeString(Integer type)  {
		if ( type != null ) {
			switch ( type ) {
			case VT_NULL:
				return "VT_NULL";
			case VT_BOOLEAN:
				return "VT_BOOLEAN";
			case VT_STRING:
				return "VT_STRING";
			case VT_NUMBER:
				return "VT_NUMBER";
			case VT_OBJECT:
				return "VT_OBJECT";
			case VT_ARRAY:
				return "VT_ARRAY";
			default:
				return "Unknown(" + type + ")";
			}
		}
		else {
			return "null";
		}
	}

}
