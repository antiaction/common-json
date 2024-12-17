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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.antiaction.common.json.JSONConstants;
import com.antiaction.common.json.JSONEncoder;

/**
 * JSON Object implementation.
 *
 * @author Nicholas
 * Created on 01/08/2012
 */
public class JSONObject extends JSONCollection {

	/** <code>Map</code> of JSON Object key/value pairs. */
	protected Map<JSONString, JSONValue> nameValuePairs = new HashMap<JSONString, JSONValue>();

	/**
	 * Construct a JSON OBject.
	 */
	public JSONObject() {
		type = JSONConstants.VT_OBJECT;
	}

	@Override
	public void encode(JSONEncoder encoder) throws IOException {
		encoder.write( '{' );
		Iterator<Entry<JSONString, JSONValue>> iter = nameValuePairs.entrySet().iterator();
		Entry<JSONString, JSONValue> entry;
		int i = 0;
		while ( iter.hasNext() ) {
			if ( i > 0 ) {
				encoder.write( ',' );
			}
			entry = iter.next();
			entry.getKey().encode( encoder );
			encoder.write( ':' );
			entry.getValue().encode( encoder );
			++i;
		}
		encoder.write( '}' );
	}

	@Override
	public void encode(JSONEncoder encoder, String indentation, String indent) throws IOException {
		encoder.write( "{\n" );
		Iterator<Entry<JSONString, JSONValue>> iter = nameValuePairs.entrySet().iterator();
		Entry<JSONString, JSONValue> entry;
		String innerIndentation = indentation + indent;
		int i = 0;
		while ( iter.hasNext() ) {
			if ( i > 0 ) {
				encoder.write( ",\n" );
			}
			entry = iter.next();
			encoder.write( innerIndentation );
			entry.getKey().encode( encoder, innerIndentation, indent );
			encoder.write( ": " );
			entry.getValue().encode( encoder, innerIndentation, indent );
			++i;
		}
		encoder.write( "\n" );
		encoder.write( indentation );
		encoder.write( "}" );
	}

	@Override
	public JSONObject addObject(String name) {
		JSONObject json_object = new JSONObject();
		JSONString json_string = JSONString.String( name );
		nameValuePairs.put( json_string, json_object );
		return json_object;
	}

	@Override
	public JSONObject addObject(JSONString name) {
		JSONObject json_object = new JSONObject();
		nameValuePairs.put( name, json_object );
		return json_object;
	}

	@Override
	public JSONArray addArray(String name) {
		JSONArray json_array = new JSONArray();
		JSONString json_string = JSONString.String( name );
		nameValuePairs.put( json_string, json_array );
		return json_array;
	}

	@Override
	public JSONArray addArray(JSONString name) {
		JSONArray json_array = new JSONArray();
		nameValuePairs.put( name, json_array );
		return json_array;
	}

	@Override
	public JSONString put(String name, JSONValue value) {
		JSONString json_string = JSONString.String( name );
		nameValuePairs.put( json_string, value );
		return json_string;
	}

	@Override
	public void put(JSONString name, JSONValue value) {
		nameValuePairs.put( name, value );
	}

	@Override
	public JSONObject getObject() {
		return this;
	}

	@Override
	public JSONValue get(String key) {
		return nameValuePairs.get( JSONString.String( key ) );
	}

	@Override
	public JSONValue get(JSONString key) {
		return nameValuePairs.get( key );
	}

	@Override
	public Set<JSONString> keySet() {
		return nameValuePairs.keySet();
	}

	@Override
	public Set<Entry<JSONString, JSONValue>> entrySet() {
		return nameValuePairs.entrySet();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( '{' );
		Iterator<Entry<JSONString, JSONValue>> iter = nameValuePairs.entrySet().iterator();
		Entry<JSONString, JSONValue> entry;
		int x = 0;
		while (iter.hasNext()) {
			entry = iter.next();
			if ( x > 0 ) {
				sb.append( ", " );
			}
			sb.append( entry.getKey().toString() );
			sb.append( ": " );
			sb.append( entry.getValue().toString() );
			++x;
		}
		sb.append( '}' );
		return sb.toString();
	}

}
