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
 * TODO javadoc
 * @author Nicholas
 * Created on 27/07/2014
 */
public class PackageAccess {

	public static void setObjectValues(JSONCollection json_struct, JSONString key, JSONValue value) {
		((JSONObject)json_struct).nameValuePairs.put( key, value );
	}

}
