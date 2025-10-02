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
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.antiaction.common.json.representation.JSONString;

/**
 * Serialize Java Object(s) into a JSON data stream.
 *
 * @author Nicholas
 * Created on 17/10/2013
 */
public class JSONStreamMarshaller {

	private static final int S_START = 0;
	private static final int S_OBJECT_BEGIN = 1;
	private static final int S_OBJECT_END = 2;
	private static final int S_ARRAY_BEGIN = 3;
	private static final int S_ARRAY_END = 4;
	private static final int S_LIST_BEGIN = 5;
	private static final int S_LIST_END = 6;
	private static final int S_MAP_BEGIN = 7;
	private static final int S_MAP_END = 8;
	private static final int S_SET_BEGIN = 9;
	private static final int S_OBJECT = 10;
	private static final int S_ARRAY = 11;
	private static final int S_LIST = 12;
	private static final int S_MAP = 13;
	private static final int S_SET = 14;

	/** Null string cached as bytes. */
	protected static final byte[] nullBytes = "null".getBytes();

	/** True string cached as bytes. */
	protected static final byte[] trueBytes = "true".getBytes();

	/** False string cached as bytes. */
	protected static final byte[] falseBytes = "false".getBytes();

	protected byte[] indentationArr;

	protected JSONObjectMappings objectMappings;

	protected Map<String, JSONObjectMapping> classMappings;

	private static final class StackEntry {
		int state;
		Object object;
		JSONObjectMapping objectMapping;
		JSONObjectFieldMapping[] fieldMappingsArr;
		int fieldMappingIdx;
		JSONObjectFieldMapping fieldMapping;
		Object array;
		Iterator<?> iterator;
		int arrayIdx;
		int arrayLen;
	}

	public JSONStreamMarshaller(JSONObjectMappings objectMappings) {
		this.objectMappings = objectMappings;
		this.classMappings = objectMappings.classMappings;
		indentationArr = new byte[ 32 ];
		Arrays.fill( indentationArr, (byte)' ' );
	}

	public <T> void toJSONText(T srcObj, JSONEncoder encoder, boolean bPretty, OutputStream out) throws IOException, JSONException {
		toJSONText( srcObj, null, encoder, bPretty, out );
	}

