package com.antiaction.common.classmapper;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.antiaction.common.json.ClassTypeModifiers;
import com.antiaction.common.json.JSONException;
import com.antiaction.common.json.JSONObjectMappingConstants;

// TODO Possible check for a class implementing multiple different collection interfaces.
public class TypeData {

	public static final TypeData[] EMPTY_PARAMETER_TYPES_ARR = new TypeData[0];

	/** Interface or Annotation. */
	public static final int ACC_INTERFACE_ANNOTATION = ClassModifier.ACC_INTERFACE | ClassModifier.ACC_ANNOTATION;

	/** Enum or Module. */
	public static final int ACC_ENUM_MODULE = ClassModifier.ACC_ENUM | ClassModifier.ACC_MODULE;

	public static final int PT_PRIMITIVE = 0;

	public static final int PT_CLASS = 1;

	public static final int PT_PARAMTYPE = 2;

	public static final int PT_TYPE_VARIABLE = 3;

	public static Map<String, Integer> typeVarNameIdMap = new TreeMap<>();

	public static int typeVarNameIdNumber = 0;

	public int typeId;

	public int arrayTypeId;

	public Integer colTypeId;

	public int primaryType;

	/** Java Class. */
	public Class<?> clazz;

	/** Java Type. */
	public Type type;

	/** Internal runtime class name. */
	public String className;

	/** Reader friendly class name. */
	public String typeName;

	public int modifiers;

	public boolean bInterfaceInstance;

	public int level;

	public boolean bCollection;

	public boolean bParameterizedType;

	public TypeData[] parameterTypes = EMPTY_PARAMETER_TYPES_ARR;

	public boolean bTypeVariable;

	public String typeVarName;

	public int typeVarNameId;

	public boolean bWildCardType;

	public boolean bUnresolved;

	public ClassData classData;

	public static synchronized int getTypeVarNameId(String typeVarName) {
		Integer typeVarNameId = typeVarNameIdMap.get(typeVarName);
		if (typeVarNameId == null) {
			typeVarNameId = ++typeVarNameIdNumber;
			typeVarNameIdMap.put(typeVarName, typeVarNameId);
		}
		return typeVarNameId;
	}

	public static synchronized int[] getTypeVarNameIds(TypeVariable<?>[] typeParameters) {
		int len = typeParameters.length;
		int[] typeVarNameIdArr = new int[len];
		String typeVarName;
		Integer typeVarNameId;
		int idx = 0;
		while (idx < len) {
			typeVarName = typeParameters[idx].getName();
			typeVarNameId = typeVarNameIdMap.get(typeVarName);
			if (typeVarNameId == null) {
				typeVarNameId = ++typeVarNameIdNumber;
				typeVarNameIdMap.put(typeVarName, typeVarNameId);
			}
			typeVarNameIdArr[idx] = typeVarNameId;
			++idx;
		}
		return typeVarNameIdArr;
	}

	public TypeData cloneObj() {
		TypeData typeData = new TypeData();
		return copyToObj(typeData);
	}

	public TypeData copyToObj(TypeData typeData) {
		typeData.typeId = typeId;
		typeData.arrayTypeId = arrayTypeId;
		typeData.colTypeId = colTypeId;
		typeData.primaryType = primaryType;
		typeData.clazz = clazz;
		typeData.type = type;
		typeData.typeName = typeName;
		typeData.modifiers = modifiers;
		typeData.bInterfaceInstance = bInterfaceInstance;
		typeData.level = level;
		typeData.bCollection = bCollection;
		typeData.bParameterizedType = bParameterizedType;
		typeData.parameterTypes = parameterTypes;
		typeData.bTypeVariable = bTypeVariable;
		typeData.typeVarName = typeVarName;
		typeData.typeVarNameId = typeVarNameId;
		typeData.bUnresolved = bUnresolved;
		typeData.classData = classData;
		return typeData;
	}

	protected static TypeData[] tdArr = new TypeData[16];

