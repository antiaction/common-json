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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.classmapper.TestClassTypeModifiers;
import com.antiaction.common.classmapper.TestClassTypeModifiers.TestAbstractMemberClass;
import com.antiaction.common.classmapper.TestClassTypeModifiers.TestEnum;
import com.antiaction.common.classmapper.TestClassTypeModifiers.TestInterface;
import com.antiaction.common.classmapper.TestClassTypeModifiers.TestMemberClass;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 15/12/2012
 */
@RunWith(JUnit4.class)
public class TestJSONObjectMappings_InvalidField {

	@Test
	public void test_jsonobjectmappings_register_invalidfields() {
		JSONObjectMappingConstants json_omc = new JSONObjectMappingConstants();
		Assert.assertNotNull( json_omc );

		JSONObjectMappings json_om = new JSONObjectMappings();
		try {
			json_om.register( InvalidField1.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( InvalidField2.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( InvalidField3.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( InvalidField4.class );
			Assert.fail( "Exception expected!" );
		}
		catch (JSONException e) {
		}
		try {
			json_om.register( ValidField1.class );
		}
		catch (JSONException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	public static class InvalidField1 {
		TestEnum enumse;
	}

	public static class InvalidField2 {
		TestInterface interf;
	}

	public static class InvalidField3 {
		TestAbstractMemberClass tabstract;
	}

	public static class InvalidField4 {
		TestMemberClass memass;
	}

	public static class ValidField1 {
		TestClassTypeModifiers normass;
	}

}
