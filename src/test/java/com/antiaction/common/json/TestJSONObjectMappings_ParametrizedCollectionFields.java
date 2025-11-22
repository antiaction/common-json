/*
 * JSON library.
 * Copyright 2012-2013 Antiaction (http://antiaction.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antiaction.common.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.annotation.JSONTypeInstance;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 18/12/2012
 */
@RunWith(JUnit4.class)
public class TestJSONObjectMappings_ParametrizedCollectionFields {

	@Test
	@Ignore
	public void test_jsonobjectmappings_generic_field() {
		JSONObjectMappings json_om = new JSONObjectMappings();
		try {
			json_om.register( TestJSONMapObjectGenerics.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
	}

	public static class TestJSONMapObjectGenerics<T> {

		protected int i1;

		protected Integer i2;

		protected TestJSONMapObject obj;

		protected T t;

	}

	public static class TestJSONMapObject {

		protected Integer i;

	}

	public static String missing(String className) {
		return "Missing @JSONTypeInstance annotation on collection interface field of type: " + className;
	}

	public static String parametrized(String className) {
		return "Collection must have parametrized type(s). (" + className + ")";
	}

	public static String unsupported() {
		return "Unsupported use of wildcard parameterized types.";
	}

	@Test
	public void test_jsonobjectmappings_parametrizedcollectionfields() {
		JSONObjectMappings json_om = new JSONObjectMappings();

		/*
		 * Lists.
		 */

		Object[][] testClassesInvalid = new Object[][] {
			{ TestJSONMapInvalidList1.class, missing( "java.util.List" ) },
			{ TestJSONMapInvalidList2.class, parametrized( "java.util.ArrayList" ) },
			{ TestJSONMapInvalidList3.class, parametrized( "java.util.LinkedList" ) },
			{ TestJSONMapInvalidList4.class, missing( "java.util.List" ) },
			{ TestJSONMapInvalidList5.class, unsupported() },
			{ TestJSONMapInvalidList6.class, unsupported() },
			{ TestJSONMapInvalidList7.class, missing( "java.util.List" ) },
			{ TestJSONMapInvalidList8.class, unsupported() },
			{ TestJSONMapInvalidList9.class, unsupported() },
			{ TestJSONMapInvalidList10.class, missing( "com.antiaction.common.json.TestJSONObjectMappings_ParametrizedCollectionFields$List3" ) },
			{ TestJSONMapInvalidMap1.class, missing( "java.util.Map" ) },
			{ TestJSONMapInvalidMap2.class, parametrized( "java.util.HashMap" ) },
			{ TestJSONMapInvalidMap3.class, parametrized( "java.util.TreeMap" ) },
			{ TestJSONMapInvalidMap4.class, missing( "java.util.Map" ) },
			{ TestJSONMapInvalidMap5.class, unsupported() },
			{ TestJSONMapInvalidMap6.class, unsupported() },
			{ TestJSONMapInvalidMap7.class, missing( "java.util.Map" ) },
			{ TestJSONMapInvalidMap8.class, unsupported() },
			{ TestJSONMapInvalidMap9.class, unsupported() },
			{ TestJSONMapInvalidMap10.class, missing( "com.antiaction.common.json.TestJSONObjectMappings_ParametrizedCollectionFields$Map3" ) },
			{ TestJSONMapInvalidSet1.class, missing( "java.util.Set" ) },
			{ TestJSONMapInvalidSet2.class, parametrized( "java.util.HashSet" ) },
			{ TestJSONMapInvalidSet3.class, parametrized( "java.util.TreeSet" ) },
			{ TestJSONMapInvalidSet4.class, missing( "java.util.Set" ) },
			{ TestJSONMapInvalidSet5.class, unsupported() },
			{ TestJSONMapInvalidSet6.class, unsupported() },
			{ TestJSONMapInvalidSet7.class, missing( "java.util.Set" ) },
			{ TestJSONMapInvalidSet8.class, unsupported() },
			{ TestJSONMapInvalidSet9.class, unsupported() },
			{ TestJSONMapInvalidSet10.class, missing( "com.antiaction.common.json.TestJSONObjectMappings_ParametrizedCollectionFields$Set3" ) }
		};

		Class<?> clazz;
		String msg;

		for ( int i=0; i<testClassesInvalid.length; ++i ) {
			clazz = (Class<?>)testClassesInvalid[ i ][ 0 ];
			msg = (String)testClassesInvalid[ i ][ 1 ];
			try {
				json_om.register( clazz );
				// debug
				System.out.println("Exception expected: " + clazz.getName() + " / " + msg);
				Assert.fail( "Exception expected!" );
			}
			catch (JSONException e) {
				// debug
				//e.printStackTrace();
				//System.out.println( i );
				//System.out.println( e.getMessage() );
				//System.out.println( msg );
				Assert.assertTrue( e.getMessage().endsWith( msg ) );
			}
		}

		Class<?>[] testClassesValid = new Class<?>[] {
				TestJSONMapValidList.class,
				TestJSONMapValidMap.class,
				TestJSONMapValidSet.class
		};

		for ( int i=0; i<testClassesValid.length; ++i ) {
			try {
				json_om.register( testClassesValid[ i ] );
			}
			catch (JSONException e) {
				e.printStackTrace();
				Assert.fail( "Unexpected exception!" );
			}
		}
	}

	/*
	 * Lists.
	 */

	public static class TestJSONMapInvalidList1 {
		@SuppressWarnings("rawtypes")
		public List list;
	}

	public static class TestJSONMapInvalidList2 {
		@SuppressWarnings("rawtypes")
		public ArrayList list;
	}

	public static class TestJSONMapInvalidList3 {
		@SuppressWarnings("rawtypes")
		public LinkedList list;
	}

	public static class TestJSONMapInvalidList4 {
		public List<?> list;
	}

	public static class TestJSONMapInvalidList5 {
		public ArrayList<?> list;
	}

	public static class TestJSONMapInvalidList6 {
		public LinkedList<?> list;
	}

	public static class TestJSONMapInvalidList7 {
		public List<? extends Object> list;
	}

	public static class TestJSONMapInvalidList8 {
		public ArrayList<? extends Object> list;
	}

	public static class TestJSONMapInvalidList9 {
		public LinkedList<? extends Object> list;
	}

	public interface List2<E> extends List<E> {
	};

	public interface List3<E> extends List2<E> {
	};

	public static class TestJSONMapInvalidList10 {
		@SuppressWarnings("rawtypes")
		public List3 list;
	}

	/*
	 * Maps.
	 */

	public static class TestJSONMapInvalidMap1 {
		@SuppressWarnings("rawtypes")
		public Map map;
	}

	public static class TestJSONMapInvalidMap2 {
		@SuppressWarnings("rawtypes")
		public HashMap map;
	}

	public static class TestJSONMapInvalidMap3 {
		@SuppressWarnings("rawtypes")
		public TreeMap map;
	}

	public static class TestJSONMapInvalidMap4 {
		public Map<?, ?> map;
	}

	public static class TestJSONMapInvalidMap5 {
		public HashMap<?, Integer> map;
	}

	public static class TestJSONMapInvalidMap6 {
		public TreeMap<Integer, ?> map;
	}

	public static class TestJSONMapInvalidMap7 {
		public Map<? extends Object, ? extends Object> map;
	}

	public static class TestJSONMapInvalidMap8 {
		public HashMap<? extends Object, Integer> map;
	}

	public static class TestJSONMapInvalidMap9 {
		public TreeMap<Integer, ? extends Object> map;
	}

	public interface Map2<K, V> extends Map<K, V> {
	};

	public interface Map3<K, V> extends Map2<K, V> {
	};

	public static class TestJSONMapInvalidMap10 {
		@SuppressWarnings("rawtypes")
		public Map3 map;
	}

	/*
	 * Sets.
	 */

	public static class TestJSONMapInvalidSet1 {
		@SuppressWarnings("rawtypes")
		public Set set;
	}

	public static class TestJSONMapInvalidSet2 {
		@SuppressWarnings("rawtypes")
		public HashSet set;
	}

	public static class TestJSONMapInvalidSet3 {
		@SuppressWarnings("rawtypes")
		public TreeSet set;
	}

	public static class TestJSONMapInvalidSet4 {
		public Set<?> set;
	}

	public static class TestJSONMapInvalidSet5 {
		public HashSet<?> set;
	}

	public static class TestJSONMapInvalidSet6 {
		public TreeSet<?> set;
	}

	public static class TestJSONMapInvalidSet7 {
		public Set<? extends Object> set;
	}

	public static class TestJSONMapInvalidSet8 {
		public HashSet<? extends Object> set;
	}

	public static class TestJSONMapInvalidSet9 {
		public TreeSet<? extends Object> set;
	}

	public interface Set2<E> extends Set<E> {
	};

	public interface Set3<E> extends Set2<E> {
	};

	public static class TestJSONMapInvalidSet10 {
		@SuppressWarnings("rawtypes")
		public Set3 list;
	}

	/*
	 * Valid use of List, Map, Set.
	 */

	public static class TestJSONMapValidList {
		public ArrayList<String> list1;
		public LinkedList<Integer> list2;
	}

	public static class TestJSONMapValidMap {
		public HashMap<String, String> map1;
		public TreeMap<String, Integer> map22;
	}

	public static class TestJSONMapValidSet {
		public HashSet<String> set1;
		public TreeSet<Integer> set2;
	}

	@Test
	public void test_jsonobjectmapping_typeinstance() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in;

		TypeInstance ti1 = new TypeInstance();
		ti1.list1 = TestHelpers.arrayToLinkedList( new Integer[] { 1, 2 } );
		ti1.list2 = TestHelpers.arrayToArrayList( new Integer[] { 3, 4 } );
		ti1.list3 = TestHelpers.arrayToLinkedList( new Integer[] { 5, 6 } );
		ti1.list4 = TestHelpers.arrayToArrayList( new Integer[] { 7, 8 } );

		JSONObjectMappings json_om = new JSONObjectMappings();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		JSONDecoder json_decoder = json_encoding.getJSONDecoder( JSONEncoding.E_UTF8 );

		try {
			json_om.register( TypeInstance.class );

			out.reset();
			json_om.getStreamMarshaller().toJSONText( ti1, json_encoder, false, out);

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			in = new ByteArrayInputStream( out.toByteArray() );
			TypeInstance ti2 = json_om.getStreamUnmarshaller().toObject( in, json_decoder, TypeInstance.class );

			Assert.assertNotNull( ti2 );
			TestHelpers.assertListEquals( ti1.list1, ti2.list1 );
			TestHelpers.assertListEquals( ti1.list2, ti2.list2 );
			TestHelpers.assertListEquals( ti1.list3, ti2.list3 );
			TestHelpers.assertListEquals( ti1.list4, ti2.list4 );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	public static class TypeInstance {

		protected LinkedList<Integer> list1;

		protected ArrayList<Integer> list2;

		@JSONTypeInstance(LinkedList.class)
		protected List<Integer> list3;

		@JSONTypeInstance(ArrayList.class)
		protected List<Integer> list4;

	}

}
