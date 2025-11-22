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

package com.antiaction.common.json.representation;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Map;

import com.antiaction.common.json.JSONConverterAbstract;
import com.antiaction.common.json.JSONException;
import com.antiaction.common.json.JSONObjectFieldMapping;
import com.antiaction.common.json.JSONObjectMapping;
import com.antiaction.common.json.JSONObjectMappingConstants;
import com.antiaction.common.json.JSONObjectMappings;

/**
 * Serialize Java Object(s) into a JSON structure.
 *
 * @author Nicholas
 * Created on 24/07/2013
 */
public class JSONStructureMarshaller {

	private static final int S_START = 0;
	private static final int S_OBJECT_BEGIN = 1;
	private static final int S_OBJECT_END = 2;
	private static final int S_ARRAY_BEGIN = 3;
	private static final int S_ARRAY_END = 4;
	private static final int S_OBJECT_VALUE = 5;
	private static final int S_OBJECT = 6;
	private static final int S_ARRAY_VALUE = 7;
	private static final int S_ARRAY = 8;

	protected JSONObjectMappings objectMappings;

	protected Map<String, JSONObjectMapping> classMappings;

	private static final class StackEntry {
		int state;
		JSONCollection json_struct;
		Object object;
		JSONObjectMapping objectMapping;
		JSONObjectFieldMapping[] fieldMappingsArr;
		int fieldMappingIdx;
		JSONObjectFieldMapping fieldMapping;
		Object array;
		int arrayIdx;
		int arrayLen;
	}

	public JSONStructureMarshaller(JSONObjectMappings objectMappings) {
		this.objectMappings = objectMappings;
		this.classMappings = objectMappings.classMappings;
	}

	public <T> JSONCollection toJSONStructure(T srcObj) throws JSONException {
		return toJSONStructure( srcObj, null );
	}

