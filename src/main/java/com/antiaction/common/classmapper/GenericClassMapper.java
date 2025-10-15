package com.antiaction.common.classmapper;

import java.util.ArrayList;
import java.util.List;

public class GenericClassMapper extends SimpleClassMapper {

	//protected SimpleClassMapper scm = new SimpleClassMapper();

	public GenericClassMapper() {
		//scm = new SimpleClassMapper();
	}

	@Override
	public synchronized ClassData mapClass(Class<?> clazz, TypeData[] parameterTypes) throws ClassMapperException {
		//ClassData classData = scm.mapClass(clazz, clazzArgumentsIgnored);
		ClassData topClassData = super.mapClass(clazz);
		ClassData[] topClassDataArr;
		//ClassData preClassData;
		ClassData currClassData;
		int[] typeVarNameIds;
		List<FieldData> fieldsList;
		FieldData[] fieldsArr;
		boolean bLoop;
		if (topClassData != null) {
			topClassDataArr = topClassData.classDataArr;
			if (topClassData.classDataArr.length == 0) {
				throw new ClassMapperException("ClassData array must not be zero length!");
			}
			else if (topClassData.classDataArr.length == 1) {
				if (topClassData.typeParameters.length == 0) {
					return topClassData;
				}
				throw new ClassMapperException("Missing root class parameters!");
			}
			else {
				//preClassData = classData;
				int cIdx = 0;
				bLoop = true;
				while (bLoop) {
					if (cIdx < topClassDataArr.length) {
						currClassData = topClassDataArr[cIdx];
						if (currClassData.typeParameters.length == 0 && currClassData.typeParameters.length == 0) {
							++cIdx;
						}
						else {
							bLoop = false;
						}
					}
					else {
						bLoop = false;
					}
				}
				if (cIdx == topClassDataArr.length) {
					return topClassData;
				}
				if (cIdx == 0) {
					currClassData = topClassDataArr[cIdx];
					typeVarNameIds = currClassData.typeVarNameIds;
					if (parameterTypes.length != typeVarNameIds.length) {
						throw new ClassMapperException("Missing root class parameter(s)!");
					}
				}
				else {
					currClassData = topClassDataArr[cIdx -1];
					parameterTypes = currClassData.superTypeData.parameterTypes;
					currClassData = topClassDataArr[cIdx];
					typeVarNameIds = currClassData.typeVarNameIds;
					if (parameterTypes.length != typeVarNameIds.length) {
						throw new ClassMapperException("Mismatch parameterTypes.length != typeVarNameIds.length!");
					}
				}
				FieldData[] fields;
				FieldData fieldData;
				fieldsList = new ArrayList<>();
				do {
					fields = currClassData.fieldsClass;
					if (typeVarNameIds.length > 0) {
						for (int i=0; i<fields.length; ++i) {
							fieldData = fields[i];
							System.out.println(fields[i].toString());
							if (fields[i].bUnresolved) {
								fieldData = FieldData.replaceTypeVarNames(fieldData, typeVarNameIds, parameterTypes);
								System.out.println(fieldData);
							}
							fieldsList.add(fieldData);
						}
					}
					else {
						for (int i=0; i<fields.length; ++i) {
							fieldData = fields[i];
							fieldsList.add(fieldData);
						}
					}
					if (currClassData.typeParameters.length > 0) {
						parameterTypes = TypeData.replaceTypeVarNames(currClassData.superTypeData, typeVarNameIds, parameterTypes).parameterTypes;
					}
					else {
						parameterTypes = currClassData.superTypeData.parameterTypes;
					}
					++cIdx;
					if (cIdx < topClassDataArr.length) {
						currClassData = topClassDataArr[cIdx];
						typeVarNameIds = currClassData.typeVarNameIds;
						if (parameterTypes.length != typeVarNameIds.length) {
							throw new ClassMapperException("Mismatch parameterTypes.length != typeVarNameIds.length!");
						}
					}
				} while (cIdx < topClassDataArr.length);
				fieldsArr = fieldsList.toArray(new FieldData[fieldsList.size()]);
			}
		}
		return topClassData;
	}

}
