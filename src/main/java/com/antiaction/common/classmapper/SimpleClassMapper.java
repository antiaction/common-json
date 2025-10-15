package com.antiaction.common.classmapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Prepare class for simple object serialization and deserialization.
 * This class does not fully support generic types but stores information for post processing.
 */
public class SimpleClassMapper {

	public static final TypeData[] ZERO_CLAZZ_ARGUMENTS = new TypeData[0];

	public final Map<String, ClassData> classMappings = new TreeMap<String, ClassData>();

	/** Interface or Annotation. */
	public static final int ACC_INTERFACE_ANNOTATION = ClassModifier.ACC_INTERFACE | ClassModifier.ACC_ANNOTATION;

	/** Enum or Module. */
	public static final int ACC_ENUM_MODULE = ClassModifier.ACC_ENUM | ClassModifier.ACC_MODULE;

	public static final int ACC_IGNORED_FIELD_FLAGS = FieldFlags.ACC_STATIC | FieldFlags.ACC_FINAL | FieldFlags.ACC_VOLATILE | FieldFlags.ACC_TRANSIENT | FieldFlags.ACC_SYNTHETIC | FieldFlags.ACC_ENUM;

	public SimpleClassMapper() {
	}

	public synchronized ClassData mapClass(Class<?> clazz, TypeData[] parameterTypes) throws ClassMapperException {
		return mapClass(clazz);
	}

