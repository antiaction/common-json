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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import com.antiaction.common.json.JSONEncoder;

/**
 * JSON Null implementation.
 *
 * @author Nicholas
 * Created on 18/08/2012
 */
public class JSONNull extends JSONValue {

	/** Static Null value. */
	public static final JSONNull Null = new JSONNull();

	/** Null string cached as bytes. */
	protected static final byte[] nullBytes = "null".getBytes();

	/** Cached hashCode value of Null string as bytes. */
	protected static int hashCode = Arrays.deepHashCode( new Object[] { nullBytes } );

	/**
	 * Construct a JSON Null.
	 */
	protected JSONNull() {
		type = JSONConstants.VT_NULL;
	}

	@Override
	public JSONArray getArray() {
		return null;
	}

	@Override
	public JSONObject getObject() {
		return null;
	}

	@Override
	public Boolean getBoolean() {
		return null;
	}

	@Override
	public String getString() {
		return null;
	}

	@Override
	public byte[] getBytes() {
		return null;
	}

	@Override
	public Integer getInteger() {
		return null;
	}

	@Override
	public Long getLong() {
		return null;
	}

	@Override
	public Float getFloat() {
		return null;
	}

	@Override
	public Double getDouble() {
		return null;
	}

	@Override
	public BigInteger getBigInteger() {
		return null;
	}

	@Override
	public BigDecimal getBigDecimal() {
		return null;
	}

	@Override
	public void encode(JSONEncoder encoder) throws IOException {
		encoder.write( nullBytes );
	}

	@Override
	public void encode(JSONEncoder encoder, String indentation, String indent) throws IOException {
		encoder.write( nullBytes );
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null || !(obj instanceof JSONNull) ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

}
