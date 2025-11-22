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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.representation.JSONArray;
import com.antiaction.common.json.representation.JSONBoolean;
import com.antiaction.common.json.representation.JSONNull;
import com.antiaction.common.json.representation.JSONNumber;
import com.antiaction.common.json.representation.JSONObject;
import com.antiaction.common.json.representation.JSONString;
import com.antiaction.common.json.representation.JSONValue;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 07/09/2012
 */
@RunWith(JUnit4.class)
public class TestJSONValue {

	@Test
	public void test_jsonvalue() {
		JSONValue value;

		value = new JSONValue() {
		};
		Assert.assertEquals( 0, value.type );

		try {
			value.encode( null );
			Assert.fail( "Exception expected !" );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (UnsupportedOperationException e) {
		}

		try {
			value.encode( null, null, null );
			Assert.fail( "Exception expected !" );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (UnsupportedOperationException e) {
		}

		value = JSONNull.Null;
		Assert.assertEquals( JSONConstants.VT_NULL, value.type );

		value = JSONBoolean.False;
		Assert.assertEquals( JSONConstants.VT_BOOLEAN, value.type );

		value = JSONBoolean.True;
		Assert.assertEquals( JSONConstants.VT_BOOLEAN, value.type );

		value = new JSONString( "name" );
		Assert.assertEquals( JSONConstants.VT_STRING, value.type );

		value = JSONNumber.Integer( 42 );
		Assert.assertEquals( JSONConstants.VT_NUMBER, value.type );

		value = new JSONArray();
		Assert.assertEquals( JSONConstants.VT_ARRAY, value.type );

		value = new JSONObject();
		Assert.assertEquals( JSONConstants.VT_OBJECT, value.type );
	}

}
