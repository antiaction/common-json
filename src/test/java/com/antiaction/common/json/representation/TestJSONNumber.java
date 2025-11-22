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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.JSONDecoder;
import com.antiaction.common.json.JSONDecoderCharset;
import com.antiaction.common.json.JSONEncoder;
import com.antiaction.common.json.JSONEncoderCharset;
import com.antiaction.common.json.JSONEncoding;
import com.antiaction.common.json.JSONException;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 07/09/2012
 */
@RunWith(JUnit4.class)
public class TestJSONNumber {

	@Test
	public void test_jsonnumber() {
		try {
			JSONNumber json_number;

			json_number = new JSONNumber( "Hello" );
			Assert.assertNotNull( json_number );
			Assert.assertEquals( JSONConstants.VT_NUMBER, json_number.type );
			Assert.assertNull( json_number.intVal );
			Assert.assertNull( json_number.longVal );
			Assert.assertNull( json_number.floatVal );
			Assert.assertNull( json_number.doubleVal );
			Assert.assertNull( json_number.bigIntegerVal );
			Assert.assertNull( json_number.bigDecimalVal );
			Assert.assertArrayEquals( "Hello".getBytes(), json_number.numberBytes );
			Assert.assertEquals( "Hello", json_number.toString() );

			json_number = JSONNumber.Integer( 42 );
			Assert.assertNotNull( json_number );
			Assert.assertEquals( JSONConstants.VT_NUMBER, json_number.type );
			Assert.assertNotNull( json_number.intVal );
			Assert.assertNull( json_number.longVal );
			Assert.assertNull( json_number.floatVal );
			Assert.assertNull( json_number.doubleVal );
			Assert.assertNull( json_number.bigIntegerVal );
			Assert.assertNull( json_number.bigDecimalVal );
			Assert.assertArrayEquals( "42".getBytes(), json_number.numberBytes );
			Assert.assertEquals( "42", json_number.toString() );

			json_number = JSONNumber.Long( 12345678901234L );
			Assert.assertNotNull( json_number );
			Assert.assertEquals( JSONConstants.VT_NUMBER, json_number.type );
			Assert.assertNull( json_number.intVal );
			Assert.assertNotNull( json_number.longVal );
			Assert.assertNull( json_number.floatVal );
			Assert.assertNull( json_number.doubleVal );
			Assert.assertNull( json_number.bigIntegerVal );
			Assert.assertNull( json_number.bigDecimalVal );
			Assert.assertArrayEquals( "12345678901234".getBytes(), json_number.numberBytes );
			Assert.assertEquals( "12345678901234", json_number.toString() );

			json_number = JSONNumber.Float( 1.0F / 3.0F );
			Assert.assertNotNull( json_number );
			Assert.assertEquals( JSONConstants.VT_NUMBER, json_number.type );
			Assert.assertNull( json_number.intVal );
			Assert.assertNull( json_number.longVal );
			Assert.assertNotNull( json_number.floatVal );
			Assert.assertNull( json_number.doubleVal );
			Assert.assertNull( json_number.bigIntegerVal );
			Assert.assertNull( json_number.bigDecimalVal );
			Assert.assertArrayEquals( new Float( 1.0F / 3.0F ).toString().getBytes(), json_number.numberBytes );
			Assert.assertEquals( new Float( 1.0F / 3.0F ).toString(), json_number.toString() );

			json_number = JSONNumber.Double( 1.0 / 3.0 );
			Assert.assertNotNull( json_number );
			Assert.assertEquals( JSONConstants.VT_NUMBER, json_number.type );
			Assert.assertNull( json_number.intVal );
			Assert.assertNull( json_number.longVal );
			Assert.assertNull( json_number.floatVal );
			Assert.assertNotNull( json_number.doubleVal );
			Assert.assertNull( json_number.bigIntegerVal );
			Assert.assertNull( json_number.bigDecimalVal );
			Assert.assertArrayEquals( new Double( 1.0 / 3.0 ).toString().getBytes(), json_number.numberBytes );
			Assert.assertEquals( new Double( 1.0 / 3.0 ).toString(), json_number.toString() );

			json_number = JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) );
			Assert.assertNotNull( json_number );
			Assert.assertEquals( JSONConstants.VT_NUMBER, json_number.type );
			Assert.assertNull( json_number.intVal );
			Assert.assertNull( json_number.longVal );
			Assert.assertNull( json_number.floatVal );
			Assert.assertNull( json_number.doubleVal );
			Assert.assertNotNull( json_number.bigIntegerVal );
			Assert.assertNull( json_number.bigDecimalVal );
			Assert.assertArrayEquals( new BigInteger( "123456789012345678901234567890123456789012" ).toString().getBytes(), json_number.numberBytes );
			Assert.assertEquals( new BigInteger( "123456789012345678901234567890123456789012" ).toString(), json_number.toString() );