	public synchronized ClassData mapClass(Class<?> clazz) throws ClassMapperException {
		AnnotationsFactory<JSONAnnotations> annoFact = new JSONAnnotationsFactory();

		ClassData classData;
		List<ClassData> classDataList;
		List<ClassData> postProcessClassFields;
		ClassData[] classDataArr;
		//Class<?>[] clazzArguments = ZERO_CLAZZ_ARGUMENTS;
		TypeVariable<?>[] typeParameters;
		Type superType;
		//ParameterizedType parameterizedType;
		//Type[] argumentTypes;
		//Class<?> superClazz;
		//Type argumentType;
		//String varName;
		//int aIdx;
		int idx;
		int len;
		boolean bLoop;
		int modifiers;
		int maskedModifiers;
		/*
		 * Check if we mapped this class definition already.
		 */
		classData = classMappings.get(clazz.getName());
		if (classData == null) {
			try {
				classDataList = new ArrayList<>(8);
				postProcessClassFields = new ArrayList<>(4);
				bLoop = clazz != null && "java.lang.Object".compareTo(clazz.getTypeName()) != 0;
				if (!bLoop) {
					throw new ClassMapperException(String.format("Mapping class %s not supported.", clazz.getTypeName()));
				}
				/*
				if (maskedModifiers == ACC_INTERFACE_ANNOTATION) {
					throw new ClassMapperException(String.format("Annotation interface %s can not be mapped.", clazz.getTypeName()));
				}
				*/
				/*
				if ((modifiers & ClassModifier.ACC_PUBLIC) == 0) {
					throw new ClassMapperException(String.format("Class %s not public.", clazz.getTypeName()));
				}
				*/
				while (bLoop) {
					modifiers = clazz.getModifiers();
					maskedModifiers = modifiers & ACC_INTERFACE_ANNOTATION;
					if (maskedModifiers != 0) {
						throw new ClassMapperException(String.format("Mapping (annotation) interfaces not supported. %s can not be mapped.", clazz.getTypeName()));
					}
					else {
						maskedModifiers = modifiers & ACC_ENUM_MODULE;
						if (maskedModifiers != 0) {
							throw new ClassMapperException(String.format("Modifiers for class %s not supported by mapper. (%s)", clazz.getTypeName(), Modifier.toString(maskedModifiers)));
						}
					}
					classData = new ClassData();
					postProcessClassFields.add(classData);
					classData.clazz = clazz;
					classData.annotations = annoFact.getInstance();
					classData.annotations.processClass(clazz);
					classData.modifiers = modifiers;
					classDataList.add(classData);
					classMappings.put(clazz.getName(), classData);
					// debug
					//System.out.println("[" + clazzDataList.size() + "]: " + clazz.getClass().getTypeName());
					//System.out.println("[" + clazzDataList.size() + "]: " + clazz.getTypeName());
					/*
					 * Check for generic TypeVariable(s).
					 */
					typeParameters = clazz.getTypeParameters();
					classData.typeParameters = typeParameters;
					classData.typeVarNameIds = TypeData.getTypeVarNameIds(typeParameters);
					//classData.clazzArguments = clazzArguments;
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
					/*
					if (typeParameters.length != clazzArguments.length) {
						throw new ClassMapperException("Class '" + clazz.getTypeName() + "' # of parameters(" + typeParameters.length + ") do not equal # of arguments(" + clazzArguments.length + ").");
					}
					*/
					/*
					 * Check for ActualTypeArgument(s) in extended Class.
					 */
					superType = clazz.getGenericSuperclass();
					TypeData typeData = new TypeData();
					TypeData.mapType(superType, "Class: '" + clazz.getName() + "' extends", typeData);
					classData.superType = superType;
					classData.superTypeData = typeData;
					// FIXME Not really needed with the new typedata structure..
					//parameterizedType = null;
					//argumentTypes = null;
					//superClazz = null;
					/*
					if (superType != null) {
						// debug
						//System.out.println("  genericSuperClassType: " + superType.getClass().getTypeName());
						//System.out.println("      genericSuperClass: " + superType.getTypeName());
						if (superType instanceof ParameterizedType) {
							parameterizedType = (ParameterizedType)superType;
							classData.parameterizedType = parameterizedType;
							argumentTypes = parameterizedType.getActualTypeArguments();
							classData.argumentTypes = argumentTypes;
						}
						else if (superType instanceof Class) {
							// Pretty much like clazz.getSuperclass();
							superClazz = (Class<?>)superType;
							classData.superClazz = superClazz;
						}
						else {
							throw new ClassMapperException(String.format("Unexpected generic superclass type for class %s. (type: %s class: %s)", clazz.getTypeName(), superType.getClass().getTypeName(), superType.getTypeName()));
						}
					}
					*/
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
					Field[] declaredFields = clazz.getDeclaredFields();
					classData.declaredFields = declaredFields;
					Field field;
					boolean bIgnore;
					Class<?> fieldType = null;
					String fieldTypeName;
					//½int classTypeMask;

					Type type;

					List<FieldData> fields = new ArrayList<>(32);
					FieldData fieldData;
					for (int fIdx = 0; fIdx<declaredFields.length; ++fIdx) {
						field = declaredFields[fIdx];
						bIgnore = classData.annotations.ignore(clazz.getName(), field);
						modifiers = field.getModifiers();
						if (!bIgnore && (modifiers & ACC_IGNORED_FIELD_FLAGS) == 0) {
							fieldType = field.getType();
							fieldTypeName = fieldType.getName();
							//classTypeMask = ClassTypeModifiers.getClassTypeModifiersMask(fieldType);
							// treeset3 hashmap3
							// debug.
							/*
							if ("hashmap3".equalsIgnoreCase(field.getName())) {
								System.out.println("Breakpoint...");
							}
							*/
							fieldData = new FieldData();
							type = field.getGenericType();
							TypeData.mapType(type, "Field: '" + field.getName() + "'", fieldData);
							fieldData.field = field;
							fieldData.fieldName = field.getName();
							fieldData.fieldType = fieldType;
							fieldData.fieldTypeName = fieldTypeName;

							/*
							fieldData.type = type;
							fieldData.arrayType = arrayType;
							fieldData.fieldTypeInstance = fieldTypeInstance;
							fieldData.fieldObjectMapping = fieldObjectMapping;
							fieldData.parametrizedObjectTypes = parametrizedObjectTypes;
							fieldData.parametrizedObjectMappings = parametrizedObjectMappings;
							clazzData.fieldDataList.add(fieldData);
							fieldDataList.add(fieldData);
							*/
							fields.add(fieldData);
							/*
							System.out.println("");
							System.out.println(fieldData.typeToString());
							System.out.println("");
							*/
							/*
							*/

							//typeId = null;
	
							//typeId = JSONObjectMappingConstants.primitiveTypeMappings.get(fieldTypeName);
							//if (typeId != null) {
								/*
								 * Primitive type.
								 */
								/*
								fieldData = new FieldData();
								fieldData.field = field;
								fieldData.fieldName = field.getName();
								fieldData.fieldType = fieldType;
								fieldData.fieldTypeName = fieldTypeName;
								fieldData.modifiers = modifiers;
								fieldData.typeId = typeId;
								fields.add(fieldData);
								*/
							//}
						} 
					}
					classData.fieldsClass = fields.toArray(new FieldData[fields.size()]);
					/*
					 * Prepare for parent class next.
					 */
					clazz = clazz.getSuperclass();
					bLoop = clazz != null && "java.lang.Object".compareTo(clazz.getTypeName()) != 0;
					if (bLoop) {
						classData = classMappings.get(clazz.getName());
						if (classData != null) {
							classDataArr = classData.classDataArr;
							for (int i=0; i<classDataArr.length; ++i) {
								classDataList.add(classDataArr[i]);
							}
							bLoop = false;
						}
					}
					/*
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
											clazzArguments[i] = classData.clazzArguments[aIdx];
										}
										++aIdx;
									}
								}
								else {
									throw new ClassMapperException("Unsupported generics argument: " + argumentType.getClass());
								}
							}
						}
					}
					*/
				}
				classDataArr = classDataList.toArray(new ClassData[classDataList.size()]);
				idx = classDataArr.length - 1;
				len = 1;
				while (idx >= 0) {
					classData = classDataArr[idx]; 
					classData.classDataArr = new ClassData[len];
					System.arraycopy(classDataArr, idx, classData.classDataArr, 0, len);
					--idx;
					++len;
				}
				idx = classDataArr.length - 1;
				FieldData[] inheritedFieldsArr = classDataArr[idx].fieldsClass;
				classDataArr[idx].fieldsInherited = inheritedFieldsArr;
				FieldData[] fieldsClassArr;
				FieldData[] newFieldsInheritedArr;
				--idx;
				while (idx >= 0) {
					classData = classDataArr[idx]; 
					fieldsClassArr = classData.fieldsClass;
					newFieldsInheritedArr = new FieldData[fieldsClassArr.length + inheritedFieldsArr.length];
					classDataArr[idx].fieldsInherited = newFieldsInheritedArr;
					System.arraycopy(fieldsClassArr, 0, newFieldsInheritedArr, 0, fieldsClassArr.length);
					System.arraycopy(inheritedFieldsArr, 0, newFieldsInheritedArr, fieldsClassArr.length, inheritedFieldsArr.length);
					inheritedFieldsArr = newFieldsInheritedArr;
					--idx;
				}
				// ClassDataArrr needs to be available to map <code>TypeData</code> classes.
				ClassData tmpClassData;
				FieldData fieldData;
				for (int i=0; i<postProcessClassFields.size(); ++i) {
					tmpClassData = postProcessClassFields.get(i);
					for (int j=0; j<tmpClassData.fieldsClass.length; ++j) {
						fieldData = tmpClassData.fieldsClass[j];
						mapTypeDataClasses(fieldData);
						if (fieldData.bUnresolved) {
							++tmpClassData.unresolvedFields;
						}
					}
				}
			}
			// ClassNotFoundException
			catch (Throwable e) {
				throw new ClassMapperException(e);
			}
		}
		return classData;
	}

