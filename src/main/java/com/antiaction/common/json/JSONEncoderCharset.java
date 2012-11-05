/*
 * Created on 23/08/2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.json;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class JSONEncoderCharset implements JSONEncoder {

	protected CharsetEncoder encoder;

	protected char[] charArray;

	protected CharBuffer charBuffer;

	protected byte[] byteArray;

	protected ByteBuffer byteBuffer;

	protected OutputStream out;

	public JSONEncoderCharset(Charset charset) {
		encoder = charset.newEncoder();
		encoder.onMalformedInput( CodingErrorAction.REPORT );
		encoder.onUnmappableCharacter( CodingErrorAction.REPORT );
		charArray = new char[ 1024 ];
		charBuffer = CharBuffer.wrap( charArray );
		byteArray = new byte[ 1024 ];
		byteBuffer = ByteBuffer.wrap( byteArray );
	}

	@Override
	public void init(OutputStream out) {
		encoder.reset();
		this.out = out;
		// Switch buffer to write mode.
		charBuffer.clear();
		// Switch buffer to write mode.
		byteBuffer.clear();
	}

	protected void encode_buffer(boolean endOfInput) throws IOException {
		// Switch buffer to read mode.
		charBuffer.flip();
		boolean bEncodeLoop = true;
		while ( bEncodeLoop ) {
			CoderResult result = encoder.encode( charBuffer, byteBuffer, endOfInput );
			if (result == CoderResult.UNDERFLOW) {
				bEncodeLoop = false;
			}
			else if ( result == CoderResult.OVERFLOW ) {
				// Switch buffer to read mode.
				byteBuffer.flip();
				int pos = byteBuffer.position();
				int limit = byteBuffer.limit();
				out.write( byteArray, pos, limit - pos );
				byteBuffer.position( limit );
				// Switch buffer to write mode.
				byteBuffer.compact();
			}
			else if ( result.isError() ) {
				throw new IOException( "Encoding error!" );
			}
		}
		// Switch buffer to write mode.
		charBuffer.compact();
	}

	@Override
	public void write(String str) throws IOException {
		char[] tmpChars = str.toCharArray();
		write( tmpChars, 0, tmpChars.length );
	}

	@Override
	public void write(char c) throws IOException {
		if ( charBuffer.remaining() == 0 ) {
			encode_buffer( false );
		}
		charBuffer.put( c );
	}

	@Override
	public void write(char[] c) throws IOException {
		write( c, 0, c.length );
	}

	@Override
	public void write(char[] c, int off, int len) throws IOException {
		while ( len > 0 ) {
			if ( charBuffer.remaining() == 0 ) {
				encode_buffer( false );
			}
			int pos = charBuffer.position();
			int limit = charBuffer.remaining();
			if ( limit > len ) {
				limit = len;
			}
			len -= limit;
			while ( limit > 0 ) {
				charArray[ pos++ ] = c[ off++ ];
				--limit;
			}
			charBuffer.position( pos );
		}
	}

	@Override
	public void write(int b) throws IOException {
		if ( charBuffer.remaining() == 0 ) {
			encode_buffer( false );
		}
		charBuffer.put( (char)(b & 255) );
	}

	@Override
	public void write(byte[] b) throws IOException {
		write( b, 0, b.length );
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		while( len > 0 ) {
			if ( charBuffer.remaining() == 0 ) {
				encode_buffer( false );
			}
			int pos = charBuffer.position();
			int limit = charBuffer.remaining();
			if ( limit > len ) {
				limit = len;
			}
			len -= limit;
			while ( limit > 0 ) {
				charArray[ pos++ ] = (char)(b[ off++ ] & 255);
				--limit;
			}
			charBuffer.position( pos );
		}
	}

	@Override
	public void close() throws IOException {
		encode_buffer( true );
		if ( byteBuffer.position() > 0 ) {
			byteBuffer.flip();
			int pos = byteBuffer.position();
			int limit = byteBuffer.limit();
			out.write( byteArray, pos, limit - pos );
			byteBuffer.position( limit );
			byteBuffer.compact();
		}
	}

}
