/*
 * Created on 28/12/2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.annotation.JSONNullValues;
import com.antiaction.common.json.annotation.JSONNullable;

@RunWith(JUnit4.class)
public class TestJSONObjectMapper_Arrays {

	@Test
	public void test_jsonobjectmapper_array_tojson() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONText json = new JSONText();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		JSONObjectMapper json_om = new JSONObjectMapper();
		try {
			json_om.register( TestJSONMapObjectArrays.class );

			TestJSONMapObjectArrays obj = getTestJSONMapObjectArraysObjectMold();
			TestJSONMapObjectArrays result;

			JSONStructure json_struct = json_om.toJSON( obj );

			out.reset();
			json.encodeJSONtext( json_struct, json_encoder, false, out );

			System.out.println( new String( out.toByteArray() ) );

			result = json_om.toObject( json_struct, TestJSONMapObjectArrays.class );
			assert_jsonobjectmapper_array_result( result );

			out.reset();
			json.encodeJSONtext( json_struct, json_encoder, true, out );

			System.out.println( new String( out.toByteArray() ) );

			result = json_om.toObject( json_struct, TestJSONMapObjectArrays.class );
			assert_jsonobjectmapper_array_result( result );
			/*
			 * Null values, primitives.
			 */
			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].b1_arr = new boolean[ 1 ];
			json_struct = json_om.toJSON( obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].i1_arr = new int[ 1 ];
			json_struct = json_om.toJSON( obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].l1_arr = new long[ 1 ];
			json_struct = json_om.toJSON( obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].f1_arr = new float[ 1 ];
			json_struct = json_om.toJSON( obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].d1_arr = new double[ 1 ];
			json_struct = json_om.toJSON( obj );
			/*
			 * Null values.
			 */
			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].b2_arr = new Boolean[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].i2_arr = new Integer[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].l2_arr = new Long[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].f2_arr = new Float[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].d2_arr = new Double[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].bi_arr = new BigInteger[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].bd_arr = new BigDecimal[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].s_arr = new String[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysObjectMold();
			obj.obj_arr[ 0 ].obj_arr = new TestJSONMapObjectArrays[ 1 ];
			assert_tojson_exception( json_om, obj );
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
		catch (InstantiationException e) {
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

	public void assert_tojson_exception(JSONObjectMapper json_om, Object obj) throws IllegalArgumentException, IllegalAccessException {
		try {
			json_om.toJSON( obj );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
	}

	@Test
	public void test_jsonobjectmapper_arraywithav_tojson() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONText json = new JSONText();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		JSONObjectMapper json_om = new JSONObjectMapper();
		try {
			json_om.register( TestJSONMapObjectArraysWithAV.class );

			TestJSONMapObjectArraysWithAV obj = getTestJSONMapObjectArraysWithAVObjectMold();
			TestJSONMapObjectArraysWithAV result;

			JSONStructure json_struct = json_om.toJSON( obj );

			out.reset();
			json.encodeJSONtext( json_struct, json_encoder, false, out );

			System.out.println( new String( out.toByteArray() ) );

			result = json_om.toObject( json_struct, TestJSONMapObjectArraysWithAV.class );
			assert_jsonobjectmapper_arraywithav_result( result );

			out.reset();
			json.encodeJSONtext( json_struct, json_encoder, true, out );

			System.out.println( new String( out.toByteArray() ) );

			result = json_om.toObject( json_struct, TestJSONMapObjectArraysWithAV.class );
			assert_jsonobjectmapper_arraywithav_result( result );
			/*
			 * Null values, primitives.
			 */
			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].b1_arr = new boolean[ 1 ];
			json_struct = json_om.toJSON( obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].i1_arr = new int[ 1 ];
			json_struct = json_om.toJSON( obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].l1_arr = new long[ 1 ];
			json_struct = json_om.toJSON( obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].f1_arr = new float[ 1 ];
			json_struct = json_om.toJSON( obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].d1_arr = new double[ 1 ];
			json_struct = json_om.toJSON( obj );
			/*
			 * Null values.
			 */
			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].b2_arr = new Boolean[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].i2_arr = new Integer[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].l2_arr = new Long[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].f2_arr = new Float[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].d2_arr = new Double[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].bi_arr = new BigInteger[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].bd_arr = new BigDecimal[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].s_arr = new String[ 1 ];
			assert_tojson_exception( json_om, obj );

			obj = getTestJSONMapObjectArraysWithAVObjectMold();
			obj.obj_arr[ 0 ].obj_arr = new TestJSONMapObjectArraysWithAV[ 1 ];
			assert_tojson_exception( json_om, obj );
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
		catch (InstantiationException e) {
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
		JSONText json = new JSONText();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		JSONObjectMapper json_om = new JSONObjectMapper();
		try {
			json_om.register( TestJSONMapObjectArraysNullValues_Invalid1.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( TestJSONMapObjectArraysNullValues_Invalid2.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( TestJSONMapObjectArraysNullValues_Invalid3.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( TestJSONMapObjectArraysNullValues_Invalid4.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( TestJSONMapObjectArraysNullValues_Invalid5.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( TestJSONMapObjectArraysNullValues.class );

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

			JSONStructure json_struct = json_om.toJSON( obj );

			out.reset();
			json.encodeJSONtext( json_struct, json_encoder, false, out );

			System.out.println( new String( out.toByteArray() ) );

			result = json_om.toObject( json_struct, TestJSONMapObjectArraysNullValues.class );
			assert_jsonobjectmapper_nullvalues_result( result );

			out.reset();
			json.encodeJSONtext( json_struct, json_encoder, true, out );

			System.out.println( new String( out.toByteArray() ) );

			result = json_om.toObject( json_struct, TestJSONMapObjectArraysNullValues.class );
			assert_jsonobjectmapper_nullvalues_result( result );
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
		catch (InstantiationException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	public static class TestJSONMapObjectArraysNullValues_Invalid1 {
		@JSONNullable
		@JSONNullValues(true)
		public boolean[] b1_arr;
	}

	public static class TestJSONMapObjectArraysNullValues_Invalid2 {
		@JSONNullable
		@JSONNullValues(true)
		public int[] i1_arr;
	}

	public static class TestJSONMapObjectArraysNullValues_Invalid3 {
		@JSONNullable
		@JSONNullValues(true)
		public long[] l1_arr;
	}

	public static class TestJSONMapObjectArraysNullValues_Invalid4 {
		@JSONNullable
		@JSONNullValues(true)
		public float[] f1_arr;
	}

	public static class TestJSONMapObjectArraysNullValues_Invalid5 {
		@JSONNullable
		@JSONNullValues(true)
		public double[] d1_arr;
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
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONText json = new JSONText();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		JSONObjectMapper json_om = new JSONObjectMapper();
		try {
			json_om.register( TestJSONMapObjectArrays.class );
			json_om.register( TestJSONMapObjectArraysWithAV.class );
			json_om.register( TestJSONMapObjectArraysNullValues.class );

			TestJSONMapObjectArrays obj = getTestJSONMapObjectArraysObjectMold();
			JSONStructure json_struct;
			JSONObject json_object;
			TestJSONMapObjectArrays result;

			JSONArray json_array = new JSONArray();
			json_array.add( JSONNull.Null );
			json_array.add( JSONNull.Null );
			json_array.add( JSONNull.Null );
			json_array.add( JSONNull.Null );
			json_array.add( JSONNull.Null );

			String[] fieldNamesPrimitives = { "b1_arr", "i1_arr", "l1_arr", "f1_arr", "d1_arr" };
			for ( int i=0; i<fieldNamesPrimitives.length; ++i ) {
				json_struct = json_om.toJSON( obj );
				json_object = json_struct.getObject();
				json_object.values.put( JSONString.String( fieldNamesPrimitives[ i ] ), json_array );
				try {
					json_om.toObject( json_struct, TestJSONMapObjectArrays.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				try {
					json_om.toObject( json_struct, TestJSONMapObjectArraysWithAV.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				try {
					json_om.toObject( json_struct, TestJSONMapObjectArraysNullValues.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
			}

			String[] fieldNamesObjects = { "b2_arr", "i2_arr", "l2_arr", "f2_arr", "d2_arr", "bi_arr", "bd_arr", "s_arr", "obj_arr" };
			for ( int i=0; i<fieldNamesObjects.length; ++i ) {
				json_struct = json_om.toJSON( obj );
				json_object = json_struct.getObject();
				json_object.values.put( JSONString.String( fieldNamesObjects[ i ] ), json_array );
				try {
					json_om.toObject( json_struct, TestJSONMapObjectArrays.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				try {
					json_om.toObject( json_struct, TestJSONMapObjectArraysWithAV.class );
					Assert.fail( "Exception expected!" );
				}
				catch (JSONException e) {
				}
				json_om.toObject( json_struct, TestJSONMapObjectArraysNullValues.class );
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
		catch (InstantiationException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );		
		}
	}

}
