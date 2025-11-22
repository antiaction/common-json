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
import java.util.LinkedList;
import java.util.List;

import com.antiaction.common.json.JSONEncoder;

/**
 * TODO javadoc
 * @author Nicholas
 * Created on 20/08/2012
 */
public class JSONString extends JSONValue {

	public static class Block {
		char[] chars;
		byte[] bytes;
		int offset;
		int len;
		public Block(char[] chars, int offset, int len) {
			this.chars = chars;
			this.offset = offset;
			this.len = len;
		}
		public Block(byte[] bytes, int offset, int len) {
			this.bytes = bytes;
			this.offset = offset;
			this.len = len;
		}
	}

	protected static Block[] escapeTable = new Block[ 128 ];

	protected String str;

	protected byte[] strBytes;

	protected char[] strChars;

	protected List<Block> blocks = new LinkedList<Block>();

	static {
		char[] tmpChars;
		for ( int i=0; i<32; ++i ) {
			switch ( i ) {
			case 0x08:
				tmpChars = "\\b".toCharArray();
				escapeTable[ 0x08 ] = new Block( tmpChars, 0, tmpChars.length );
				break;
			case 0x09:
				tmpChars = "\\t".toCharArray();
				escapeTable[ 0x09 ] = new Block( tmpChars, 0, tmpChars.length );
				break;
			case 0x0A:
				tmpChars = "\\n".toCharArray();
				escapeTable[ 0x0A ] = new Block( tmpChars, 0, tmpChars.length );
				break;
			case 0x0C:
				tmpChars = "\\f".toCharArray();
				escapeTable[ 0x0C ] = new Block( tmpChars, 0, tmpChars.length );
				break;
			case 0x0D:
				tmpChars = "\\r".toCharArray();
				escapeTable[ 0x0D ] = new Block( tmpChars, 0, tmpChars.length );
				break;
			default:
				tmpChars = ("\\u" + Integer.toHexString(0x10000 | i).substring(1).toUpperCase()).toCharArray();
				escapeTable[ i ] = new Block( tmpChars, 0, tmpChars.length );
				break;
			}
		}
		tmpChars = "\\\"".toCharArray();
		escapeTable[ 0x22 ] = new Block( tmpChars, 0, tmpChars.length );
		tmpChars = "\\/".toCharArray();
		escapeTable[ 0x2F ] = new Block( tmpChars, 0, tmpChars.length );
		tmpChars = "\\\\".toCharArray();
		escapeTable[ 0x5C ] = new Block( tmpChars, 0, tmpChars.length );
	}

	public static final JSONString String(String str) {
		return new JSONString( str );
	}

	public static final JSONString String(byte[] bytes) {
		return new JSONString( bytes );
	}

	public JSONString(String str) {
		type = JSONConstants.VT_STRING;
		this.str = str;
		strChars = str.toCharArray();
		// Blockify.
		int pos = 0;
		int prev = pos;
		int len = strChars.length;
		char c;
		Block escaped;
		while ( pos < len ) {
			c = strChars[ pos ];
			if ( c < 128 ) {
				escaped = escapeTable[ c ];
				if ( escaped != null ) {
					if ( pos > prev ) {
						blocks.add( new Block( strChars, prev, pos - prev ) );
					}
					blocks.add( escaped );
					++pos;
					prev = pos;
				}
				else {
					++pos;
				}
			}
			else {
				++pos;
			}
		}
		if ( pos > prev ) {
			blocks.add( new Block( strChars, prev, pos - prev ) );
		}
	}

	public JSONString(byte[] bytes) {
		type = JSONConstants.VT_STRING;
		strBytes = bytes;
		// Blockify.
		int pos = 0;
		int prev = pos;
		int len = bytes.length;
		int c;
		Block escaped;
		while ( pos < len ) {
			c = bytes[ pos ] & 255;
			if ( c < 128 ) {
				escaped = escapeTable[ c ];
				if ( escaped != null ) {
					if ( pos > prev ) {
						blocks.add( new Block( bytes, prev, pos - prev ) );
					}
					blocks.add( escaped );
					++pos;
					prev = pos;
				}
				else {
					++pos;
				}
			}
			else {
				++pos;
			}
		}
		if ( pos > prev ) {
			blocks.add( new Block( bytes, prev, pos - prev ) );
		}
	}

	@Override
	public String getString() {
		return str;
	}

	@Override
	public byte[] getBytes() {
		if ( strBytes == null ) {
			strBytes = new byte[ str.length() ];
			int len = str.length();
			char c;
			for ( int i=0; i<len; ++i ) {
				c = strChars[ i ];
				if ( c < 256 ) {
					strBytes[ i ] = (byte)(c & 255);
				}
				else {
					strBytes = null;
					throw new NumberFormatException( "Not a compatible byte stream!" );
				}
			}
		}
		return strBytes;
	}

	@Override
	public void encode(JSONEncoder encoder) throws IOException {
		encoder.write( '"' );
		Block block;
		for ( int i=0; i<blocks.size(); ++i ) {
			block = blocks.get( i );
			if ( block.chars != null ) {
				encoder.write( block.chars, block.offset, block.len );
			}
			else {
				encoder.write( block.bytes, block.offset, block.len );
			}
		}
		encoder.write( '"' );
	}

	@Override
	public void encode(JSONEncoder encoder, String indentation, String indent) throws IOException {
		encoder.write( '"' );
		Block block;
		for ( int i=0; i<blocks.size(); ++i ) {
			block = blocks.get( i );
			if ( block.chars != null ) {
				encoder.write( block.chars, block.offset, block.len );
			}
			else {
				encoder.write( block.bytes, block.offset, block.len );
			}
		}
		encoder.write( '"' );
	}

	@Override
	public String toString() {
		return '"' + str + '"';
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null || !(obj instanceof JSONString) ) {
			return false;
		}
		JSONString json_stringObj = (JSONString)obj;
		if ( !str.equals( json_stringObj.str ) ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return str.hashCode();
	}

	public static void escape(JSONEncoder encoder, String stringValue) throws IOException {
		int idx = 0;
		int pIdx = 0;
		char[] charArr = stringValue.toCharArray();
		int len = charArr.length;
		int c;
		while ( idx < len ) {
			c = charArr[ idx++ ];
			if ( c < 128 && escapeTable[ c ] != null ) {
				if (pIdx < idx ) {
					encoder.write( charArr, pIdx, idx - 1 - pIdx );
				}
				encoder.write( escapeTable[ c ].chars );
				pIdx = idx;
			}
		}
		if (pIdx < len ) {
			encoder.write( charArr, pIdx, len - pIdx );
		}
	}

	public static void escape(JSONEncoder encoder, byte[] byteArr) throws IOException {
		int idx = 0;
		int pIdx = 0;
		int len = byteArr.length;
		int c;
		while ( idx < len ) {
			c = byteArr[ idx++ ] & 255;
			if ( c < 128 && escapeTable[ c ] != null ) {
				if (pIdx < idx ) {
					encoder.write( byteArr, pIdx, idx - 1 - pIdx );
				}
				encoder.write( escapeTable[ c ].chars );
				pIdx = idx;
			}
		}
		if (pIdx < len ) {
			encoder.write( byteArr, pIdx, len - pIdx );
		}
	}

}
