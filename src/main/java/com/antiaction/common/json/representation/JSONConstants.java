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

/**
 * JSON value types.
 *
 * @author Nicholas
 * Created on 01/08/2012
 */
public class JSONConstants {

	/** JSON String value type. */
	public static final int VT_STRING = 1;

	/** JSON Number value type. */
	public static final int VT_NUMBER = 2;

	/** JSON Object value type. */
	public static final int VT_OBJECT = 3;

	/** JSON Array value type. */
	public static final int VT_ARRAY = 4;

	/** JSON Boolean value type. */
	public static final int VT_BOOLEAN = 5;

	/** JSON Null value type. */
	public static final int VT_NULL = 6;

	/**
	 * Constructor to prohibit instantiation.
	 */
	protected JSONConstants() {
	}

}
