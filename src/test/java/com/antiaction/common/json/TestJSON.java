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
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.representation.JSONArray;
import com.antiaction.common.json.representation.JSONBoolean;
import com.antiaction.common.json.representation.JSONNumber;
import com.antiaction.common.json.representation.JSONObject;
import com.antiaction.common.json.representation.JSONString;
import com.antiaction.common.json.representation.JSONCollection;
import com.antiaction.common.json.representation.JSONTextUnmarshaller;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 02/11/2012
 */
@RunWith(JUnit4.class)
public class TestJSON {

	@Test
	public void test_json() {
		JSONCollection json_struct;
		JSONObject json_object;
		JSONObject json_object2;
		JSONArray json_array;

		try {
			String text;
			byte[] bytes;
			PushbackInputStream pbin;
			int encoding;

			JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
			JSONDecoder json_decoder;
			JSONTextUnmarshaller json = new JSONTextUnmarshaller();

			/*
			 * stopforumspam.com example.
			 */

			text = "{\"success\":true,\"email\":{\"lastseen\":\"2009-06-25 00:24:29\",\"frequency\":2,\"appears\":true},\"username\":{\"frequency\":0,\"appears\":false}}";
			bytes = text.getBytes( "UTF-8" );

			pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );

			json_object = (JSONObject)json_struct;
			Assert.assertEquals( JSONBoolean.True, json_object.get( "success" ) );
			json_object2 = (JSONObject)json_object.get( "email" );
			Assert.assertEquals( JSONString.String( "2009-06-25 00:24:29" ), json_object2.get( "lastseen" ) );
			Assert.assertEquals( JSONNumber.Integer( 2 ), json_object2.get( "frequency" ) );
			Assert.assertEquals( JSONBoolean.True, json_object2.get( "appears" ) );
			json_object2 = (JSONObject)json_object.get( "username" );
			Assert.assertEquals( JSONNumber.Integer( 0 ), json_object2.get( "frequency" ) );
			Assert.assertEquals( JSONBoolean.False, json_object2.get( "appears" ) );

			/*
			 * SOLR example.
			 */

			text = ""
					+ "[\n"
					+ "  {\"id\" : \"TestDoc1\", \"title\" : \"test1\"},\n"
					+ "  {\"id\" : \"TestDoc2\", \"title\" : \"another test\"}\n"
					+ "]\n";
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
			Assert.assertEquals( JSONString.String( "TestDoc1" ), json_object.get( "id") );
			Assert.assertEquals( JSONString.String( "test1" ), json_object.get( "title" ) );
			json_object = (JSONObject)json_array.get( 1 );
			Assert.assertEquals( JSONString.String( "TestDoc2" ), json_object.get( "id" ));
			Assert.assertEquals( JSONString.String( "another test"), json_object.get( "title" ) );

			/*
			 * SOLR example.
			 */

			text = ""
					+ "{\n"
					+ "  \"add\": {\"doc\": {\"id\" : \"TestDoc1\", \"title\" : \"test1\"} },\n"
					+ "  \"add\": {\"doc\": {\"id\" : \"TestDoc2\", \"title\" : \"another test\"} }\n"
					+ "}\n";
			bytes = text.getBytes( "UTF-8" );

			pbin = new PushbackInputStream( new ByteArrayInputStream( bytes ), 4 );
			encoding = JSONEncoding.encoding( pbin );
			Assert.assertEquals( JSONEncoding.E_UTF8, encoding );
			json_decoder = json_encoding.getJSONDecoder( encoding );
			Assert.assertNotNull( json_decoder );
			json_struct = json.toJSONStructure( pbin, json_decoder );
			Assert.assertNotNull( json_struct );
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
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

}
