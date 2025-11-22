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
import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.LinkedList;

import com.antiaction.common.json.JSONDecoder;
import com.antiaction.common.json.JSONException;

/**
 * De-serialize a JSON data stream into a JSON structure.
 *
 * @author Nicholas
 * Created on 06/08/2012
 */
public class JSONTextUnmarshaller {

	private static final int S_START = 0;
	private static final int S_OBJECT = 1;
	private static final int S_OBJECT_NAME = 20;
	private static final int S_OBJECT_COLON = 21;
	private static final int S_OBJECT_VALUE = 23;
	private static final int S_OBJECT_VALUE_NEXT = 24;
	private static final int S_OBJECT_NAME_NEXT = 25;
	private static final int S_ARRAY = 2;
	private static final int S_ARRAY_VALUE = 3;
	private static final int S_ARRAY_NEXT = 4;
	private static final int S_VALUE_START = 5;
	private static final int S_STRING = 6;
	private static final int S_STRING_UNESCAPE = 7;
	private static final int S_STRING_UNHEX = 8;
	private static final int S_CONSTANT = 9;
	private static final int S_NUMBER_MINUS = 10;
	private static final int S_NUMBER_ZERO = 11;
	private static final int S_NUMBER_INTEGER = 12;
	private static final int S_NUMBER_DECIMAL = 13;
	private static final int S_NUMBER_DECIMALS = 14;
	private static final int S_NUMBER_E = 15;
	private static final int S_NUMBER_EXPONENT = 16;
	private static final int S_NUMBER_EXPONENTS = 17;
	private static final int S_EOF = 18;

	/** Temporary <code>StringBuilder</code> used to store JSON strings and values. */
	protected StringBuilder sbStr = new StringBuilder();

	private static final class StackEntry {
		public JSONCollection json_structure;
		public JSONString json_name;
		public StackEntry(JSONCollection json_structure, JSONString json_name) {
			this.json_structure = json_structure;
			this.json_name = json_name;
		}
	}

