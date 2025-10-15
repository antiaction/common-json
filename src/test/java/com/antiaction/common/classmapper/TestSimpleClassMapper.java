package com.antiaction.common.classmapper;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.TestJSONObjectExtendsMappings.Test2_1;
import com.antiaction.common.json.TestJSONObjectExtendsMappings.Test2_2;
import com.antiaction.common.json.TestJSONObjectExtendsMappings.Test2_3;
import com.antiaction.common.json.annotation.JSONTypeInstance;

@RunWith(JUnit4.class)
public class TestSimpleClassMapper {

	public static class Test1 {
		public int i;
	}

	/*
	public static class Test2<T> {
		public T t;
	}
	*/

	public static class Test3_1 {
		@JSONTypeInstance(LinkedList.class)
		public List<Test3_2<Test3_3>> list;
	}

	public static class Test3_2<T> {
		public T t;
	}

	public static class Test3_3 {
	}

	/*
	public static class Test4 {
		public List<String>[] arrayOfLists;
	}
	*/

	@Test
	public void test_simpleclassmapper() {
		SimpleClassMapper scm;
		ClassData classData1;
		ClassData classData2;
		ClassData classData3;
		ClassData classDataCached;
		try {
			scm = new SimpleClassMapper();

			// 1

			classData1 = scm.mapClass(Test1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(1, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
			System.out.println(classData1);

			//Assert.assertEquals(1, scm.classMappings.size());

			classDataCached = scm.mapClass(Test1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);
			classDataCached = scm.mapClass(Test1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);

			Assert.assertEquals(1, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);

			//Assert.assertEquals(1, scm.classMappings.size());

			// 2,1

			classData1 = scm.mapClass(Test2_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(3, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
			System.out.println(classData1);

			//7Assert.assertEquals(4, scm.classMappings.size());

			classDataCached = scm.mapClass(Test2_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);
			classDataCached = scm.mapClass(Test2_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);

			Assert.assertEquals(3, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
 
			//Assert.assertEquals(4, scm.classMappings.size());

			// 2.2

			classData2 = scm.mapClass(Test2_2.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(2, classData2.classDataArr.length);
			Assert.assertEquals(classData2, classData2.classDataArr[0]);
			System.out.println(classData2);

			//Assert.assertEquals(4, scm.classMappings.size());

			classDataCached = scm.mapClass(Test2_2.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData2 == classDataCached);
			classDataCached = scm.mapClass(Test2_2.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData2 == classDataCached);

			Assert.assertEquals(2, classData2.classDataArr.length);
			Assert.assertEquals(classData2, classData2.classDataArr[0]);

			Assert.assertEquals(3, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
			Assert.assertEquals(classData2, classData1.classDataArr[1]);

			//Assert.assertEquals(4, scm.classMappings.size());

			// 2.3

			classData3 = scm.mapClass(Test2_3.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(1, classData3.classDataArr.length);
			Assert.assertEquals(classData3, classData3.classDataArr[0]);
			System.out.println(classData3);

			//Assert.assertEquals(4, scm.classMappings.size());

			classDataCached = scm.mapClass(Test2_3.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData3 == classDataCached);
			classDataCached = scm.mapClass(Test2_3.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData3 == classDataCached);

			Assert.assertEquals(1, classData3.classDataArr.length);
			Assert.assertEquals(classData3, classData3.classDataArr[0]);

			Assert.assertEquals(2, classData2.classDataArr.length);
			Assert.assertEquals(classData2, classData2.classDataArr[0]);
			Assert.assertEquals(classData3, classData2.classDataArr[1]);

			Assert.assertEquals(3, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
			Assert.assertEquals(classData2, classData1.classDataArr[1]);
			Assert.assertEquals(classData3, classData1.classDataArr[2]);

			//Assert.assertEquals(4, scm.classMappings.size());

			// 3.1

			classData1 = scm.mapClass(Test3_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(1, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
			System.out.println(classData1);

			//Assert.assertEquals(5, scm.classMappings.size());

			classDataCached = scm.mapClass(Test3_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);
			classDataCached = scm.mapClass(Test3_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);

			Assert.assertEquals(1, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);

			//Assert.assertEquals(5, scm.classMappings.size());

			// 4

			/*
			classData1 = scm.mapClass(Test4.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(1, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
			System.out.println(classData1);

			Assert.assertEquals(6, scm.classMappings.size());

			classDataCached = scm.mapClass(Test4.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);
			classDataCached = scm.mapClass(Test4.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);

			Assert.assertEquals(1, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);

			Assert.assertEquals(6, scm.classMappings.size());
			*/

			System.out.println(scm.toString());

			scm = new SimpleClassMapper();

			// 2.3

			classData3 = scm.mapClass(Test2_3.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(1, classData3.classDataArr.length);
			Assert.assertEquals(classData3, classData3.classDataArr[0]);
			System.out.println(classData3);

			//Assert.assertEquals(1, scm.classMappings.size());

			classDataCached = scm.mapClass(Test2_3.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData3 == classDataCached);
			classDataCached = scm.mapClass(Test2_3.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData3 == classDataCached);

			Assert.assertEquals(1, classData3.classDataArr.length);
			Assert.assertEquals(classData3, classData3.classDataArr[0]);

			//Assert.assertEquals(1, scm.classMappings.size());

			// 2.2

			classData2 = scm.mapClass(Test2_2.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(2, classData2.classDataArr.length);
			Assert.assertEquals(classData2, classData2.classDataArr[0]);
			Assert.assertEquals(classData3, classData2.classDataArr[1]);
			System.out.println(classData2);

			//Assert.assertEquals(2, scm.classMappings.size());

			classDataCached = scm.mapClass(Test2_2.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData2 == classDataCached);
			classDataCached = scm.mapClass(Test2_2.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData2 == classDataCached);

			Assert.assertEquals(2, classData2.classDataArr.length);
			Assert.assertEquals(classData2, classData2.classDataArr[0]);
			Assert.assertEquals(classData3, classData2.classDataArr[1]);

			Assert.assertEquals(1, classData3.classDataArr.length);
			Assert.assertEquals(classData3, classData3.classDataArr[0]);

			//Assert.assertEquals(2, scm.classMappings.size());

			// 2.1

			classData1 = scm.mapClass(Test2_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertEquals(3, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
			Assert.assertEquals(classData2, classData1.classDataArr[1]);
			Assert.assertEquals(classData3, classData1.classDataArr[2]);
			System.out.println(classData1);

			//Assert.assertEquals(3, scm.classMappings.size());

			classDataCached = scm.mapClass(Test2_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);
			classDataCached = scm.mapClass(Test2_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			Assert.assertTrue(classData1 == classDataCached);

			Assert.assertEquals(3, classData1.classDataArr.length);
			Assert.assertEquals(classData1, classData1.classDataArr[0]);
			Assert.assertEquals(classData2, classData1.classDataArr[1]);
			Assert.assertEquals(classData3, classData1.classDataArr[2]);

			Assert.assertEquals(2, classData2.classDataArr.length);
			Assert.assertEquals(classData2, classData2.classDataArr[0]);
			Assert.assertEquals(classData3, classData2.classDataArr[1]);

			Assert.assertEquals(1, classData3.classDataArr.length);
			Assert.assertEquals(classData3, classData3.classDataArr[0]);

			//Assert.assertEquals(3, scm.classMappings.size());

			System.out.println(scm.toString());

			Class<?> clazz = Test2_1.class;
			Type superType = clazz.getGenericSuperclass();
			TypeData typeData = new TypeData();
			//TypeData.mapType(superType, "Class: '" + clazz.getName() + "' extends", typeData);
			TypeData.mapType(superType, "...", typeData);

			clazz = Test2_2.class;
			superType = clazz.getGenericSuperclass();
			typeData = new TypeData();
			//TypeData.mapType(superType, "Class: '" + clazz.getName() + "' extends", typeData);
			TypeData.mapType(superType, "...", typeData);

			clazz = Test2_3.class;
			superType = clazz.getGenericSuperclass();
			typeData = new TypeData();
			//TypeData.mapType(superType, "Class: '" + clazz.getName() + "' extends", typeData);
			TypeData.mapType(superType, "...", typeData);
		}
		catch (ClassMapperException e) {
			e.printStackTrace();
		}

		//System.out.println(JSONObjectMappingConstants.primitivTypesToString());
		//System.out.println(JSONObjectMappingConstants.arrayPrimitiveTypesToString());
	}

}