	@SuppressWarnings("unchecked")
	public <T> void toJSONText(T srcObj, JSONConverterAbstract[] converters, JSONEncoder encoder, boolean bPretty, OutputStream out) throws IOException, JSONException {
		Boolean booleanVal;
		Byte byteVal;
		Character charVal;
		Integer intVal;
		Long longVal;
		Float floatVal;
		Double doubleVal;
		Date dateVal;
		Timestamp timestampVal;
		BigInteger bigIntegerVal;
		BigDecimal bigDecimalVal;
		String stringVal;
		byte[] byteArray;
		Object tmpObject;
		List<?> tmpList;
		Map<String, ?> tmpMap;
		Entry<String, ?> tmpEntry;
		Set<?> tmpSet;

		Object tmpArray;
		boolean[] arrayOf_boolean;
		int[] arrayOf_int;
		long[] arrayOf_long;
		float[] arrayOf_float;
		double[] arrayOf_double;
		Boolean[] arrayOf_Boolean;
		Integer[] arrayOf_Integer;
		Long[] arrayOf_Long;
		Float[] arrayOf_Float;
		Double[] arrayOf_Double;
		BigInteger[] arrayOf_BigInteger;
		BigDecimal[] arrayOf_BigDecimal;
		String[] arrayOf_String;
		Object[] arrayOf_Object;

		encoder.init( out );

		Object object = srcObj;
		Object array = null;
		Iterator<?> iterator = null;

		JSONObjectMapping objectMapping = classMappings.get( object.getClass().getName() );
		if ( objectMapping == null ) {
			throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
		}
		if ( objectMapping.converters == true && converters == null ) {
			throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
		}

		JSONObjectFieldMapping[] fieldMappingsArr = null;
		int fieldMappingIdx = 0;
		JSONObjectFieldMapping fieldMapping = null;
		int arrayIdx = 0;
		int arrayLen = 0;

		LinkedList<StackEntry> stack = new LinkedList<StackEntry>();
		StackEntry stackEntry;

		int state = S_START;
		int indentation = 0;
		boolean bLoop = true;
		boolean bFieldLoop;
		try {
			while ( bLoop ) {
				// debug
				//System.out.println( stateStr.get( state ) + " (" + state + ")" );
				switch ( state ) {
				case S_START:
					switch ( objectMapping.type ) {
					case JSONObjectMapping.OMT_OBJECT:
						state = S_OBJECT_BEGIN;
						break;
					case JSONObjectMapping.OMT_ARRAY:
						fieldMapping = objectMapping.fieldMapping;
						array = srcObj;
						//arrayIdx = 0;
						arrayLen = Array.getLength( array );
						state = S_ARRAY_BEGIN;
						break;
					default:
						throw new JSONException( "Invalid object mapping class!" );
					}
					break;
				case S_OBJECT_BEGIN:
					if ( bPretty ) {
						encoder.write( "{\n" );
						indentation += 2;
						if ( indentation > indentationArr.length ) {
							byte[] newIndentationArr = new byte[ indentationArr.length * 2 ];
							Arrays.fill( newIndentationArr, (byte)' ' );
							indentationArr = newIndentationArr;
						}
					}
					else {
						encoder.write( '{' );
					}
					fieldMappingsArr = objectMapping.fieldMappingsArr;
					fieldMappingIdx = 0;
					state = S_OBJECT;
					break;
				case S_OBJECT_END:
					if ( bPretty ) {
						indentation -= 2;
						encoder.write( '\n' );
						encoder.write( indentationArr, 0, indentation );
					}
					encoder.write( '}' );
					if ( stack.size() > 0 ) {
						stackEntry = stack.removeLast();
						state = stackEntry.state;
						object = stackEntry.object;
						objectMapping = stackEntry.objectMapping;
						fieldMappingsArr = stackEntry.fieldMappingsArr;
						fieldMappingIdx = stackEntry.fieldMappingIdx;
						fieldMapping = stackEntry.fieldMapping;
						array = stackEntry.array;
						iterator = stackEntry.iterator;
						arrayIdx = stackEntry.arrayIdx;
						arrayLen = stackEntry.arrayLen;
					}
					else {
						bLoop = false;
					}
					break;
				case S_ARRAY_BEGIN:
					if ( bPretty ) {
						encoder.write( '[' );
						if ( arrayLen > 0 ) {
							encoder.write( '\n' );
							indentation += 2;
							if ( indentation > indentationArr.length ) {
								byte[] newIndentationArr = new byte[ indentationArr.length * 2 ];
								Arrays.fill( newIndentationArr, (byte)' ' );
								indentationArr = newIndentationArr;
							}
						}
					}
					else {
						encoder.write( '[' );
					}
					if ( arrayLen > 0 ) {
						state = S_ARRAY;
					}
					else {
						state = S_ARRAY_END;
					}
					break;
				case S_ARRAY_END:
					if ( bPretty ) {
						if ( arrayLen > 0 ) {
							indentation -= 2;
							encoder.write( '\n' );
							encoder.write( indentationArr, 0, indentation );
						}
					}
					encoder.write( ']' );
					if ( stack.size() > 0 ) {
						stackEntry = stack.removeLast();
						state = stackEntry.state;
						object = stackEntry.object;
						objectMapping = stackEntry.objectMapping;
						fieldMappingsArr = stackEntry.fieldMappingsArr;
						fieldMappingIdx = stackEntry.fieldMappingIdx;
						fieldMapping = stackEntry.fieldMapping;
						array = stackEntry.array;
						iterator = stackEntry.iterator;
						arrayIdx = stackEntry.arrayIdx;
						arrayLen = stackEntry.arrayLen;
					}
					else {
						bLoop = false;
					}
					break;
				case S_LIST_BEGIN:
					if ( bPretty ) {
						encoder.write( '[' );
						if ( arrayLen > 0 ) {
							encoder.write( '\n' );
							indentation += 2;
							if ( indentation > indentationArr.length ) {
								byte[] newIndentationArr = new byte[ indentationArr.length * 2 ];
								Arrays.fill( newIndentationArr, (byte)' ' );
								indentationArr = newIndentationArr;
							}
						}
					}
					else {
						encoder.write( '[' );
					}
					if ( arrayLen > 0 ) {
						state = S_LIST;
					}
					else {
						state = S_LIST_END;
					}
					break;
				case S_LIST_END:
					if ( bPretty ) {
						if ( arrayLen > 0 ) {
							indentation -= 2;
							encoder.write( '\n' );
							encoder.write( indentationArr, 0, indentation );
						}
					}
					encoder.write( ']' );
					if ( stack.size() > 0 ) {
						stackEntry = stack.removeLast();
						state = stackEntry.state;
						object = stackEntry.object;
						objectMapping = stackEntry.objectMapping;
						fieldMappingsArr = stackEntry.fieldMappingsArr;
						fieldMappingIdx = stackEntry.fieldMappingIdx;
						fieldMapping = stackEntry.fieldMapping;
						array = stackEntry.array;
						iterator = stackEntry.iterator;
						arrayIdx = stackEntry.arrayIdx;
						arrayLen = stackEntry.arrayLen;
					}
					else {
						bLoop = false;
					}
					break;
				case S_MAP_BEGIN:
					if ( bPretty ) {
						encoder.write( "{\n" );
						indentation += 2;
						if ( indentation > indentationArr.length ) {
							byte[] newIndentationArr = new byte[ indentationArr.length * 2 ];
							Arrays.fill( newIndentationArr, (byte)' ' );
							indentationArr = newIndentationArr;
						}
					}
					else {
						encoder.write( '{' );
					}
					//fieldMappingsArr = objectMapping.fieldMappingsArr;
					//fieldMappingIdx = 0;
					state = S_MAP;
					break;
				case S_MAP_END:
					if ( bPretty ) {
						indentation -= 2;
						encoder.write( '\n' );
						encoder.write( indentationArr, 0, indentation );
					}
					encoder.write( '}' );
					if ( stack.size() > 0 ) {
						stackEntry = stack.removeLast();
						state = stackEntry.state;
						object = stackEntry.object;
						objectMapping = stackEntry.objectMapping;
						fieldMappingsArr = stackEntry.fieldMappingsArr;
						fieldMappingIdx = stackEntry.fieldMappingIdx;
						fieldMapping = stackEntry.fieldMapping;
						array = stackEntry.array;
						iterator = stackEntry.iterator;
						arrayIdx = stackEntry.arrayIdx;
						arrayLen = stackEntry.arrayLen;
					}
					else {
						bLoop = false;
					}
					break;
				case S_SET_BEGIN:
					if ( bPretty ) {
						encoder.write( "[\n" );
						indentation += 2;
						if ( indentation > indentationArr.length ) {
							byte[] newIndentationArr = new byte[ indentationArr.length * 2 ];
							Arrays.fill( newIndentationArr, (byte)' ' );
							indentationArr = newIndentationArr;
						}
					}
					else {
						encoder.write( '[' );
					}
					//fieldMappingsArr = objectMapping.fieldMappingsArr;
					//fieldMappingIdx = 0;
					state = S_SET;
					break;
				case S_OBJECT:
					bFieldLoop = true;
					while ( bFieldLoop ) {
						if ( fieldMappingIdx < fieldMappingsArr.length ) {
							if ( fieldMappingIdx > 0 ) {
								if ( bPretty ) {
									encoder.write( ",\n" );
								}
								else {
									encoder.write( ',' );
								}
							}
							if ( bPretty ) {
								encoder.write( indentationArr, 0, indentation );
							}
							fieldMapping = fieldMappingsArr[ fieldMappingIdx++ ];
							encoder.write( '"' );
							encoder.write( fieldMapping.jsonName );
							encoder.write( '"' );
							if ( bPretty ) {
								encoder.write( ": " );
							}
							else {
								encoder.write( ':' );
							}
							switch ( fieldMapping.type ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								booleanVal = fieldMapping.field.getBoolean( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									/*
									booleanVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
									if ( booleanVal == null ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
									*/
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( booleanVal ) {
									encoder.write( trueBytes );
								}
								else {
									encoder.write( falseBytes );
								}
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_BYTE:
								byteVal = fieldMapping.field.getByte( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									/*
									byteVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, byteVal );
									if ( byteVal == null ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
									*/
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								encoder.write( byteVal.toString().getBytes() );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_CHAR:
								charVal = fieldMapping.field.getChar( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									/*
									charVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, charVal );
									if ( charVal == null ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
									*/
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								encoder.write( Integer.toString( charVal ).getBytes() );
								break;

							case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
								intVal = fieldMapping.field.getInt( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									/*
									intVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
									if ( intVal == null ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
									*/
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								encoder.write( intVal.toString().getBytes() );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
								longVal = fieldMapping.field.getLong( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									/*
									longVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
									if ( longVal == null ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
									*/
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								encoder.write( longVal.toString().getBytes() );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
								floatVal = fieldMapping.field.getFloat( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									/*
									floatVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
									if ( floatVal == null ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
									*/
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								encoder.write( floatVal.toString().getBytes() );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
								doubleVal = fieldMapping.field.getDouble( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									/*
									doubleVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
									if ( doubleVal == null ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
									*/
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								encoder.write( doubleVal.toString().getBytes() );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								booleanVal = (Boolean)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//booleanVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( booleanVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( booleanVal != null ) {
									if ( booleanVal ) {
										encoder.write( trueBytes );
									}
									else {
										encoder.write( falseBytes );
									}
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_BYTE:
								byteVal = (Byte)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//byteVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, byteVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( byteVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( byteVal != null) {
									encoder.write( byteVal.toString().getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_CHARACTER:
								charVal = (Character)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//charVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, charVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( charVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( charVal != null) {
									encoder.write( Integer.toString( charVal ).getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_INTEGER:
								intVal = (Integer)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//intVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( intVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( intVal != null) {
									encoder.write( intVal.toString().getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_LONG:
								longVal = (Long)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//longVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( longVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( longVal != null ) {
									encoder.write( longVal.toString().getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_FLOAT:
								floatVal = (Float)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//floatVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( floatVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( floatVal != null ) {
									encoder.write( floatVal.toString().getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_DOUBLE:
								doubleVal = (Double)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//doubleVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( doubleVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( doubleVal != null ) {
									encoder.write( doubleVal.toString().getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_DATE:
								dateVal = (Date)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//dateVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, dateVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( dateVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( dateVal != null ) {
									encoder.write( Long.toString( dateVal.getTime() ).getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_TIMESTAMP:
								timestampVal = (Timestamp)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//timestampVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, timestampVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( timestampVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( timestampVal != null ) {
									encoder.write( Long.toString( timestampVal.getTime() ).getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_BIGINTEGER:
								bigIntegerVal = (BigInteger)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//bigIntegerVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigIntegerVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( bigIntegerVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( bigIntegerVal != null ) {
									encoder.write( bigIntegerVal.toString().getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_BIGDECIMAL:
								bigDecimalVal = (BigDecimal)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//bigDecimalVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigDecimalVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( bigDecimalVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( bigDecimalVal != null ) {
									encoder.write( bigDecimalVal.toString().getBytes() );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_STRING:
								stringVal = (String)fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//strVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, strVal );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( stringVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( stringVal != null ) {
									encoder.write( '"' );
									JSONString.escape( encoder, stringVal );
									encoder.write( '"' );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_BYTEARRAY:
								byteArray = (byte[])fieldMapping.field.get( object );
								if ( fieldMapping.converterId != -1 ) {
									// TODO
									//byteArray = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, byteArray );
									throw new JSONException( "Converter unsupported in stream marshaller." );
								}
								if ( byteArray == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								if ( byteArray != null ) {
									encoder.write( '"' );
									JSONString.escape( encoder, byteArray );
									encoder.write( '"' );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_OBJECT:
								tmpObject = (Object)fieldMapping.field.get( object );
								if ( tmpObject != null ) {
									stackEntry = new StackEntry();
									stackEntry.state = state;
									stackEntry.object = object;
									stackEntry.objectMapping = objectMapping;
									stackEntry.fieldMappingsArr = fieldMappingsArr;
									stackEntry.fieldMappingIdx = fieldMappingIdx;
									stackEntry.fieldMapping = fieldMapping;
									stack.add( stackEntry );
									object = tmpObject;
									objectMapping = classMappings.get( object.getClass().getName() );
									if ( objectMapping == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
									}
									if ( objectMapping.converters == true && converters == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
									}
									state = S_OBJECT_BEGIN;
									bFieldLoop = false;
								}
								else if ( !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_ARRAY:
								tmpArray = fieldMapping.field.get( object );
								if ( tmpArray != null ) {
									stackEntry = new StackEntry();
									stackEntry.state = state;
									stackEntry.object = object;
									stackEntry.objectMapping = objectMapping;
									stackEntry.fieldMappingsArr = fieldMappingsArr;
									stackEntry.fieldMappingIdx = fieldMappingIdx;
									stackEntry.fieldMapping = fieldMapping;
									stack.add( stackEntry );
									array = tmpArray;
									arrayIdx = 0;
									arrayLen = Array.getLength( array );
									/*
									objectMapping = classMappings.get( object.getClass().getName() );
									if ( objectMapping == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
									}
									if ( objectMapping.converters == true && converters == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
									}
									*/
									state = S_ARRAY_BEGIN;
									bFieldLoop = false;
								}
								else if ( !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_LIST:
								tmpList = (List<?>)fieldMapping.field.get( object );
								if ( tmpList != null ) {
									stackEntry = new StackEntry();
									stackEntry.state = state;
									stackEntry.object = object;
									stackEntry.objectMapping = objectMapping;
									stackEntry.fieldMappingsArr = fieldMappingsArr;
									stackEntry.fieldMappingIdx = fieldMappingIdx;
									stackEntry.fieldMapping = fieldMapping;
									stack.add( stackEntry );
									iterator = tmpList.iterator();
									arrayIdx = 0;
									arrayLen = tmpList.size();
									/*
									objectMapping = classMappings.get( object.getClass().getName() );
									if ( objectMapping == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
									}
									if ( objectMapping.converters == true && converters == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
									}
									*/
									state = S_LIST_BEGIN;
									bFieldLoop = false;
								}
								else if ( !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_MAP:
								tmpMap = (Map<String, ?>)fieldMapping.field.get( object );
								if ( tmpMap != null ) {
									stackEntry = new StackEntry();
									stackEntry.state = state;
									stackEntry.object = object;
									stackEntry.objectMapping = objectMapping;
									stackEntry.fieldMappingsArr = fieldMappingsArr;
									stackEntry.fieldMappingIdx = fieldMappingIdx;
									stackEntry.fieldMapping = fieldMapping;
									stack.add( stackEntry );
									iterator = tmpMap.entrySet().iterator();
									arrayIdx = 0;
									arrayLen = tmpMap.size();
									/*
									objectMapping = classMappings.get( object.getClass().getName() );
									if ( objectMapping == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
									}
									if ( objectMapping.converters == true && converters == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
									}
									*/
									state = S_MAP_BEGIN;
									bFieldLoop = false;
								}
								else if ( !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								else {
									encoder.write( nullBytes );
								}
								break;
							case JSONObjectMappingConstants.T_SET:
								// TODO Work in progress!
								tmpSet = (Set<Object>)fieldMapping.field.get( object );
								if (tmpSet != null) {
									stackEntry = new StackEntry();
									stackEntry.state = state;
									stackEntry.object = object;
									stackEntry.objectMapping = objectMapping;
									stackEntry.fieldMappingsArr = fieldMappingsArr;
									stackEntry.fieldMappingIdx = fieldMappingIdx;
									stackEntry.fieldMapping = fieldMapping;
									stack.add( stackEntry );
									iterator = tmpSet.iterator();
									arrayIdx = 0;
									arrayLen = tmpSet.size();
									/*
									objectMapping = classMappings.get( object.getClass().getName() );
									if ( objectMapping == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
									}
									if ( objectMapping.converters == true && converters == null ) {
										throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
									}
									*/
									state = S_SET_BEGIN;
									bFieldLoop = false;
								}
								else if ( !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								else {
									encoder.write( nullBytes );
								}
								//throw new UnsupportedOperationException();
								break;
							default:
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported object type: " + JSONObjectMappingConstants.typeString( fieldMapping.type ) );
							}
						}
						else {
							state = S_OBJECT_END;
							bFieldLoop = false;
						}
					}
					break;
				case S_ARRAY:
					switch ( fieldMapping.arrayType ) {
					case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
						arrayOf_boolean = (boolean[])array;
						while ( arrayIdx < arrayLen ) {
							booleanVal = arrayOf_boolean[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								/*
								booleanVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
								*/
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( booleanVal ) {
								encoder.write( trueBytes );
							}
							else {
								encoder.write( falseBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
						arrayOf_int = (int[])array;
						while ( arrayIdx < arrayLen ) {
							intVal = arrayOf_int[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								/*
								intVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
								if ( intVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
								*/
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							encoder.write( intVal.toString().getBytes() );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
						arrayOf_long = (long[])array;
						while ( arrayIdx < arrayLen ) {
							longVal = arrayOf_long[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								/*
								longVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
								if ( longVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
								*/
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							encoder.write( longVal.toString().getBytes() );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
						arrayOf_float = (float[])array;
						while ( arrayIdx < arrayLen ) {
							floatVal = arrayOf_float[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								/*
								floatVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
								if ( floatVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
								*/
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							encoder.write( floatVal.toString().getBytes() );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
						arrayOf_double = (double[])array;
						while ( arrayIdx < arrayLen ) {
							doubleVal = arrayOf_double[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								/*
								doubleVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
								if ( doubleVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
								*/
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							encoder.write( doubleVal.toString().getBytes() );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BOOLEAN:
						arrayOf_Boolean = (Boolean[])array;
						while ( arrayIdx < arrayLen ) {
							booleanVal = arrayOf_Boolean[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//booleanVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( booleanVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( booleanVal != null ) {
								if ( booleanVal ) {
									encoder.write( trueBytes );
								}
								else {
									encoder.write( falseBytes );
								}
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_INTEGER:
						arrayOf_Integer = (Integer[])array;
						while ( arrayIdx < arrayLen ) {
							intVal = arrayOf_Integer[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//intVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( intVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( intVal != null ) {
								encoder.write( intVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_LONG:
						arrayOf_Long = (Long[])array;
						while ( arrayIdx < arrayLen ) {
							longVal = arrayOf_Long[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//longVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( longVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( longVal != null ) {
								encoder.write( longVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_FLOAT:
						arrayOf_Float = (Float[])array;
						while ( arrayIdx < arrayLen ) {
							floatVal = arrayOf_Float[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//floatVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( floatVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( floatVal != null ) {
								encoder.write( floatVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_DOUBLE:
						arrayOf_Double = (Double[])array;
						while ( arrayIdx < arrayLen ) {
							doubleVal = arrayOf_Double[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//doubleVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( doubleVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( doubleVal != null ) {
								encoder.write( doubleVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BIGINTEGER:
						arrayOf_BigInteger = (BigInteger[])array;
						while ( arrayIdx < arrayLen ) {
							bigIntegerVal = arrayOf_BigInteger[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//bigIntegerVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigIntegerVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bigIntegerVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( bigIntegerVal != null ) {
								encoder.write( bigIntegerVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BIGDECIMAL:
						arrayOf_BigDecimal = (BigDecimal[])array;
						while ( arrayIdx < arrayLen ) {
							bigDecimalVal = arrayOf_BigDecimal[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//bigDecimalVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigDecimalVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bigDecimalVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( bigDecimalVal != null ) {
								encoder.write( bigDecimalVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_STRING:
						arrayOf_String = (String[])array;
						while ( arrayIdx < arrayLen ) {
							stringVal = arrayOf_String[ arrayIdx++ ];
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//strVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, strVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( stringVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( stringVal != null ) {
								encoder.write( '"' );
								JSONString.escape( encoder, stringVal );
								encoder.write( '"' );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_OBJECT:
						arrayOf_Object = (Object[])array;
						if ( arrayIdx < arrayLen ) {
							tmpObject = arrayOf_Object[ arrayIdx++ ];
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( tmpObject != null ) {
								stackEntry = new StackEntry();
								stackEntry.state = state;
								stackEntry.object = object;
								stackEntry.objectMapping = objectMapping;
								stackEntry.fieldMappingsArr = fieldMappingsArr;
								stackEntry.fieldMappingIdx = fieldMappingIdx;
								stackEntry.fieldMapping = fieldMapping;
								stackEntry.array = array;
								stackEntry.arrayIdx = arrayIdx;
								stackEntry.arrayLen = arrayLen;
								stack.add( stackEntry );
								object = tmpObject;
								objectMapping = classMappings.get( object.getClass().getName() );
								if ( objectMapping == null ) {
									throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
								}
								if ( objectMapping.converters == true && converters == null ) {
									throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
								}
								state = S_OBJECT_BEGIN;
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							else {
								encoder.write( nullBytes );
							}
							/*
							objectVal = arrayOf_Object[ arrayIdx ];
							if ( objectVal != null ) {
								objectVal = toJSON( objectVal, converters );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							json_array.add( json_value );
							*/
						}
						else {
							state = S_ARRAY_END;
						}
						break;
					default:
						throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported array type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayType ) );
					}
					break;
				case S_LIST:
					switch ( fieldMapping.parametrizedObjectTypes[ 0 ] ) {
					case JSONObjectMappingConstants.T_BOOLEAN:
						Iterator<Boolean> iteratorOf_Boolean = (Iterator<Boolean>)iterator;
						while ( iteratorOf_Boolean.hasNext() ) {
							++arrayIdx;
							booleanVal = iteratorOf_Boolean.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//booleanVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( booleanVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( booleanVal != null ) {
								if ( booleanVal ) {
									encoder.write( trueBytes );
								}
								else {
									encoder.write( falseBytes );
								}
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					case JSONObjectMappingConstants.T_INTEGER:
						Iterator<Integer> iteratorOf_Integer = (Iterator<Integer>)iterator;
						while ( iteratorOf_Integer.hasNext() ) {
							++arrayIdx;
							intVal = iteratorOf_Integer.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//intVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( intVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( intVal != null ) {
								encoder.write( intVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					case JSONObjectMappingConstants.T_LONG:
						Iterator<Long> iteratorOf_Long = (Iterator<Long>)iterator;
						while ( iteratorOf_Long.hasNext() ) {
							++arrayIdx;
							longVal = iteratorOf_Long.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//longVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( longVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( longVal != null ) {
								encoder.write( longVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					case JSONObjectMappingConstants.T_FLOAT:
						Iterator<Float> iteratorOf_Float = (Iterator<Float>)iterator;
						while ( iteratorOf_Float.hasNext() ) {
							++arrayIdx;
							floatVal = iteratorOf_Float.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//floatVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( floatVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( floatVal != null ) {
								encoder.write( floatVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					case JSONObjectMappingConstants.T_DOUBLE:
						Iterator<Double> iteratorOf_Double = (Iterator<Double>)iterator;
						while ( iteratorOf_Double.hasNext() ) {
							++arrayIdx;
							doubleVal = iteratorOf_Double.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//doubleVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( doubleVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( doubleVal != null ) {
								encoder.write( doubleVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					case JSONObjectMappingConstants.T_BIGINTEGER:
						Iterator<BigInteger> iteratorOf_BigInteger = (Iterator<BigInteger>)iterator;
						while ( iteratorOf_BigInteger.hasNext() ) {
							++arrayIdx;
							bigIntegerVal = iteratorOf_BigInteger.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//bigIntegerVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigIntegerVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bigIntegerVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( bigIntegerVal != null ) {
								encoder.write( bigIntegerVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					case JSONObjectMappingConstants.T_BIGDECIMAL:
						Iterator<BigDecimal> iteratorOf_BigDecimal = (Iterator<BigDecimal>)iterator;
						while ( iteratorOf_BigDecimal.hasNext() ) {
							++arrayIdx;
							bigDecimalVal = iteratorOf_BigDecimal.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//bigDecimalVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigDecimalVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( bigDecimalVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( bigDecimalVal != null ) {
								encoder.write( bigDecimalVal.toString().getBytes() );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					case JSONObjectMappingConstants.T_STRING:
						Iterator<String> iteratorOf_String = (Iterator<String>)iterator;
						while ( iteratorOf_String.hasNext() ) {
							++arrayIdx;
							stringVal = iteratorOf_String.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//strVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, strVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( stringVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( stringVal != null ) {
								encoder.write( '"' );
								JSONString.escape( encoder, stringVal );
								encoder.write( '"' );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					case JSONObjectMappingConstants.T_OBJECT:
						if ( iterator.hasNext() ) {
							++arrayIdx;
							tmpObject = iterator.next();
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( tmpObject != null ) {
								stackEntry = new StackEntry();
								stackEntry.state = state;
								stackEntry.object = object;
								stackEntry.objectMapping = objectMapping;
								stackEntry.fieldMappingsArr = fieldMappingsArr;
								stackEntry.fieldMappingIdx = fieldMappingIdx;
								stackEntry.fieldMapping = fieldMapping;
								stackEntry.iterator = iterator;
								stackEntry.arrayIdx = arrayIdx;
								stackEntry.arrayLen = arrayLen;
								stack.add( stackEntry );
								object = tmpObject;
								objectMapping = classMappings.get( object.getClass().getName() );
								if ( objectMapping == null ) {
									throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
								}
								if ( objectMapping.converters == true && converters == null ) {
									throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
								}
								state = S_OBJECT_BEGIN;
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							else {
								encoder.write( nullBytes );
							}
							/*
							objectVal = arrayOf_Object[ arrayIdx ];
							if ( objectVal != null ) {
								objectVal = toJSON( objectVal, converters );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							json_array.add( json_value );
							*/
						}
						else {
							state = S_LIST_END;
						}
						break;
					default:
						throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported list type." + JSONObjectMappingConstants.typeString( fieldMapping.parametrizedObjectTypes[ 0 ] ) );
					}
					break;
				case S_MAP:
					switch ( fieldMapping.parametrizedObjectTypes[ 1 ] ) {
					case JSONObjectMappingConstants.T_OBJECT:
						if ( iterator.hasNext() ) {
							++arrayIdx;
							tmpEntry = (Entry<String, ?>)iterator.next();
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							// TODO Is this string escaped correctly?
							encoder.write( '"' );
							encoder.write( tmpEntry.getKey() );
							encoder.write( '"' );
							encoder.write( ":" );
							if ( bPretty ) {
								encoder.write( " " );
							}
							tmpObject = tmpEntry.getValue();
							if ( tmpObject != null ) {
								stackEntry = new StackEntry();
								stackEntry.state = state;
								stackEntry.object = object;
								stackEntry.objectMapping = objectMapping;
								stackEntry.fieldMappingsArr = fieldMappingsArr;
								stackEntry.fieldMappingIdx = fieldMappingIdx;
								stackEntry.fieldMapping = fieldMapping;
								stackEntry.iterator = iterator;
								stackEntry.arrayIdx = arrayIdx;
								stackEntry.arrayLen = arrayLen;
								stack.add( stackEntry );
								object = tmpObject;
								objectMapping = classMappings.get( object.getClass().getName() );
								if ( objectMapping == null ) {
									throw new JSONException( "Class '" + object.getClass().getName() + "' not registered." );
								}
								if ( objectMapping.converters == true && converters == null ) {
									throw new JSONException( "Class '" + object.getClass().getName() + "' may required converters!" );
								}
								state = S_OBJECT_BEGIN;
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							else {
								encoder.write( nullBytes );
							}
							/*
							objectVal = arrayOf_Object[ arrayIdx ];
							if ( objectVal != null ) {
								objectVal = toJSON( objectVal, converters );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							json_array.add( json_value );
							*/
						}
						else {
							state = S_MAP_END;
						}
						break;
					default:
						throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported map type." + JSONObjectMappingConstants.typeString( fieldMapping.parametrizedObjectTypes[ 0 ] ) );
					}
					break;
				case S_SET:
					switch ( fieldMapping.parametrizedObjectTypes[ 0 ] ) {
					case JSONObjectMappingConstants.T_STRING:
						Iterator<String> iteratorOf_String = (Iterator<String>)iterator;
						while ( iteratorOf_String.hasNext() ) {
							++arrayIdx;
							stringVal = iteratorOf_String.next();
							if ( fieldMapping.converterId != -1 ) {
								// TODO
								//strVal = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, strVal );
								throw new JSONException( "Converter unsupported in stream marshaller." );
							}
							if ( stringVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							if ( bPretty ) {
								if ( arrayIdx > 1 ) {
									encoder.write( ",\n" );
								}
								encoder.write( indentationArr, 0, indentation );
							}
							else {
								if ( arrayIdx > 1 ) {
									encoder.write( ',' );
								}
							}
							if ( stringVal != null ) {
								encoder.write( '"' );
								JSONString.escape( encoder, stringVal );
								encoder.write( '"' );
							}
							else {
								encoder.write( nullBytes );
							}
						}
						state = S_LIST_END;
						break;
					default:
						throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported list type." + JSONObjectMappingConstants.typeString( fieldMapping.parametrizedObjectTypes[ 0 ] ) );
					}
					break;
				}
			}
		}
		catch (IllegalAccessException e) {
			throw new JSONException( e );
		}

		encoder.close();
	}

	private static Map<Integer, String> stateStr = new TreeMap<Integer, String>();

	static {
		stateStr.put( S_START, "S_START" );
		stateStr.put( S_OBJECT_BEGIN, "S_OBJECT_START" );
		stateStr.put( S_OBJECT_END, "S_OBJECT_END" );
		stateStr.put( S_ARRAY_BEGIN, "S_ARRAY_START" );
		stateStr.put( S_ARRAY_END, "S_ARRAY_END" );
		stateStr.put( S_LIST_BEGIN, "S_LIST_BEGIN" );
		stateStr.put( S_LIST_END, "S_LIST_END" );
		stateStr.put( S_MAP_BEGIN, "S_MAP_BEGIN" );
		stateStr.put( S_MAP_END, "S_MAP_END" );
		stateStr.put( S_OBJECT, "S_OBJECT" );
		stateStr.put( S_ARRAY, "S_ARRAY" );
		stateStr.put( S_LIST, "S_LIST" );
		stateStr.put( S_MAP, "S_MAP" );
	}

}
