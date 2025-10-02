package com.antiaction.common.json;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestJSONObjectMapping {

	@Test
	public void test_objectmapping_tostring() {
		JSONObjectMapping objectMapping;

		objectMapping = JSONObjectMapping.getObjectMapping();
		Assert.assertNotNull( objectMapping );
		Assert.assertEquals( JSONObjectMapping.OMT_OBJECT, objectMapping.type );
		Assert.assertNotNull( objectMapping.ignore );
		Assert.assertNotNull( objectMapping.nullable );
		Assert.assertNotNull( objectMapping.nullValues );
		Assert.assertNotNull( objectMapping.fieldMappingsMap );
		Assert.assertNotNull( objectMapping.fieldMappingsList );
		Assert.assertNull( objectMapping.fieldMappingsArr );
		Assert.assertFalse( objectMapping.converters );
		Assert.assertEquals( 0, objectMapping.arrayType );
		Assert.assertNull( objectMapping.className );
		Assert.assertNull( objectMapping.clazz );
		Assert.assertNull( objectMapping.fieldMapping );
		Assert.assertNull( objectMapping.objectMapping );

		objectMapping = JSONObjectMapping.getArrayMapping();
		Assert.assertNotNull( objectMapping );
		Assert.assertEquals( JSONObjectMapping.OMT_ARRAY, objectMapping.type );
		Assert.assertNull( objectMapping.ignore );
		Assert.assertNull( objectMapping.nullable );
		Assert.assertNull( objectMapping.nullValues );
		Assert.assertNull( objectMapping.fieldMappingsMap );
		Assert.assertNull( objectMapping.fieldMappingsList );
		Assert.assertNull( objectMapping.fieldMappingsArr );
		Assert.assertFalse( objectMapping.converters );
		Assert.assertEquals( 0, objectMapping.arrayType );
		Assert.assertNull( objectMapping.className );
		Assert.assertNull( objectMapping.clazz );
		Assert.assertNull( objectMapping.fieldMapping );
		Assert.assertNull( objectMapping.objectMapping );
	}

	@Test
	public void test_objectmapping_settostring() {
		StringBuilder sb = new StringBuilder();
		Set<String> set = new TreeSet<String>();
		JSONObjectMapping.toString( set, sb );
		Assert.assertEquals( "[]", sb.toString() );

		set.add( "one" );
		sb.setLength( 0 );
		JSONObjectMapping.toString( set, sb );
		Assert.assertEquals( "[]".length() + "one".length(), sb.length() );
		Assert.assertNotEquals( -1, sb.toString().indexOf( "one" ) );
		Assert.assertEquals( -1, sb.toString().indexOf( "four" ) );
		Assert.assertEquals( -1, sb.toString().indexOf( "three" ) );

		set.add( "four" );
		sb.setLength( 0 );
		JSONObjectMapping.toString( set, sb );
		Assert.assertEquals( "[]".length() + "one".length() + ", ".length() + "four".length(), sb.length() );
		Assert.assertNotEquals( -1, sb.toString().indexOf( "one" ) );
		Assert.assertNotEquals( -1, sb.toString().indexOf( "four" ) );
		Assert.assertEquals( -1, sb.toString().indexOf( "three" ) );

		set.add( "three" );
		sb.setLength( 0 );
		JSONObjectMapping.toString( set, sb );
		Assert.assertEquals( "[]".length() + "one".length() + ", ".length() + "four".length() + ", ".length() + "three".length(), sb.length() );
		Assert.assertNotEquals( -1, sb.toString().indexOf( "one" ) );
		Assert.assertNotEquals( -1, sb.toString().indexOf( "four" ) );
		Assert.assertNotEquals( -1, sb.toString().indexOf( "three" ) );
	}

}
