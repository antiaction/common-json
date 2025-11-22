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
import java.util.List;
import java.util.Map;

import com.antiaction.common.json.JSONConverterAbstract;
import com.antiaction.common.json.JSONException;
import com.antiaction.common.json.JSONObjectFieldMapping;
import com.antiaction.common.json.JSONObjectMapping;
import com.antiaction.common.json.JSONObjectMappingConstants;
import com.antiaction.common.json.JSONObjectMappings;

/**
 * De-serialize a JSON structure into Java Object(s).
 *
 * @author Nicholas
 * Created on 24/07/2013
 */
public class JSONStructureUnmarshaller {

	private static final int S_START = 0;
	private static final int S_OBJECT_BEGIN = 1;
	private static final int S_OBJECT_END = 2;
	private static final int S_ARRAY_BEGIN = 3;
	private static final int S_ARRAY_END = 4;
	private static final int S_OBJECT_VALUE = 5;
	private static final int S_OBJECT = 6;
	private static final int S_ARRAY = 7;
	private static final int S_ARRAY_OBJECT_VALUE = 8;
	private static final int S_ARRAY_OBJECT = 9;

	protected JSONObjectMappings objectMappings;

	protected Map<String, JSONObjectMapping> classMappings;

	private static final class StackEntry {
		int state;
		JSONObject jsonObject;
		Object curObj;
		JSONObjectFieldMapping[] fieldMappingsArr;
		int fieldMappingsArrIdx;
		JSONObjectFieldMapping fieldMapping;
		List<JSONValue> jsonValues;
		int jsonValuesIdx;
		Object[] arrayOf_Object;
	}

	public JSONStructureUnmarshaller(JSONObjectMappings objectMappings) {
		this.objectMappings = objectMappings;
		this.classMappings = objectMappings.classMappings;
	}

	public <T> T toObject(JSONCollection json_struct, Class<T> clazz) throws JSONException {
		return toObject( json_struct, clazz, null );
	}