			json_number = JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) );
			Assert.assertNotNull( json_number );
			Assert.assertEquals( JSONConstants.VT_NUMBER, json_number.type );
			Assert.assertNull( json_number.intVal );
			Assert.assertNull( json_number.longVal );
			Assert.assertNull( json_number.floatVal );
			Assert.assertNull( json_number.doubleVal );
			Assert.assertNull( json_number.bigIntegerVal );
			Assert.assertNotNull( json_number.bigDecimalVal );
			Assert.assertArrayEquals( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ).toString().getBytes(), json_number.numberBytes );
			Assert.assertEquals( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ).toString(), json_number.toString() );

			/*
			 *
			 */

			JSONNumber json_number_int = JSONNumber.Integer( 42 );
			JSONNumber json_number_long = JSONNumber.Long( 12345678901234L );
			JSONNumber json_number_float = JSONNumber.Float( 1.0F / 3.0F );
			JSONNumber json_number_double = JSONNumber.Double( 1.0 / 3.0 );
			JSONNumber json_number_bigint = JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) );
			JSONNumber json_number_bigdec = JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) );

			Charset charset = Charset.forName("UTF-8");
			JSONEncoder json_encoder = new JSONEncoderCharset( charset );

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
			JSONTextUnmarshaller json_textUnmarshaller = new JSONTextUnmarshaller();

			JSONArray json_array = new JSONArray();
			json_array.add( json_number_int );
			json_array.add( json_number_long );
			json_array.add( json_number_float );
			json_array.add( json_number_double );
			json_array.add( json_number_bigint );
			json_array.add( json_number_bigdec );

			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() )  );

			ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
			JSONDecoder json_decoder = new JSONDecoderCharset( charset );

			JSONCollection json_structure = json_textUnmarshaller.toJSONStructure( in, json_decoder );

			Assert.assertNotNull( json_structure );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_structure.type );
			json_array = (JSONArray)json_structure;
			Assert.assertEquals( 6, json_array.values.size() );
			Assert.assertEquals( json_number_int, json_array.values.get( 0 ) );
			Assert.assertEquals( json_number_long, json_array.values.get( 1 ) );
			Assert.assertEquals( json_number_float, json_array.values.get( 2 ) );
			Assert.assertEquals( json_number_double, json_array.values.get( 3 ) );
			Assert.assertEquals( json_number_bigint, json_array.values.get( 4 ) );
			Assert.assertEquals( json_number_bigdec, json_array.values.get( 5 ) );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}
	}

	@Test
	public void test_jsonnumber_decode() {
		byte[] bytes;
		PushbackInputStream pbin = null;
		int encoding;

		JSONTextUnmarshaller json = new JSONTextUnmarshaller();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONDecoder json_decoder = null;

		JSONCollection json_struct;
		JSONArray json_array;

		String json_text;
		try {
			json_text = ""
					+ "["
					+ "0,"
					+ "-0,"
					+ "1234,"
					+ "-1234,"
					+ "0.0,"
					+ "3.14159,"
					+ "12345678901234,"
					+ "123456789012345678901234567890123456789012,"
					+ "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825,"
					+ "3E+02,"
					+ "314159e-04,"
					+ "0E02,"
					+ "0e02,"
					+ "314.159E+02,"
					+ "314.159e-02"
					+ "]";
			bytes = json_text.getBytes( "UTF-8" );

			pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );

			json_array = (JSONArray)json_struct;
			/*
			for (int i=0; i<json_array.values.size(); ++i) {
				System.out.println( json_array.values.get( i ).toString() );
			}
			*/

			Assert.assertEquals( new Integer( 0 ), ((JSONNumber)(json_array.get( 0 ))).getInteger() );
			Assert.assertEquals( new Float( -0.0F ), ((JSONNumber)(json_array.get( 1 ))).getFloat(), 0 );
			Assert.assertEquals( new Double( -0.0 ), ((JSONNumber)(json_array.get( 1 ))).getDouble(), 0 );
			Assert.assertEquals( new Integer( 1234 ), ((JSONNumber)(json_array.get( 2 ))).getInteger() );
			Assert.assertEquals( new Integer( -1234 ), ((JSONNumber)(json_array.get( 3 ))).getInteger() );
			Assert.assertEquals( new Float( -0.0 ), ((JSONNumber)(json_array.get( 4 ))).getFloat(), 0 );
			Assert.assertEquals( new Double( 0.0 ), ((JSONNumber)(json_array.get( 4 ))).getDouble(), 0 );
			Assert.assertEquals( 3.14159F, ((JSONNumber)(json_array.get( 5 ))).getFloat(), 0 );
			Assert.assertEquals( new Long( 12345678901234L ), ((JSONNumber)(json_array.get( 6 ))).getLong() );
			Assert.assertEquals( new BigInteger( "123456789012345678901234567890123456789012" ), ((JSONNumber)(json_array.get( 7 ))).getBigInteger() );
			Assert.assertEquals( new BigDecimal( "123456789012345678901234567890123456789012" ), ((JSONNumber)(json_array.get( 7 ))).getBigDecimal() );
			Assert.assertEquals( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ), ((JSONNumber)(json_array.get( 8 ))).getBigDecimal() );
			Assert.assertEquals( 300.0F, ((JSONNumber)(json_array.get( 9 ))).getFloat(), 0 );
			Assert.assertEquals( 300.0, ((JSONNumber)(json_array.get( 9 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "3E+02" ), ((JSONNumber)(json_array.get( 9 ))).getBigDecimal() );
			Assert.assertEquals( 314159e-04F, ((JSONNumber)(json_array.get( 10 ))).getFloat(), 0 );
			Assert.assertEquals( 314159e-04, ((JSONNumber)(json_array.get( 10 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "314159e-04" ), ((JSONNumber)(json_array.get( 10 ))).getBigDecimal() );
			Assert.assertEquals( 0E02F, ((JSONNumber)(json_array.get( 11 ))).getFloat(), 0 );
			Assert.assertEquals( 0E02, ((JSONNumber)(json_array.get( 11 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "0E02" ), ((JSONNumber)(json_array.get( 11 ))).getBigDecimal() );
			Assert.assertEquals( 0e02F, ((JSONNumber)(json_array.get( 12 ))).getFloat(), 0 );
			Assert.assertEquals( 0e02, ((JSONNumber)(json_array.get( 12 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "0e02" ), ((JSONNumber)(json_array.get( 12 ))).getBigDecimal() );
			Assert.assertEquals( 314.159e+02F, ((JSONNumber)(json_array.get( 13 ))).getFloat(), 0 );
			Assert.assertEquals( 314.159e+02, ((JSONNumber)(json_array.get( 13 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "314.159e+02" ), ((JSONNumber)(json_array.get( 13 ))).getBigDecimal() );
			Assert.assertEquals( 314.159E-02F, ((JSONNumber)(json_array.get( 14 ))).getFloat(), 0 );
			Assert.assertEquals( 314.159E-02, ((JSONNumber)(json_array.get( 14 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "314.159E-02" ), ((JSONNumber)(json_array.get( 14 ))).getBigDecimal() );

			Assert.assertEquals( new Integer( 0 ), ((JSONNumber)(json_array.get( 0 ))).getInteger() );
			Assert.assertEquals( new Float( -0.0F ), ((JSONNumber)(json_array.get( 1 ))).getFloat(), 0 );
			Assert.assertEquals( new Double( -0.0 ), ((JSONNumber)(json_array.get( 1 ))).getDouble(), 0 );
			Assert.assertEquals( new Integer( 1234 ), ((JSONNumber)(json_array.get( 2 ))).getInteger() );
			Assert.assertEquals( new Integer( -1234 ), ((JSONNumber)(json_array.get( 3 ))).getInteger() );
			Assert.assertEquals( new Float( -0.0 ), ((JSONNumber)(json_array.get( 4 ))).getFloat(), 0 );
			Assert.assertEquals( new Double( 0.0 ), ((JSONNumber)(json_array.get( 4 ))).getDouble(), 0 );
			Assert.assertEquals( 3.14159F, ((JSONNumber)(json_array.get( 5 ))).getFloat(), 0 );
			Assert.assertEquals( new Long( 12345678901234L ), ((JSONNumber)(json_array.get( 6 ))).getLong() );
			Assert.assertEquals( new BigInteger( "123456789012345678901234567890123456789012" ), ((JSONNumber)(json_array.get( 7 ))).getBigInteger() );
			Assert.assertEquals( new BigDecimal( "123456789012345678901234567890123456789012" ), ((JSONNumber)(json_array.get( 7 ))).getBigDecimal() );
			Assert.assertEquals( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ), ((JSONNumber)(json_array.get( 8 ))).getBigDecimal() );
			Assert.assertEquals( 300.0F, ((JSONNumber)(json_array.get( 9 ))).getFloat(), 0 );
			Assert.assertEquals( 300.0, ((JSONNumber)(json_array.get( 9 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "3E+02" ), ((JSONNumber)(json_array.get( 9 ))).getBigDecimal() );
			Assert.assertEquals( 314159e-04F, ((JSONNumber)(json_array.get( 10 ))).getFloat(), 0 );
			Assert.assertEquals( 314159e-04, ((JSONNumber)(json_array.get( 10 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "314159e-04" ), ((JSONNumber)(json_array.get( 10 ))).getBigDecimal() );
			Assert.assertEquals( 0E02F, ((JSONNumber)(json_array.get( 11 ))).getFloat(), 0 );
			Assert.assertEquals( 0E02, ((JSONNumber)(json_array.get( 11 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "0E02" ), ((JSONNumber)(json_array.get( 11 ))).getBigDecimal() );
			Assert.assertEquals( 0e02F, ((JSONNumber)(json_array.get( 12 ))).getFloat(), 0 );
			Assert.assertEquals( 0e02, ((JSONNumber)(json_array.get( 12 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "0e02" ), ((JSONNumber)(json_array.get( 12 ))).getBigDecimal() );
			Assert.assertEquals( 314.159e+02F, ((JSONNumber)(json_array.get( 13 ))).getFloat(), 0 );
			Assert.assertEquals( 314.159e+02, ((JSONNumber)(json_array.get( 13 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "314.159e+02" ), ((JSONNumber)(json_array.get( 13 ))).getBigDecimal() );
			Assert.assertEquals( 314.159E-02F, ((JSONNumber)(json_array.get( 14 ))).getFloat(), 0 );
			Assert.assertEquals( 314.159E-02, ((JSONNumber)(json_array.get( 14 ))).getDouble(), 0 );
			Assert.assertEquals( new BigDecimal( "314.159E-02" ), ((JSONNumber)(json_array.get( 14 ))).getBigDecimal() );
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected expection!" );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected expection!" );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected expection!" );
		}

		JSONNumber json_number = JSONNumber.Integer( 0 );
		Assert.assertFalse( json_number.getBoolean() );
		json_number = JSONNumber.Integer( 1 );
		Assert.assertTrue( json_number.getBoolean() );
	}

	@Test
	public void test_jsonnumber_unsupporten() {
		JSONNumber json_number = JSONNumber.Integer( 42 );
		try {
			json_number.getArray();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_number.getObject();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_number.getBoolean();
			Assert.fail( "Exception expected !" );
		}
		catch (NumberFormatException e) {
		}
		try {
			json_number.getString();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_number.getBytes();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
	}

	@Test
	public void test_jsonnumber_equals_hashcode() {
		JSONNumber json_number_int = JSONNumber.Integer( 42 );
		JSONNumber json_number_long = JSONNumber.Long( 12345678901234L );
		JSONNumber json_number_float = JSONNumber.Float( 1.0F / 3.0F );
		JSONNumber json_number_double = JSONNumber.Double( 1.0 / 3.0 );
		JSONNumber json_number_bigint = JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) );
		JSONNumber json_number_bigdec = JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) );

		Assert.assertFalse( json_number_int.equals( null ) );
		Assert.assertFalse( json_number_long.equals( null ) );
		Assert.assertFalse( json_number_float.equals( null ) );
		Assert.assertFalse( json_number_double.equals( null ) );
		Assert.assertFalse( json_number_bigint.equals( null ) );
		Assert.assertFalse( json_number_bigdec.equals( null ) );

		Assert.assertFalse( json_number_int.equals( "42" ) );
		Assert.assertFalse( json_number_long.equals( "12345678901234L" ) );
		Assert.assertFalse( json_number_float.equals( "1.0F / 3.0F" ) );
		Assert.assertFalse( json_number_double.equals( "1.0 / 3.0" ) );
		Assert.assertFalse( json_number_bigint.equals( "123456789012345678901234567890123456789012" ) );
		Assert.assertFalse( json_number_bigdec.equals( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) );

		Assert.assertFalse( json_number_int.equals( json_number_long ) );
		Assert.assertFalse( json_number_int.equals( json_number_float ) );
		Assert.assertFalse( json_number_int.equals( json_number_double ) );
		Assert.assertFalse( json_number_int.equals( json_number_bigint ) );
		Assert.assertFalse( json_number_int.equals( json_number_bigdec ) );

		Assert.assertFalse( json_number_long.equals( json_number_int ) );
		Assert.assertFalse( json_number_long.equals( json_number_float ) );
		Assert.assertFalse( json_number_long.equals( json_number_double ) );
		Assert.assertFalse( json_number_long.equals( json_number_bigint ) );
		Assert.assertFalse( json_number_long.equals( json_number_bigdec ) );

		Assert.assertFalse( json_number_float.equals( json_number_int ) );
		Assert.assertFalse( json_number_float.equals( json_number_long ) );
		Assert.assertFalse( json_number_float.equals( json_number_double ) );
		Assert.assertFalse( json_number_float.equals( json_number_bigint ) );
		Assert.assertFalse( json_number_float.equals( json_number_bigdec ) );

		Assert.assertFalse( json_number_double.equals( json_number_int ) );
		Assert.assertFalse( json_number_double.equals( json_number_long ) );
		Assert.assertFalse( json_number_double.equals( json_number_float ) );
		Assert.assertFalse( json_number_double.equals( json_number_bigint ) );
		Assert.assertFalse( json_number_double.equals( json_number_bigdec ) );

		Assert.assertFalse( json_number_bigint.equals( json_number_int ) );
		Assert.assertFalse( json_number_bigint.equals( json_number_long ) );
		Assert.assertFalse( json_number_bigint.equals( json_number_float ) );
		Assert.assertFalse( json_number_bigint.equals( json_number_double ) );
		Assert.assertFalse( json_number_bigint.equals( json_number_bigdec ) );

		Assert.assertFalse( json_number_bigdec.equals( json_number_int ) );
		Assert.assertFalse( json_number_bigdec.equals( json_number_long ) );
		Assert.assertFalse( json_number_bigdec.equals( json_number_float ) );
		Assert.assertFalse( json_number_bigdec.equals( json_number_double ) );
		Assert.assertFalse( json_number_bigdec.equals( json_number_bigint ) );

		Assert.assertTrue( json_number_int.equals( JSONNumber.Integer( 42 ) ) );
		Assert.assertTrue( json_number_long.equals( JSONNumber.Long( 12345678901234L ) ) );
		Assert.assertTrue( json_number_float.equals( JSONNumber.Float( 1.0F / 3.0F ) ) );
		Assert.assertTrue( json_number_double.equals( JSONNumber.Double( 1.0 / 3.0 ) ) );
		Assert.assertTrue( json_number_bigint.equals( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ) ) );
		Assert.assertTrue( json_number_bigdec.equals( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ) ) );

		Assert.assertEquals( json_number_int.hashCode(), JSONNumber.Integer( 42 ).hashCode() );
		Assert.assertEquals( json_number_long.hashCode(), JSONNumber.Long( 12345678901234L ).hashCode() );
		Assert.assertEquals( json_number_float.hashCode(), JSONNumber.Float( 1.0F / 3.0F ).hashCode() );
		Assert.assertEquals( json_number_double.hashCode(), JSONNumber.Double( 1.0 / 3.0 ).hashCode() );
		Assert.assertEquals( json_number_bigint.hashCode(), JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ).hashCode() );
		Assert.assertEquals( json_number_bigdec.hashCode(), JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ).hashCode() );
	}

}
