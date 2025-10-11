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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.json.JSONException;
import com.antiaction.common.json.JSONObjectFieldMapping;
import com.antiaction.common.json.JSONObjectMapping;
import com.antiaction.common.json.JSONObjectMappingConstants;
import com.antiaction.common.json.JSONObjectMappings;

@RunWith(JUnit4.class)
public class TestStructureMarshaller_Exceptions {

	@Test
	public void test_structuremarshaller_exceptions() {
		JSONObjectMappings json_om;

		/*
		 * 1.
		 */

		json_om = new JSONObjectMappings();
		try {
			JSONObjectMapping om = json_om.register( TestClass.class );
			Assert.assertEquals( JSONObjectMapping.OMT_OBJECT, om.type );
			om.type = 42;
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}

		TestClass srcObj = new TestClass();

		try {
			json_om.getStructureMarshaller().toJSONStructure( srcObj );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Invalid object mapping type: 42", e.getMessage() );
		}

		/*
		 * 2.
		 */

		json_om = new JSONObjectMappings();
		try {
			JSONObjectMapping om = json_om.register( TestClass.class );
			JSONObjectFieldMapping fm = om.fieldMappingsArr[ 0 ];
			Assert.assertEquals( JSONObjectMappingConstants.T_PRIMITIVE_INTEGER, fm.type );
			fm.type = 42;
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}

		try {
			json_om.getStructureMarshaller().toJSONStructure( srcObj );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Field 'i' has an unsupported type: Unknown(42)", e.getMessage() );
		}

		/*
		 * 3.
		 */

		json_om = new JSONObjectMappings();
		try {
			JSONObjectMapping om = json_om.register( TestClass.class );
			JSONObjectFieldMapping fm = om.fieldMappingsArr[ 1 ];
			Assert.assertEquals( JSONObjectMappingConstants.T_ARRAY, fm.type );
			Assert.assertEquals( JSONObjectMappingConstants.T_PRIMITIVE_INTEGER, fm.arrayType );
			fm.arrayType = 42;
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}

		try {
			json_om.getStructureMarshaller().toJSONStructure( srcObj );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Field 'ia' has an unsupported array type: Unknown(42)", e.getMessage() );
		}

		/*
		 * 4.
		 */

		json_om = new JSONObjectMappings();
		try {
			json_om.register( TestClass1.class );
			Assert.assertEquals( 2, json_om.classMappings.size() );
			Assert.assertEquals( true, json_om.classMappings.containsKey( TestClass.class.getName() ) );
			json_om.classMappings.remove( TestClass.class.getName() );
			Assert.assertEquals( 1, json_om.classMappings.size() );
			Assert.assertEquals( false, json_om.classMappings.containsKey( TestClass.class.getName() ) );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}

		TestClass1 srcObj1 = new TestClass1();

		try {
			json_om.getStructureMarshaller().toJSONStructure( srcObj1 );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Class '" + TestClass.class.getName() + "' not registered.", e.getMessage() );
		}

		/*
		 * 5.
		 */

		/*
		 * 6.
		 */

		json_om = new JSONObjectMappings();
		try {
			json_om.register( TestClass2.class );
			Assert.assertEquals( 2, json_om.classMappings.size() );
			Assert.assertEquals( true, json_om.classMappings.containsKey( TestClass.class.getName() ) );
			json_om.classMappings.remove( TestClass.class.getName() );
			Assert.assertEquals( 1, json_om.classMappings.size() );
			Assert.assertEquals( false, json_om.classMappings.containsKey( TestClass.class.getName() ) );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}

		TestClass2 srcObj2 = new TestClass2();

		try {
			json_om.getStructureMarshaller().toJSONStructure( srcObj2 );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Class '" + TestClass.class.getName() + "' not registered.", e.getMessage() );
		}

	}

	public static class TestClass {

		private int i = 1234;

		private int[] ia = new int[ 0 ];

	}

	public static class TestClass1 {

		private TestClass c = new TestClass();

	}

	public static class TestClass2 {

		private TestClass[] ca = new TestClass[] { new TestClass() };

	}

	@Test
	public void test_structureunmarshaller_exceptions() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in;
		//JSONEncoding json_encoding = JSONEncoding.getJSONEncoding();
		//JSONEncoder json_encoder = json_encoding.getJSONEncoder( JSONEncoding.E_UTF8 );
		//JSONDecoder json_decoder = json_encoding.getJSONDecoder( JSONEncoding.E_UTF8 );
		JSONObjectMappings json_om;
		JSONCollection json_col;

		json_om = new JSONObjectMappings();
		try {
			json_om.register( TestClass.class );
			json_om.register( TestClass[].class );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}

		/*
		 * 1.
		 */

		json_col = new JSONObject();

		try {
			json_om.getStructureUnmarshaller().toObject( json_col, TestClass[].class );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Destination is not an object!", e.getMessage() );
		}

		/*
		 * 2.
		 */

		json_col = new JSONArray();

		try {
			json_om.getStructureUnmarshaller().toObject( json_col, TestClass.class );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Destination is not an array!", e.getMessage() );
		}

		/*
		 * 3.
		 */

		json_col = new JSONCollectionMock();

		try {
			json_om.getStructureUnmarshaller().toObject( json_col, TestClass.class );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Invalid json structure representation!", e.getMessage() );
		}

		/*
		 * 4.
		 */

		json_col = new JSONObject();
		json_col.put( "i", JSONNumber.Integer( 42 ) );
		json_col.put( "ia", new JSONArray() );

		json_om = new JSONObjectMappings();
		try {
			JSONObjectMapping om = json_om.register( TestClass.class );
			JSONObjectFieldMapping fm = om.fieldMappingsArr[ 0 ];
			Assert.assertEquals( JSONObjectMappingConstants.T_PRIMITIVE_INTEGER, fm.type );
			fm.type = 42;
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}

		try {
			json_om.getStructureUnmarshaller().toObject( json_col, TestClass.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Field 'i' has an unsupported type: Unknown(42)", e.getMessage() );
		}

		/*
		 * 5.
		 */

		json_om = new JSONObjectMappings();
		try {
			JSONObjectMapping om = json_om.register( TestClass.class );
			JSONObjectFieldMapping fm = om.fieldMappingsArr[ 1 ];
			Assert.assertEquals( JSONObjectMappingConstants.T_ARRAY, fm.type );
			Assert.assertEquals( JSONObjectMappingConstants.T_PRIMITIVE_INTEGER, fm.arrayType );
			fm.arrayType = 42;
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}

		try {
			json_om.getStructureUnmarshaller().toObject( json_col, TestClass.class  );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
			// debug
			//e.printStackTrace();
			Assert.assertEquals( "Field 'ia' has an unsupported array type: Unknown(42)", e.getMessage() );
		}
	}

	public static class JSONCollectionMock extends JSONCollection {
	}

}
