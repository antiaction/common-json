package com.antiaction.common.classmapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.classmapper.TestSimpleClassMapper.Test3_1;

@RunWith(JUnit4.class)
public class TestGenerics {

	public String string;

	public List list;

	public Map map;

	public List<String> glist;

	public Map<String, Integer> gmap;

	@Test
	@Ignore
	public void test_generics() {
		Field field;
		Type type;
		ParameterizedType pType;
		Type[] types;
		System.out.println("--");
		try {
			field = Test3_1.class.getField("list");
			System.out.println(field);
			type = field.getGenericType();
			System.out.println(type.getClass());
			System.out.println(type);
			if (type instanceof ParameterizedType) {
				pType = (ParameterizedType) type;
				System.out.println(pType.getRawType().getTypeName());
				types = pType.getActualTypeArguments();
				System.out.println(types.length);
				System.out.println(types[0]);
				type = types[0];
				System.out.println(type.getClass());
				System.out.println(type);
				if (type instanceof ParameterizedType) {
					pType = (ParameterizedType) type;
					System.out.println(pType.getRawType().getTypeName());
					types = pType.getActualTypeArguments();
					System.out.println(types.length);
					System.out.println(types[0]);
					type = types[0];
					System.out.println(type.getClass());
					System.out.println(type.getTypeName());
				}
			}

			FieldData testFieldData;

			// TODO
			/*
			System.out.println("--");

			field = TestGenerics.class.getField("string");
			testFieldData = new FieldData(field);
			System.out.println(testFieldData);

			System.out.println("--");

			field = TestGenerics.class.getField("list");
			testFieldData = new FieldData(field);
			System.out.println(testFieldData);

			System.out.println("--");

			field = TestGenerics.class.getField("map");
			testFieldData = new FieldData(field);
			System.out.println(testFieldData);

			System.out.println("--");

			field = TestGenerics.class.getField("glist");
			testFieldData = new FieldData(field);
			System.out.println(testFieldData);

			System.out.println("--");

			field = TestGenerics.class.getField("gmap");
			testFieldData = new FieldData(field);
			System.out.println(testFieldData);

			System.out.println("--");

			field = Test3_1.class.getField("list");
			testFieldData = new FieldData(field);
			System.out.println(testFieldData);
			*/
		}
		catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		System.out.println("--");

		ObjectClassFromReflection<?> ocfr = new ObjectClassFromReflection<List<String>>(new ArrayList<String>());

		System.out.println(ocfr.getClazz().toGenericString());
		*/
	}

}
