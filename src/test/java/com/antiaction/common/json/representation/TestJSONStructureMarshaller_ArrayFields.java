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

package com.antiaction.common.json.representation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.JSONEncoder;
import com.antiaction.common.json.JSONEncoding;
import com.antiaction.common.json.JSONException;
import com.antiaction.common.json.JSONObjectMappings;
import com.antiaction.common.json.TestHelpers;
import com.antiaction.common.json.annotation.JSON;
import com.antiaction.common.json.annotation.JSONNullValues;
import com.antiaction.common.json.annotation.JSONNullable;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 28/12/2012
 */
@RunWith(JUnit4.class)
public class TestJSONStructureMarshaller_ArrayFields {

	@Test
	public void test_jsonobjectmapper_array_tojson() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		JSONObjectMappings json_objectmappings = new JSONObjectMappings();
		JSONStructureMarshaller marshaller = json_objectmappings.getStructureMarshaller();
		JSONStructureUnmarshaller unmarshaller = json_objectmappings.getStructureUnmarshaller();
		try {
			json_objectmappings.register( TestJSONMapObjectArrays.class );

			TestJSONMapObjectArrays obj = getTestJSONMapObjectArraysObjectMold();
			TestJSONMapObjectArrays result;

			JSONCollection json_struct = marshaller.toJSONStructure( obj );

			out.reset();
			json_textMarshaller.toJSONText( json_struct, json_encoder, false, out );

			byte[] json_compact = out.toByteArray();
			// debug
			//System.out.println( new String( json_compact ) );

			result = unmarshaller.toObject( json_struct, TestJSONMapObjectArrays.class );
			assert_jsonobjectmapper_array_result( result );

			out.reset();
			json_textMarshaller.toJSONText( json_struct, json_encoder, true, out );

			byte[] json_pretty = out.toByteArray();
			// debug
			//System.out.println( new String( json_pretty ) );

			result = unmarshaller.toObject( json_struct, TestJSONMapObjectArrays.class );
			assert_jsonobjectmapper_array_result( result );

            Assert.assertThat( json_compact.length, is( not( equalTo( json_pretty.length ) ) ) );

            json_pretty = TestHelpers.filterWhitespaces( json_pretty );

            Assert.assertEquals( json_compact.length, json_pretty.length );
            Assert.assertArrayEquals( json_compact, json_pretty );
			/*
			 * Null values, primitives.
			 */
			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].b1_arr = new boolean[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].i1_arr = new int[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].l1_arr = new long[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].f1_arr = new float[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].d1_arr = new double[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );
			/*
			 * Null values.
			 */
			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].b2_arr = new Boolean[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].i2_arr = new Integer[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].l2_arr = new Long[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].f2_arr = new Float[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].d2_arr = new Double[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].bi_arr = new BigInteger[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].bd_arr = new BigDecimal[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].s_arr = new String[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].obj_arr = new TestJSONMapObjectArrays[ 1 ];
			assert_tojson_exception( marshaller, obj );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	public TestJSONMapObjectArrays getTestJSONMapObjectArraysObjectMold() {
		TestJSONMapObjectArrays obj = new TestJSONMapObjectArrays();
		obj.b1_arr = new boolean[] { true, false };
		obj.i1_arr = new int[] { 42, 4213 };
		obj.l1_arr = new long[] { 12345678901234L, 12345678901234L * 2L };
		obj.f1_arr = new float[] { 1.0F / 3.0F, 1.0F / 5.0F };
		obj.d1_arr = new double[] { 1.0 / 3.0, 1.0 / 5.0 };
		obj.b2_arr = new Boolean[] { false, true };
		obj.i2_arr = new Integer[] { 1234, 4321 };
		obj.l2_arr = new Long[] { 43210987654321L, 43210987654321L * 2L };
		obj.f2_arr = new Float[] { 3.0F, 5.0F };
		obj.d2_arr = new Double[] { 3.0, 5.0 };
		obj.bi_arr = new BigInteger[] { new BigInteger( "123456789012345678901234567890123456789012" ), new BigInteger( "123456789012345678901234567890123456789012" ).multiply( new BigInteger( "2" ) ) };
		obj.bd_arr = new BigDecimal[] { new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ), new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ).multiply( new BigDecimal( "2" ) ) };
		obj.s_arr = new String[] { "json", "JSON" };
		//obj.b = "bytes".getBytes();
		TestJSONMapObjectArrays obj2 = new TestJSONMapObjectArrays();
		obj.obj_arr = new TestJSONMapObjectArrays[] { obj2 };
		obj2.b1_arr = new boolean[] {};
		obj2.i1_arr = new int[] {};
		obj2.l1_arr = new long[] {};
		obj2.f1_arr = new float[] {};
		obj2.d1_arr = new double[] {};
		obj2.b2_arr = new Boolean[] {};
		obj2.i2_arr = new Integer[] {};
		obj2.l2_arr = new Long[] {};
		obj2.f2_arr = new Float[] {};
		obj2.d2_arr = new Double[] {};
		obj2.bi_arr = new BigInteger[] {};
		obj2.bd_arr = new BigDecimal[] {};
		obj2.s_arr = new String[] {};
		//obj2.b = "BYTES".getBytes();
		obj2.obj_arr = new TestJSONMapObjectArrays[] { new TestJSONMapObjectArrays() };
		return obj;
	}

	public static class TestJSONMapObjectArrays {

		@JSONNullable
		public boolean[] b1_arr;

		@JSONNullable
		public int[] i1_arr;

		@JSONNullable
		public long[] l1_arr;

		@JSONNullable
		public float[] f1_arr;

		@JSONNullable
		public double[] d1_arr;

		@JSONNullable
		public Boolean[] b2_arr;

		@JSONNullable
		public Integer[] i2_arr;

		@JSONNullable
		public Long[] l2_arr;

		@JSONNullable
		public Float[] f2_arr;

		@JSONNullable
		public Double[] d2_arr;

		@JSONNullable
		public BigInteger[] bi_arr;

		@JSONNullable
		public BigDecimal[] bd_arr;

		@JSONNullable
		public String[] s_arr;

		@JSONNullable
		public TestJSONMapObjectArrays[] obj_arr;

	}

	public void assert_jsonobjectmapper_array_result(TestJSONMapObjectArrays result) {
		boolean[] b1_arr = new boolean[] { true, false };
		int[] i1_arr = new int[] { 42, 4213 };
		long[] l1_arr = new long[] { 12345678901234L, 12345678901234L * 2L };
		float[] f1_arr = new float[] { 1.0F / 3.0F, 1.0F / 5.0F };
		double[] d1_arr = new double[] { 1.0 / 3.0, 1.0 / 5.0 };
		Boolean[] b2_arr = new Boolean[] { false, true };
		Integer[] i2_arr = new Integer[] { 1234, 4321 };
		Long[] l2_arr = new Long[] { 43210987654321L, 43210987654321L * 2L };
		Float[] f2_arr = new Float[] { 3.0F, 5.0F };
		Double[] d2_arr = new Double[] { 3.0, 5.0 };
		BigInteger[] bi_arr = new BigInteger[] { new BigInteger( "123456789012345678901234567890123456789012" ), new BigInteger( "123456789012345678901234567890123456789012" ).multiply( new BigInteger( "2" ) ) };
		BigDecimal[] bd_arr = new BigDecimal[] { new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ), new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ).multiply( new BigDecimal( "2" ) ) };
		String[] s_arr = new String[] { "json", "JSON" };

		Assert.assertNotNull( result );
		TestHelpers.assertArrayEquals( b1_arr, result.b1_arr );
		Assert.assertArrayEquals( i1_arr, result.i1_arr );
		Assert.assertArrayEquals( l1_arr, result.l1_arr );
		TestHelpers.assertArrayEquals( f1_arr, result.f1_arr );
		TestHelpers.assertArrayEquals( d1_arr, result.d1_arr );
		Assert.assertArrayEquals( b2_arr, result.b2_arr );
		Assert.assertArrayEquals( i2_arr, result.i2_arr );
		Assert.assertArrayEquals( l2_arr, result.l2_arr );
		Assert.assertArrayEquals( f2_arr, result.f2_arr );
		Assert.assertArrayEquals( d2_arr, result.d2_arr );
		Assert.assertArrayEquals( bi_arr, result.bi_arr );
		Assert.assertArrayEquals( bd_arr, result.bd_arr );
		Assert.assertArrayEquals( s_arr, result.s_arr );

		b1_arr = new boolean[] {};
		i1_arr = new int[] {};
		l1_arr = new long[] {};
		f1_arr = new float[] {};
		d1_arr = new double[] {};
		b2_arr = new Boolean[] {};
		i2_arr = new Integer[] {};
		l2_arr = new Long[] {};
		f2_arr = new Float[] {};
		d2_arr = new Double[] {};
		bi_arr = new BigInteger[] {};
		bd_arr = new BigDecimal[] {};
		s_arr = new String[] {};

		Assert.assertNotNull( result.obj_arr );
		Assert.assertEquals( 1, result.obj_arr.length );
		result = result.obj_arr[ 0 ];

		TestHelpers.assertArrayEquals( b1_arr, result.b1_arr );
		Assert.assertArrayEquals( i1_arr, result.i1_arr );
		Assert.assertArrayEquals( l1_arr, result.l1_arr );
		TestHelpers.assertArrayEquals( f1_arr, result.f1_arr );
		TestHelpers.assertArrayEquals( d1_arr, result.d1_arr );
		Assert.assertArrayEquals( b2_arr, result.b2_arr );
		Assert.assertArrayEquals( i2_arr, result.i2_arr );
		Assert.assertArrayEquals( l2_arr, result.l2_arr );
		Assert.assertArrayEquals( f2_arr, result.f2_arr );
		Assert.assertArrayEquals( d2_arr, result.d2_arr );
		Assert.assertArrayEquals( bi_arr, result.bi_arr );
		Assert.assertArrayEquals( bd_arr, result.bd_arr );
		Assert.assertArrayEquals( s_arr, result.s_arr );

		Assert.assertNotNull( result.obj_arr );
		Assert.assertEquals( 1, result.obj_arr.length );
		result = result.obj_arr[ 0 ];

		Assert.assertNull( result.b1_arr );
		Assert.assertNull( result.i1_arr );
		Assert.assertNull( result.l1_arr );
		Assert.assertNull( result.f1_arr );
		Assert.assertNull( result.d1_arr );
		Assert.assertNull( result.b2_arr );
		Assert.assertNull( result.i2_arr );
		Assert.assertNull( result.l2_arr );
		Assert.assertNull( result.f2_arr );
		Assert.assertNull( result.d2_arr );
		Assert.assertNull( result.bi_arr );
		Assert.assertNull( result.bd_arr );
		Assert.assertNull( result.s_arr );
		Assert.assertNull( result.obj_arr );
	}

	public void assert_tojson_exception(JSONStructureMarshaller marshaller, Object obj) throws IllegalArgumentException, IllegalAccessException {
		try {
			marshaller.toJSONStructure( obj );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
	}

	@Test
	public void test_jsonobjectmapper_arraywithav_tojson() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		JSONObjectMappings json_objectmappings = new JSONObjectMappings();
		JSONStructureMarshaller marshaller = json_objectmappings.getStructureMarshaller();
		JSONStructureUnmarshaller unmarshaller = json_objectmappings.getStructureUnmarshaller();
		try {
			json_objectmappings.register( TestJSONMapObjectArraysWithAV.class );

			TestJSONMapObjectArraysWithAV obj = getTestJSONMapObjectArraysWithAVObjectMold();
			TestJSONMapObjectArraysWithAV result;

			JSONCollection json_struct = marshaller.toJSONStructure( obj );

			out.reset();
			json_textMarshaller.toJSONText( json_struct, json_encoder, false, out );

			byte[] json_compact = out.toByteArray();
			// debug
			//System.out.println( new String( json_compact ) );

			result = unmarshaller.toObject( json_struct, TestJSONMapObjectArraysWithAV.class );
			assert_jsonobjectmapper_arraywithav_result( result );

			out.reset();
			json_textMarshaller.toJSONText( json_struct, json_encoder, true, out );

			byte[] json_pretty = out.toByteArray();
			// debug
			//System.out.println( new String( json_pretty ) );

			result = unmarshaller.toObject( json_struct, TestJSONMapObjectArraysWithAV.class );
			assert_jsonobjectmapper_arraywithav_result( result );

            Assert.assertThat( json_compact.length, is( not( equalTo( json_pretty.length ) ) ) );

            json_pretty = TestHelpers.filterWhitespaces( json_pretty );

            Assert.assertEquals( json_compact.length, json_pretty.length );
            Assert.assertArrayEquals( json_compact, json_pretty );
			/*
			 * Null values, primitives.
			 */
			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].b1_arr = new boolean[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].i1_arr = new int[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].l1_arr = new long[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].f1_arr = new float[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].d1_arr = new double[ 1 ];
			json_struct = marshaller.toJSONStructure( obj );
			/*
			 * Null values.
			 */
			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].b2_arr = new Boolean[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].i2_arr = new Integer[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].l2_arr = new Long[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].f2_arr = new Float[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].d2_arr = new Double[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].bi_arr = new BigInteger[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].bd_arr = new BigDecimal[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].s_arr = new String[ 1 ];
			assert_tojson_exception( marshaller, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].obj_arr = new TestJSONMapObjectArraysWithAV[ 1 ];
			assert_tojson_exception( marshaller, obj );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	public TestJSONMapObjectArraysWithAV getTestJSONMapObjectArraysWithAVObjectMold() {
		TestJSONMapObjectArraysWithAV obj = new TestJSONMapObjectArraysWithAV();
		obj.b1_arr = new boolean[] { true, false };
		obj.i1_arr = new int[] { 42, 4213 };
		obj.l1_arr = new long[] { 12345678901234L, 12345678901234L * 2L };
		obj.f1_arr = new float[] { 1.0F / 3.0F, 1.0F / 5.0F };
		obj.d1_arr = new double[] { 1.0 / 3.0, 1.0 / 5.0 };
		obj.b2_arr = new Boolean[] { false, true };
		obj.i2_arr = new Integer[] { 1234, 4321 };
		obj.l2_arr = new Long[] { 43210987654321L, 43210987654321L * 2L };
		obj.f2_arr = new Float[] { 3.0F, 5.0F };
		obj.d2_arr = new Double[] { 3.0, 5.0 };
		obj.bi_arr = new BigInteger[] { new BigInteger( "123456789012345678901234567890123456789012" ), new BigInteger( "123456789012345678901234567890123456789012" ).multiply( new BigInteger( "2" ) ) };
		obj.bd_arr = new BigDecimal[] { new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ), new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ).multiply( new BigDecimal( "2" ) ) };
		obj.s_arr = new String[] { "json", "JSON" };
		//obj.b = "bytes".getBytes();
		TestJSONMapObjectArraysWithAV obj2 = new TestJSONMapObjectArraysWithAV();
		obj.obj_arr = new TestJSONMapObjectArraysWithAV[] { obj2 };
		obj2.b1_arr = new boolean[] {};
		obj2.i1_arr = new int[] {};
		obj2.l1_arr = new long[] {};
		obj2.f1_arr = new float[] {};
		obj2.d1_arr = new double[] {};
		obj2.b2_arr = new Boolean[] {};
		obj2.i2_arr = new Integer[] {};
		obj2.l2_arr = new Long[] {};
		obj2.f2_arr = new Float[] {};
		obj2.d2_arr = new Double[] {};
		obj2.bi_arr = new BigInteger[] {};
		obj2.bd_arr = new BigDecimal[] {};
		obj2.s_arr = new String[] {};
		//obj2.b = "BYTES".getBytes();
		obj2.obj_arr = new TestJSONMapObjectArraysWithAV[] { new TestJSONMapObjectArraysWithAV() };
		return obj;
	}

	public static class TestJSONMapObjectArraysWithAV {

		@JSONNullable
		@JSONNullValues(false)
		public boolean[] b1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public int[] i1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public long[] l1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public float[] f1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public double[] d1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public Boolean[] b2_arr;

		@JSONNullable
		@JSONNullValues(false)
		public Integer[] i2_arr;

		@JSONNullable
		@JSONNullValues(false)
		public Long[] l2_arr;

		@JSONNullable
		@JSONNullValues(false)
		public Float[] f2_arr;

		@JSONNullable
		@JSONNullValues(false)
		public Double[] d2_arr;

		@JSONNullable
		@JSONNullValues(false)
		public BigInteger[] bi_arr;

		@JSONNullable
		@JSONNullValues(false)
		public BigDecimal[] bd_arr;

		@JSONNullable
		@JSONNullValues(false)
		public String[] s_arr;

		@JSONNullable
		@JSONNullValues(false)
		public TestJSONMapObjectArraysWithAV[] obj_arr;

	}

	public void assert_jsonobjectmapper_arraywithav_result(TestJSONMapObjectArraysWithAV result) {
		boolean[] b1_arr = new boolean[] { true, false };
		int[] i1_arr = new int[] { 42, 4213 };
		long[] l1_arr = new long[] { 12345678901234L, 12345678901234L * 2L };
		float[] f1_arr = new float[] { 1.0F / 3.0F, 1.0F / 5.0F };
		double[] d1_arr = new double[] { 1.0 / 3.0, 1.0 / 5.0 };
		Boolean[] b2_arr = new Boolean[] { false, true };
		Integer[] i2_arr = new Integer[] { 1234, 4321 };
		Long[] l2_arr = new Long[] { 43210987654321L, 43210987654321L * 2L };
		Float[] f2_arr = new Float[] { 3.0F, 5.0F };
		Double[] d2_arr = new Double[] { 3.0, 5.0 };
		BigInteger[] bi_arr = new BigInteger[] { new BigInteger( "123456789012345678901234567890123456789012" ), new BigInteger( "123456789012345678901234567890123456789012" ).multiply( new BigInteger( "2" ) ) };
		BigDecimal[] bd_arr = new BigDecimal[] { new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ), new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ).multiply( new BigDecimal( "2" ) ) };
		String[] s_arr = new String[] { "json", "JSON" };

		Assert.assertNotNull( result );
		TestHelpers.assertArrayEquals( b1_arr, result.b1_arr );
		Assert.assertArrayEquals( i1_arr, result.i1_arr );
		Assert.assertArrayEquals( l1_arr, result.l1_arr );
		TestHelpers.assertArrayEquals( f1_arr, result.f1_arr );
		TestHelpers.assertArrayEquals( d1_arr, result.d1_arr );
		Assert.assertArrayEquals( b2_arr, result.b2_arr );
		Assert.assertArrayEquals( i2_arr, result.i2_arr );
		Assert.assertArrayEquals( l2_arr, result.l2_arr );
		Assert.assertArrayEquals( f2_arr, result.f2_arr );
		Assert.assertArrayEquals( d2_arr, result.d2_arr );
		Assert.assertArrayEquals( bi_arr, result.bi_arr );
		Assert.assertArrayEquals( bd_arr, result.bd_arr );
		Assert.assertArrayEquals( s_arr, result.s_arr );

		b1_arr = new boolean[] {};
		i1_arr = new int[] {};
		l1_arr = new long[] {};
		f1_arr = new float[] {};
		d1_arr = new double[] {};
		b2_arr = new Boolean[] {};
		i2_arr = new Integer[] {};
		l2_arr = new Long[] {};
		f2_arr = new Float[] {};
		d2_arr = new Double[] {};
		bi_arr = new BigInteger[] {};
		bd_arr = new BigDecimal[] {};
		s_arr = new String[] {};

		Assert.assertNotNull( result.obj_arr );
		Assert.assertEquals( 1, result.obj_arr.length );
		result = result.obj_arr[ 0 ];

		TestHelpers.assertArrayEquals( b1_arr, result.b1_arr );
		Assert.assertArrayEquals( i1_arr, result.i1_arr );
		Assert.assertArrayEquals( l1_arr, result.l1_arr );
		TestHelpers.assertArrayEquals( f1_arr, result.f1_arr );
		TestHelpers.assertArrayEquals( d1_arr, result.d1_arr );
		Assert.assertArrayEquals( b2_arr, result.b2_arr );
		Assert.assertArrayEquals( i2_arr, result.i2_arr );
		Assert.assertArrayEquals( l2_arr, result.l2_arr );
		Assert.assertArrayEquals( f2_arr, result.f2_arr );
		Assert.assertArrayEquals( d2_arr, result.d2_arr );
		Assert.assertArrayEquals( bi_arr, result.bi_arr );
		Assert.assertArrayEquals( bd_arr, result.bd_arr );
		Assert.assertArrayEquals( s_arr, result.s_arr );

		Assert.assertNotNull( result.obj_arr );
		Assert.assertEquals( 1, result.obj_arr.length );
		result = result.obj_arr[ 0 ];

		Assert.assertNull( result.b1_arr );
		Assert.assertNull( result.i1_arr );
		Assert.assertNull( result.l1_arr );
		Assert.assertNull( result.f1_arr );
		Assert.assertNull( result.d1_arr );
		Assert.assertNull( result.b2_arr );
		Assert.assertNull( result.i2_arr );
		Assert.assertNull( result.l2_arr );
		Assert.assertNull( result.f2_arr );
		Assert.assertNull( result.d2_arr );
		Assert.assertNull( result.bi_arr );
		Assert.assertNull( result.bd_arr );
		Assert.assertNull( result.s_arr );
		Assert.assertNull( result.obj_arr );
	}

	@Test
	public void test_jsonobjectmapper_nullvalues() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		JSONObjectMappings json_objectmappings = new JSONObjectMappings();
		JSONStructureMarshaller marshaller = json_objectmappings.getStructureMarshaller();
		JSONStructureUnmarshaller unmarshaller = json_objectmappings.getStructureUnmarshaller();
		try {
			json_objectmappings.register( TestJSONMapObjectArraysNullValues.class );

			TestJSONMapObjectArraysNullValues result;

			TestJSONMapObjectArraysNullValues obj = new TestJSONMapObjectArraysNullValues();
			obj.b1_arr = new boolean[ 5 ];
			obj.i1_arr = new int[ 5 ];
			obj.l1_arr = new long[ 5 ];
			obj.f1_arr = new float[ 5 ];
			obj.d1_arr = new double[ 5 ];
			obj.b2_arr = new Boolean[ 5 ];
			obj.i2_arr = new Integer[ 5 ];
			obj.l2_arr = new Long[ 5 ];
			obj.f2_arr = new Float[ 5 ];
			obj.d2_arr = new Double[ 5 ];
			obj.bi_arr = new BigInteger[ 5 ];
			obj.bd_arr = new BigDecimal[ 5 ];
			obj.s_arr = new String[ 5 ];
			obj.obj_arr = new TestJSONMapObjectArraysNullValues[ 5 ];

			JSONCollection json_struct = marshaller.toJSONStructure( obj );

			out.reset();
			json_textMarshaller.toJSONText( json_struct, json_encoder, false, out );

			byte[] json_compact = out.toByteArray();
			// debug
			//System.out.println( new String( json_compact ) );

			result = unmarshaller.toObject( json_struct, TestJSONMapObjectArraysNullValues.class );
			assert_jsonobjectmapper_nullvalues_result( result );

			out.reset();
			json_textMarshaller.toJSONText( json_struct, json_encoder, true, out );

			byte[] json_pretty = out.toByteArray();
			// debug
			//System.out.println( new String( json_pretty ) );

			result = unmarshaller.toObject( json_struct, TestJSONMapObjectArraysNullValues.class );
			assert_jsonobjectmapper_nullvalues_result( result );

            Assert.assertThat( json_compact.length, is( not( equalTo( json_pretty.length ) ) ) );

            json_pretty = TestHelpers.filterWhitespaces( json_pretty );

            Assert.assertEquals( json_compact.length, json_pretty.length );
            Assert.assertArrayEquals( json_compact, json_pretty );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	public static class TestJSONMapObjectArraysNullValues {

		@JSONNullable
		@JSONNullValues(false)
		public boolean[] b1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public int[] i1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public long[] l1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public float[] f1_arr;

		@JSONNullable
		@JSONNullValues(false)
		public double[] d1_arr;

		@JSONNullable
		@JSONNullValues(true)
		public Boolean[] b2_arr;

		@JSONNullable
		@JSONNullValues(true)
		public Integer[] i2_arr;

		@JSONNullable
		@JSONNullValues(true)
		public Long[] l2_arr;

		@JSONNullable
		@JSONNullValues(true)
		public Float[] f2_arr;

		@JSONNullable
		@JSONNullValues(true)
		public Double[] d2_arr;

		@JSONNullable
		@JSONNullValues(true)
		public BigInteger[] bi_arr;

		@JSONNullable
		@JSONNullValues(true)
		public BigDecimal[] bd_arr;

		@JSONNullable
		@JSONNullValues(true)
		public String[] s_arr;

		@JSONNullable
		@JSONNullValues(true)
		public TestJSONMapObjectArraysNullValues[] obj_arr;

	}

	public void assert_jsonobjectmapper_nullvalues_result(TestJSONMapObjectArraysNullValues result) {
		boolean[] b1_arr = new boolean[ 5 ];
		int[] i1_arr = new int[ 5 ];
		long[] l1_arr = new long[ 5 ];
		float[] f1_arr = new float[ 5 ];
		double[] d1_arr = new double[ 5 ];
		Boolean[] b2_arr = new Boolean[ 5 ];
		Integer[] i2_arr = new Integer[ 5 ];
		Long[] l2_arr = new Long[ 5 ];
		Float[] f2_arr = new Float[ 5 ];
		Double[] d2_arr = new Double[ 5 ];
		BigInteger[] bi_arr = new BigInteger[ 5 ];
		BigDecimal[] bd_arr = new BigDecimal[ 5 ];
		String[] s_arr = new String[ 5 ];
		TestJSONMapObjectArraysNullValues[] obj_arr = new TestJSONMapObjectArraysNullValues[ 5 ];

		TestHelpers.assertArrayEquals( b1_arr, result.b1_arr );
		Assert.assertArrayEquals( i1_arr, result.i1_arr );
		Assert.assertArrayEquals( l1_arr, result.l1_arr );
		TestHelpers.assertArrayEquals( f1_arr, result.f1_arr );
		TestHelpers.assertArrayEquals( d1_arr, result.d1_arr );
		Assert.assertArrayEquals( b2_arr, result.b2_arr );
		Assert.assertArrayEquals( i2_arr, result.i2_arr );
		Assert.assertArrayEquals( l2_arr, result.l2_arr );
		Assert.assertArrayEquals( f2_arr, result.f2_arr );
		Assert.assertArrayEquals( d2_arr, result.d2_arr );
		Assert.assertArrayEquals( bi_arr, result.bi_arr );
		Assert.assertArrayEquals( bd_arr, result.bd_arr );
		Assert.assertArrayEquals( s_arr, result.s_arr );
		Assert.assertArrayEquals( obj_arr, result.obj_arr );
	}

	@Test
	public void test_jsonobjectmapper_toobject_nullvalues() {
		JSONObjectMappings json_objectmappings = new JSONObjectMappings();
		JSONStructureMarshaller marshaller = json_objectmappings.getStructureMarshaller();
		JSONStructureUnmarshaller unmarshaller = json_objectmappings.getStructureUnmarshaller();
		try {
			json_objectmappings.register( TestJSONMapObjectArrays.class );
			json_objectmappings.register( TestJSONMapObjectArraysWithAV.class );
			json_objectmappings.register( TestJSONMapObjectArraysNullValues.class );
			json_objectmappings.register( TestJSONMapObjectArraysNotNullableNullValues.class );
			json_objectmappings.register( TestJSONMapObjectArraysNotNullableNullValuesGlobal.class );

			TestJSONMapObjectArrays obj = getTestJSONMapObjectArraysObjectMold();
			JSONCollection json_struct;
			JSONObject json_object;

			JSONArray json_array = new JSONArray();
			json_array.add( JSONNull.Null );
			json_array.add( JSONNull.Null );
			json_array.add( JSONNull.Null );
			json_array.add( JSONNull.Null );
			json_array.add( JSONNull.Null );

			String[] fieldNamesPrimitives = { "b1_arr", "i1_arr", "l1_arr", "f1_arr", "d1_arr" };
			for ( int i=0; i<fieldNamesPrimitives.length; ++i ) {
				json_struct = marshaller.toJSONStructure( obj );
				json_object = json_struct.getObject();
				json_object.nameValuePairs.put( JSONString.String( fieldNamesPrimitives[ i ] ), json_array );
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArrays.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArraysWithAV.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArraysNullValues.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				json_object.nameValuePairs.put( JSONString.String( fieldNamesPrimitives[ i ] ), JSONNull.Null );
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArraysNotNullableNullValues.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArraysNotNullableNullValuesGlobal.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
			}

			String[] fieldNamesObjects = { "b2_arr", "i2_arr", "l2_arr", "f2_arr", "d2_arr", "bi_arr", "bd_arr", "s_arr", "obj_arr" };
			for ( int i=0; i<fieldNamesObjects.length; ++i ) {
				json_struct = marshaller.toJSONStructure( obj );
				json_object = json_struct.getObject();
				json_object.nameValuePairs.put( JSONString.String( fieldNamesObjects[ i ] ), json_array );
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArrays.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArraysWithAV.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				unmarshaller.toObject( json_struct, TestJSONMapObjectArraysNullValues.class );
				json_object.nameValuePairs.put( JSONString.String( fieldNamesObjects[ i ] ), JSONNull.Null );
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArraysNotNullableNullValues.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				try {
					unmarshaller.toObject( json_struct, TestJSONMapObjectArraysNotNullableNullValuesGlobal.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}

				TestJSONMapObjectArraysNotNullableNullValuesGlobal moldObj;

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.b1_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.i1_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.l1_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.f1_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.d1_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.b2_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.i2_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.l2_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.f2_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.d2_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.bi_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.bd_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.s_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );

				moldObj = getTestJSONMapObjectArraysNullValuesObjectMold();
				moldObj.obj_arr = null;
				assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception( marshaller, moldObj );
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	public static class TestJSONMapObjectArraysNotNullableNullValues {

		@JSONNullable(false)
		@JSONNullValues(false)
		public boolean[] b1_arr;

		@JSONNullable(false)
		@JSONNullValues(false)
		public int[] i1_arr;

		@JSONNullable(false)
		@JSONNullValues(false)
		public long[] l1_arr;

		@JSONNullable(false)
		@JSONNullValues(false)
		public float[] f1_arr;

		@JSONNullable(false)
		@JSONNullValues(false)
		public double[] d1_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public Boolean[] b2_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public Integer[] i2_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public Long[] l2_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public Float[] f2_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public Double[] d2_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public BigInteger[] bi_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public BigDecimal[] bd_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public String[] s_arr;

		@JSONNullable(false)
		@JSONNullValues(true)
		public TestJSONMapObjectArraysNullValues[] obj_arr;

	}

	@JSON(nullValues={"b2_arr", "i2_arr", "l2_arr", "f2_arr", "d2_arr", "bi_arr", "bd_arr", "s_arr", "obj_arr"})
	public static class TestJSONMapObjectArraysNotNullableNullValuesGlobal {

		@JSONNullable(false)
		@JSONNullValues(false)
		public boolean[] b1_arr;

		@JSONNullable(false)
		@JSONNullValues(false)
		public int[] i1_arr;

		@JSONNullable(false)
		@JSONNullValues(false)
		public long[] l1_arr;

		@JSONNullable(false)
		@JSONNullValues(false)
		public float[] f1_arr;

		@JSONNullable(false)
		@JSONNullValues(false)
		public double[] d1_arr;

		@JSONNullable(false)
		public Boolean[] b2_arr;

		@JSONNullable(false)
		public Integer[] i2_arr;

		@JSONNullable(false)
		public Long[] l2_arr;

		@JSONNullable(false)
		public Float[] f2_arr;

		@JSONNullable(false)
		public Double[] d2_arr;

		@JSONNullable(false)
		public BigInteger[] bi_arr;

		@JSONNullable(false)
		public BigDecimal[] bd_arr;

		@JSONNullable(false)
		public String[] s_arr;

		@JSONNullable(false)
		public TestJSONMapObjectArraysNullValues[] obj_arr;

	}

	public TestJSONMapObjectArraysNotNullableNullValuesGlobal getTestJSONMapObjectArraysNullValuesObjectMold() {
		TestJSONMapObjectArraysNotNullableNullValuesGlobal obj = new TestJSONMapObjectArraysNotNullableNullValuesGlobal();
		obj.b1_arr = new boolean[] { true, false };
		obj.i1_arr = new int[] { 42, 4213 };
		obj.l1_arr = new long[] { 12345678901234L, 12345678901234L * 2L };
		obj.f1_arr = new float[] { 1.0F / 3.0F, 1.0F / 5.0F };
		obj.d1_arr = new double[] { 1.0 / 3.0, 1.0 / 5.0 };
		obj.b2_arr = new Boolean[] { false, true };
		obj.i2_arr = new Integer[] { 1234, 4321 };
		obj.l2_arr = new Long[] { 43210987654321L, 43210987654321L * 2L };
		obj.f2_arr = new Float[] { 3.0F, 5.0F };
		obj.d2_arr = new Double[] { 3.0, 5.0 };
		obj.bi_arr = new BigInteger[] { new BigInteger( "123456789012345678901234567890123456789012" ), new BigInteger( "123456789012345678901234567890123456789012" ).multiply( new BigInteger( "2" ) ) };
		obj.bd_arr = new BigDecimal[] { new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ), new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ).multiply( new BigDecimal( "2" ) ) };
		obj.s_arr = new String[] { "json", "JSON" };
		//obj.b = "bytes".getBytes();
		TestJSONMapObjectArraysNullValues obj2 = new TestJSONMapObjectArraysNullValues();
		obj.obj_arr = new TestJSONMapObjectArraysNullValues[] { obj2 };
		obj2.b1_arr = new boolean[] {};
		obj2.i1_arr = new int[] {};
		obj2.l1_arr = new long[] {};
		obj2.f1_arr = new float[] {};
		obj2.d1_arr = new double[] {};
		obj2.b2_arr = new Boolean[] {};
		obj2.i2_arr = new Integer[] {};
		obj2.l2_arr = new Long[] {};
		obj2.f2_arr = new Float[] {};
		obj2.d2_arr = new Double[] {};
		obj2.bi_arr = new BigInteger[] {};
		obj2.bd_arr = new BigDecimal[] {};
		obj2.s_arr = new String[] {};
		//obj2.b = "BYTES".getBytes();
		obj2.obj_arr = new TestJSONMapObjectArraysNullValues[] { new TestJSONMapObjectArraysNullValues() };
		return obj;
	}

	public void assert_JSONMapObjectArraysNotNullableNullValuesGlobal_exception(JSONStructureMarshaller marshaller, Object obj) throws IllegalArgumentException, IllegalAccessException {
		try {
			marshaller.toJSONStructure( obj );
		}
		catch (JSONException e) {
		}
	}

}