	public JSONCollection toJSONStructure(InputStream in, JSONDecoder decoder) throws IOException, JSONException {
		LinkedList<StackEntry> stack = new LinkedList<StackEntry>();
		StackEntry stackEntry = null;
		JSONCollection current = null;

		char[] charArray = new char[ 1024 ];
		CharBuffer charBuffer = CharBuffer.wrap( charArray );

		decoder.init( in );
		decoder.fill( charBuffer );

		// Switch buffer to read mode.
		charBuffer.flip();

		int pos = charBuffer.position();
		int limit = charBuffer.limit();

		int hexValue = 0;
		int hexCount = 0;
		int i;
		String constant;

		JSONString json_string = null;
		JSONString json_name = null;
		JSONValue json_value = null;

		int y = 1;
		int x = 1;

		int state = S_START;
		int rstate = -1;
		boolean bLoop = true;
		char c;
		while ( bLoop ) {
			while ( pos < limit ) {
				c = charArray[ pos ];
				++x;
				switch ( state ) {
				case S_START:
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					case '{':
						current = new JSONObject();
						stack.add( new StackEntry( current, json_name ) );
						state = S_OBJECT;
						break;
					case '[':
						current = new JSONArray();
						stack.add( new StackEntry( current, json_name ) );
						state = S_ARRAY;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_OBJECT:
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					case '}':
						/*
						if ( stack.size() == 0 ) {
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						*/
						stackEntry = stack.removeLast();
						current = stackEntry.json_structure;
						json_name = stackEntry.json_name;
						json_value = current;
						if ( stack.size() > 0 ) {
							current = stack.getLast().json_structure;
							if (current.type == JSONConstants.VT_OBJECT) {
								state = S_OBJECT_VALUE;
							}
							else {
								state = S_ARRAY_VALUE;
							}
						}
						else {
							state = S_EOF;
						}
						break;
					case '"':
						sbStr.setLength( 0 );
						state = S_STRING;
						rstate = S_OBJECT_NAME;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_OBJECT_NAME:
					json_name = json_string;
					state = S_OBJECT_COLON;
					// debug
					//System.out.println( json_value.toString() );
				case S_OBJECT_COLON:
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					case ':':
						state = S_VALUE_START;
						rstate = S_OBJECT_VALUE;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_OBJECT_VALUE:
					current.put( json_name, json_value );
					state = S_OBJECT_VALUE_NEXT;
					// debug
					//System.out.println( json_value.toString() );
				case S_OBJECT_VALUE_NEXT:
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					case '}':
						/*
						if ( stack.size() == 0 ) {
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						*/
						stackEntry = stack.removeLast();
						current = stackEntry.json_structure;
						json_name = stackEntry.json_name;
						json_value = current;
						if ( stack.size() > 0 ) {
							current = stack.getLast().json_structure;
							if (current.type == JSONConstants.VT_OBJECT) {
								state = S_OBJECT_VALUE;
							}
							else {
								state = S_ARRAY_VALUE;
							}
						}
						else {
							state = S_EOF;
						}
						break;
					case ',':
						state = S_OBJECT_NAME_NEXT;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_OBJECT_NAME_NEXT:
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					case '"':
						sbStr.setLength( 0 );
						state = S_STRING;
						rstate = S_OBJECT_NAME;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_ARRAY:
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					case ']':
						/*
						if ( stack.size() == 0 ) {
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						*/
						stackEntry = stack.removeLast();
						current = stackEntry.json_structure;
						json_name = stackEntry.json_name;
						json_value = current;
						if ( stack.size() > 0 ) {
							current = stack.getLast().json_structure;
							if (current.type == JSONConstants.VT_OBJECT) {
								state = S_OBJECT_VALUE;
							}
							else {
								state = S_ARRAY_VALUE;
							}
						}
						else {
							state = S_EOF;
						}
						break;
					case '{':
						current = new JSONObject();
						stack.add( new StackEntry( current, json_name ) );
						state = S_OBJECT;
						break;
					case '[':
						current = new JSONArray();
						stack.add( new StackEntry( current, json_name ) );
						state = S_ARRAY;
						break;
					case '"':
						sbStr.setLength( 0 );
						state = S_STRING;
						rstate = S_ARRAY_VALUE;
						break;
					case 'f':
					case 'n':
					case 't':
						sbStr.setLength( 0 );
						sbStr.append( c );
						state = S_CONSTANT;
						rstate = S_ARRAY_VALUE;
						break;
					case '-':
						sbStr.setLength( 0 );
						sbStr.append( c );
						state = S_NUMBER_MINUS;
						rstate = S_ARRAY_VALUE;
						break;
					case '0':
						sbStr.setLength( 0 );
						sbStr.append( c );
						state = S_NUMBER_ZERO;
						rstate = S_ARRAY_VALUE;
						break;
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.setLength( 0 );
						sbStr.append( c );
						state = S_NUMBER_INTEGER;
						rstate = S_ARRAY_VALUE;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_ARRAY_VALUE:
					current.add( json_value );
					state = S_ARRAY_NEXT;
					// debug
					//System.out.println( json_value.toString() );
				case S_ARRAY_NEXT:
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					case ']':
						/*
						if ( stack.size() == 0 ) {
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						*/
						stackEntry = stack.removeLast();
						current = stackEntry.json_structure;
						json_name = stackEntry.json_name;
						json_value = current;
						if ( stack.size() > 0 ) {
							current = stack.getLast().json_structure;
							if (current.type == JSONConstants.VT_OBJECT) {
								state = S_OBJECT_VALUE;
							}
							else {
								state = S_ARRAY_VALUE;
							}
						}
						else {
							state = S_EOF;
						}
						break;
					case ',':
						state = S_VALUE_START;
						rstate = S_ARRAY_VALUE;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_VALUE_START:
					// rstate should be set prior to this state.
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					case '{':
						current = new JSONObject();
						stack.add( new StackEntry( current, json_name ) );
						state = S_OBJECT;
						break;
					case '[':
						current = new JSONArray();
						stack.add( new StackEntry( current, json_name ) );
						state = S_ARRAY;
						break;
					case '"':
						sbStr.setLength( 0 );
						state = S_STRING;
						break;
					case 'f':
					case 'n':
					case 't':
						sbStr.setLength( 0 );
						sbStr.append( c );
						state = S_CONSTANT;
						break;
					case '-':
						sbStr.setLength( 0 );
						sbStr.append( c );
						state = S_NUMBER_MINUS;
						break;
					case '0':
						sbStr.setLength( 0 );
						sbStr.append( c );
						state = S_NUMBER_ZERO;
						break;
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.setLength( 0 );
						sbStr.append( c );
						state = S_NUMBER_INTEGER;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_STRING:
					switch ( c ) {
					case '"':
						json_string = JSONString.String( sbStr.toString() );
						json_value = json_string;
						state = rstate;
						break;
					case '\\':
						state = S_STRING_UNESCAPE;
						break;
					default:
						if ( c < 32 ) {
							throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
						}
						sbStr.append( c );
						break;
					}
					++pos;
					break;
				case S_STRING_UNESCAPE:
					switch ( c ) {
					case '"':
						sbStr.append( '"');
						state = S_STRING;
						break;
					case '/':
						sbStr.append( '/' );
						state = S_STRING;
						break;
					case '\\':
						sbStr.append( '\\' );
						state = S_STRING;
						break;
					case 'b':
						sbStr.append( (char)0x08 );
						state = S_STRING;
						break;
					case 't':
						sbStr.append( (char)0x09 );
						state = S_STRING;
						break;
					case 'n':
						sbStr.append( (char)0x0A );
						state = S_STRING;
						break;
					case 'f':
						sbStr.append( (char)0x0C );
						state = S_STRING;
						break;
					case 'r':
						sbStr.append( (char)0x0D );
						state = S_STRING;
						break;
					case 'u':
						hexValue = 0;
						hexCount = 4;
						state = S_STRING_UNHEX;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_STRING_UNHEX:
					if ( c > 255 ) {
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					i = asciiHexTab[ c ];
					if ( i == -1 ) {
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					hexValue <<= 4;
					hexValue |= i;
					--hexCount;
					if (hexCount == 0) {
						sbStr.append( (char)hexValue );
						state = S_STRING;
					}
					++pos;
					break;
				case S_CONSTANT:
					switch ( c ) {
					case 'a':
					case 'l':
					case 's':
					case 'e':
					case 'r':
					case 'u':
						sbStr.append( c );
						++pos;
						break;
					default:
						constant = sbStr.toString();
						if ( "false".equals( constant ) ) {
							json_value = JSONBoolean.False;
						}
						else if ( "null".equals( constant ) ) {
							json_value = JSONNull.Null;
						}
						else if ( "true".equals( constant ) ) {
							json_value = JSONBoolean.True;
						}
						else {
							throw new JSONException( "Invalid JSON constant: '" + constant + "' at (" + y + ":" + x + ")!" );
						}
						state = rstate;
						break;
					}
					break;
				case S_NUMBER_MINUS:
					switch ( c ) {
					case '0':
						sbStr.append( c );
						state = S_NUMBER_ZERO;
						break;
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.append( c );
						state = S_NUMBER_INTEGER;
						break;
					default:
						throw new JSONException( "Invalid JSON number structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_NUMBER_INTEGER:
					switch ( c ) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.append( c );
						++pos;
						break;
					case '.':
						sbStr.append( c );
						state = S_NUMBER_DECIMAL;
						++pos;
						break;
					case 'e':
						sbStr.append( c );
						state = S_NUMBER_E;
						++pos;
						break;
					case 'E':
						sbStr.append( c );
						state = S_NUMBER_E;
						++pos;
						break;
					default:
						json_value = new JSONNumber( sbStr.toString() );
						state = rstate;
						break;
					}
					break;
				case S_NUMBER_ZERO:
					switch ( c ) {
					case '.':
						sbStr.append( c );
						state = S_NUMBER_DECIMAL;
						++pos;
						break;
					case 'e':
						sbStr.append( c );
						state = S_NUMBER_E;
						++pos;
						break;
					case 'E':
						sbStr.append( c );
						state = S_NUMBER_E;
						++pos;
						break;
					default:
						json_value = new JSONNumber( sbStr.toString() );
						state = rstate;
						break;
					}
					break;
				case S_NUMBER_DECIMAL:
					switch ( c ) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.append( c );
						state = S_NUMBER_DECIMALS;
						++pos;
						break;
					default:
						throw new JSONException( "Invalid JSON number structure at (" + y + ":" + x + ")!" );
					}
					break;
				case S_NUMBER_DECIMALS:
					switch ( c ) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.append( c );
						++pos;
						break;
					case 'e':
						sbStr.append( c );
						state = S_NUMBER_E;
						++pos;
						break;
					case 'E':
						sbStr.append( c );
						state = S_NUMBER_E;
						++pos;
						break;
					default:
						json_value = new JSONNumber( sbStr.toString() );
						state = rstate;
					}
					break;
				case S_NUMBER_E:
					switch ( c ) {
					case '+':
						sbStr.append( c );
						state = S_NUMBER_EXPONENT;
						break;
					case '-':
						sbStr.append( c );
						state = S_NUMBER_EXPONENT;
						break;
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.append( c );
						state = S_NUMBER_EXPONENTS;
						break;
					default:
						throw new JSONException( "Invalid JSON number structure at (" + y + ":" + x + ")!" );
					}
					++pos;
					break;
				case S_NUMBER_EXPONENT:
					switch ( c ) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.append( c );
						state = S_NUMBER_EXPONENTS;
						++pos;
						break;
					default:
						throw new JSONException( "Invalid JSON number structure at (" + y + ":" + x + ")!" );
					}
					break;
				case S_NUMBER_EXPONENTS:
					switch ( c ) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						sbStr.append( c );
						++pos;
						break;
					default:
						json_value = new JSONNumber( sbStr.toString() );
						state = rstate;
					}
					break;
				case S_EOF:
					switch ( c ) {
					case 0x20:
					case 0x09:
					case 0x0D:
						// Whitespace.
						break;
					case 0x0A:
						++y;
						x = 1;
						break;
					default:
						throw new JSONException( "Invalid JSON structure at (" + y + ":" + x + ")!" );
					}
					++pos;
				}
			}
			// Switch buffer to write mode.
			charBuffer.clear();
			decoder.fill( charBuffer );
			// Switch buffer to read mode.
			charBuffer.flip();

			pos = charBuffer.position();
			limit = charBuffer.limit();

			bLoop = !(pos == limit);
		}
		if (current == null || stack.size() > 0) {
			throw new JSONException( "Invalid JSON structure!" );
		}
		return current;
	}

	/** Integer to hex char conversion table. */
	//private static char[] hexTab = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/** Hex char to integer conversion table. */
	private static int[] asciiHexTab = new int[256];

	/*
	 * Initialize ASCII hex table.
	 */
	static {
		String hex = "0123456789abcdef";
		for (int i=0; i<asciiHexTab.length; ++i) {
			asciiHexTab[i] = hex.indexOf(i);
		}
		hex = hex.toUpperCase();
		for (int i=0; i<hex.length(); ++i) {
			asciiHexTab[hex.charAt(i)] = i;
		}
	}

}
