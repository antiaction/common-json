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
public class TestJSONNull {

	@Test
	public void test_jsonnull() {
		try {
			JSONNull json_null;

			json_null = JSONNull.Null;
			Assert.assertNotNull( json_null );
			Assert.assertEquals( JSONConstants.VT_NULL, json_null.type );

			Assert.assertEquals( "null", json_null.toString() );

			json_null = new JSONNull();
			Assert.assertNotNull( json_null );
			Assert.assertEquals( JSONConstants.VT_NULL, json_null.type );

			Assert.assertEquals( "null", json_null.toString() );

			/*
			 *
			 */

			Charset charset = Charset.forName("UTF-8");
			JSONEncoder json_encoder = new JSONEncoderCharset( charset );

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JSONTextMarshaller json_textMarshaller = new JSONTextMarshaller();
			JSONTextUnmarshaller json_textUnmarshaller = new JSONTextUnmarshaller();

			JSONArray json_array = new JSONArray();
			json_array.add( json_null );

			json_textMarshaller.toJSONText( json_array, json_encoder, false, out );

			// debug
			//System.out.println( new String( out.toByteArray() )  );

			ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
			JSONDecoder json_decoder = new JSONDecoderCharset( charset );

			JSONCollection json_structure = json_textUnmarshaller.toJSONStructure( in, json_decoder );

			Assert.assertNotNull( json_structure );
			Assert.assertEquals( JSONConstants.VT_ARRAY, json_structure.type );
			json_array = (JSONArray)json_structure;
			Assert.assertEquals( 1, json_array.values.size() );
			Assert.assertEquals( json_null, json_array.values.get( 0 ) );
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
	public void test_jsonnull_supported() {
		JSONNull json_null = JSONNull.Null;
		Assert.assertNull( json_null.getArray() );
		Assert.assertNull( json_null.getObject() );
		Assert.assertNull( json_null.getBoolean() );
		Assert.assertNull( json_null.getString() );
		Assert.assertNull( json_null.getBytes() );
		Assert.assertNull( json_null.getInteger() );
		Assert.assertNull( json_null.getLong() );
		Assert.assertNull( json_null.getFloat() );
		Assert.assertNull( json_null.getDouble() );
		Assert.assertNull( json_null.getBigInteger() );
		Assert.assertNull( json_null.getBigDecimal() );
	}

	@Test
	public void test_jsonnull_equals_hashcode() {
		JSONNull json_null = JSONNull.Null;

		Assert.assertFalse( json_null.equals( null ) );
		Assert.assertFalse( json_null.equals( "null" ) );
		Assert.assertTrue( json_null.equals( json_null ) );

		JSONNull json_null2 = new JSONNull();
		Assert.assertTrue( json_null.equals( json_null2 ) );
		Assert.assertTrue( json_null2.equals( json_null ) );
		Assert.assertTrue( json_null2.equals( json_null2 ) );
		Assert.assertEquals( json_null.hashCode(), json_null2.hashCode() );
	}

}
