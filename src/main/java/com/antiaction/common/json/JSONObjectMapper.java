/*
 * Created on 13/11/2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.antiaction.common.json.annotation.JSON;
import com.antiaction.common.json.annotation.JSONIgnore;
import com.antiaction.common.json.annotation.JSONNullValues;
import com.antiaction.common.json.annotation.JSONNullable;

public class JSONObjectMapper {

	public static final int T_PRIMITIVE_BOOLEAN = 1;
	public static final int T_PRIMITIVE_INTEGER = 2;
	public static final int T_PRIMITIVE_LONG = 3;
	public static final int T_PRIMITIVE_FLOAT = 4;
	public static final int T_PRIMITIVE_DOUBLE = 5;
	public static final int T_OBJECT = 100;
	public static final int T_BOOLEAN = 101;
	public static final int T_INTEGER = 102;
	public static final int T_LONG = 103;
	public static final int T_FLOAT = 104;
	public static final int T_DOUBLE = 105;
	public static final int T_BIGINTEGER = 106;
	public static final int T_BIGDECIMAL = 107;
	public static final int T_STRING = 108;
	public static final int T_BYTEARRAY = 109;
	public static final int T_ARRAY = 200;

	// TODO maybe better not static.
	protected static Map<String, JSONObjectMapping> classMappings = new TreeMap<String, JSONObjectMapping>();

	protected static Map<String, Integer> typeMappings = new TreeMap<String, Integer>();

	protected static Map<String, Integer> arrayTypeMappings = new TreeMap<String, Integer>();

	static {
		typeMappings.put( boolean.class.getName(), T_PRIMITIVE_BOOLEAN );
		typeMappings.put( int.class.getName(), T_PRIMITIVE_INTEGER );
		typeMappings.put( long.class.getName(), T_PRIMITIVE_LONG );
		typeMappings.put( float.class.getName(), T_PRIMITIVE_FLOAT );
		typeMappings.put( double.class.getName(), T_PRIMITIVE_DOUBLE );
		typeMappings.put( Boolean.class.getName(), T_BOOLEAN );
		typeMappings.put( Integer.class.getName(), T_INTEGER );
		typeMappings.put( Long.class.getName(), T_LONG );
		typeMappings.put( Float.class.getName(), T_FLOAT );
		typeMappings.put( Double.class.getName(), T_DOUBLE );
		typeMappings.put( BigInteger.class.getName(), T_BIGINTEGER );
		typeMappings.put( BigDecimal.class.getName(), T_BIGDECIMAL );
		typeMappings.put( String.class.getName(), T_STRING );
		typeMappings.put( byte[].class.getName(), T_BYTEARRAY );

		arrayTypeMappings.put( boolean[].class.getName(), T_PRIMITIVE_BOOLEAN );
		arrayTypeMappings.put( int[].class.getName(), T_PRIMITIVE_INTEGER );
		arrayTypeMappings.put( long[].class.getName(), T_PRIMITIVE_LONG );
		arrayTypeMappings.put( float[].class.getName(), T_PRIMITIVE_FLOAT );
		arrayTypeMappings.put( double[].class.getName(), T_PRIMITIVE_DOUBLE );
		arrayTypeMappings.put( Boolean[].class.getName(), T_BOOLEAN );
		arrayTypeMappings.put( Integer[].class.getName(), T_INTEGER );
		arrayTypeMappings.put( Long[].class.getName(), T_LONG );
		arrayTypeMappings.put( Float[].class.getName(), T_FLOAT );
		arrayTypeMappings.put( Double[].class.getName(), T_DOUBLE );
		arrayTypeMappings.put( BigInteger[].class.getName(), T_BIGINTEGER );
		arrayTypeMappings.put( BigDecimal[].class.getName(), T_BIGDECIMAL );
		arrayTypeMappings.put( String[].class.getName(), T_STRING );
	}

	public static final int CLASS_INVALID_TYPE_MODIFIERS_MASK = ClassTypeModifiers.CT_ANNOTATION
			| ClassTypeModifiers.CT_ANONYMOUSCLASS
			| ClassTypeModifiers.CT_ARRAY
			| ClassTypeModifiers.CT_ENUM
			| ClassTypeModifiers.CT_INTERFACE
			| ClassTypeModifiers.CT_LOCALCLASS
			| ClassTypeModifiers.CT_PRIMITIVE
			| ClassTypeModifiers.CM_ABSTRACT
			| ClassTypeModifiers.CM_NATIVE;

	public static final int CLASS_VALID_TYPE_MODIFIERS_MASK = ClassTypeModifiers.CT_MEMBERCLASS
			| ClassTypeModifiers.CT_CLASS
			| ClassTypeModifiers.CM_STATIC;

	public static final int VALID_CLASS = ClassTypeModifiers.CT_CLASS;

	public static final int VALID_MEMBER_CLASS = ClassTypeModifiers.CT_MEMBERCLASS | ClassTypeModifiers.CM_STATIC;

	public static final int VALID_ARRAY_CLASS = ClassTypeModifiers.CT_ARRAY | ClassTypeModifiers.CM_ABSTRACT;

	// ClassNotFoundException, SecurityException, NoSuchFieldException
	public void register(Class<?> clazz) throws JSONException {
		int classTypeMask = ClassTypeModifiers.getClassTypeModifiersMask( clazz );
		if ( (classTypeMask & CLASS_INVALID_TYPE_MODIFIERS_MASK) != 0 ) {
			throw new JSONException( "Unsupported class type." );
		}
		classTypeMask &= CLASS_VALID_TYPE_MODIFIERS_MASK;
		if ( (classTypeMask != VALID_CLASS) && (classTypeMask != VALID_MEMBER_CLASS) ) {
			throw new JSONException( "Unsupported class type." );
		}
		if ( !classMappings.containsKey( clazz.getName() ) ) {
			mapClass( clazz );
		}
	}

	public static final int FIELD_IGNORE_TYPE_MODIFIER = ClassTypeModifiers.CM_STATIC
			| ClassTypeModifiers.CM_TRANSIENT;

	// TODO support List/Set interface but required impl class annotation.
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
			| ClassTypeModifiers.CM_STATIC;

	// ClassNotFoundException, SecurityException, NoSuchFieldException
	protected JSONObjectMapping mapClass(Class<?> clazz) throws JSONException {
		try {
			JSONObjectFieldMapping json_fm;
			JSON json;
			JSONIgnore ignore;

			JSONObjectMapping json_om = new JSONObjectMapping();
			classMappings.put( clazz.getName(), json_om );

			json = clazz.getAnnotation( JSON.class );
			if ( json != null ) {
				// debug
				//System.out.println( "json" );
				String[] ignores = json.ignore();
				for ( int i=0; i<ignores.length; ++i) {
					json_om.ignore.add( ignores[ i ] );
				}
				String [] nullables = json.nullable();
				for ( int i=0; i<nullables.length; ++i ) {
					json_om.nullableSet.add( nullables[ i ] );
				}
				String[] nullValues = json.nullValues();
				for ( int i=0; i<nullValues.length; ++i ) {
					json_om.nullValuesSet.add( nullValues[ i ] );
				}
			}

			Field[] fields = clazz.getDeclaredFields();
			Field field;
			boolean bIgnore;
			Class<?> fieldType = null;
			String fieldTypeName;
			Integer type;
			Integer arrayType = 0;
			int fieldModsMask = 0;
			int classTypeMask = 0;
			JSONObjectMapping fieldObjectMapping;
			int level;
			JSONNullable nullable;
			boolean bNullable;
			JSONNullValues nullValues;
			boolean bNullValues;
			for ( int i=0; i<fields.length; ++i ) {
				field = fields[ i ];
				// debug
				//System.out.println( field.getName() );
				bIgnore = json_om.ignore.contains( field.getName() );
				ignore = field.getAnnotation( JSONIgnore.class );
				if ( ignore != null ) {
					// debug
					//System.out.println( "ignore" );
					bIgnore = true;
				}
				if ( !bIgnore ) {
					fieldModsMask = ClassTypeModifiers.getFieldModifiersMask( field );
					// debug
					System.out.println( field.getName() + " - " + ClassTypeModifiers.toString( fieldModsMask ) );
					bIgnore = (fieldModsMask & FIELD_IGNORE_TYPE_MODIFIER) != 0;
				}
				if ( !bIgnore ) {
					fieldType = field.getType();
					fieldTypeName = fieldType.getName();
					classTypeMask = ClassTypeModifiers.getClassTypeModifiersMask( fieldType );
					// debug
					System.out.println( fieldTypeName + " " + ClassTypeModifiers.toString( classTypeMask ) );

					type = typeMappings.get( fieldTypeName );
					fieldObjectMapping = null;
					if ( type == null ) {
						if ( (classTypeMask & FIELD_INVALID_TYPE_MODIFIERS_MASK) != 0 ) {
							throw new JSONException( "Unsupported field class type." );
						}
						classTypeMask &= FIELD_VALID_TYPE_MODIFIERS_MASK;
						if ( (classTypeMask == VALID_CLASS) || (classTypeMask == VALID_MEMBER_CLASS) ) {
							Type genericType = field.getGenericType();
							System.out.println( "GT: " + genericType + " " + genericType.getClass() );

							type = T_OBJECT;
							// Cache
							fieldObjectMapping = classMappings.get( fieldTypeName );
							if ( fieldObjectMapping == null ) {
								fieldObjectMapping = mapClass( Class.forName( fieldTypeName, true, clazz.getClassLoader() ) );
							}

							if( genericType instanceof ParameterizedType ) {
								ParameterizedType parameterizedType = (ParameterizedType)genericType;
								Type[] typeArguments = parameterizedType.getActualTypeArguments();
								for ( Type typeArgument : typeArguments ) {
									Class<?> classType = ((Class<?>)typeArgument);
									System.out.println( "Field " + field.getName() + " has a parameterized type of " + classType.getSimpleName() );
								}
							}
						}
						else if ( classTypeMask == VALID_ARRAY_CLASS ) {
							type = T_ARRAY;
							arrayType = arrayTypeMappings.get( fieldTypeName );
							if ( arrayType == null ) {
								level = 0;
								while ( level < fieldTypeName.length() && fieldTypeName.charAt( level ) == '[' ) {
									++level;
								}
								// [L<class>;
								if ( level == 1 && level < fieldTypeName.length() && fieldTypeName.charAt( level ) == 'L' && fieldTypeName.endsWith( ";" ) ) {
									arrayType = T_OBJECT;
									fieldTypeName = fieldTypeName.substring( level + 1, fieldTypeName.length() - 1 );
									fieldType = Class.forName( fieldTypeName, true, clazz.getClassLoader() );
									// Cache
									fieldObjectMapping = classMappings.get( fieldTypeName );
									if ( fieldObjectMapping == null ) {
										fieldObjectMapping = mapClass( fieldType );
									}
								}
								else {
									throw new JSONException( "Unsupported array definitation '" + fieldTypeName + "'." );
								}
							}
						}
						else {
							throw new JSONException( "Unsupported field class type." );
						}
					}
					// debug
					//System.out.println( type );

					if ( type != null ) {
						json_fm = new JSONObjectFieldMapping();
						json_fm.name = field.getName();
						json_fm.type = type;
						json_fm.arrayType = arrayType;
						json_fm.className = fieldTypeName;
						json_fm.clazz = fieldType;
						json_fm.objectMapping = fieldObjectMapping;
						json_fm.field = clazz.getDeclaredField( json_fm.name );
						json_fm.field.setAccessible( true );
						json_om.fieldMappingsMap.put( json_fm.name, json_fm );
						json_om.fieldMappingsList.add( json_fm );

						bNullable = json_om.nullableSet.contains( json_fm.name );
						if ( !bNullable ) {
							nullable = field.getAnnotation( JSONNullable.class );
							if ( nullable != null ) {
								bNullable = nullable.value();
							}
						}
						if ( bNullable ) {
							if ( json_fm.type < T_OBJECT ) {
								throw new JSONException( "Primitive types can not be nullable." );
							}
							json_fm.nullable = true;
						}
						bNullValues = json_om.nullValuesSet.contains( json_fm.name );
						if ( !bNullValues) {
							nullValues = field.getAnnotation( JSONNullValues.class );
							if ( nullValues != null ) {
								bNullValues = nullValues.value();
							}
						}
						if ( bNullValues ) {
							if ( json_fm.type >= T_ARRAY && json_fm.arrayType < T_OBJECT ) {
								throw new JSONException( "Array of primitive type can not have null values." );
							}
							json_fm.nullValues = true;
						}
					}
				}
			}
			return json_om;
		}
		catch (Exception e) {
			throw new JSONException( e );
		}
	}

	public <T> T toObject(JSONStructure json_struct, Class<T> clazz) throws InstantiationException, IllegalAccessException, JSONException {
		Boolean booleanVal;
		Integer intVal;
		Long longVal;
		Float floatVal;
		Double doubleVal;
		BigInteger bigIntegerVal;
		BigDecimal bigDecimalVal;
		String strVal;
		byte[] byteArray;
		Object object;
		JSONObject json_object;

		JSONArray json_array;
		List<JSONValue> json_values;
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

		JSONObjectMapping json_om = classMappings.get( clazz.getName() );
		if ( json_om == null ) {
			throw new IllegalArgumentException( "Class '" + clazz.getName() + "'not registered." );
		}

		JSONObject srcJSONObject = json_struct.getObject();
		T dstObj = clazz.newInstance();

		Iterator<JSONObjectFieldMapping> fieldMappingsIter = json_om.fieldMappingsList.iterator();
		JSONObjectFieldMapping fieldMapping;
		JSONValue json_value;
		while ( fieldMappingsIter.hasNext() ) {
			fieldMapping = fieldMappingsIter.next();
			json_value = srcJSONObject.get( fieldMapping.name );
			if ( json_value != null ) {
				switch ( fieldMapping.type ) {
				case T_PRIMITIVE_BOOLEAN:
					booleanVal = json_value.getBoolean();
					if ( booleanVal == null ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
					}
					fieldMapping.field.setBoolean( dstObj, booleanVal );
					break;
				case T_PRIMITIVE_INTEGER:
					intVal = json_value.getInteger();
					if ( intVal == null ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
					}
					fieldMapping.field.setInt( dstObj, intVal );
					break;
				case T_PRIMITIVE_LONG:
					longVal = json_value.getLong();
					if ( longVal == null ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
					}
					fieldMapping.field.setLong( dstObj, longVal );
					break;
				case T_PRIMITIVE_FLOAT:
					floatVal = json_value.getFloat();
					if ( floatVal == null ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
					}
					fieldMapping.field.setFloat( dstObj, floatVal );
					break;
				case T_PRIMITIVE_DOUBLE:
					doubleVal = json_value.getDouble();
					if ( doubleVal == null ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
					}
					fieldMapping.field.setDouble( dstObj, doubleVal );
					break;
				case T_BOOLEAN:
					booleanVal = json_value.getBoolean();
					if ( booleanVal == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, booleanVal );
					break;
				case T_INTEGER:
					intVal = json_value.getInteger();
					if ( intVal == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, intVal );
					break;
				case T_LONG:
					longVal = json_value.getLong();
					if ( longVal == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, longVal );
					break;
				case T_FLOAT:
					floatVal = json_value.getFloat();
					if ( floatVal == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, floatVal );
					break;
				case T_DOUBLE:
					doubleVal = json_value.getDouble();
					if ( doubleVal == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, doubleVal );
					break;
				case T_BIGINTEGER:
					bigIntegerVal = json_value.getBigInteger();
					if ( bigIntegerVal == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, bigIntegerVal );
					break;
				case T_BIGDECIMAL:
					bigDecimalVal = json_value.getBigDecimal();
					if ( bigDecimalVal == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, bigDecimalVal );
					break;
				case T_STRING:
					strVal = json_value.getString();
					if ( strVal == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, strVal );
					break;
				case T_BYTEARRAY:
					byteArray = json_value.getBytes();
					if ( byteArray == null && !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					fieldMapping.field.set( dstObj, byteArray );
					break;
				case T_OBJECT:
					json_object = json_value.getObject();
					if ( json_object != null ) {
						object = toObject( json_object, fieldMapping.clazz );
					}
					else if ( !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					else {
						object = null;
					}
					fieldMapping.field.set( dstObj, object );
					break;
				case T_ARRAY:
					json_array = json_value.getArray();
					if ( json_array != null ) {
						switch (  fieldMapping.arrayType ) {
						case T_PRIMITIVE_BOOLEAN:
							json_values = json_array.values;
							arrayOf_boolean = new boolean[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								booleanVal = json_value.getBoolean();
								if ( booleanVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
								}
								arrayOf_boolean[ i ] = booleanVal;
							}
							object = arrayOf_boolean;
							break;
						case T_PRIMITIVE_INTEGER:
							json_values = json_array.values;
							arrayOf_int = new int[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								intVal = json_value.getInteger();
								if ( intVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
								}
								arrayOf_int[ i ] = intVal;
							}
							object = arrayOf_int;
							break;
						case T_PRIMITIVE_LONG:
							json_values = json_array.values;
							arrayOf_long = new long[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								longVal = json_value.getLong();
								if ( longVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
								}
								arrayOf_long[ i ] = longVal;
							}
							object = arrayOf_long;
							break;
						case T_PRIMITIVE_FLOAT:
							json_values = json_array.values;
							arrayOf_float = new float[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								floatVal = json_value.getFloat();
								if ( floatVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
								}
								arrayOf_float[ i ] = floatVal;
							}
							object = arrayOf_float;
							break;
						case T_PRIMITIVE_DOUBLE:
							json_values = json_array.values;
							arrayOf_double = new double[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								doubleVal = json_value.getDouble();
								if ( doubleVal == null ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' is primitive and can not be null." );
								}
								arrayOf_double[ i ] =  doubleVal;
							}
							object = arrayOf_double;
							break;
						case T_BOOLEAN:
							json_values = json_array.values;
							arrayOf_Boolean = new Boolean[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								booleanVal = json_value.getBoolean();
								if ( booleanVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								arrayOf_Boolean[ i ] = booleanVal;
							}
							object = arrayOf_Boolean;
							break;
						case T_INTEGER:
							json_values = json_array.values;
							arrayOf_Integer = new Integer[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								intVal = json_value.getInteger();
								if ( intVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								arrayOf_Integer[ i ] = intVal;
							}
							object = arrayOf_Integer;
							break;
						case T_LONG:
							json_values = json_array.values;
							arrayOf_Long = new Long[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								longVal = json_value.getLong();
								if ( longVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								arrayOf_Long[ i ] = longVal;
							}
							object = arrayOf_Long;
							break;
						case T_FLOAT:
							json_values = json_array.values;
							arrayOf_Float = new Float[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								floatVal = json_value.getFloat();
								if ( floatVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								arrayOf_Float[ i ] = floatVal;
							}
							object = arrayOf_Float;
							break;
						case T_DOUBLE:
							json_values = json_array.values;
							arrayOf_Double = new Double[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								doubleVal = json_value.getDouble();
								if ( doubleVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								arrayOf_Double[ i ] = doubleVal;
							}
							object = arrayOf_Double;
							break;
						case T_BIGINTEGER:
							json_values = json_array.values;
							arrayOf_BigInteger = new BigInteger[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								bigIntegerVal = json_value.getBigInteger();
								if ( bigIntegerVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								arrayOf_BigInteger[ i ] = bigIntegerVal;
							}
							object = arrayOf_BigInteger;
							break;
						case T_BIGDECIMAL:
							json_values = json_array.values;
							arrayOf_BigDecimal = new BigDecimal[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								bigDecimalVal = json_value.getBigDecimal();
								if ( bigDecimalVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								arrayOf_BigDecimal[ i ] = bigDecimalVal;
							}
							object = arrayOf_BigDecimal;
							break;
						case T_STRING:
							json_values = json_array.values;
							arrayOf_String = new String[ json_values.size() ];
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								strVal = json_value.getString();
								if ( strVal == null && !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								arrayOf_String[ i ] = strVal;
							}
							object = arrayOf_String;
							break;
						case T_OBJECT:
							json_values = json_array.values;
							arrayOf_Object = (Object[])Array.newInstance( fieldMapping.clazz, json_array.values.size() );
							for ( int i=0; i<json_values.size(); ++i ) {
								json_value = json_values.get( i );
								json_object = json_value.getObject();
								if ( json_object != null ) {
									object = toObject( json_object, fieldMapping.clazz );
								}
								else if ( !fieldMapping.nullValues ) {
									throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
								}
								else {
									object = null;
								}
								arrayOf_Object[ i ] = object;
							}
							object = arrayOf_Object;
							break;
						default:
							throw new JSONException( "Field '" + fieldMapping.name + "' has an unsupported array type." );
						}
						//throw new UnsupportedOperationException( "Not implemented!" );
					}
					else if ( !fieldMapping.nullable ) {
						throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
					}
					else {
						object = null;
					}
					fieldMapping.field.set( dstObj, object );
				}
			}
			else {
				if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
			}
		}

		return dstObj;
	}

	public <T> JSONStructure toJSON(T srcObj) throws JSONException, IllegalArgumentException, IllegalAccessException {
		Boolean booleanVal;
		Integer intVal;
		Long longVal;
		Float floatVal;
		Double doubleVal;
		BigInteger bigIntegerVal;
		BigDecimal bigDecimalVal;
		String strVal;
		byte[] byteArray;
		Object objectVal;

		Object array_object;
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

		JSONObjectMapping json_om = classMappings.get( srcObj.getClass().getName() );
		if ( json_om == null ) {
			throw new IllegalArgumentException( "Class not registered." );
		}

		JSONStructure json_struct = new JSONObject();
		JSONArray json_array;

		Iterator<JSONObjectFieldMapping> fieldMappingsIter = json_om.fieldMappingsList.iterator();
		JSONObjectFieldMapping fieldMapping;
		JSONValue json_value;
		int len;
		while ( fieldMappingsIter.hasNext() ) {
			fieldMapping = fieldMappingsIter.next();
			switch ( fieldMapping.type ) {
			case T_PRIMITIVE_BOOLEAN:
				booleanVal = fieldMapping.field.getBoolean( srcObj );
				json_value = JSONBoolean.Boolean( booleanVal );
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_PRIMITIVE_INTEGER:
				intVal = fieldMapping.field.getInt( srcObj );
				json_value = JSONNumber.Integer( intVal );
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_PRIMITIVE_LONG:
				longVal = fieldMapping.field.getLong( srcObj );
				json_value = JSONNumber.Long( longVal );
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_PRIMITIVE_FLOAT:
				floatVal = fieldMapping.field.getFloat( srcObj );
				json_value = JSONNumber.Float( floatVal );
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_PRIMITIVE_DOUBLE:
				doubleVal = fieldMapping.field.getDouble( srcObj );
				json_value = JSONNumber.Double( doubleVal );
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_BOOLEAN:
				booleanVal = (Boolean)fieldMapping.field.get( srcObj );
				if ( booleanVal != null ) {
					json_value = JSONBoolean.Boolean( booleanVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_INTEGER:
				intVal = (Integer)fieldMapping.field.get( srcObj );
				if ( intVal != null ) {
					json_value = JSONNumber.Integer( intVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_LONG:
				longVal = (Long)fieldMapping.field.get( srcObj );
				if ( longVal != null ) {
					json_value = JSONNumber.Long( longVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_FLOAT:
				floatVal = (Float)fieldMapping.field.get( srcObj );
				if ( floatVal != null ) {
					json_value = JSONNumber.Float( floatVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_DOUBLE:
				doubleVal = (Double)fieldMapping.field.get( srcObj );
				if ( doubleVal != null ) {
					json_value = JSONNumber.Double( doubleVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_BIGINTEGER:
				bigIntegerVal = (BigInteger)fieldMapping.field.get( srcObj );
				if ( bigIntegerVal != null ) {
					json_value = JSONNumber.BigInteger( bigIntegerVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_BIGDECIMAL:
				bigDecimalVal = (BigDecimal)fieldMapping.field.get( srcObj );
				if ( bigDecimalVal != null ) {
					json_value = JSONNumber.BigDecimal( bigDecimalVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_STRING:
				strVal = (String)fieldMapping.field.get( srcObj );
				if ( strVal != null ) {
					json_value = JSONString.String( strVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_BYTEARRAY:
				byteArray = (byte[])fieldMapping.field.get( srcObj );
				if ( byteArray != null ) {
					json_value = JSONString.String( byteArray );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_OBJECT:
				objectVal = (Object)fieldMapping.field.get( srcObj );
				if ( objectVal != null ) {
					json_value = toJSON( objectVal );
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
				break;
			case T_ARRAY:
				array_object = fieldMapping.field.get( srcObj );
				if ( array_object != null ) {
					len = Array.getLength( array_object );
					json_array = new JSONArray();
					switch ( fieldMapping.arrayType ) {
					case T_PRIMITIVE_BOOLEAN:
						arrayOf_boolean = (boolean[])array_object;
						for ( int i=0; i<len; ++i ) {
							booleanVal = arrayOf_boolean[ i ];
							json_value = JSONBoolean.Boolean( booleanVal );
							json_array.add( json_value );
						}
						break;
					case T_PRIMITIVE_INTEGER:
						arrayOf_int = (int[])array_object;
						for ( int i=0; i<len; ++i ) {
							intVal = arrayOf_int[ i ];
							json_value = JSONNumber.Integer( intVal );
							json_array.add( json_value );
						}
						break;
					case T_PRIMITIVE_LONG:
						arrayOf_long = (long[])array_object;
						for ( int i=0; i<len; ++i ) {
							longVal = arrayOf_long[ i ];
							json_value = JSONNumber.Long( longVal );
							json_array.add( json_value );
						}
						break;
					case T_PRIMITIVE_FLOAT:
						arrayOf_float = (float[])array_object;
						for ( int i=0; i<len; ++i ) {
							floatVal = arrayOf_float[ i ];
							json_value = JSONNumber.Float( floatVal );
							json_array.add( json_value );
						}
						break;
					case T_PRIMITIVE_DOUBLE:
						arrayOf_double = (double[])array_object;
						for ( int i=0; i<len; ++i ) {
							doubleVal = arrayOf_double[ i ];
							json_value = JSONNumber.Double( doubleVal );
							json_array.add( json_value );
						}
						break;
					case T_BOOLEAN:
						arrayOf_Boolean = (Boolean[])array_object;
						for ( int i=0; i<len; ++i ) {
							booleanVal = arrayOf_Boolean[ i ];
							if ( booleanVal != null ) {
								json_value = JSONBoolean.Boolean( booleanVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					case T_INTEGER:
						arrayOf_Integer = (Integer[])array_object;
						for ( int i=0; i<len; ++i ) {
							intVal = arrayOf_Integer[ i ];
							if ( intVal != null ) {
								json_value = JSONNumber.Integer( intVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					case T_LONG:
						arrayOf_Long = (Long[])array_object;
						for ( int i=0; i<len; ++i ) {
							longVal = arrayOf_Long[ i ];
							if ( longVal != null ) {
								json_value = JSONNumber.Long( longVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					case T_FLOAT:
						arrayOf_Float = (Float[])array_object;
						for ( int i=0; i<len; ++i ) {
							floatVal = arrayOf_Float[ i ];
							if ( floatVal != null ) {
								json_value = JSONNumber.Float( floatVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					case T_DOUBLE:
						arrayOf_Double = (Double[])array_object;
						for ( int i=0; i<len; ++i ) {
							doubleVal = arrayOf_Double[ i ];
							if ( doubleVal != null ) {
								json_value = JSONNumber.Double( doubleVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					case T_BIGINTEGER:
						arrayOf_BigInteger = (BigInteger[])array_object;
						for ( int i=0; i<len; ++i ) {
							bigIntegerVal = arrayOf_BigInteger[ i ];
							if ( bigIntegerVal != null ) {
								json_value = JSONNumber.BigInteger( bigIntegerVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					case T_BIGDECIMAL:
						arrayOf_BigDecimal = (BigDecimal[])array_object;
						for ( int i=0; i<len; ++i ) {
							bigDecimalVal = arrayOf_BigDecimal[ i ];
							if ( bigDecimalVal != null ) {
								json_value = JSONNumber.BigDecimal( bigDecimalVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					case T_STRING:
						arrayOf_String = (String[])array_object;
						for ( int i=0; i<len; ++i ) {
							strVal = arrayOf_String[ i ];
							if ( strVal != null ) {
								json_value = JSONString.String( strVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					case T_OBJECT:
						arrayOf_Object = (Object[])array_object;
						for ( int i=0; i<len; ++i ) {
							objectVal = arrayOf_Object[ i ];
							if ( objectVal != null ) {
								json_value = toJSON( objectVal );
							}
							else if ( !fieldMapping.nullValues ) {
								throw new JSONException( "Field '" + fieldMapping.name + "' does not allow null values." );
							}
							else {
								json_value = JSONNull.Null;
							}
							json_array.add( json_value );
						}
						break;
					default:
						throw new JSONException( "Field '" + fieldMapping.name + "' has an unsupported array type." );
					}
					json_value = json_array;
				}
				else if ( !fieldMapping.nullable ) {
					throw new JSONException( "Field '" + fieldMapping.name + "' is not nullable." );
				}
				else {
					json_value = JSONNull.Null;
				}
				json_struct.put( fieldMapping.name, json_value );
			}
		}

		return json_struct;
	}

}
