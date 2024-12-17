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

import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Set;

/**
 * JSON collection abstraction. Object and Array are the only current top level
 * JSON collections.
 *
 * @author Nicholas
 * Created on 02/08/2012
 */
public abstract class JSONCollection extends JSONValue {

	/**
	 * Create a new JSON object, add it to this array and return it to caller.
	 * @return JSON object added to this array
	 */
	public JSONObject addObject() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create a new JSON object, add to this object using the supplied name and return it to caller.
	 * @param name object name used when adding the newly created object to this object
	 * @return JSON object added to this object
	 */
	public JSONObject addObject(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create a new JSON object, add to this object using the supplied name and return it to caller.
	 * @param name object name used when adding the newly created object to this object
	 * @return JSON object added to this object
	 */
	public JSONObject addObject(JSONString name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create a new JSON array, add it to this array and return it to caller.
	 * @return JSON array added to this array
	 */
	public JSONArray addArray() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create a new JSON array, add to this object using the supplied name and return it to caller.
	 * @param name object name used when adding the newly created array to this object
	 * @return JSON array added to this object
	 */
	public JSONArray addArray(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create a new JSON array, add to this object using the supplied name and return it to caller.
	 * @param name object name used when adding the newly created array to this object
	 * @return JSON array added to this object
	 */
	public JSONArray addArray(JSONString name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Add a JSON value to an array.
	 * @param value JSON value
	 */
	public void add(JSONValue value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Add a JSON key/value pair to an object.
	 * @param name string key
	 * @param value JSON value
	 * @return the JSON String instance for the key
	 */
	public JSONString put(String name, JSONValue value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Add a JSON key/value pair to an object.
	 * @param name JSON String key
	 * @param value JSON value
	 */
	public void put(JSONString name, JSONValue value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the JSON value in the array at the given index.
	 * @param index index in array
	 * @return JSON value in the array at the given index
	 */
	public JSONValue get(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the JSON value in the array associated with the given key.
	 * @param key string key
	 * @return JSON value in the array associated with the given key
	 */
	public JSONValue get(String key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the JSON value in the array associated with the given key.
	 * @param key JSON String key
	 * @return JSON value in the array associated with the given key
	 */
	public JSONValue get(JSONString key) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		throw new UnsupportedOperationException();
	}

	public Iterator<JSONValue> iterator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a <code>Set</code> of JSONString key names.
	 * @return a <code>Set</code> of JSONString key names
	 */
	public Set<JSONString> keySet() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the entry <code>Set</code> containing all the name value pairs for this object.
	 * @return the entry <code>Set</code> containing all the name value pairs for this object
	 */
	public Set<Entry<JSONString, JSONValue>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
