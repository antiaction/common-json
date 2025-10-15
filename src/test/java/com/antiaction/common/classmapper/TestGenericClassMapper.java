package com.antiaction.common.classmapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.classmapper.TestSimpleClassMapper.Test1;
import com.antiaction.common.classmapper.TestSimpleClassMapper.Test3_1;
import com.antiaction.common.json.TestJSONObjectExtendsMappings.Test2_1;
import com.antiaction.common.json.TestJSONObjectExtendsMappings.Test2_2;
import com.antiaction.common.json.TestJSONObjectExtendsMappings.Test2_3;

@RunWith(JUnit4.class)
public class TestGenericClassMapper {

	@Test
	public void test_genericclassmapper() {
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

			//Assert.assertEquals(4, scm.classMappings.size());

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

			// GCM

			System.out.println("--------");
			System.out.println("GenericClassMapper.");

			GenericClassMapper gcm = new GenericClassMapper();

			classData1 = gcm.mapClass(Test1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			System.out.println(classData1);

			classData1 = gcm.mapClass(Test2_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			System.out.println(classData1);

			classData1 = gcm.mapClass(Test3_1.class, SimpleClassMapper.ZERO_CLAZZ_ARGUMENTS);
			System.out.println(classData1);

			System.out.println(gcm.toString());
		}
		catch (ClassMapperException e) {
			e.printStackTrace();
		}
	}

}