	public <T> T toObject(JSONCollection json_struct, Class<T> clazz, JSONConverterAbstract[] converters) throws JSONException {
		Boolean booleanVal;
		Integer intVal;
		Long longVal;
		Float floatVal;
		Double doubleVal;
		BigInteger bigIntegerVal;
		BigDecimal bigDecimalVal;
		String stringVal;
		byte[] byteArray;

		JSONObject jsonObjectVal = null;
		JSONArray jsonArrayVal = null;
		List<JSONValue> jsonValues = null;
		int jsonValuesIdx = 0;
		Object object = null;

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
		Object[] arrayOf_Object = null;

		LinkedList<StackEntry> stack = new LinkedList<StackEntry>();
		StackEntry stackEntry = null;

		JSONObjectMapping objectMapping = classMappings.get( clazz.getName() );
		if ( objectMapping == null ) {
			throw new JSONException( "Class '" + clazz.getName() + "' not registered." );
		}
		if ( objectMapping.converters == true && converters == null ) {
			throw new JSONException( "Class '" + clazz.getName() + "' may required converters!" );
		}

		JSONObject jsonObject = null;
		//JSONArray jsonArray = null;
		T dstObj = null;
		Object curObj = null;

		JSONObjectFieldMapping[] fieldMappingsArr = null;
		int fieldMappingsArrIdx = 0;
		JSONObjectFieldMapping fieldMapping = null;
		JSONValue json_value;

		int state = S_START;
		int r_state = -1;
		boolean bLoop = true;
		try {
			while ( bLoop ) {
				// debug
				//System.out.println( state );
				switch ( state ) {
				case S_START:
					switch ( json_struct.type ) {
					case JSONConstants.VT_OBJECT:
						if ( objectMapping.typeId != JSONObjectMapping.OMT_OBJECT ) {
							throw new JSONException( "Destination is not an object!" );
						}
						jsonObject = json_struct.getObject();
						dstObj = clazz.newInstance();
						curObj = dstObj;
						fieldMappingsArr = objectMapping.fieldMappingsArr;
						state = S_OBJECT;
						break;
					case JSONConstants.VT_ARRAY:
						if ( objectMapping.typeId != JSONObjectMapping.OMT_ARRAY ) {
							throw new JSONException( "Destination is not an array!" );
						}
						jsonArrayVal = json_struct.getArray();
						jsonValues = jsonArrayVal.values;
						fieldMapping = objectMapping.fieldMapping;
						//jsonArray = json_struct.getArray();
						state = S_ARRAY;
						break;
					default:
						throw new JSONException( "Invalid json structure representation!" );
					}
					break;
				case S_OBJECT_BEGIN:
					stackEntry = new StackEntry();
					stackEntry.state = r_state;
					stackEntry.jsonObject = jsonObject;
					stackEntry.curObj = curObj;
					stackEntry.fieldMappingsArr = fieldMappingsArr;
					stackEntry.fieldMappingsArrIdx = fieldMappingsArrIdx;
					stackEntry.fieldMapping = fieldMapping;
					stackEntry.jsonValues = jsonValues;
					stackEntry.jsonValuesIdx = jsonValuesIdx;
					stackEntry.arrayOf_Object = arrayOf_Object;
					stack.add( stackEntry );

					jsonObject = jsonObjectVal;
					curObj = fieldMapping.clazz.newInstance();
					objectMapping = classMappings.get( fieldMapping.clazz.getName() );
					if ( objectMapping == null ) {
						throw new JSONException( "Class '" + fieldMapping.clazz.getName() + "' not registered." );
					}
					if ( objectMapping.converters == true && converters == null ) {
						throw new JSONException( "Class '" + fieldMapping.clazz.getName() + "' may required converters!" );
					}
					fieldMappingsArr = objectMapping.fieldMappingsArr;
					fieldMappingsArrIdx = 0;
					state = S_OBJECT;
					break;
				case S_OBJECT_END:
					if ( stack.size() > 0 ) {
						object = curObj;
						stackEntry = stack.removeLast();
						state = stackEntry.state;
						jsonObject = stackEntry.jsonObject;
						curObj = stackEntry.curObj;
						fieldMappingsArr = stackEntry.fieldMappingsArr;
						fieldMappingsArrIdx = stackEntry.fieldMappingsArrIdx;
						fieldMapping = stackEntry.fieldMapping;
						jsonValues = stackEntry.jsonValues;
						jsonValuesIdx = stackEntry.jsonValuesIdx;
						arrayOf_Object = stackEntry.arrayOf_Object;
					}
					else {
						bLoop = false;
					}
					break;
				case S_ARRAY_BEGIN:
					stackEntry = new StackEntry();
					stackEntry.state = r_state;
					stackEntry.jsonObject = jsonObject;
					stackEntry.curObj = curObj;
					stackEntry.fieldMappingsArr = fieldMappingsArr;
					stackEntry.fieldMappingsArrIdx = fieldMappingsArrIdx;
					stackEntry.fieldMapping = fieldMapping;
					stackEntry.jsonValues = jsonValues;
					stackEntry.jsonValuesIdx = jsonValuesIdx;
					stackEntry.arrayOf_Object = arrayOf_Object;
					stack.add( stackEntry );

					state = S_ARRAY;
					break;
				case S_ARRAY_END:
					if ( stack.size() > 0 ) {
						//object = curObj;
						stackEntry = stack.removeLast();
						state = stackEntry.state;
						jsonObject = stackEntry.jsonObject;
						curObj = stackEntry.curObj;
						fieldMappingsArr = stackEntry.fieldMappingsArr;
						fieldMappingsArrIdx = stackEntry.fieldMappingsArrIdx;
						fieldMapping = stackEntry.fieldMapping;
						jsonValues = stackEntry.jsonValues;
						jsonValuesIdx = stackEntry.jsonValuesIdx;
						arrayOf_Object = stackEntry.arrayOf_Object;
					}
					else {
						dstObj = (T)object;
						bLoop = false;
					}
					break;
				case S_OBJECT_VALUE:
					fieldMapping.field.set( curObj, object );
					state = S_OBJECT;
				case S_OBJECT:
					if ( fieldMappingsArrIdx < fieldMappingsArr.length ) {
						fieldMapping = fieldMappingsArr[ fieldMappingsArrIdx++ ];
						json_value = jsonObject.get( fieldMapping.jsonName );
						if ( json_value != null ) {
							switch ( fieldMapping.typeId ) {
							case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
								if ( fieldMapping.converterId == -1 ) {
									booleanVal = json_value.getBoolean();
								}
								else {
									booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
								}
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								fieldMapping.field.setBoolean( curObj, booleanVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
								if ( fieldMapping.converterId == -1 ) {
									intVal = json_value.getInteger();
								}
								else {
									intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
								}
								if ( intVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								fieldMapping.field.setInt( curObj, intVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
								if ( fieldMapping.converterId == -1 ) {
									longVal = json_value.getLong();
								}
								else {
									longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
								}
								if ( longVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								fieldMapping.field.setLong( curObj, longVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
								if ( fieldMapping.converterId == -1 ) {
									floatVal = json_value.getFloat();
								}
								else {
									floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
								}
								if ( floatVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								fieldMapping.field.setFloat( curObj, floatVal );
								break;
							case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
								if ( fieldMapping.converterId == -1 ) {
									doubleVal = json_value.getDouble();
								}
								else {
									doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
								}
								if ( doubleVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not be null." );
								}
								fieldMapping.field.setDouble( curObj, doubleVal );
								break;
							case JSONObjectMappingConstants.T_BOOLEAN:
								if ( fieldMapping.converterId == -1 ) {
									booleanVal = json_value.getBoolean();
								}
								else {
									booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
								}
								if ( booleanVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, booleanVal );
								break;
							case JSONObjectMappingConstants.T_INTEGER:
								if ( fieldMapping.converterId == -1 ) {
									intVal = json_value.getInteger();
								}
								else {
									intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
								}
								if ( intVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, intVal );
								break;
							case JSONObjectMappingConstants.T_LONG:
								if ( fieldMapping.converterId == -1 ) {
									longVal = json_value.getLong();
								}
								else {
									longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
								}
								if ( longVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, longVal );
								break;
							case JSONObjectMappingConstants.T_FLOAT:
								if ( fieldMapping.converterId == -1 ) {
									floatVal = json_value.getFloat();
								}
								else {
									floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
								}
								if ( floatVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, floatVal );
								break;
							case JSONObjectMappingConstants.T_DOUBLE:
								if ( fieldMapping.converterId == -1 ) {
									doubleVal = json_value.getDouble();
								}
								else {
									doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
								}
								if ( doubleVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, doubleVal );
								break;
							case JSONObjectMappingConstants.T_BIGINTEGER:
								if ( fieldMapping.converterId == -1 ) {
									bigIntegerVal = json_value.getBigInteger();
								}
								else {
									bigIntegerVal = converters[ fieldMapping.converterId ].getBigInteger( fieldMapping.fieldName, json_value );
								}
								if ( bigIntegerVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, bigIntegerVal );
								break;
							case JSONObjectMappingConstants.T_BIGDECIMAL:
								if ( fieldMapping.converterId == -1 ) {
									bigDecimalVal = json_value.getBigDecimal();
								}
								else {
									bigDecimalVal = converters[ fieldMapping.converterId ].getBigDecimal( fieldMapping.fieldName, json_value );
								}
								if ( bigDecimalVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, bigDecimalVal );
								break;
							case JSONObjectMappingConstants.T_STRING:
								if ( fieldMapping.converterId == -1 ) {
									stringVal = json_value.getString();
								}
								else {
									stringVal = converters[ fieldMapping.converterId ].getString( fieldMapping.fieldName, json_value );
								}
								if ( stringVal == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, stringVal );
								break;
							case JSONObjectMappingConstants.T_BYTEARRAY:
								if ( fieldMapping.converterId == -1 ) {
									byteArray = json_value.getBytes();
								}
								else {
									byteArray = converters[ fieldMapping.converterId ].getBytes( fieldMapping.fieldName, json_value );
								}
								if ( byteArray == null && !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								fieldMapping.field.set( curObj, byteArray );
								break;
							case JSONObjectMappingConstants.T_OBJECT:
								jsonObjectVal = json_value.getObject();
								if ( jsonObjectVal != null ) {
									//object = toObject( json_object, fieldMapping.clazz, converters );
									r_state = S_OBJECT_VALUE;
									state = S_OBJECT_BEGIN;
								}
								else if ( !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								else {
									object = null;
									fieldMapping.field.set( curObj, object );
								}
								break;
							case JSONObjectMappingConstants.T_ARRAY:
								jsonArrayVal = json_value.getArray();
								if ( jsonArrayVal != null ) {
									r_state = S_OBJECT_VALUE;
									state = S_ARRAY_BEGIN;
								}
								else if ( !fieldMapping.nullable ) {
									throw new JSONException( "Field '" + fieldMapping.fieldName + "' is not nullable." );
								}
								else {
									object = null;
									fieldMapping.field.set( curObj, object );
								}
								break;
							default:
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported type: " + JSONObjectMappingConstants.typeString( fieldMapping.typeId ) );
							}
						}
						else {
							if ( !fieldMapping.nullable ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "'/'" + fieldMapping.jsonName + "' is not nullable." );
							}
						}
					}
					else {
						state = S_OBJECT_END;
					}
					break;
				case S_ARRAY:
					switch ( fieldMapping.arrayTypeId ) {
					case JSONObjectMappingConstants.T_PRIMITIVE_BOOLEAN:
						jsonValues = jsonArrayVal.values;
						arrayOf_boolean = new boolean[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								booleanVal = json_value.getBoolean();
							}
							else {
								booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
							}
							if ( booleanVal == null ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
							}
							arrayOf_boolean[ i ] = booleanVal;
						}
						object = arrayOf_boolean;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_INTEGER:
						jsonValues = jsonArrayVal.values;
						arrayOf_int = new int[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								intVal = json_value.getInteger();
							}
							else {
								intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
							}
							if ( intVal == null ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
							}
							arrayOf_int[ i ] = intVal;
						}
						object = arrayOf_int;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_LONG:
						jsonValues = jsonArrayVal.values;
						arrayOf_long = new long[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								longVal = json_value.getLong();
							}
							else {
								longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
							}
							if ( longVal == null ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
							}
							arrayOf_long[ i ] = longVal;
						}
						object = arrayOf_long;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_FLOAT:
						jsonValues = jsonArrayVal.values;
						arrayOf_float = new float[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								floatVal = json_value.getFloat();
							}
							else {
								floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
							}
							if ( floatVal == null ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
							}
							arrayOf_float[ i ] = floatVal;
						}
						object = arrayOf_float;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_PRIMITIVE_DOUBLE:
						jsonValues = jsonArrayVal.values;
						arrayOf_double = new double[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								doubleVal = json_value.getDouble();
							}
							else {
								doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
							}
							if ( doubleVal == null ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' is primitive and can not have null values." );
							}
							arrayOf_double[ i ] =  doubleVal;
						}
						object = arrayOf_double;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BOOLEAN:
						jsonValues = jsonArrayVal.values;
						arrayOf_Boolean = new Boolean[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								booleanVal = json_value.getBoolean();
							}
							else {
								booleanVal = converters[ fieldMapping.converterId ].getBoolean( fieldMapping.fieldName, json_value );
							}
							if ( booleanVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							arrayOf_Boolean[ i ] = booleanVal;
						}
						object = arrayOf_Boolean;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_INTEGER:
						jsonValues = jsonArrayVal.values;
						arrayOf_Integer = new Integer[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								intVal = json_value.getInteger();
							}
							else {
								intVal = converters[ fieldMapping.converterId ].getInteger( fieldMapping.fieldName, json_value );
							}
							if ( intVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							arrayOf_Integer[ i ] = intVal;
						}
						object = arrayOf_Integer;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_LONG:
						jsonValues = jsonArrayVal.values;
						arrayOf_Long = new Long[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								longVal = json_value.getLong();
							}
							else {
								longVal = converters[ fieldMapping.converterId ].getLong( fieldMapping.fieldName, json_value );
							}
							if ( longVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							arrayOf_Long[ i ] = longVal;
						}
						object = arrayOf_Long;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_FLOAT:
						jsonValues = jsonArrayVal.values;
						arrayOf_Float = new Float[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								floatVal = json_value.getFloat();
							}
							else {
								floatVal = converters[ fieldMapping.converterId ].getFloat( fieldMapping.fieldName, json_value );
							}
							if ( floatVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							arrayOf_Float[ i ] = floatVal;
						}
						object = arrayOf_Float;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_DOUBLE:
						jsonValues = jsonArrayVal.values;
						arrayOf_Double = new Double[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								doubleVal = json_value.getDouble();
							}
							else {
								doubleVal = converters[ fieldMapping.converterId ].getDouble( fieldMapping.fieldName, json_value );
							}
							if ( doubleVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							arrayOf_Double[ i ] = doubleVal;
						}
						object = arrayOf_Double;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BIGINTEGER:
						jsonValues = jsonArrayVal.values;
						arrayOf_BigInteger = new BigInteger[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								bigIntegerVal = json_value.getBigInteger();
							}
							else {
								bigIntegerVal = converters[ fieldMapping.converterId ].getBigInteger( fieldMapping.fieldName, json_value );
							}
							if ( bigIntegerVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							arrayOf_BigInteger[ i ] = bigIntegerVal;
						}
						object = arrayOf_BigInteger;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_BIGDECIMAL:
						jsonValues = jsonArrayVal.values;
						arrayOf_BigDecimal = new BigDecimal[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								bigDecimalVal = json_value.getBigDecimal();
							}
							else {
								bigDecimalVal = converters[ fieldMapping.converterId ].getBigDecimal( fieldMapping.fieldName, json_value );
							}
							if ( bigDecimalVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							arrayOf_BigDecimal[ i ] = bigDecimalVal;
						}
						object = arrayOf_BigDecimal;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_STRING:
						jsonValues = jsonArrayVal.values;
						arrayOf_String = new String[ jsonValues.size() ];
						for ( int i=0; i<jsonValues.size(); ++i ) {
							json_value = jsonValues.get( i );
							if ( fieldMapping.converterId == -1 ) {
								stringVal = json_value.getString();
							}
							else {
								stringVal = converters[ fieldMapping.converterId ].getString( fieldMapping.fieldName, json_value );
							}
							if ( stringVal == null && !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
							}
							arrayOf_String[ i ] = stringVal;
						}
						object = arrayOf_String;
						state = S_ARRAY_END;
						break;
					case JSONObjectMappingConstants.T_OBJECT:
						jsonValues = jsonArrayVal.values;
						jsonValuesIdx = 0;
						arrayOf_Object = (Object[])Array.newInstance( fieldMapping.clazz, jsonArrayVal.values.size() );
						state = S_ARRAY_OBJECT;
						break;
					default:
						throw new JSONException( "Field '" + fieldMapping.fieldName + "' has an unsupported array type: " + JSONObjectMappingConstants.typeString( fieldMapping.arrayTypeId ) );
					}
					break;
				case S_ARRAY_OBJECT_VALUE:
					arrayOf_Object[ jsonValuesIdx++ ] = object;
				case S_ARRAY_OBJECT:
					if ( jsonValuesIdx < jsonValues.size() ) {
						json_value = jsonValues.get( jsonValuesIdx );
						jsonObjectVal = json_value.getObject();
						if ( jsonObjectVal != null ) {
							//object = toObject( jsonObjectVal, fieldMapping.clazz, converters );
							r_state = S_ARRAY_OBJECT_VALUE;
							state = S_OBJECT_BEGIN;
						}
						else if ( !fieldMapping.nullValues ) {
							throw new JSONException( "Field '" + fieldMapping.fieldName + "' does not allow null values." );
						}
						else {
							object = null;
							arrayOf_Object[ jsonValuesIdx++ ] = object;
						}
					}
					else {
						object = arrayOf_Object;
						state = S_ARRAY_END;
					}
					break;
				}
			}
		}
		catch (InstantiationException e) {
			throw new JSONException( e );
		}
		catch (IllegalAccessException e) {
			throw new JSONException( e );
		}
		return dstObj;
	}

}
