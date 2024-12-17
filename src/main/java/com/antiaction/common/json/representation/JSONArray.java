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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.antiaction.common.json.JSONConstants;
import com.antiaction.common.json.JSONEncoder;

/**
 * JSON Array implementation.
 *
 * @author Nicholas
 * Created on 01/08/2012
 */
public class JSONArray extends JSONCollection {

	/** <code>List</code> of JSON Array values. */
	protected List<JSONValue> values = new ArrayList<JSONValue>();

	/**
	 * Construct a JSON Array.
	 */
	public JSONArray() {
		type = JSONConstants.VT_ARRAY;
	}

	@Override
	public void encode(JSONEncoder encoder) throws IOException {
		encoder.write( '[' );
		for (int i=0; i<values.size(); ++i) {
			if ( i > 0 ) {
				encoder.write( ',' );
			}
			values.get( i ).encode( encoder );
		}
		encoder.write( ']' );
	}

	@Override
	public void encode(JSONEncoder encoder, String indentation, String indent) throws IOException {
		encoder.write( "[\n" );
		String innerIndentation = indentation + indent;
		for (int i=0; i<values.size(); ++i) {
			if ( i > 0 ) {
				encoder.write( ",\n" );
			}
			encoder.write( innerIndentation );
			values.get( i ).encode( encoder, innerIndentation, indent );
		}
		encoder.write( "\n" );
		encoder.write( indentation );
		encoder.write( ']' );
	}

	@Override
	public JSONObject addObject() {
		JSONObject json_object = new JSONObject();
		values.add( json_object );
		return json_object;
	}

	@Override
	public JSONArray addArray() {
		JSONArray json_array = new JSONArray();
		values.add( json_array );
		return json_array;
	}

	@Override
	public void add(JSONValue value) {
		values.add(value);
	}

	@Override
	public JSONArray getArray() {
		return this;
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public JSONValue get(int index) {
		return values.get( index );
	}

	@Override
	public Iterator<JSONValue> iterator() {
		return values.iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( '[' );
		for (int i=0; i<values.size(); ++i) {
			if ( i > 0) {
				sb.append( ", " );
			}
			sb.append( values.get( i ).toString() );
		}
		sb.append( ']' );
		return sb.toString();
	}

}
