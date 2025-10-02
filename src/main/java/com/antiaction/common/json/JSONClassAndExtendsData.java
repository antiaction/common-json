package com.antiaction.common.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.antiaction.common.json.annotation.JSON;
import com.antiaction.common.json.annotation.JSONIgnore;
import com.antiaction.common.json.annotation.JSONParamTypeInstance;
import com.antiaction.common.json.annotation.JSONTypeInstance;

public class JSONClassAndExtendsData {

	public static final Class<?>[] ZERO_CLAZZ_ARGUMENTS = new Class<?>[0];

	public List<JSONClassData> clazzDataList = new ArrayList<>(4);

	public Set<String> ignore;

	public Set<String> nullable;

	public Set<String> nullValues;

	public List<JSONFieldData> fieldDataList = new ArrayList<>(32);

	public JSONClassAndExtendsData() {
	}

	public void prepClassAndExtends(JSONObjectMappings om, Class<?> clazz, Class<?>[] clazzArguments, Map<String, Set<String>> overrideIgnoreMapSet) throws JSONException {
		try {
			if (ignore == null) {
				ignore = new TreeSet<>();
			}
			if (nullable == null) {
				nullable = new TreeSet<>();
			}
			if (nullValues == null) {
				nullValues = new TreeSet<>();
			}
			JSONClassData clazzData;
			JSON json;
			TypeVariable<?>[] typeParameters;
			Type superType;
			ParameterizedType parameterizedType;
			Type[] argumentTypes;
			Class<?> superClazz;
			//Class<?>[] clazzArguments = new Class<?>[0];
			//Class<?>[] clazzArguments = ZERO_CLAZZ_ARGUMENTS;
			Type argumentType;
			boolean bLoop = clazz != null && "java.lang.Object".compareTo(clazz.getTypeName()) != 0;
			while (bLoop) {
				clazzData = new JSONClassData();
				clazzData.clazz = clazz;
				clazzDataList.add(clazzData);
				/*
				 * Class level annotations.
				 */
				json = clazz.getAnnotation( JSON.class );
				if ( json != null ) {
					String[] ignoresArr = json.ignore();
					for ( int i=0; i<ignoresArr.length; ++i) {
						clazzData.ignore.add( ignoresArr[ i ] );
						ignore.add(ignoresArr[ i ]);
					}
					String [] nullablesArr = json.nullable();
					for ( int i=0; i<nullablesArr.length; ++i ) {
						clazzData.nullable.add( nullablesArr[ i ] );
						nullable.add(nullablesArr[ i ]);
					}
					String[] nullValuesArr = json.nullValues();
					for ( int i=0; i<nullValuesArr.length; ++i ) {
						clazzData.nullValues.add( nullValuesArr[ i ] );
						nullValues.add(nullValuesArr[ i ]);
					}
				}
				// debug
				//System.out.println("[" + clazzDataList.size() + "]: " + clazz.getClass().getTypeName());
				//System.out.println("[" + clazzDataList.size() + "]: " + clazz.getTypeName());
				/*
				 * Check for generic TypeVariable(s).
				 */
				typeParameters = clazz.getTypeParameters();
				clazzData.typeParameters = typeParameters;
				clazzData.clazzArguments = clazzArguments;
				// debug
				/*
				System.out.println("  typeVariables: " + typeParameters.length);
				if (typeParameters.length != 0) {
					for (int i=0; i<typeParameters.length; ++i) {
						// debug
						System.out.println("    [" + i + "]: " + typeParameters[i].getName());
						//System.out.println("    [" + i + "]: " + typeParameters[i].getTypeName());
					}
				}
				*/
				if (typeParameters.length != clazzArguments.length) {
					throw new JSONException("Class '" + clazz.getTypeName() + "' # of parameters(" + typeParameters.length + ") do not equal # of arguments(" + clazzArguments.length + ").");
				}
				/*
				 * Check for ActualTypeArgument(s) in extended Class.
				 */
				superType = clazz.getGenericSuperclass();
				clazzData.superType = superType;
				parameterizedType = null;
				argumentTypes = null;
				superClazz = null;
				if (superType != null) {
					// debug
					//System.out.println("  genericSuperClassType: " + superType.getClass().getTypeName());
					//System.out.println("      genericSuperClass: " + superType.getTypeName());
					if (superType instanceof ParameterizedType) {
						parameterizedType = (ParameterizedType)superType;
						clazzData.parameterizedType = parameterizedType;
						argumentTypes = parameterizedType.getActualTypeArguments();
						clazzData.argumentTypes = argumentTypes;
					}
					else if (superType instanceof Class) {
						// Pretty much like clazz.getSuperclass();
						superClazz = (Class<?>)superType;
						clazzData.superClazz = superClazz;
					}
					else {
						throw new JSONException(String.format("Unexpected generic superclass for %s. (type: %s class: %s)", clazz.getTypeName(), superType.getClass().getTypeName(), superType.getTypeName()));
					}
				}
				/*
				if (argumentTypes != null) {
					// debug
					System.out.println("  argumentTypes: " + argumentTypes.length);
					if (argumentTypes.length > 0) {
						for (int i=0; i<argumentTypes.length; ++i) {
							// debug
							System.out.println("    [" + i + "]: " + argumentTypes[i].getClass().getTypeName());
							System.out.println("    [" + i + "]: " + argumentTypes[i]);
						}
					}
				}
				*/
				/*
				if (superClazz != null) {
					// debug
					System.out.println("  superClass: " + superClazz.getClass().getTypeName());
					System.out.println("  superClass: " + superClazz.getTypeName());
				}
				*/
				Field[] fields = clazz.getDeclaredFields();
				boolean bIgnore;
				JSONIgnore jsonIgnore;
				int fieldModsMask;
				JSONTypeInstance jsonTypeInstance;
				Boolean bInterfaceInstance;
				Class<?> fieldParamClass;
				String varName;
				int aIdx;
				int level;

				Field field;
				Class<?> fieldType = null;
				String fieldTypeName;
				Integer type;
				int classTypeMask;
				Integer arrayType = 0;
				Class<?> fieldTypeInstance;
				JSONObjectMapping fieldObjectMapping;
				Integer[] parametrizedObjectTypes; 
				JSONObjectMapping[] parametrizedObjectMappings;

				for (int i = 0; i<fields.length; ++i) {
					field = fields[ i ];
					bIgnore = ignore.contains( field.getName() );
					jsonIgnore = field.getAnnotation( JSONIgnore.class );
					if ( jsonIgnore != null ) {
						// debug
						//System.out.println( "ignore" );
						bIgnore = true;
					}
					Set<String> overrideFieldIgnoreSet = overrideIgnoreMapSet.get( clazz.getName() );
					if ( bIgnore ) {
						if ( overrideFieldIgnoreSet != null && overrideFieldIgnoreSet.contains( field.getName() ) ) {
							bIgnore = false;
						}
					}
					if ( !bIgnore ) {
						fieldModsMask = ClassTypeModifiers.getFieldModifiersMask( field );
						// debug
						//System.out.println( field.getName() + " - " + ClassTypeModifiers.toString( fieldModsMask ) );
						bIgnore = (fieldModsMask & JSONObjectMappingConstants.FIELD_IGNORE_TYPE_MODIFIER) != 0;
					}
					if (!bIgnore) {
						JSONFieldData fieldData = new JSONFieldData();
						fieldData.field = field;
						fieldData.fieldName = field.getName();
						fieldType = field.getType();
						fieldTypeName = fieldType.getName();
						classTypeMask = ClassTypeModifiers.getClassTypeModifiersMask( fieldType );
						// debug
						//System.out.println( fieldTypeName + " " + ClassTypeModifiers.toString( classTypeMask ) );
						arrayType = 0;
						fieldObjectMapping = null;
						parametrizedObjectTypes = null;
						parametrizedObjectMappings = null;
						fieldTypeInstance = null;
						bInterfaceInstance = false;
						type = JSONObjectMappingConstants.primitiveTypeMappings.get( fieldTypeName );
						if ( type == null ) {
							/*
							 * JSONTypeInstance annotation.
							 */
							jsonTypeInstance = field.getAnnotation( JSONTypeInstance.class );
							if ( jsonTypeInstance != null ) {
								fieldTypeInstance = jsonTypeInstance.value();
								if ( fieldTypeInstance == null ) {
									throw new JSONException( "JSONTypeInstance annotation with null value is not allowed." );
								}
								int typeInstanceMask = ClassTypeModifiers.getClassTypeModifiersMask( fieldTypeInstance );
								if ( (typeInstanceMask & JSONObjectMappingConstants.FIELD_INVALID_TYPE_MODIFIERS_MASK) != 0 ) {
									throw new JSONException( "Unsupported field instance type modifier(s) [" + ClassTypeModifiers.toString( typeInstanceMask ) + "] for class: " + fieldTypeInstance.getName() );
								}
							}
							/*
							 * Interfaces and collection types.
							 */
							if ( (classTypeMask & JSONObjectMappingConstants.FIELD_INVALID_TYPE_MODIFIERS_MASK) != 0 ) {
								if ( (classTypeMask & ClassTypeModifiers.CT_INTERFACE) == 0 ) {
									// debug
									//System.out.println( "Unsupported field modifier(s) [" + ClassTypeModifiers.toString( classTypeMask ) + "] for class: " + fieldTypeName );
									throw new JSONException( "Unsupported field modifier(s) [" + ClassTypeModifiers.toString( classTypeMask ) + "] for class: " + fieldTypeName );
								}
								else {
									// Check the interface itself.
									int colType = ClassTypeModifiers.getCollectionInterfaceType( fieldType );
									if ( colType == ClassTypeModifiers.COLTYPE_OTHER ) {
										colType = ClassTypeModifiers.getCollectionType( fieldType );
									}
									if ( colType != ClassTypeModifiers.COLTYPE_OTHER ) {
										if ( fieldTypeInstance == null ) {
											throw new JSONException( "[" + clazz.getName() + "] Missing @JSONTypeInstance annotation on collection interface field of type: " + fieldTypeName );
										}
										int instanceColType = ClassTypeModifiers.getCollectionType( fieldTypeInstance );
										if ( colType != instanceColType ) {
											throw new JSONException( "Field interface type(" + ClassTypeModifiers.colTypeToString( colType ) + ") and instance type(" + ClassTypeModifiers.colTypeToString( instanceColType ) + ") are not compatible." );
										}
										bInterfaceInstance = true;
									}
									/*
									switch ( colType ) {
									case ClassTypeModifiers.COLTYPE_LIST:
										throw new JSONException( "Unsupported collection interface field type. (" + fieldTypeName + ")" );
									case ClassTypeModifiers.COLTYPE_MAP:
										throw new JSONException( "Unsupported collection interface field type. (" + fieldTypeName + ")" );
									case ClassTypeModifiers.COLTYPE_SET:
										throw new JSONException( "Unsupported collection interface field type. (" + fieldTypeName + ")" );
									default:
										// Check extended interfaces.
										colType = ClassTypeModifiers.getCollectionType( fieldType );
										switch ( colType ) {
										case ClassTypeModifiers.COLTYPE_LIST:
											throw new JSONException( "Unsupported collection interface field type. (" + List.class.getName() + " .. " + fieldTypeName + ")" );
										case ClassTypeModifiers.COLTYPE_MAP:
											throw new JSONException( "Unsupported collection interface field type. (" + Map.class.getName() + " .. " + fieldTypeName + ")" );
										case ClassTypeModifiers.COLTYPE_SET:
											throw new JSONException( "Unsupported collection interface field type. (" + Set.class.getName() + " .. " + fieldTypeName + ")" );
										default:
											throw new JSONException( "Unsupported field class type." );
										}
									}
									*/
								}
							}
							classTypeMask &= JSONObjectMappingConstants.FIELD_VALID_TYPE_MODIFIERS_MASK;
							if ( (classTypeMask == JSONObjectMappingConstants.VALID_CLASS) || (classTypeMask == JSONObjectMappingConstants.VALID_MEMBER_CLASS || bInterfaceInstance) ) {
								Type genericType = field.getGenericType();
								// debug - uncomment
								//System.out.println( "GT: " + genericType + " * " + genericType.getClass() );
								if ( genericType instanceof Class ) {
									int colType = ClassTypeModifiers.getCollectionType( (Class<?>)genericType );
									switch ( colType ) {
									case ClassTypeModifiers.COLTYPE_LIST:
									case ClassTypeModifiers.COLTYPE_MAP:
									case ClassTypeModifiers.COLTYPE_SET:
										throw new JSONException( "Collection must have parametrized type(s). (" + fieldTypeName + ")" );
									default:
										type = JSONObjectMappingConstants.T_OBJECT;
										// Cache
										fieldObjectMapping = om.classMappings.get( fieldTypeName );
										if ( fieldObjectMapping == null ) {
											// FIXME Arguments
											fieldObjectMapping = om.mapClass( Class.forName( fieldTypeName, true, clazz.getClassLoader() ), JSONClassAndExtendsData.ZERO_CLAZZ_ARGUMENTS );
										}
										break;
									}
								}
								else if ( genericType instanceof ParameterizedType ) {
									parameterizedType = (ParameterizedType)genericType;
									Type[] typeArguments = parameterizedType.getActualTypeArguments();
									boolean bWildCardUsed = false;
									for ( Type typeArgument : typeArguments ) {
										if ( typeArgument instanceof WildcardType ) {
											bWildCardUsed = true;
										}
									}
									if ( bWildCardUsed ) {
										throw new JSONException( "Unsupported use of wildcard parameterized types." );
									}
									parametrizedObjectTypes = new Integer[ typeArguments.length ];
									parametrizedObjectMappings = new JSONObjectMapping[ typeArguments.length ];
									for ( int j=0; j<typeArguments.length; ++j ) {
										Type typeArgument = typeArguments[ j ];
										// TODO Workarround.
										if (typeArgument instanceof Class) {
											fieldParamClass = ((Class<?>)typeArgument);
											parametrizedObjectTypes[ j ] = JSONObjectMappingConstants.primitiveTypeMappings.get( fieldParamClass.getName() );
											if ( parametrizedObjectTypes[ j ] == null ) {
												parametrizedObjectTypes[ j ] = JSONObjectMappingConstants.T_OBJECT;
												parametrizedObjectMappings[ j ] = om.classMappings.get( fieldParamClass.getName() );
												if ( parametrizedObjectMappings[ j ] == null ) {
													// FIXME Arguments
													parametrizedObjectMappings[ j ] = om.mapClass( fieldParamClass, JSONClassAndExtendsData.ZERO_CLAZZ_ARGUMENTS );
												}
											}
										}
										// tooth fairy = tiny fairy with boundary issues
										else if (typeArgument instanceof TypeVariable) {
											/*
											System.out.println("TypeVariable: " + ((TypeVariable)typeArgument).getName());
											System.out.println("TypeVariable: " + ((TypeVariable)typeArgument).getTypeName());
											System.out.println("TypeVariable: " + ((TypeVariable)typeArgument).getGenericDeclaration());
											GenericDeclaration gd = ((TypeVariable)typeArgument).getGenericDeclaration();
											System.out.println(gd.getClass());
											TypeVariable[] tv = gd.getTypeParameters();
											System.out.println(tv.length);
											for (int k=0; k<tv.length; ++k) {
												System.out.println(tv[k]);
											}
											*/
											varName = ((TypeVariable<?>) typeArgument).getName();
											// debug
											//System.out.println("varName: " + varName);
											aIdx = 0;
											while (parametrizedObjectMappings[ j ] == null && aIdx < typeParameters.length) {
												if (varName.compareTo(typeParameters[aIdx].getName()) == 0) {
													fieldParamClass = clazzArguments[aIdx];
													parametrizedObjectTypes[ j ] = JSONObjectMappingConstants.primitiveTypeMappings.get( fieldParamClass.getName() );
													if ( parametrizedObjectTypes[ j ] == null ) {
														parametrizedObjectTypes[ j ] = JSONObjectMappingConstants.T_OBJECT;
														parametrizedObjectMappings[ j ] = om.classMappings.get( fieldParamClass.getName() );
														if ( parametrizedObjectMappings[ j ] == null ) {
															// FIXME Arguments
															parametrizedObjectMappings[ j ] = om.mapClass( fieldParamClass, JSONClassAndExtendsData.ZERO_CLAZZ_ARGUMENTS );
														}
													}
												}
												++aIdx;
											}
										}
										else if (typeArgument instanceof ParameterizedType) {
											fieldParamClass = null;
											JSONParamTypeInstance[] bob = field.getAnnotationsByType( JSONParamTypeInstance.class );
											// debug
											//System.out.println(bob.length);
											if (bob != null) {
												int idx = 0;
												while (fieldParamClass == null && idx < bob.length) {
													if (j == bob[idx].index()) {
														fieldParamClass = bob[idx].clazz();
													}
													else {
														++idx;
													}
												}
											}
											// debug
											//System.out.println("ParameterizedType: " + typeArgument.getTypeName());
											//System.out.println(((ParameterizedType) typeArgument).getRawType());
											ParameterizedType fieldParamType = (ParameterizedType) typeArgument;
											if (fieldParamClass == null) {
												fieldParamClass = (Class<?>)fieldParamType.getRawType();
											}
											// debug
											//System.out.println(fieldParamClass);
											parametrizedObjectTypes[ j ] = JSONObjectMappingConstants.primitiveTypeMappings.get( fieldParamClass.getName() );
											if ( parametrizedObjectTypes[ j ] == null ) {
												parametrizedObjectTypes[ j ] = JSONObjectMappingConstants.T_OBJECT;
												parametrizedObjectMappings[ j ] = om.classMappings.get( fieldParamClass.getName() );
												if ( parametrizedObjectMappings[ j ] == null ) {
													// FIXME Arguments
													parametrizedObjectMappings[ j ] = om.mapClass( fieldParamClass, JSONClassAndExtendsData.ZERO_CLAZZ_ARGUMENTS );
												}
											}
										}
										else {
											throw new JSONException("Unsupported ParameterizedType type: " + typeArgument.getClass().getCanonicalName());
										}
									}
									int colType = ClassTypeModifiers.getCollectionType( (Class<?>)parameterizedType.getRawType() );
									switch ( colType ) {
									case ClassTypeModifiers.COLTYPE_LIST:
										type = JSONObjectMappingConstants.T_LIST;
										break;
									case ClassTypeModifiers.COLTYPE_MAP:
										type = JSONObjectMappingConstants.T_MAP;
										break;
									case ClassTypeModifiers.COLTYPE_SET:
										type = JSONObjectMappingConstants.T_SET;
										break;
									default:
										throw new JSONException( "Unsupported generic class. " + field.getClass() );
									}
								}
								else if ( genericType instanceof TypeVariable ) {
									throw new JSONException( "Unable to determine type of generic field '" + field.getName() + "'" );
								}
							}
							else if ( classTypeMask == JSONObjectMappingConstants.VALID_ARRAY_CLASS ) {
								type = JSONObjectMappingConstants.T_ARRAY;
								arrayType = JSONObjectMappingConstants.arrayPrimitiveTypeMappings.get( fieldTypeName );
								if ( arrayType == null ) {
									level = 0;
									while ( level < fieldTypeName.length() && fieldTypeName.charAt( level ) == '[' ) {
										++level;
									}
									// [L<class>;
									if ( level < fieldTypeName.length() ) {
										if ( level == 1 ) {
											if ( fieldTypeName.charAt( level ) == 'L' && fieldTypeName.endsWith( ";" ) ) {
												arrayType = JSONObjectMappingConstants.T_OBJECT;
												fieldTypeName = fieldTypeName.substring( level + 1, fieldTypeName.length() - 1 );
												fieldType = Class.forName( fieldTypeName, true, clazz.getClassLoader() );
												// Cache
												fieldObjectMapping = om.classMappings.get( fieldTypeName );
												if ( fieldObjectMapping == null ) {
													// FIXME Arguments
													fieldObjectMapping = om.mapClass( fieldType, JSONClassAndExtendsData.ZERO_CLAZZ_ARGUMENTS );
												}
											}
											else {
												throw new JSONException( "Unsupported array type '" + fieldTypeName + "'." );
											}
										}
										else {
											throw new JSONException( "Unsupported multi-dimensional array type '" + fieldTypeName + "'." );
										}
									}
									else {
										throw new JSONException( "Invalid array type '" + fieldTypeName + "'." );
									}
								}
							}
							else {
								throw new JSONException( "Unsupported field class type." );
							}
						}

						fieldData.fieldType = fieldType;
						fieldData.fieldTypeName = fieldTypeName;
						fieldData.type = type;
						fieldData.arrayType = arrayType;
						fieldData.fieldTypeInstance = fieldTypeInstance;
						fieldData.fieldObjectMapping = fieldObjectMapping;
						fieldData.parametrizedObjectTypes = parametrizedObjectTypes;
						fieldData.parametrizedObjectMappings = parametrizedObjectMappings;
						clazzData.fieldDataList.add(fieldData);
						fieldDataList.add(fieldData);
					}
				}
				/*
				 * Prepare for parent class next.
				 */
				clazz = clazz.getSuperclass();
				bLoop = clazz != null && "java.lang.Object".compareTo(clazz.getTypeName()) != 0;
				if (bLoop) {
					if (argumentTypes == null) {
						//clazzArguments = new Class<?>[0];
						clazzArguments = ZERO_CLAZZ_ARGUMENTS;
					}
					else {
						clazzArguments = new Class<?>[argumentTypes.length];
						for (int i=0; i<clazzArguments.length; ++i) {
							argumentType = argumentTypes[i];
							if (argumentType instanceof Class) {
								clazzArguments[i] = (Class<?>)argumentType;
							}
							else if (argumentType instanceof TypeVariable) {
								varName = ((TypeVariable<?>) argumentType).getName();
								// debug
								//System.out.println("varName: " + varName);
								aIdx = 0;
								while (clazzArguments[i] == null && aIdx < typeParameters.length) {
									if (varName.compareTo(typeParameters[aIdx].getName()) == 0) {
										// debug
										//System.out.println("typeParameterName: " + typeParameters[aIdx].getName());
										clazzArguments[i] = clazzData.clazzArguments[aIdx];
									}
									++aIdx;
								}
							}
							else {
								throw new JSONException("Unsupported generics argument: " + argumentType.getClass());
							}
						}
					}
				}
			}
		}
		catch (ClassNotFoundException e) {
			throw new JSONException( e );
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		JSONClassData.toString(clazzDataList, sb);
		return sb.toString();
	}

}
