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
 * JSON Number implementation.
 *
 * @author Nicholas
 * Created on 02/09/2012
 */
public class JSONNumber extends JSONValue {

	/** <code>Integer</code> representation of the number, if possible. */
	protected Integer intVal;

	/** <code>Long</code> representation of the number, if possible. */
	protected Long longVal;

	/** <code>Float</code> representation of the number, if possible. */
	protected Float floatVal;

	/** <code>Double</code> representation of the number, if possible. */
	protected Double doubleVal;

	/** <code>BigInteger</code> representation of the number, if possible. */
	protected BigInteger bigIntegerVal;

	/** <code>BigDecimal</code> representation of the number, if possible. */
	protected BigDecimal bigDecimalVal;

	/** String representation of the number. */
	protected String numberStr;

	/** String representation of the number as bytes. */
	protected byte[] numberBytes;

	/**
	 * Construct a JSON Number from an integer.
	 * @param intVal integer value
	 * @return JSON Number object
	 */
	public static JSONNumber Integer(int intVal) {
		JSONNumber number = new JSONNumber( Integer.toString( intVal ) );
		number.intVal = intVal;
		return number;
	}

	/**
	 * Construct a JSON Number from a long.
	 * @param longVal long value
	 * @return JSON Number object
	 */
	public static JSONNumber Long(long longVal) {
		JSONNumber number = new JSONNumber( Long.toString( longVal ) );
		number.longVal = longVal;
		return number;
	}

	/**
	 * Construct a JSON Number from a float.
	 * @param floatVal float value
	 * @return JSON Number object
	 */
	public static JSONNumber Float(float floatVal) {
		JSONNumber number = new JSONNumber( Float.toString( floatVal ) );
		number.floatVal = floatVal;
		return number;
	}

	/**
	 * Construct a JSON Number from a double.
	 * @param doubleVal double value
	 * @return JSON Number object
	 */
	public static JSONNumber Double(double doubleVal) {
		JSONNumber number = new JSONNumber( Double.toString( doubleVal ) );
		number.doubleVal = doubleVal;
		return number;
	}

	/**
	 * Construct a JSON Number from a big integer.
	 * @param bigIntegerVal big integer value
	 * @return JSON Number object
	 */
	public static JSONNumber BigInteger(BigInteger bigIntegerVal) {
		JSONNumber number = new JSONNumber( bigIntegerVal.toString() );
		number.bigIntegerVal = bigIntegerVal;
		return number;
	}

	/**
	 * Construct a JSON Number from a big decimal.
	 * @param bigDecimalVal big decimal value
	 * @return JSON Number object
	 */
	public static JSONNumber BigDecimal(BigDecimal bigDecimalVal) {
		JSONNumber number = new JSONNumber( bigDecimalVal.toString() );
		number.bigDecimalVal = bigDecimalVal;
		return number;
	}

	/**
	 * Internal JSON Number constructor.
	 * @param str number string
	 */
	protected JSONNumber(String str) {
		type = JSONConstants.VT_NUMBER;
		this.numberStr = str;
		this.numberBytes = str.getBytes();
	}

	@Override
	public Boolean getBoolean() {
		if ( intVal == null ) {
			intVal = Integer.parseInt( numberStr );
		}
		if ( intVal == 0 ) {
			return false;
		}
		else if ( intVal == 1 ) {
			return true;
		}
		else {
			throw new NumberFormatException("Unimplemented");
		}
	}

	@Override
	public Integer getInteger() {
		if ( intVal == null ) {
			intVal = Integer.parseInt( numberStr );
		}
		return intVal;
	}

	@Override
	public Long getLong() {
		if ( longVal == null ) {
			longVal = Long.parseLong( numberStr );
		}
		return longVal;
	}

	@Override
	public Float getFloat() {
		if ( floatVal == null ) {
			floatVal = Float.parseFloat( numberStr );
		}
		return floatVal;
	}

	@Override
	public Double getDouble() {
		if ( doubleVal == null ) {
			doubleVal = Double.parseDouble( numberStr );
		}
		return doubleVal;
	}

	@Override
	public BigInteger getBigInteger() {
		if ( bigIntegerVal == null ) {
			 bigIntegerVal = new BigInteger( numberStr );
		}
		return  bigIntegerVal;
	}

	@Override
	public BigDecimal getBigDecimal() {
		if ( bigDecimalVal == null ) {
			bigDecimalVal = new BigDecimal( numberStr );
		}
		return bigDecimalVal;
	}

	@Override
	public void encode(JSONEncoder encoder) throws IOException {
		encoder.write( numberBytes );
	}

	@Override
	public void encode(JSONEncoder encoder, String indentation, String indent) throws IOException {
		encoder.write( numberBytes );
	}

	@Override
	public String toString() {
		return new String( numberBytes );
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null || !(obj instanceof JSONNumber) ) {
			return false;
		}
		JSONNumber json_numberObj = (JSONNumber)obj;
		if ( !Arrays.equals( numberBytes, json_numberObj.numberBytes ) ) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode( new Object[] { numberBytes } );
	}

}
