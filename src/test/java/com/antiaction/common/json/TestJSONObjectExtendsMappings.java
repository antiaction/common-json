package com.antiaction.common.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.annotation.JSONParamTypeInstance;
import com.antiaction.common.json.annotation.JSONTypeInstance;

@RunWith(JUnit4.class)
public class TestJSONObjectExtendsMappings {

	public static class Test2_3<T extends Test2_3<?>> {
		@JSONTypeInstance(ArrayList.class)
		public List<T> list3;
		@JSONTypeInstance(HashMap.class)
		public Map<String, T> map3;
		@JSONTypeInstance(HashSet.class)
		public Set<T> set3;

		public LinkedList<String> linkedlist3;
		@JSONParamTypeInstance(index=0, clazz=Test2_1.class)
		public TreeSet<Test2_3<T>> treeset3;
		@JSONParamTypeInstance(index=0, clazz=Integer.class)
		@JSONParamTypeInstance(index=1, clazz=Test2_1.class)
		public HashMap<String, Test2_3<Test2_2<T>>> hashmap3;
	}

	public static class Test2_2<T extends Test2_3<?>> extends Test2_3<T> {
		@JSONTypeInstance(ArrayList.class)
		public List<T> list2;
		@JSONTypeInstance(HashMap.class)
		public Map<String, T> map2;
		@JSONTypeInstance(HashSet.class)
		public Set<T> set2;
	}

	public static class Test2_1 extends Test2_2<Test2_1> {
		@JSONTypeInstance(ArrayList.class)
		public List<Test2_1> list1;
		public ArrayList<Test2_1> arraylist1;
		@JSONTypeInstance(HashMap.class)
		public Map<String, Test2_1> map1;
		public TreeMap<String, Test2_1> treemap1;
		@JSONTypeInstance(HashSet.class)
		public Set<Test2_1> set1;
		public HashSet<Test2_1> hashset1;
	}

	/*
	public static class Four {
	}
	*/

	@Test
	public void test_jsonmappings_objectextends() {
		try {
			JSONObjectMappings json_objectmappings = new JSONObjectMappings();
			//JSONObjectMapping objectMapping1 = json_objectmappings.register(Four.class);
			JSONObjectMapping objectMapping2 = json_objectmappings.register(Test2_1.class);
			System.out.println(JSONClassData.toString(objectMapping2.classDataList));
			System.out.println(objectMapping2.toString());
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception!");
		}
		Class<?> clazz = Test2_1.class;
		System.out.println("declared fields: " + clazz.getDeclaredFields().length);
		System.out.println("fields: " + clazz.getFields().length);
	}

	/*
	@Test
	public void test_jsonmappings_objectextends2() {
		try {
			JSONObjectMappings json_objectmappings = new JSONObjectMappings();
			//JSONObjectMapping objectMapping1 = json_objectmappings.register(Four.class);
			JSONObjectMapping objectMapping2 = json_objectmappings.register(T2a.class);
			System.out.println(JSONClassData.toString(objectMapping2.classDataList));
			System.out.println(objectMapping2.toString());
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception!");
		}
	}
	*/

}
