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
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.JSONConstants;
import com.antiaction.common.json.JSONDecoder;
import com.antiaction.common.json.JSONEncoder;
import com.antiaction.common.json.JSONEncoderCharset;
import com.antiaction.common.json.JSONEncoding;
import com.antiaction.common.json.JSONException;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 17/08/2012
 */
@RunWith(JUnit4.class)
public class TestJSONText {

	@Test
	public void test_jsontext_decode_invalid() {
		byte[] bytes;
		PushbackInputStream pbin = null;
		int encoding;

		JSONTextUnmarshaller json = new JSONTextUnmarshaller();
		JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		JSONDecoder json_decoder = null;

		String[] invalid_cases = new String[] {
				new String( new byte[] { 0x20, 0x09, 0x0A, 0x0D } ),
				"[ ",
				" ]",
				"][",
				"{ ",
				" }",
				"}{",
				"[fail]",
				"[+42]",
				"[\"string\"+]",
				"[\"string\u0000\"]",
				"[\"string\\?\"]",
				"[\"\\u" + (char)0x0100 + "abc\"]",
				"[\"\\u?abc\"]",
				"[-.14159]",
				"[-03.14159]",
				"[314159:]",
				"[3.e10]",
				"[3.1415ea]",
				"[3.1415e+a]",
				"[3.1415e+1a]",
				"[]]",
				"{}}",
				"[],",
				"{},",
				"[42,]",
				"{42}",
				"{\"42\",}",
				"{\"life\":42:}",
				"{\"life\":42,}",
		};

		for (int i=0; i<invalid_cases.length; ++i) {
			bytes = null;
			try {
				bytes = invalid_cases[ i ].getBytes( "UTF-8" );
			}
			catch (UnsupportedEncodingException e1) {
				Assert.fail( "Unexpected exception!" );
			}
			//System.out.println( bytesToHex( bytes ) );
			try {
				pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
				encoding = JSONEncoding.encoding( pbin );
				Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
				json_decoder = json_encoding.getJSONDecoder( encoding );
				Assert.assertNotNull( json_decoder );
			}
			catch (IOException e) {
				Assert.fail( "Unexpected exception!" );
			}
			try {
				json.toJSONStructure( pbin, json_decoder );
				Assert.fail( "Exception expected!" );
			}
			catch (IOException e) {
			}
			catch (JSONException e) {
			}
		}

	}

	@Test
	public void test_json_text_encode_exception() {
		try {
			JSONCollection json_structure = null;
			Charset charset = Charset.forName( "UTF-8" );
			JSONEncoder json_encoder = new JSONEncoderCharset( charset );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
			json_textMarshaller.toJSONText( json_structure, json_encoder, false, out );
			Assert.fail( "Exception expected!" );
		}
		catch (IOException e) {
		}
		catch (JSONException e) {
		}
	}

	@Test
	public void test_jsontext_encodings() {
		Charset charset;

		charset = Charset.forName("UTF-8");
		test_jsontext_encode_decode( charset, JSONEncoding.E_UTF8 );

		charset = Charset.forName("UTF-16");
		test_jsontext_encode_decode( charset, JSONEncoding.E_UTF16BE );

		charset = Charset.forName("UTF-16BE");
		test_jsontext_encode_decode( charset, JSONEncoding.E_UTF16BE );

		charset = Charset.forName("UTF-16LE");
		test_jsontext_encode_decode( charset, JSONEncoding.E_UTF16LE );
	}

	public void test_jsontext_encode_decode(Charset charset, int expected_encoding) {
		JSONCollection json_struct;
		JSONCollection json_struct2;
		JSONArray json_array;
		JSONObject json_object;
		JSONArray json_array2;
		JSONObject json_object2;

		try {
			PushbackInputStream pbin;
			int encoding;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bytes;

			JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
			JSONEncoder json_encoder = new JSONEncoderCharset( charset );
			JSONDecoder json_decoder;

			JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
			JSONTextUnmarshaller json_textUnmarshaller = new JSONTextUnmarshaller();

			/*
			 * []
			 */

			json_array = new JSONArray();
			out.reset();
			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );

			Assert.assertEquals( json_array.toString(), json_struct.toString() );

			bytes = new byte[] { 0x20, 0x09, 0x0A, 0x0D, '[', 0x20, 0x09, 0x0A, 0x0D, ']', 0x20, 0x09, 0x0A, 0x0D };

			pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct2 = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct2 );

			/*
			 * {}
			 */

			json_object = new JSONObject();
			out.reset();
			json_textMarshaller.toJSONText( json_object, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );

			Assert.assertEquals( json_object.toString(), json_struct.toString() );

			bytes = new byte[] { 0x20, 0x09, 0x0A, 0x0D, '{', 0x20, 0x09, 0x0A, 0x0D, '}', 0x20, 0x09, 0x0A, 0x0D };

			pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct2 = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct2 );

			/*
			 * [...]
			 */

			json_array = new JSONArray();
			json_array.add( JSONNull.Null );
			json_array.add( JSONBoolean.False );
			json_array.add( JSONBoolean.True );
			json_array.add( JSONString.String( "streng" ) );
			json_array.add( JSONNumber.Integer( 0 ) );
			json_array.add( JSONNumber.Integer( -0 ) );
			json_array.add( JSONNumber.Integer( -31415 ) );
			json_array.add( JSONNumber.Float( 3.1415F ) );
			json_array.add( JSONNumber.Double( 3.141592 ) );
			json_array.add( JSONNumber.BigInteger( new BigInteger( "31415" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "3.141592e04" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "3.141592E04" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "0.0003141592e04" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "0.0003141592E04" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "3141592e04" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "3141592E04" ) ) );
			out.reset();
			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_struct.type );

			Assert.assertEquals( json_array.toString(), json_struct.toString() );

			Assert.assertEquals( JSONNull.Null, json_struct.get( 0 ) );
			Assert.assertEquals( JSONBoolean.False, json_struct.get( 1 ) );
			Assert.assertEquals( JSONBoolean.True, json_struct.get( 2 ) );
			Assert.assertEquals( JSONString.String( "streng" ), json_struct.get( 3 ) );
			Assert.assertEquals( JSONNumber.Integer( 0 ), json_struct.get( 4 ) );
			Assert.assertEquals( JSONNumber.Integer( -0 ), json_struct.get( 5 ) );
			Assert.assertEquals( JSONNumber.Integer( -31415 ), json_struct.get( 6 ) );
			Assert.assertEquals( JSONNumber.Float( 3.1415F ), json_struct.get( 7 ) );
			Assert.assertEquals( JSONNumber.Double( 3.141592 ), json_struct.get( 8 ) );
			Assert.assertEquals( JSONNumber.BigInteger( new BigInteger( "31415" ) ), json_struct.get( 9 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592e04" ) ), json_struct.get( 10 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592E04" ) ), json_struct.get( 11 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "0.0003141592e04" ) ), json_struct.get( 12 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "0.0003141592E04" ) ), json_struct.get( 13 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3141592e04" ) ), json_struct.get( 14 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3141592E04" ) ), json_struct.get( 15 ) );

			/*
			 * {...}
			 */

			json_object = new JSONObject();
			json_object.put( JSONString.String( "0" ), JSONNull.Null );
			json_object.put( JSONString.String( "1" ), JSONBoolean.False );
			json_object.put( JSONString.String( "2" ), JSONBoolean.True );
			json_object.put( JSONString.String( "3" ), JSONString.String( "streng" ) );
			json_object.put( JSONString.String( "4" ), JSONNumber.Integer( 0 ) );
			json_object.put( JSONString.String( "5" ), JSONNumber.Integer( -0 ) );
			json_object.put( JSONString.String( "6" ), JSONNumber.Integer( -31415 ) );
			json_object.put( JSONString.String( "7" ), JSONNumber.Float( 3.1415F ) );
			json_object.put( JSONString.String( "8" ), JSONNumber.Double( 3.141592 ) );
			json_object.put( JSONString.String( "9" ), JSONNumber.BigInteger( new BigInteger( "31415" ) ) );
			json_object.put( JSONString.String( "10" ), JSONNumber.BigDecimal( new BigDecimal( "3.141592e04" ) ) );
			json_object.put( JSONString.String( "11" ), JSONNumber.BigDecimal( new BigDecimal( "3.141592E04" ) ) );
			json_object.put( JSONString.String( "12" ), JSONNumber.BigDecimal( new BigDecimal( "0.0003141592e04" ) ) );
			json_object.put( JSONString.String( "13" ), JSONNumber.BigDecimal( new BigDecimal( "0.0003141592E04" ) ) );
			json_object.put( JSONString.String( "14" ), JSONNumber.BigDecimal( new BigDecimal( "3141592e04" ) ) );
			json_object.put( JSONString.String( "15" ), JSONNumber.BigDecimal( new BigDecimal( "3141592E04" ) ) );
			out.reset();
			json_textMarshaller.toJSONText( json_object, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_OBJECT, json_struct.type );

			Assert.assertEquals( json_object.toString(), json_struct.toString() );

			Assert.assertEquals( JSONNull.Null, json_struct.get( JSONString.String( "0" ) ) );
			Assert.assertEquals( JSONBoolean.False, json_struct.get( JSONString.String( "1" ) ) );
			Assert.assertEquals( JSONBoolean.True, json_struct.get( JSONString.String( "2" ) ) );
			Assert.assertEquals( JSONString.String( "streng" ), json_struct.get( JSONString.String( "3" ) ) );
			Assert.assertEquals( JSONNumber.Integer( 0 ), json_struct.get( JSONString.String( "4" ) ) );
			Assert.assertEquals( JSONNumber.Integer( -0 ), json_struct.get( JSONString.String( "5" ) ) );
			Assert.assertEquals( JSONNumber.Integer( -31415 ), json_struct.get( JSONString.String( "6" ) ) );
			Assert.assertEquals( JSONNumber.Float( 3.1415F ), json_struct.get( JSONString.String( "7" ) ) );
			Assert.assertEquals( JSONNumber.Double( 3.141592 ), json_struct.get( JSONString.String( "8" ) ) );
			Assert.assertEquals( JSONNumber.BigInteger( new BigInteger( "31415" ) ), json_struct.get( JSONString.String( "9" ) ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592e04" ) ), json_struct.get( JSONString.String( "10" ) ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592E04" ) ), json_struct.get( JSONString.String( "11" ) ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "0.0003141592e04" ) ), json_struct.get( JSONString.String( "12" ) ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "0.0003141592E04" ) ), json_struct.get( JSONString.String( "13" ) ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3141592e04" ) ), json_struct.get( JSONString.String( "14" ) ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3141592E04" ) ), json_struct.get( JSONString.String( "15" ) ) );

			/*
			 * {...}
			 */

			json_object = new JSONObject();
			json_object.put( "0", JSONNull.Null );
			json_object.put( "1", JSONBoolean.False );
			json_object.put( "2", JSONBoolean.True );
			json_object.put( "3", JSONString.String( "streng" ) );
			json_object.put( "4", JSONNumber.Integer( 0 ) );
			json_object.put( "5", JSONNumber.Integer( -0 ) );
			json_object.put( "6", JSONNumber.Integer( -31415 ) );
			json_object.put( "7", JSONNumber.Float( 3.1415F ) );
			json_object.put( "8", JSONNumber.Double( 3.141592 ) );
			json_object.put( "9", JSONNumber.BigInteger( new BigInteger( "31415" ) ) );
			json_object.put( "10", JSONNumber.BigDecimal( new BigDecimal( "3.141592e04" ) ) );
			json_object.put( "11", JSONNumber.BigDecimal( new BigDecimal( "3.141592E04" ) ) );
			json_object.put( "12", JSONNumber.BigDecimal( new BigDecimal( "0.0003141592e04" ) ) );
			json_object.put( "13", JSONNumber.BigDecimal( new BigDecimal( "0.0003141592E04" ) ) );
			json_object.put( "14", JSONNumber.BigDecimal( new BigDecimal( "3141592e04" ) ) );
			json_object.put( "15", JSONNumber.BigDecimal( new BigDecimal( "3141592E04" ) ) );
			out.reset();
			json_textMarshaller.toJSONText( json_object, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_OBJECT, json_struct.type );

			Assert.assertEquals( json_object.toString(), json_struct.toString() );

			Assert.assertEquals( JSONNull.Null, json_struct.get( JSONString.String( "0" ) ) );
			Assert.assertEquals( JSONBoolean.False, json_struct.get( JSONString.String( "1" ) ) );
			Assert.assertEquals( JSONBoolean.True, json_struct.get( JSONString.String( "2" ) ) );
			Assert.assertEquals( JSONString.String( "streng" ), json_struct.get( JSONString.String( "3" ) ) );
			Assert.assertEquals( JSONNumber.Integer( 0 ), json_struct.get( "4" ) );
			Assert.assertEquals( JSONNumber.Integer( -0 ), json_struct.get( "5" ) );
			Assert.assertEquals( JSONNumber.Integer( -31415 ), json_struct.get( "6" ) );
			Assert.assertEquals( JSONNumber.Float( 3.1415F ), json_struct.get( "7" ) );
			Assert.assertEquals( JSONNumber.Double( 3.141592 ), json_struct.get( "8" ) );
			Assert.assertEquals( JSONNumber.BigInteger( new BigInteger( "31415" ) ), json_struct.get( "9" ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592e04" ) ), json_struct.get( "10" ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592E04" ) ), json_struct.get( "11" ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "0.0003141592e04" ) ), json_struct.get( "12" ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "0.0003141592E04" ) ), json_struct.get( "13" ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3141592e04" ) ), json_struct.get( "14" ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3141592E04" ) ), json_struct.get( "15" ) );

			/*
			 * [...,[...]]
			 */

			json_array = new JSONArray();
			json_array.add( JSONNull.Null );
			json_array.add( JSONBoolean.False );
			json_array.add( JSONBoolean.True );
			json_array.add( JSONNumber.Integer( 42 ) );
			json_array.add( JSONNumber.Long( 12345678901234L ) );
			json_array.add( JSONNumber.Float( 1.0F / 3.0F ) );
			json_array.add( JSONNumber.Double( 1.0 / 3.0 ) );
			json_array.add( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ) );
			json_array2 = new JSONArray();
			json_array.add( json_array2 );
			json_array2.add( JSONNull.Null );
			json_array2.add( JSONBoolean.False );
			json_array2.add( JSONBoolean.True );
			json_array2.add( JSONNumber.Integer( 42 ) );
			json_array2.add( JSONNumber.Long( 12345678901234L ) );
			json_array2.add( JSONNumber.Float( 1.0F / 3.0F ) );
			json_array2.add( JSONNumber.Double( 1.0/3.0 ) );
			json_array2.add( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ) );
			json_array2.add( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ) );
			out.reset();
			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_struct.type );

			Assert.assertEquals( json_array.toString(), json_struct.toString() );

			Assert.assertEquals( JSONNull.Null, json_array.get( 0 ) );
			Assert.assertEquals( JSONBoolean.False, json_array.get( 1 ) );
			Assert.assertEquals( JSONBoolean.True, json_array.get( 2 ) );
			Assert.assertEquals( JSONNumber.Integer( 42 ), json_array.get( 3 ) );
			Assert.assertEquals( JSONNumber.Long( 12345678901234L ), json_array.get( 4 ) );
			Assert.assertEquals( JSONNumber.Float( 1.0F / 3.0F ), json_array.get( 5 ) );
			Assert.assertEquals( JSONNumber.Double( 1.0 / 3.0 ), json_array.get( 6 ) );
			Assert.assertEquals( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ), json_array.get( 7 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ), json_array.get( 8 ) );
			json_array2 = (JSONArray)json_array.get( 9 );
			Assert.assertEquals( JSONNull.Null, json_array2.get( 0 ) );
			Assert.assertEquals( JSONBoolean.False, json_array2.get( 1 ) );
			Assert.assertEquals( JSONBoolean.True, json_array2.get( 2 ) );
			Assert.assertEquals( JSONNumber.Integer( 42 ), json_array2.get( 3 ) );
			Assert.assertEquals( JSONNumber.Long( 12345678901234L ), json_array2.get( 4 ) );
			Assert.assertEquals( JSONNumber.Float( 1.0F / 3.0F ), json_array2.get( 5 ) );
			Assert.assertEquals( JSONNumber.Double( 1.0 / 3.0 ), json_array2.get( 6 ) );
			Assert.assertEquals( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ), json_array2.get( 7 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ), json_array2.get( 8 ) );

			/*
			 * [...,[],{}]
			 */

			json_array = new JSONArray();
			json_array.add( JSONNull.Null );
			json_array.add( JSONBoolean.False );
			json_array.add( JSONBoolean.True );
			json_array.add( JSONNumber.Integer( 42 ) );
			json_array.add( JSONNumber.Long( 12345678901234L ) );
			json_array.add( JSONNumber.Float( 1.0F / 3.0F ) );
			json_array.add( JSONNumber.Double( 1.0 / 3.0 ) );
			json_array.add( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ) );
			json_array2 = new JSONArray();
			json_array.add( json_array2 );
			json_object2 = new JSONObject();
			json_array.add( json_object2 );
			out.reset();
			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_struct.type );

			Assert.assertEquals( json_array.toString(), json_struct.toString() );

			Assert.assertEquals( JSONNull.Null, json_array.get( 0 ) );
			Assert.assertEquals( JSONBoolean.False, json_array.get( 1 ) );
			Assert.assertEquals( JSONBoolean.True, json_array.get( 2 ) );
			Assert.assertEquals( JSONNumber.Integer( 42 ), json_array.get( 3 ) );
			Assert.assertEquals( JSONNumber.Long( 12345678901234L ), json_array.get( 4 ) );
			Assert.assertEquals( JSONNumber.Float( 1.0F / 3.0F ), json_array.get( 5 ) );
			Assert.assertEquals( JSONNumber.Double( 1.0 / 3.0 ), json_array.get( 6 ) );
			Assert.assertEquals( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ), json_array.get( 7 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ), json_array.get( 8 ) );
			json_array2 = (JSONArray)json_array.get( 9 );
			Assert.assertTrue( json_array2.values.isEmpty() );
			json_object2 = (JSONObject)json_array.get( 10 );
			Assert.assertTrue( json_object2.nameValuePairs.isEmpty() );

			/*
			 * [[...],...]
			 */

			json_array = new JSONArray();
			json_array2 = new JSONArray();
			json_array.add( json_array2 );
			json_array2.add( JSONNull.Null );
			json_array2.add( JSONBoolean.False );
			json_array2.add( JSONBoolean.True );
			json_array2.add( JSONNumber.Integer( 42 ) );
			json_array2.add( JSONNumber.Long( 12345678901234L ) );
			json_array2.add( JSONNumber.Float( 1.0F / 3.0F ) );
			json_array2.add( JSONNumber.Double( 1.0/3.0 ) );
			json_array2.add( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ) );
			json_array2.add( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ) );
			json_array.add( JSONNull.Null );
			json_array.add( JSONBoolean.False );
			json_array.add( JSONBoolean.True );
			json_array.add( JSONNumber.Integer( 42 ) );
			json_array.add( JSONNumber.Long( 12345678901234L ) );
			json_array.add( JSONNumber.Float( 1.0F / 3.0F ) );
			json_array.add( JSONNumber.Double( 1.0 / 3.0 ) );
			json_array.add( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ) );
			json_array.add( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ) );
			out.reset();
			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_struct.type );

			Assert.assertEquals( json_array.toString(), json_struct.toString() );

			json_array2 = (JSONArray)json_array.get( 0 );
			Assert.assertEquals( JSONNull.Null, json_array2.get( 0 ) );
			Assert.assertEquals( JSONBoolean.False, json_array2.get( 1 ) );
			Assert.assertEquals( JSONBoolean.True, json_array2.get( 2 ) );
			Assert.assertEquals( JSONNumber.Integer( 42 ), json_array2.get( 3 ) );
			Assert.assertEquals( JSONNumber.Long( 12345678901234L ), json_array2.get( 4 ) );
			Assert.assertEquals( JSONNumber.Float( 1.0F / 3.0F ), json_array2.get( 5 ) );
			Assert.assertEquals( JSONNumber.Double( 1.0 / 3.0 ), json_array2.get( 6 ) );
			Assert.assertEquals( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ), json_array2.get( 7 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ), json_array2.get( 8 ) );

			Assert.assertEquals( JSONNull.Null, json_array.get( 1 ) );
			Assert.assertEquals( JSONBoolean.False, json_array.get( 2 ) );
			Assert.assertEquals( JSONBoolean.True, json_array.get( 3 ) );
			Assert.assertEquals( JSONNumber.Integer( 42 ), json_array.get( 4 ) );
			Assert.assertEquals( JSONNumber.Long( 12345678901234L ), json_array.get( 5 ) );
			Assert.assertEquals( JSONNumber.Float( 1.0F / 3.0F ), json_array.get( 6 ) );
			Assert.assertEquals( JSONNumber.Double( 1.0 / 3.0 ), json_array.get( 7 ) );
			Assert.assertEquals( JSONNumber.BigInteger( new BigInteger( "123456789012345678901234567890123456789012" ) ), json_array.get( 8 ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825" ) ), json_array.get( 9 ) );

			/*
			 *  [...,{...}]
			 */

			json_array = new JSONArray();
			json_array.add( JSONNull.Null );
			json_array.add( JSONBoolean.False );
			json_array.add( JSONBoolean.True );
			json_object = new JSONObject();
			json_array.add( json_object );
			json_object.put( new JSONString( "Null" ), JSONNull.Null );
			json_object.put( new JSONString( "False" ), JSONBoolean.False );
			json_object.put( new JSONString( "True" ), JSONBoolean.True );
			out.reset();
			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_struct.type );

			//Assert.assertEquals( json_array.toString(), json_struct.toString() );

			Assert.assertEquals( JSONNull.Null, json_struct.get( 0 ) );
			Assert.assertEquals( JSONBoolean.False, json_struct.get( 1 ) );
			Assert.assertEquals( JSONBoolean.True, json_struct.get( 2 ) );
			json_object2 = (JSONObject)json_struct.get( 3 );
			Assert.assertEquals( JSONNull.Null, json_object2.get( new JSONString( "Null" ) ) );
			Assert.assertEquals( JSONBoolean.False, json_object2.get( new JSONString( "False" ) ) );
			Assert.assertEquals( JSONBoolean.True, json_object2.get( new JSONString( "True" ) ) );

			/*
			 *  [{...},...]
			 */

			json_array = new JSONArray();
			json_object = new JSONObject();
			json_array.add( json_object );
			json_object.put( new JSONString( "Null" ), JSONNull.Null );
			json_object.put( new JSONString( "False" ), JSONBoolean.False );
			json_object.put( new JSONString( "True" ), JSONBoolean.True );
			json_array.add( JSONNull.Null );
			json_array.add( JSONBoolean.False );
			json_array.add( JSONBoolean.True );
			out.reset();
			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_struct.type );

			//Assert.assertEquals( json_array.toString(), json_struct.toString() );

			json_object2 = (JSONObject)json_struct.get( 0 );
			Assert.assertEquals( JSONNull.Null, json_object2.get( new JSONString( "Null" ) ) );
			Assert.assertEquals( JSONBoolean.False, json_object2.get( new JSONString( "False" ) ) );
			Assert.assertEquals( JSONBoolean.True, json_object2.get( new JSONString( "True" ) ) );

			Assert.assertEquals( JSONNull.Null, json_struct.get( 1 ) );
			Assert.assertEquals( JSONBoolean.False, json_struct.get( 2 ) );
			Assert.assertEquals( JSONBoolean.True, json_struct.get( 3 ) );

			/*
			 * {...,[...]}
			 */

			json_object = new JSONObject();
			json_object.put( new JSONString( "Null" ), JSONNull.Null );
			json_object.put( new JSONString( "False" ), JSONBoolean.False );
			json_object.put( new JSONString( "True" ), JSONBoolean.True );
			json_array = new JSONArray();
			json_object.put( new JSONString( "Array" ), json_array );
			json_array.add( JSONNull.Null );
			json_array.add( JSONBoolean.False );
			json_array.add( JSONBoolean.True );
			out.reset();
			json_textMarshaller.toJSONText( json_object, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_OBJECT, json_struct.type );

			//Assert.assertEquals( json_object.toString(), json_struct.toString() );

			Assert.assertEquals( json_object.get( new JSONString( "Null" ) ), JSONNull.Null );
			Assert.assertEquals( json_object.get( new JSONString( "False" ) ), JSONBoolean.False );
			Assert.assertEquals( json_object.get( new JSONString( "True" ) ), JSONBoolean.True );
			json_array2 = (JSONArray)json_struct.get( new JSONString( "Array" ) );
			Assert.assertEquals( JSONNull.Null, json_array2.get( 0 ) );
			Assert.assertEquals( JSONBoolean.False, json_array2.get( 1 ) );
			Assert.assertEquals( JSONBoolean.True, json_array2.get( 2 ) );

			/*
			 * {...,{...}}
			 */

			json_object = new JSONObject();
			json_object.put( new JSONString( "Null" ), JSONNull.Null );
			json_object.put( new JSONString( "False" ), JSONBoolean.False );
			json_object.put( new JSONString( "True" ), JSONBoolean.True );
			json_object2 = new JSONObject();
			json_object.put( new JSONString( "Object" ), json_object2 );
			json_object2.put( new JSONString( "Null" ), JSONNull.Null );
			json_object2.put( new JSONString( "False" ), JSONBoolean.False );
			json_object2.put( new JSONString( "True" ), JSONBoolean.True );
			out.reset();
			json_textMarshaller.toJSONText( json_object, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_OBJECT, json_struct.type );

			//Assert.assertEquals( json_object.toString(), json_struct.toString() );

			Assert.assertEquals( JSONNull.Null, json_struct.get( new JSONString( "Null" ) ) );
			Assert.assertEquals( JSONBoolean.False, json_struct.get( new JSONString( "False" ) ) );
			Assert.assertEquals( JSONBoolean.True, json_struct.get( new JSONString( "True" ) ) );
			json_object2 = (JSONObject)json_struct.get( new JSONString( "Object" ) );
			Assert.assertEquals( JSONNull.Null, json_object2.get( new JSONString( "Null" ) ) );
			Assert.assertEquals( JSONBoolean.False, json_object2.get( new JSONString( "False" ) ) );
			Assert.assertEquals( JSONBoolean.True, json_object2.get( new JSONString( "True" ) ) );

			/*
			 * {...,{},[]}
			 */

			json_object = new JSONObject();
			json_object.put( new JSONString( "Null" ), JSONNull.Null );
			json_object.put( new JSONString( "False" ), JSONBoolean.False );
			json_object.put( new JSONString( "True" ), JSONBoolean.True );
			json_object2 = new JSONObject();
			json_object.put( new JSONString( "Object" ), json_object2 );
			json_array2 = new JSONArray();
			json_object.put( new JSONString( "Array" ), json_array2 );
			out.reset();
			json_textMarshaller.toJSONText( json_object, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json_textUnmarshaller.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
			Assert.assertEquals( JSONConstants.VT_OBJECT, json_struct.type );

			//Assert.assertEquals( json_object.toString(), json_struct.toString() );

			Assert.assertEquals( JSONNull.Null, json_struct.get( new JSONString( "Null" ) ) );
			Assert.assertEquals( JSONBoolean.False, json_struct.get( new JSONString( "False" ) ) );
			Assert.assertEquals( JSONBoolean.True, json_struct.get( new JSONString( "True" ) ) );
			json_object2 = (JSONObject)json_struct.get( new JSONString( "Object" ) );
			Assert.assertTrue( json_object2.nameValuePairs.isEmpty() );
			json_array2 = (JSONArray)json_struct.get( new JSONString( "Array" ) );
			Assert.assertTrue( json_array2.values.isEmpty() );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}
		catch (IllegalCharsetNameException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}
		catch (UnsupportedCharsetException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}
	}

	@Test
	public void test_jsontext_whitespace() {
		JSONCollection json_struct;
		JSONArray json_array;
		JSONObject json_object;

		try {
			byte[] bytes;
			PushbackInputStream pbin;
			int encoding;

			JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
			JSONDecoder json_decoder;
			JSONTextUnmarshaller json = new JSONTextUnmarshaller();

			bytes = new byte[] {
					0x20, 0x09, 0x0A, 0x0D,
					'[',
					0x20, 0x09, 0x0A, 0x0D,
					'0',
					0x20, 0x09, 0x0A, 0x0D,
					',',
					0x20, 0x09, 0x0A, 0x0D,
					'n', 'u', 'l', 'l',
					0x20, 0x09, 0x0A, 0x0D,
					',',
					0x20, 0x09, 0x0A, 0x0D,
					'{',
					0x20, 0x09, 0x0A, 0x0D,
					'"', 'v', '1', '"',
					0x20, 0x09, 0x0A, 0x0D,
					':',
					0x20, 0x09, 0x0A, 0x0D,
					'0',
					0x20, 0x09, 0x0A, 0x0D,
					',',
					0x20, 0x09, 0x0A, 0x0D,
					'"', 'v', '2', '"',
					0x20, 0x09, 0x0A, 0x0D,
					':',
					0x20, 0x09, 0x0A, 0x0D,
					'-', '1',
					0x20, 0x09, 0x0A, 0x0D,
					',',
					0x20, 0x09, 0x0A, 0x0D,
					'"', 'v', '3', '"',
					0x20, 0x09, 0x0A, 0x0D,
					':',
					0x20, 0x09, 0x0A, 0x0D,
					'"', 's', 't', 'r', '"',
					0x20, 0x09, 0x0A, 0x0D,
					'}',
					0x20, 0x09, 0x0A, 0x0D,
					']',
					0x20, 0x09, 0x0A, 0x0D
			};

			// debug
			//System.out.println( new String( bytes ) );

			pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );

			json_array = (JSONArray)json_struct;
			Assert.assertEquals( JSONNumber.Integer( 0 ), json_array.get( 0 ) );
			Assert.assertEquals( JSONNull.Null, json_array.get( 1 ) );
			json_object = (JSONObject)json_array.get( 2 );
			Assert.assertEquals( JSONNumber.Integer( 0 ), json_object.get( "v1" ) );
			Assert.assertEquals( JSONNumber.Integer( -1 ), json_object.get( "v2" ) );
			Assert.assertEquals( JSONString.String( "str" ), json_object.get( "v3" ) );
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
	public void test_jsontext_() {
	}

	@Test
	public void test_jsontext_rfc() {
		JSONCollection json_struct;
		JSONArray json_array;
		JSONObject json_object;
		JSONObject json_object2;

		try {
			String text;
			byte[] bytes;
			PushbackInputStream pbin;
			int encoding;

			JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
			JSONDecoder json_decoder;
			JSONTextUnmarshaller json = new JSONTextUnmarshaller();

			text = "{\n"
					+ "    \"Image\": {\n"
					+ "        \"Width\":  800,\n"
					+ "        \"Height\": 600,\n"
					+ "        \"Title\":  \"View from 15th Floor\",\n"
					+ "        \"Thumbnail\": {\n"
					+ "            \"Url\":    \"http://www.example.com/image/481989943\",\n"
					+ "            \"Height\": 125,\n"
					+ "            \"Width\":  \"100\"\n"
					+ "        },\n"
					+ "        \"IDs\": [116, 943, 234, 38793]\n"
					+ "    }\n"
					+ "}\n";
			bytes = text.getBytes( "UTF-8" );

			pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );

			json_object = (JSONObject)json_struct;
			json_object = (JSONObject)json_object.get( "Image" );
			Assert.assertEquals( JSONNumber.Integer( 800 ), json_object.get( "Width" ) );
			Assert.assertEquals( JSONNumber.Integer( 600 ), json_object.get( "Height" ) );
			Assert.assertEquals( JSONString.String( "View from 15th Floor" ), json_object.get( "Title" ) );
			json_object2 = (JSONObject)json_object.get( "Thumbnail" );
			Assert.assertEquals( JSONString.String( "http://www.example.com/image/481989943" ), json_object2.get( "Url" ) );
			Assert.assertEquals( JSONNumber.Integer( 125 ), json_object2.get( "Height" ) );
			Assert.assertEquals( JSONString.String( "100" ), json_object2.get( "Width" ) );
			json_array = (JSONArray)json_object.get( "IDs" );
			Assert.assertEquals( JSONNumber.Integer( 116 ), json_array.get( 0 ) );
			Assert.assertEquals( JSONNumber.Integer( 943 ), json_array.get( 1 ) );
			Assert.assertEquals( JSONNumber.Integer( 234 ), json_array.get( 2 ) );
			Assert.assertEquals( JSONNumber.Integer( 38793 ), json_array.get( 3 ) );

			text = "[\r\n"
					+ "    {\r\n"
					+ "       \"precision\": \"zip\",\r\n"
					+ "       \"Latitude\":  37.7668,\r\n"
					+ "       \"Longitude\": -122.3959,\r\n"
					+ "       \"Address\":   \"\",\r\n"
					+ "       \"City\":      \"SAN FRANCISCO\",\r\n"
					+ "       \"State\":     \"CA\",\r\n"
					+ "       \"Zip\":       \"94107\",\r\n"
					+ "       \"Country\":   \"US\"\r\n"
					+ "    },\r\n"
					+ "    {\r\n"
					+ "       \"precision\": \"zip\",\r\n"
					+ "       \"Latitude\":  37.371991,\r\n"
					+ "       \"Longitude\": -122.026020,\r\n"
					+ "       \"Address\":   \"\",\r\n"
					+ "       \"City\":      \"SUNNYVALE\",\r\n"
					+ "       \"State\":     \"CA\",\r\n"
					+ "       \"Zip\":       \"94085\",\r\n"
					+ "       \"Country\":   \"US\"\r\n"
					+ "    }\r\n"
					+ "]\r\n";
			bytes = text.getBytes( "UTF-8" );

			pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );

			json_array = (JSONArray)json_struct;
			json_object = (JSONObject)json_array.get( 0 );
			Assert.assertEquals( JSONString.String( "zip" ), json_object.get( "precision" ) );
			Assert.assertEquals( JSONNumber.Float( 37.7668F ), json_object.get( "Latitude" ) );
			Assert.assertEquals( JSONNumber.Float( -122.3959F ), json_object.get( "Longitude" ) );
			Assert.assertEquals( JSONString.String( "" ), json_object.get( "Address" ) );
			Assert.assertEquals( JSONString.String( "SAN FRANCISCO" ), json_object.get( "City" ) );
			Assert.assertEquals( JSONString.String( "CA" ), json_object.get( "State" ) );
			Assert.assertEquals( JSONString.String( "94107" ), json_object.get( "Zip" ) );
			Assert.assertEquals( JSONString.String( "US" ), json_object.get( "Country" ) );
			json_object = (JSONObject)json_array.get( 1 );
			Assert.assertEquals( JSONString.String( "zip" ), json_object.get( "precision" ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "37.371991" ) ), json_object.get( "Latitude" ) );
			Assert.assertEquals( JSONNumber.BigDecimal( new BigDecimal( "-122.026020" ) ), json_object.get( "Longitude" ) );
			Assert.assertEquals( JSONString.String( "" ), json_object.get( "Address" ) );
			Assert.assertEquals( JSONString.String( "SUNNYVALE" ), json_object.get( "City" ) );
			Assert.assertEquals( JSONString.String( "CA" ), json_object.get( "State" ) );
			Assert.assertEquals( JSONString.String( "94085" ), json_object.get( "Zip" ) );
			Assert.assertEquals( JSONString.String( "US" ), json_object.get( "Country" ) );
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

	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for ( int j = 0; j < bytes.length; j++ ) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