	protected TypeData[][] parameterTypesStack = new TypeData[16][];

	protected int[] parameterTypesIdxs = new int[16];

	public synchronized void mapTypeDataClasses(TypeData typeData) throws ClassMapperException {
		//TypeData typeData;
		TypeData[] currParameterTypes;
		int ptIdx;
		int ptStackLvl;
		if (!typeData.bTypeVariable && !typeData.bInterfaceInstance && !typeData.bCollection) {
			if (typeData.typeId == 0  && typeData.arrayType == 0 && typeData.colType == 0) {
				if (typeData.classData == null) {
					typeData.classData = mapClass((Class<?>) typeData.type);
				}
			}
		}
		// typeData.parameterTypes != null
		// Currently never null.
		if (typeData.parameterTypes.length > 0) {
			currParameterTypes = typeData.parameterTypes;
			ptIdx = 0;
			ptStackLvl = 0;
			do {
				if (ptIdx < currParameterTypes.length) {
					typeData = currParameterTypes[ptIdx++];
					if (!typeData.bTypeVariable && !typeData.bInterfaceInstance && !typeData.bCollection) {
						if (typeData.typeId == 0  && typeData.arrayType == 0 && typeData.colType == 0) {
							if (typeData.classData == null) {
								typeData.classData = mapClass((Class<?>) typeData.type);
							}
						}
					}
					// typeData.parameterTypes != null
					// Currently never null.
					if (typeData.parameterTypes.length > 0) {
						parameterTypesStack[ptStackLvl] = currParameterTypes;
						parameterTypesIdxs[ptStackLvl] = ptIdx;
						++ptStackLvl;
						currParameterTypes = typeData.parameterTypes;
						ptIdx = 0;
					}
				}
				while (ptStackLvl > 0 && ptIdx == currParameterTypes.length) {
					--ptStackLvl;
					currParameterTypes = parameterTypesStack[ptStackLvl];
					ptIdx = parameterTypesIdxs[ptStackLvl];
				}
			} while (ptStackLvl > 0 || ptIdx < currParameterTypes.length);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, ClassData>> iter = classMappings.entrySet().iterator();
		Entry<String, ClassData> entry;
		//String key;
		ClassData classData;
		while (iter.hasNext()) {
			entry = iter.next();
			//key = entry.getKey();
			classData = entry.getValue();
			//sb.append(key);
			//sb.append(" / ");
			sb.append(classData.toGenericString());
			// Currently never null.
			if (classData.typeParameters.length > 0 || classData.superTypeData.parameterTypes.length > 0) {
				sb.append("  (");
				sb.append(classData.typeParameters.length);
				sb.append("/");
				sb.append(classData.superTypeData.parameterTypes.length);
				sb.append("/");
				sb.append(classData.superTypeData.bUnresolved);
				sb.append("/");
				sb.append(classData.unresolvedFields);
				sb.append(")");
			}
			sb.append("\n");
		}
		sb.append(TypeData.typeVarNameIdsToString());
		sb.append("\n");
		return sb.toString();
	}

}