	protected static Type[][] typeArgumentsArrs = new Type[16][];

	protected static int[] typeArgumentIdxs = new int[16];

	/**
	 * Parse a <code>Type</code> structure and 
	 * @param type
	 * @param srcDesc
	 * @param typeData
	 * @throws ClassMapperException
	 */
	public static synchronized TypeData mapType(Type type, String srcDesc, TypeData typeData) throws ClassMapperException {
		if (typeData == null) {
			typeData = new TypeData();
		}
		int modifiers;
		int maskedModifiers;
		int level;
		TypeData currTypeData = typeData;
		boolean bInterfaceInstance = false;
		Integer typeId;
		Integer arrayTypeId;
		Integer colTypeId;
		String className;			// Internal runtime class name.
		String typeName;			// Reader friendly class name.
		TypeVariable<?> typeVar;
		String typeVarName;
		//Integer typeVarNameId;
		ParameterizedType pType;
		WildcardType wcType;
		Type[] typeArguments = null;
		int typeArgumentIdx = 0;
		int tdArrLvl = 0;
		do {
			if (tdArrLvl > 0 && typeArgumentIdx < typeArguments.length) {
				currTypeData = new TypeData();
				tdArr[tdArrLvl - 1].parameterTypes[typeArgumentIdx] = currTypeData;
				type = typeArguments[typeArgumentIdx];
				typeArgumentIdxs[tdArrLvl] = ++typeArgumentIdx;
			}
			if (type instanceof java.lang.reflect.TypeVariable) {
				typeVar = (TypeVariable<?>) type;
				typeVarName = typeVar.getName();
				currTypeData.typeName = typeVarName;
				currTypeData.bTypeVariable = true;
				currTypeData.typeVarName = typeVarName;
				currTypeData.typeVarNameId = getTypeVarNameId(typeVarName);
				currTypeData.bUnresolved = true;
				// debug
				//System.out.println("Field '" + field.getName() + "' - TypeVariable: " + typeVarName);
				//throw new JSONException("Field '" + field.getName() + "' uses unsupported TypeVariable.");
			}
			else if (type instanceof java.lang.reflect.GenericArrayType) {
				// debug
				//System.out.println("type: " + type.getClass().getTypeName());
				//System.out.println("type: " + type.getTypeName());
				//throw new JSONException("Field '" + field.getName() + "' uses unsupported GenericArrayType.");
				throw new ClassMapperException(srcDesc + " uses unsupported GenericArrayType.");
			}
			else if (type instanceof java.lang.reflect.ParameterizedType) {
				pType = (ParameterizedType) type;
				type = pType.getRawType();
				// debug
				//System.out.println("Field '" + field.getName() + "' - ParameterizedType: " + currTypeData.typeName);
				typeArguments = pType.getActualTypeArguments();
				typeArgumentIdx = 0;
				currTypeData.bParameterizedType = true;
				currTypeData.parameterTypes = new TypeData[typeArguments.length];
				if (typeArguments.length > 0) {
					tdArr[tdArrLvl++] = currTypeData;
					typeArgumentsArrs[tdArrLvl] = typeArguments;
					typeArgumentIdxs[tdArrLvl] = typeArgumentIdx;
				}
				else {
					typeArguments = typeArgumentsArrs[tdArrLvl];
					typeArgumentIdx = typeArgumentIdxs[tdArrLvl];
				}
			}
			else if (type instanceof java.lang.reflect.WildcardType) {
				wcType = (WildcardType) type;
				currTypeData.bWildCardType = true;
			}
			else if (!(type instanceof java.lang.Class)) {
				System.out.println("type: " + type.getClass().getTypeName());
				//System.out.println("type: " + type.getTypeName());
				//throw new JSONException("Field: '" + field.getName() + "' has an unsupported type '" + type.getTypeName() + "'.");
				throw new ClassMapperException(srcDesc + " has an unsupported type '" + type.getTypeName() + "'.");
			}
			if (type instanceof java.lang.Class) {
				className = ((Class<?>) type).getName();
				typeName = type.getTypeName();
				modifiers = ((Class<?>) type).getModifiers();
				bInterfaceInstance = false;
				level = 0;
				typeId = JSONObjectMappingConstants.primitiveTypeMappings.get(className);
				// Debug
				//System.out.println(className + " - " + typeName);
				if (typeId == null) {
					while (level < className.length() && className.charAt(level) == '[') {
						++level;
					}
					if (level > 0) {
						if (level != 1) {
							//throw new JSONException( "Unsupported multi-dimensional array type '" + fieldTypeName + "'." );
							throw new ClassMapperException( "Unsupported multi-dimensional array type '" + typeName + "'." );
						}
						typeId = JSONObjectMappingConstants.arrayPrimitiveTypeMappings.get( className );
						// Get the Array type.
						type = ((Class<?>) type).getComponentType();
						//arrayType = JSONObjectMappingConstants.arrayPrimitiveTypeMappings.get( fieldTypeName );
					}
					if (typeId == null) {
						/*
						 * Interface, Class, ParameterizedType or TypeVariable.
						 */
						maskedModifiers = modifiers & ACC_INTERFACE_ANNOTATION;
						if (maskedModifiers != 0) {
							if (maskedModifiers != ClassModifier.ACC_INTERFACE) {
								throw new ClassMapperException(String.format("Mapping (annotation) interfaces not supported. %s can not be mapped.", ((Class<?>) type).getTypeName()));
							}
							//int colType = ClassTypeModifiers.getCollectionInterfaceType(fieldType);
							colTypeId = ClassTypeModifiers.getCollectionInterfaceType((Class<?>) type);
							if (colTypeId == ClassTypeModifiers.COLTYPE_OTHER) {
								//colType = ClassTypeModifiers.getCollectionType(fieldType);
								colTypeId = ClassTypeModifiers.getCollectionType((Class<?>) type);
							}
							bInterfaceInstance = true;
								// Check the interface itself.
								// TODO Check interface vs instance at some pointer later.
								/*
								if ( colType != ClassTypeModifiers.COLTYPE_OTHER ) {
									if ( fieldTypeInstance == null ) {
										throw new JSONException( "[" + clazz.getName() + "] Missing @JSONTypeInstance annotation on collection interface field of type: " + fieldTypeName );
									}
									int instanceColType = ClassTypeModifiers.getCollectionType( fieldTypeInstance );
									if ( colType != instanceColType ) {
										throw new JSONException( "Field interface type(" + ClassTypeModifiers.colTypeToString( colType ) + ") and instance type(" + ClassTypeModifiers.colTypeToString( instanceColType ) + ") are not compatible." );
									}
								}
								*/
								//System.out.println("(Interface): " + fieldType.getName());
						}
						else {
							maskedModifiers = modifiers & ACC_ENUM_MODULE;
							if (maskedModifiers != 0) {
								throw new ClassMapperException(String.format("Modifiers for class %s not supported by mapper. (%s)", ((Class<?>) type).getTypeName(), Modifier.toString(maskedModifiers)));
							}
							colTypeId = ClassTypeModifiers.getCollectionType((Class<?>) type);
							// debug
							//System.out.println("(Class): " + fieldType.getName());
						}
						switch ( colTypeId ) {
						case ClassTypeModifiers.COLTYPE_LIST:
							typeId = JSONObjectMappingConstants.T_LIST;
							currTypeData.bCollection = true;
							currTypeData.colTypeId = colTypeId;
							break;
						case ClassTypeModifiers.COLTYPE_MAP:
							typeId = JSONObjectMappingConstants.T_MAP;
							currTypeData.bCollection = true;
							currTypeData.colTypeId = colTypeId;
							break;
						case ClassTypeModifiers.COLTYPE_SET:
							typeId = JSONObjectMappingConstants.T_SET;
							currTypeData.bCollection = true;
							currTypeData.colTypeId = colTypeId;
							break;
							//throw new JSONException( "Collection must have parametrized type(s). (" + fieldTypeName + ")" );
						default:
							typeId = JSONObjectMappingConstants.T_OBJECT;
							// Cache
							/*
							fieldObjectMapping = om.classMappings.get( fieldTypeName );
							if ( fieldObjectMapping == null ) {
								// FIXME Arguments
								fieldObjectMapping = om.mapClass( Class.forName( fieldTypeName, true, clazz.getClassLoader() ), JSONClassAndExtendsData.ZERO_CLAZZ_ARGUMENTS );
							}
							*/
							break;
						}
						/*
						if (colTypeId != null && colTypeId > 0) {
						}
						*/
					}
					if (level == 1) {
						arrayTypeId = typeId;
						typeId = JSONObjectMappingConstants.T_ARRAY;
						currTypeData.arrayTypeId = arrayTypeId;
					}
				}
				currTypeData.typeId = typeId;
				currTypeData.clazz = (Class<?>) type;
				currTypeData.type = type;
				currTypeData.className = className;
				currTypeData.typeName = typeName;
				currTypeData.modifiers = modifiers;
				currTypeData.bInterfaceInstance = bInterfaceInstance;
				currTypeData.level = level;
				// debug
				//System.out.println("Field '" + field.getName() + "' - Class: " + currTypeData.typeName);
			}
			// Make sure unresolved flag floats upwards.
			if (tdArrLvl > 0 && (typeArgumentIdx > 1 && typeArgumentIdx < typeArguments.length)) {
				tdArr[tdArrLvl - 1].bUnresolved |= tdArr[tdArrLvl - 1].parameterTypes[typeArgumentIdx - 1].bUnresolved;
			}
			while (tdArrLvl > 0 && typeArgumentIdx == typeArguments.length) {
				if (typeArgumentIdx > 0) {
					tdArr[tdArrLvl - 1].bUnresolved |= tdArr[tdArrLvl - 1].parameterTypes[typeArgumentIdx - 1].bUnresolved;
				}
				--tdArrLvl;
				typeArguments = typeArgumentsArrs[tdArrLvl];
				typeArgumentIdx = typeArgumentIdxs[tdArrLvl];
			}
		} while ((tdArrLvl > 0));
		return typeData;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		typeToString(sb);
		return sb.toString();
	}

