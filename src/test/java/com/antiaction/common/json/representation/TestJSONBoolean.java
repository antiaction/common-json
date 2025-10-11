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
import java.nio.charset.Charset;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.JSONConstants;
import com.antiaction.common.json.JSONDecoder;
import com.antiaction.common.json.JSONDecoderCharset;
import com.antiaction.common.json.JSONEncoder;
import com.antiaction.common.json.JSONEncoderCharset;
import com.antiaction.common.json.JSONException;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 07/09/2012
 */
@RunWith(JUnit4.class)
public class TestJSONBoolean {

	@Test
	public void test_jsonboolean() {
		try {
			JSONBoolean json_boolean;

			json_boolean = JSONBoolean.False;
			Assert.assertNotNull( json_boolean );
			Assert.assertEquals( JSONConstants.VT_BOOLEAN, json_boolean.type );
			Assert.assertFalse( json_boolean.b );
			Assert.assertFalse( json_boolean.getBoolean() );

			Assert.assertEquals( "false", json_boolean.toString() );

			json_boolean = JSONBoolean.True;
			Assert.assertNotNull( json_boolean );
			Assert.assertEquals( JSONConstants.VT_BOOLEAN, json_boolean.type );
			Assert.assertTrue( json_boolean.b );
			Assert.assertTrue( json_boolean.getBoolean() );

			Assert.assertEquals( "true", json_boolean.toString() );

			json_boolean = JSONBoolean.Boolean( false );
			Assert.assertNotNull( json_boolean );
			Assert.assertEquals( JSONConstants.VT_BOOLEAN, json_boolean.type );
			Assert.assertFalse( json_boolean.b );
			Assert.assertFalse( json_boolean.getBoolean() );

			Assert.assertEquals( "false", json_boolean.toString() );

			json_boolean = JSONBoolean. Boolean( true );
			Assert.assertNotNull( json_boolean );
			Assert.assertEquals( JSONConstants.VT_BOOLEAN, json_boolean.type );
			Assert.assertTrue( json_boolean.b );
			Assert.assertTrue( json_boolean.getBoolean() );

			Assert.assertEquals( "true", json_boolean.toString() );

			/*
			 *
			 */

			JSONBoolean False = JSONBoolean.False;
			JSONBoolean True = JSONBoolean.True;

			Charset charset = Charset.forName("UTF-8");
			JSONEncoder json_encoder = new JSONEncoderCharset( charset );

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
			JSONTextUnmarshaller json_textUnmarshaller = new JSONTextUnmarshaller();

			JSONArray json_array = new JSONArray();
			json_array.add( False );
			json_array.add( True );

			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() )  );

			ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
			JSONDecoder json_decoder = new JSONDecoderCharset( charset );

			JSONCollection json_structure = json_textUnmarshaller.toJSONStructure( in, json_decoder );

			Assert.assertNotNull( json_structure );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_structure.type );
			json_array = (JSONArray)json_structure;
			Assert.assertEquals( 2, json_array.values.size() );
			Assert.assertEquals( False, json_array.values.get( 0 ) );
			Assert.assertEquals( True, json_array.values.get( 1 ) );
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
	public void test_jsonboolean_unsupporten() {
		JSONBoolean json_boolean = JSONBoolean.Boolean( true );
		try {
			json_boolean.getArray();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getObject();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getString();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getBytes();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getInteger();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getLong();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getFloat();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getDouble();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getBigInteger();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
		try {
			json_boolean.getBigDecimal();
			Assert.fail( "Exception expected !" );
		}
		catch (UnsupportedOperationException e) {
		}
	}

	@Test
	public void test_jsonboolean_equals_hashcode() {
		JSONBoolean False = JSONBoolean.False;
		JSONBoolean True = JSONBoolean.True;

		Assert.assertFalse( False.equals( null ) );
		Assert.assertFalse( True.equals( null ) );
		Assert.assertFalse( False.equals( "false" ) );
		Assert.assertFalse( True.equals( "true" ) );
		Assert.assertFalse( False.equals( True ) );
		Assert.assertFalse( True.equals( False ) );
		Assert.assertFalse( False.equals( new JSONBoolean( true ) ) );
		Assert.assertFalse( True.equals( new JSONBoolean( false ) ) );

		Assert.assertTrue( False.equals( new JSONBoolean( false ) ) );
		Assert.assertTrue( True.equals( new JSONBoolean( true ) ) );

		Assert.assertEquals( False.hashCode(), new JSONBoolean( false ).hashCode() );
		Assert.assertEquals( True.hashCode(), new JSONBoolean( true ).hashCode() );
	}

}
