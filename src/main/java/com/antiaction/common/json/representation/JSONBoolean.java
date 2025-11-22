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
import java.util.Arrays;

import com.antiaction.common.json.JSONEncoder;

/**
 * JSON Boolean implementation.
 *
 * @author Nicholas
 * Created on 02/08/2012
 */
public class JSONBoolean extends JSONValue {

	/** Static False Boolean value. */
	public static final JSONBoolean False = new JSONBoolean( false );

	/** False string cached as bytes. */
	protected static final byte[] falseBytes = "false".getBytes();

	/** Cached hashCode value of False string as bytes. */
	protected static int false_hashCode = Arrays.deepHashCode( new Object[] { falseBytes } );

	/** Static True Boolean value. */
	public static final JSONBoolean True = new JSONBoolean( true );

	/** True string cached as bytes. */
	protected static final byte[] trueBytes = "true".getBytes();

	/** Cached hashCode value of True string as bytes. */
	protected static int true_hashCode = Arrays.deepHashCode( new Object[] { trueBytes } );

	/**
	 * Static JSON Boolean constructor.
	 * @param b boolean value
	 * @return JSON Boolean value
	 */
	public static final JSONBoolean Boolean(boolean b) {
		return new JSONBoolean( b );
	}

	/** The Boolean value. */
	protected boolean b;

	/**
	 * Construct a JSON Boolean.
	 * @param b boolean value
	 */
	public JSONBoolean(boolean b) {
		type = JSONConstants.VT_BOOLEAN;
		this.b = b;
	}

	@Override
	public Boolean getBoolean() {
		return b;
	}

	@Override
	public void encode(JSONEncoder encoder) throws IOException {
		if ( b ) {
			encoder.write( trueBytes );
		}
		else {
			encoder.write( falseBytes );
		}
	}

	@Override
	public void encode(JSONEncoder encoder, String indentation, String indent) throws IOException {
		if ( b ) {
			encoder.write( trueBytes );
		}
		else {
			encoder.write( falseBytes );
		}
	}

	@Override
	public String toString() {
		if ( b ) {
			return "true";
		}
		else {
			return "false";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null || !(obj instanceof JSONBoolean) ) {
			return false;
		}
		JSONBoolean json_booleanObj = (JSONBoolean)obj;
		if ( b != json_booleanObj.b ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		if ( b ) {
			return true_hashCode;
		}
		return false_hashCode;
	}

}