	protected TypeData[][] parameterTypesStack = new TypeData[16][];

	protected int[] parameterTypesIdxs = new int[16];

	public synchronized void typeToString(StringBuilder sb) {
		TypeData typeData;
		TypeData[] currParameterTypes;
		int ptIdx;
		int ptStackLvl;
		sb.append(typeName);
		// parameterTypes != null
		// Currently never null.
		if (parameterTypes.length > 0) {
			sb.append("<");
			currParameterTypes = parameterTypes;
			ptIdx = 0;
			ptStackLvl = 0;
			do {
				if (ptIdx < currParameterTypes.length) {
					if (ptIdx > 0) {
						sb.append(", ");
					}
					typeData = currParameterTypes[ptIdx++];
					sb.append(typeData.typeName);
					// typeData.parameterTypes != null
					// Currently never null.
					if (typeData.parameterTypes.length > 0) {
						parameterTypesStack[ptStackLvl] = currParameterTypes;
						parameterTypesIdxs[ptStackLvl] = ptIdx;
						++ptStackLvl;
						currParameterTypes = typeData.parameterTypes;
						ptIdx = 0;
						sb.append("<");
					}
				}
				while (ptStackLvl > 0 && ptIdx == currParameterTypes.length) {
					--ptStackLvl;
					currParameterTypes = parameterTypesStack[ptStackLvl];
					ptIdx = parameterTypesIdxs[ptStackLvl];
					sb.append(">");
				}
			} while (ptStackLvl > 0 || ptIdx < currParameterTypes.length);
			sb.append(">");
		}
		// TODO Arrays..!
	}

