/*
 * Created on 22/11/2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.json;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A JSON object mapping description.
 *
 * @author Nicholas
 */
public class JSONObjectMapping {

	public static final int OMT_OBJECT = 1;
	public static final int OMT_ARRAY = 2;

	public int type;

	/** Field names to ignore when mapping objects. */
	public Set<String> ignore = new HashSet<String>();

	/** Field names which can be null. */
	public Set<String> nullableSet = new TreeSet<String>();

	/** Field names which can have null values. */
	public Set<String> nullValuesSet = new TreeSet<String>();

	/** Map of mapped fields. */
	public Map<String, JSONObjectFieldMapping> fieldMappingsMap = new TreeMap<String, JSONObjectFieldMapping>();

	/** List of mapped fields. */
	public List<JSONObjectFieldMapping> fieldMappingsList = new LinkedList<JSONObjectFieldMapping>();

	/** Boolean indicating if one or more field mapping(s) requires a converter. */
	public boolean converters;

	private JSONObjectMapping() {
	}

	public static JSONObjectMapping getObjectMapping() {
		JSONObjectMapping om = new JSONObjectMapping();
		om.type = OMT_OBJECT;
		om.ignore = new HashSet<String>();
		om.nullableSet = new TreeSet<String>();
		om.nullValuesSet = new TreeSet<String>();
		om.fieldMappingsMap = new TreeMap<String, JSONObjectFieldMapping>();
		om.fieldMappingsList = new LinkedList<JSONObjectFieldMapping>();
		return om;
	}

	public static JSONObjectMapping getArrayMapping() {
		JSONObjectMapping om = new JSONObjectMapping();
		om.type = OMT_ARRAY;
		return om;
	}

}
