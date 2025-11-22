package com.antiaction.common.json;

import org.junit.Assert;

import com.antiaction.common.classmapper.ClassMapperException;
import com.antiaction.common.classmapper.TypeData;

public class TestPrimitiveTypes {

	public static void main(String[] args) {
		//System.out.println(JSONObjectMappingConstants.primitivTypesToString());
		//System.out.println(JSONObjectMappingConstants.arrayPrimitiveTypesToString());

		Class<?> clazz;
		String typeName;
		int typeId;
		int typeId2;
		TypeData typeData;

		try {
			for (int i=0; i<JSONObjectMappingConstants.primitiveTypesArr.length; ++i) {
				clazz = (Class<?>)JSONObjectMappingConstants.primitiveTypesArr[i][0];
				typeName = clazz.getName();
				typeId = (int)JSONObjectMappingConstants.primitiveTypesArr[i][1];
				typeId2 = JSONObjectMappingConstants.primitiveTypeMappings.get(typeName);
				Assert.assertEquals(typeId, typeId2);
				typeData = TypeData.mapType(clazz, "class", new TypeData());
				// Debug
				//System.out.println(typeName + " - " + typeData.toString());
				//System.out.println(typeId);
				//System.out.println(typeData.typeId + " - " + typeData.arrayTypeId + " - " + typeData.colTypeId);
				Assert.assertEquals(typeId, typeData.typeId);
				Assert.assertEquals(0, typeData.arrayTypeId);
				//Assert.assertEquals(Integer.valueOf(0), typeData.colTypeId);
				Assert.assertEquals(null, typeData.colTypeId);
			}

			Assert.assertEquals(JSONObjectMappingConstants.primitiveTypesArr.length, JSONObjectMappingConstants.primitiveTypeMappings.size());

			for (int i=0; i<JSONObjectMappingConstants.primitiveArrayTypesArr.length; ++i) {
				clazz = (Class<?>)JSONObjectMappingConstants.primitiveArrayTypesArr[i][0];
				typeName = clazz.getName();
				typeId = (int)JSONObjectMappingConstants.primitiveArrayTypesArr[i][1];
				typeId2 = JSONObjectMappingConstants.arrayPrimitiveTypeMappings.get(typeName);
				Assert.assertEquals(typeId, typeId2);
				typeData = TypeData.mapType(clazz, "class", new TypeData());
				// Debug
				//System.out.println(typeName + " - " + typeData.toString());
				//System.out.println(typeId);
				//System.out.println(typeData.typeId + " - " + typeData.arrayTypeId + " - " + typeData.colTypeId);
				Assert.assertEquals(JSONObjectMappingConstants.T_ARRAY, typeData.typeId);
				Assert.assertEquals(typeId, typeData.arrayTypeId);
				//Assert.assertEquals(Integer.valueOf(0), typeData.colTypeId);
				Assert.assertEquals(null, typeData.colTypeId);
			}

			Assert.assertEquals(JSONObjectMappingConstants.primitiveArrayTypesArr.length, JSONObjectMappingConstants.arrayPrimitiveTypeMappings.size());
		}
		catch (ClassMapperException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}
	}

}