	public static synchronized TypeData replaceTypeVarNames(TypeData oldTopTypeData, int[] typeVarNameIds, TypeData[] typeVarParamTypes) {
		TypeData[] oldParameterTypes;
		TypeData[] newParameterTypes;
		TypeData newTopTypeData;
		if (oldTopTypeData.bTypeVariable) {
			newTopTypeData = oldTopTypeData.cloneObj();
			throw new IllegalStateException("WIP(topTypeData.bTypeVariable)");
		}
		// topTypeData.bParameterizedType
		oldParameterTypes = oldTopTypeData.parameterTypes;
		if (oldParameterTypes.length > 0) {
			newTopTypeData = oldTopTypeData.cloneObj();
			newParameterTypes = new TypeData[oldParameterTypes.length];
			newTopTypeData.parameterTypes = newParameterTypes;
			replaceTypeVarNames(oldParameterTypes, newParameterTypes, typeVarNameIds, typeVarParamTypes);
			return newTopTypeData;
		}
		else {
			return oldTopTypeData;
		}
	}

	protected static TypeData[][] replaceOldParamTypesStack = new TypeData[16][];

	protected static TypeData[][] replaceNewParamTypesStack = new TypeData[16][];

	protected static int[] replaceParamTypesIdxs = new int[16];