	public <T> JSONCollection toJSONStructure(T srcObj, JSONConverterAbstract[] converters) throws JSONException {
		Boolean booleanVal;
		Integer intVal;
		Long longVal;
		Float floatVal;
		Double doubleVal;
		BigInteger bigIntegerVal;
		BigDecimal bigDecimalVal;
		String stringVal;
		byte[] byteArray;
		Object tmpObject;
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

		Object object = srcObj;
		Object array = null;

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
		JSONValue json_value = null;
		JSONCollection json_struct = null;
		int arrayIdx = 0;
		int arrayLen = 0;

		LinkedList<StackEntry> stack = new LinkedList<StackEntry>();
		StackEntry stackEntry;

		int state = S_START;
		boolean bLoop = true;
		boolean bFieldLoop;
		try {
			while ( bLoop ) {
				switch ( state ) {
				case S_START:
					switch ( objectMapping.typeId ) {
					case JSONObjectMapping.OMT_OBJECT:
						json_struct = new JSONObject();
						state = S_OBJECT_BEGIN;
						break;
					case JSONObjectMapping.OMT_ARRAY:
						json_struct = new JSONArray();
						array = object;
						arrayIdx = 0;
						arrayLen = Array.getLength( array );
						fieldMapping = objectMapping.fieldMapping;
						state = S_ARRAY_BEGIN;
						break;
					default:
						throw new JSONException( "Invalid object mapping type: " + objectMapping.typeId );
					}
					break;
				case S_OBJECT_BEGIN:
					fieldMappingsArr = objectMapping.fieldMappingsArr;
					fieldMappingIdx = 0;
					state = S_OBJECT;
					break;
				case S_OBJECT_END:
					if ( stack.size() > 0 ) {
						json_value = json_struct;
						stackEntry = stack.removeLast();
						state = stackEntry.state;
						json_struct = stackEntry.json_struct;
						object = stackEntry.object;
						objectMapping = stackEntry.objectMapping;
						fieldMappingsArr = stackEntry.fieldMappingsArr;
						fieldMappingIdx = stackEntry.fieldMappingIdx;
						fieldMapping = stackEntry.fieldMapping;
						array = stackEntry.array;
						arrayIdx = stackEntry.arrayIdx;
						arrayLen = stackEntry.arrayLen;
					}
					else {
						bLoop = false;
					}
					break;
				case S_ARRAY_BEGIN:
					state = S_ARRAY;
					break;
				case S_ARRAY_END:
					if ( stack.size() > 0 ) {
						json_value = json_struct;
						stackEntry = stack.removeLast();
						state = stackEntry.state;
						json_struct = stackEntry.json_struct;
						object = stackEntry.object;
						objectMapping = stackEntry.objectMapping;
						fieldMappingsArr = stackEntry.fieldMappingsArr;
						fieldMappingIdx = stackEntry.fieldMappingIdx;
						fieldMapping = stackEntry.fieldMapping;
						array = stackEntry.array;
						arrayIdx = stackEntry.arrayIdx;
						arrayLen = stackEntry.arrayLen;
					}
					else {
						bLoop = false;
					}
					break;
				case S_OBJECT_VALUE:
					json_struct.put( fieldMapping.jsonName, json_value );
					state = S_OBJECT;
				case S_OBJECT:
					bFieldLoop = true;
					while ( bFieldLoop ) {
						if ( fieldMappingIdx < fieldMappingsArr.length ) {
							fieldMapping = fieldMappingsArr[ fieldMappingIdx++ ];
							switch ( fieldMapping.typeId ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								booleanVal = fieldMapping.field.getBoolean( object );
								if ( fieldMapping.converterId == -1 ) {
									json_value = JSONBoolean.Boolean( booleanVal );
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
									if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
								intVal = fieldMapping.field.getInt( object );
								if ( fieldMapping.converterId == -1 ) {
									json_value = JSONNumber.Integer( intVal );
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
									if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
								longVal = fieldMapping.field.getLong( object );
								if ( fieldMapping.converterId == -1 ) {
									json_value = JSONNumber.Long( longVal );
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
									if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
								floatVal = fieldMapping.field.getFloat( object );
								if ( fieldMapping.converterId == -1 ) {
									json_value = JSONNumber.Float( floatVal );
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
									if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
								doubleVal = fieldMapping.field.getDouble( object );
								if ( fieldMapping.converterId == -1 ) {
									json_value = JSONNumber.Double( doubleVal );
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
									if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								booleanVal = (Boolean)fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( booleanVal != null ) {
										json_value = JSONBoolean.Boolean( booleanVal );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_INTEGER:
								intVal = (Integer)fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( intVal != null ) {
										json_value = JSONNumber.Integer( intVal );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_LONG:
								longVal = (Long)fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( longVal != null ) {
										json_value = JSONNumber.Long( longVal );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_FLOAT:
								floatVal = (Float)fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( floatVal != null ) {
										json_value = JSONNumber.Float( floatVal );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_DOUBLE:
								doubleVal = (Double)fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( doubleVal != null ) {
										json_value = JSONNumber.Double( doubleVal );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_BIGINTEGER:
								bigIntegerVal = (BigInteger)fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( bigIntegerVal != null ) {
										json_value = JSONNumber.BigInteger( bigIntegerVal );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigIntegerVal );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_BIGDECIMAL:
								bigDecimalVal = (BigDecimal)fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( bigDecimalVal != null ) {
										json_value = JSONNumber.BigDecimal( bigDecimalVal );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigDecimalVal );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_STRING:
								stringVal = (String)fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( stringVal != null ) {
										json_value = JSONString.String( stringVal );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, stringVal );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_BYTEARRAY:
								byteArray = (byte[])fieldMapping.field.get( object );
								if ( fieldMapping.converterId == -1 ) {
									if ( byteArray != null ) {
										json_value = JSONString.String( byteArray );
									}
									else {
										json_value = null;
									}
								}
								else {
									json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, byteArray );
								}
								if ( json_value == null ) {
									if ( !fieldMapping.nullable ) {
										throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
									}
									else {
										json_value = JSONNull.Null;
									}
								}
								json_struct.put( fieldMapping.jsonName, json_value );
								break;
							case JSONObjectMappingConstants.T_OBJECT:
								tmpObject = (Object)fieldMapping.field.get( object );
								if ( tmpObject != null ) {
									//json_value = toJSONStructure( objectVal, converters );
									stackEntry = new StackEntry();
									stackEntry.state = S_OBJECT_VALUE;
									stackEntry.json_struct = json_struct;
									stackEntry.object = object;
									stackEntry.objectMapping = objectMapping;
									stackEntry.fieldMappingsArr = fieldMappingsArr;
									stackEntry.fieldMappingIdx = fieldMappingIdx;
									stackEntry.fieldMapping = fieldMapping;
									stack.add( stackEntry );
									json_struct = new JSONObject();
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
									json_value = JSONNull.Null;
									json_struct.put( fieldMapping.jsonName, json_value );
								}
								break;
							case JSONObjectMappingConstants.T_ARRAY:
								tmpArray = fieldMapping.field.get( object );
								if ( tmpArray != null ) {
									stackEntry = new StackEntry();
									stackEntry.state = S_OBJECT_VALUE;
									stackEntry.json_struct = json_struct;
									stackEntry.object = object;
									stackEntry.objectMapping = objectMapping;
									stackEntry.fieldMappingsArr = fieldMappingsArr;
									stackEntry.fieldMappingIdx = fieldMappingIdx;
									stackEntry.fieldMapping = fieldMapping;
									stack.add( stackEntry );
									json_struct = new JSONArray();
									array = tmpArray;
									arrayIdx = 0;
									arrayLen = Array.getLength( array );
									/*
									objectMapping = classMappings.get( object.getClass().getName() );
									if ( objectMapping == null ) {
										throw new IllegalArgumentException( "Class '" + object.getClass().getName() + "' not registered." );
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
									json_value = JSONNull.Null;
									json_struct.put( fieldMapping.jsonName, json_value );
								}
								break;
							default:
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported type: " + JSONObjectMappingConstants.typeString( fieldMapping.typeId ) );
							}
						}
						else {
							state = S_OBJECT_END;
							bFieldLoop = false;
						}
					}
					break;
				case S_ARRAY_VALUE:
					json_struct.add( json_value );
					state = S_ARRAY;
				case S_ARRAY:
					switch ( fieldMapping.arrayTypeId ) {
					case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
						arrayOf_boolean = (boolean[])array;
						while ( arrayIdx < arrayLen ) {
							booleanVal = arrayOf_boolean[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								json_value = JSONBoolean.Boolean( booleanVal );
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
								if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
						arrayOf_int = (int[])array;
						while ( arrayIdx < arrayLen ) {
							intVal = arrayOf_int[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								json_value = JSONNumber.Integer( intVal );
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
								if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
						arrayOf_long = (long[])array;
						while ( arrayIdx < arrayLen ) {
							longVal = arrayOf_long[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								json_value = JSONNumber.Long( longVal );
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
								if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
						arrayOf_float = (float[])array;
						while ( arrayIdx < arrayLen ) {
							floatVal = arrayOf_float[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								json_value = JSONNumber.Float( floatVal );
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
								if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
						arrayOf_double = (double[])array;
						while ( arrayIdx < arrayLen ) {
							doubleVal = arrayOf_double[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								json_value = JSONNumber.Double( doubleVal );
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
								if ( json_value == null || json_value.type == JSONConstants.VT_NULL ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BOOLEAN:
						arrayOf_Boolean = (Boolean[])array;
						while ( arrayIdx < arrayLen ) {
							booleanVal = arrayOf_Boolean[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								if ( booleanVal != null ) {
									json_value = JSONBoolean.Boolean( booleanVal );
								}
								else {
									json_value = null;
								}
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, booleanVal );
							}
							if ( json_value == null ) {
								if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
								}
								else {
									json_value = JSONNull.Null;
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_INTEGER:
						arrayOf_Integer = (Integer[])array;
						while ( arrayIdx < arrayLen ) {
							intVal = arrayOf_Integer[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								if ( intVal != null ) {
									json_value = JSONNumber.Integer( intVal );
								}
								else {
									json_value = null;
								}
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, intVal );
							}
							if ( json_value == null ) {
								if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
								}
								else {
									json_value = JSONNull.Null;
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_LONG:
						arrayOf_Long = (Long[])array;
						while ( arrayIdx < arrayLen ) {
							longVal = arrayOf_Long[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								if ( longVal != null ) {
									json_value = JSONNumber.Long( longVal );
								}
								else {
									json_value = null;
								}
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, longVal );
							}
							if ( json_value == null ) {
								if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
								}
								else {
									json_value = JSONNull.Null;
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_FLOAT:
						arrayOf_Float = (Float[])array;
						while ( arrayIdx < arrayLen ) {
							floatVal = arrayOf_Float[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								if ( floatVal != null ) {
									json_value = JSONNumber.Float( floatVal );
								}
								else {
									json_value = null;
								}
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, floatVal );
							}
							if ( json_value == null ) {
								if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
								}
								else {
									json_value = JSONNull.Null;
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_DOUBLE:
						arrayOf_Double = (Double[])array;
						while ( arrayIdx < arrayLen ) {
							doubleVal = arrayOf_Double[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								if ( doubleVal != null ) {
									json_value = JSONNumber.Double( doubleVal );
								}
								else {
									json_value = null;
								}
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, doubleVal );
							}
							if ( json_value == null ) {
								if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
								}
								else {
									json_value = JSONNull.Null;
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BIGINTEGER:
						arrayOf_BigInteger = (BigInteger[])array;
						while ( arrayIdx < arrayLen ) {
							bigIntegerVal = arrayOf_BigInteger[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								if ( bigIntegerVal != null ) {
									json_value = JSONNumber.BigInteger( bigIntegerVal );
								}
								else {
									json_value = null;
								}
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigIntegerVal );
							}
							if ( json_value == null ) {
								if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
								}
								else {
									json_value = JSONNull.Null;
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BIGDECIMAL:
						arrayOf_BigDecimal = (BigDecimal[])array;
						while ( arrayIdx < arrayLen ) {
							bigDecimalVal = arrayOf_BigDecimal[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								if ( bigDecimalVal != null ) {
									json_value = JSONNumber.BigDecimal( bigDecimalVal );
								}
								else {
									json_value = null;
								}
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, bigDecimalVal );
							}
							if ( json_value == null ) {
								if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
								}
								else {
									json_value = JSONNull.Null;
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_STRING:
						arrayOf_String = (String[])array;
						while ( arrayIdx < arrayLen ) {
							stringVal = arrayOf_String[ arrayIdx++ ];
							if ( fieldMapping.converterId == -1 ) {
								if ( stringVal != null ) {
									json_value = JSONString.String( stringVal );
								}
								else {
									json_value = null;
								}
							}
							else {
								json_value = converters[ fieldMapping.converterId ].getJSONValue( fieldMapping.fieldName, stringVal );
							}
							if ( json_value == null ) {
								if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
								}
								else {
									json_value = JSONNull.Null;
								}
							}
							json_struct.add( json_value );
						}
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_OBJECT:
						arrayOf_Object = (Object[])array;
						if ( arrayIdx < arrayLen ) {
							tmpObject = arrayOf_Object[ arrayIdx++ ];
							if ( tmpObject != null ) {
								//json_value = toJSONStructure( tmpObject, converters );
								stackEntry = new StackEntry();
								stackEntry.state = S_ARRAY_VALUE;
								stackEntry.json_struct = json_struct;
								stackEntry.object = object;
								stackEntry.objectMapping = objectMapping;
								stackEntry.fieldMappingsArr = fieldMappingsArr;
								stackEntry.fieldMappingIdx = fieldMappingIdx;
								stackEntry.fieldMapping = fieldMapping;
								stackEntry.array = array;
								stackEntry.arrayIdx = arrayIdx;
								stackEntry.arrayLen = arrayLen;
								stack.add( stackEntry );
								json_struct = new JSONObject();
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
								json_value = JSONNull.Null;
								json_struct.add( json_value );
							}
						}
						else {
							state = S_ARRAY_END;
						}
						break;
					default:
						throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported array type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayTypeId ) );
					}
					break;
				}
			}
		}
		catch (IllegalAccessException e) {
			throw new JSONException( e );
		}
		return json_struct;
	}

}
