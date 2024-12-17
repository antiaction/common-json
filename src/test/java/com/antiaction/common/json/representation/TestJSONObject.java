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
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.JSONDecoder;
import com.antiaction.common.json.JSONEncoder;
import com.antiaction.common.json.JSONEncoderCharset;
import com.antiaction.common.json.JSONEncoding;
import com.antiaction.common.json.JSONException;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 19/10/2012
 */
@RunWith(JUnit4.class)
public class TestJSONObject {

	@Test
	public void test_jsonobject() {
		JSONObject json_object = new JSONObject();
		json_object.put( new JSONString( "name" ), JSONNull.Null );

		try {
			json_object.add( new JSONString( "name" ) );
			Assert.fail( "Exception expected!" );
		}
		catch (UnsupportedOperationException e) {
		}

		try {
			json_object.get( 42 );
			Assert.fail( "Exception expected!" );
		}
		catch (UnsupportedOperationException e) {
		}
	}


	@Test
	public void test_jsonobject_large() {
		Charset charset;

		charset = Charset.forName("UTF-8");
		test_jsonobject_large_params( charset, JSONEncoding.E_UTF8 );

		charset = Charset.forName("UTF-16");
		test_jsonobject_large_params( charset, JSONEncoding.E_UTF16BE );

		charset = Charset.forName("UTF-16BE");
		test_jsonobject_large_params( charset, JSONEncoding.E_UTF16BE );

		charset = Charset.forName("UTF-16LE");
		test_jsonobject_large_params( charset, JSONEncoding.E_UTF16LE );
	}

	public void test_jsonobject_large_params(Charset charset, int expected_encoding) {
		try {
			SecureRandom random = new SecureRandom();
			byte[] bytes;

			Map<String, byte[]> inObjects = new HashMap<String, byte[]>();
			for ( int i=0; i<8; ++i ) {
				bytes = new byte[ 16384 ];
				random.nextBytes( bytes );
				inObjects.put( UUID.randomUUID().toString(), bytes );
			}

			JSONCollection json_struct = new JSONObject();

			Iterator<Entry<String, byte[]>> objectIter = inObjects.entrySet().iterator();
			Entry<String, byte[]> objectEntry;
			while ( objectIter.hasNext() ) {
				objectEntry = objectIter.next();
				json_struct.put( objectEntry.getKey(), JSONString.String( objectEntry.getValue() ) );
			}

			JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
			JSONEncoder json_encoder = new JSONEncoderCharset( charset );
			int encoding;
			JSONDecoder json_decoder;
			JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
			JSONTextUnmarshaller json_textUnmarshaller = new JSONTextUnmarshaller();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PushbackInputStream in;

			out.reset();
			json_textMarshaller.toJSONText( json_struct, json_encoder, false, out );

			in = new PushbackInputStream( new ByteArrayInputStream( out.toByteArray() ), 4 );
			encoding = JSONEncoding.encoding( in );
			Assert.assertEquals( expected_encoding, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			json_struct = json_textUnmarshaller.toJSONStructure( in, json_decoder );
			in.close();

			objectIter = inObjects.entrySet().iterator();
			while ( objectIter.hasNext() ) {
				objectEntry = objectIter.next();
				Assert.assertArrayEquals( inObjects.get( objectEntry.getKey() ), json_struct.get( objectEntry.getKey() ).getBytes() );
				// debug
				//System.out.println( json_struct.get( objectEntry.getKey() ).getBytes().length );
			}
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

	@Test
	public void test_jsonobject_supported_unsupported() {
		JSONCollection json_struct = new JSONObject();
		JSONObject json_object = json_struct.getObject();
		Assert.assertEquals( json_struct, json_object );

		try {
			json_struct.getArray();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getBoolean();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getString();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getBytes();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getInteger();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getLong();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getFloat();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getDouble();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getBigInteger();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_struct.getBigDecimal();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}

		Assert.assertEquals( 0, json_object.nameValuePairs.size() );

		Assert.assertNull( json_object.get( JSONString.String( "one" ) ) );
		JSONObject json_object_added = json_object.addObject( "one" );
		Assert.assertEquals( 1, json_object.nameValuePairs.size() );
		Assert.assertTrue( json_object_added.equals( json_object.get( JSONString.String( "one" ) ) ) );

		Assert.assertNull( json_object.get( "two" ) );
		JSONObject json_object_added2 = json_object.addObject( JSONString.String( "two" ) );
		Assert.assertEquals( 2, json_object.nameValuePairs.size() );
		Assert.assertTrue( json_object_added.equals( json_object.get( JSONString.String( "one" ) ) ) );
		Assert.assertTrue( json_object_added2.equals( json_object.get( "two" ) ) );

		Assert.assertNull( json_object.get( JSONString.String( "three" ) ) );
		JSONArray json_array_added = json_object.addArray( "three" );
		Assert.assertEquals( 3, json_object.nameValuePairs.size() );
		Assert.assertTrue( json_object_added.equals( json_object.get( JSONString.String( "one" ) ) ) );
		Assert.assertTrue( json_object_added2.equals( json_object.get( "two" ) ) );
		Assert.assertTrue( json_array_added.equals( json_object.get( JSONString.String( "three" ) ) ) );

		Assert.assertNull( json_object.get( "four" ) );
		JSONArray json_array_added2 = json_object.addArray( JSONString.String( "four" ) );
		Assert.assertEquals( 4, json_object.nameValuePairs.size() );
		Assert.assertTrue( json_object_added.equals( json_object.get( JSONString.String( "one" ) ) ) );
		Assert.assertTrue( json_object_added2.equals( json_object.get( "two" ) ) );
		Assert.assertTrue( json_array_added.equals( json_object.get( JSONString.String( "three" ) ) ) );
		Assert.assertTrue( json_array_added2.equals( json_object.get( "four" ) ) );
	}

}