	public static synchronized void replaceTypeVarNames(TypeData[] oldParameterTypes, TypeData[] newParameterTypes, int[] typeVarNameIds, TypeData[] typeVarParamTypes) {
		int ptIdx;
		int ptStackLvl;
		TypeData oldTypeData;
		TypeData newTypeData;
		ptIdx = 0;
		ptStackLvl = 0;
		do {
			if (ptIdx < oldParameterTypes.length) {
				oldTypeData = oldParameterTypes[ptIdx];
				newTypeData = oldTypeData.cloneObj();
				newParameterTypes[ptIdx++] = newTypeData;
				// typeData.parameterTypes != null
				// Currently never null.
				if (oldTypeData.parameterTypes.length > 0) {
					replaceOldParamTypesStack[ptStackLvl] = oldParameterTypes;
					replaceNewParamTypesStack[ptStackLvl] = newParameterTypes;
					replaceParamTypesIdxs[ptStackLvl] = ptIdx;
					++ptStackLvl;
					oldParameterTypes = oldTypeData.parameterTypes;
					newParameterTypes = new TypeData[oldParameterTypes.length];
					newTypeData.parameterTypes = newParameterTypes;
					ptIdx = 0;
				}
				else if (oldTypeData.bTypeVariable) {
					int typeVarNameId = typeVarNameIdMap.get(oldTypeData.typeVarName);
					int idx = 0;
					while (idx < typeVarNameIds.length && typeVarNameId != typeVarNameIds[idx]) {
						++idx;
					}
					if (idx < typeVarNameIds.length) {
						newParameterTypes[ptIdx - 1] = typeVarParamTypes[idx];
					}
				}
			}
			while (ptStackLvl > 0 && ptIdx == oldParameterTypes.length) {
				--ptStackLvl;
				oldParameterTypes = replaceOldParamTypesStack[ptStackLvl];
				newParameterTypes = replaceNewParamTypesStack[ptStackLvl];
				ptIdx = replaceParamTypesIdxs[ptStackLvl];
			}
		} while (ptStackLvl > 0 || ptIdx < oldParameterTypes.length);
	}

	public static String typeVarNameIdsToString() {
		StringBuilder sb = new StringBuilder();
		typeVarNameIdsToString(sb);
		return sb.toString();
	}

	public static void typeVarNameIdsToString(StringBuilder sb) {
		sb.append(typeVarNameIdNumber);
		sb.append(":");
		Iterator<Entry<String, Integer>> iter = typeVarNameIdMap.entrySet().iterator();
		Entry<String, Integer> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			sb.append(" ");
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
		}
	}

}
