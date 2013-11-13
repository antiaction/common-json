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

package com.antiaction.common.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Small reflection helper class to convert all those useless is<x> class type
 * and modifier methods into an integer bit field.
 *
 * @author Nicholas
 * Created on 29/11/2012
 */
public class ClassTypeModifiers {

	/*
	 * Class types.
	 */

	/** Annotation class type identifier mask. */
	public static final int CT_ANNOTATION = 1 << 0;
	/** Anonymous class class type identifier mask. */
	public static final int CT_ANONYMOUSCLASS = 1 << 1;
	/** Array class type identifier mask. */
	public static final int CT_ARRAY = 1 << 2;
	/** Enum class type identifier mask. */
	public static final int CT_ENUM = 1 << 3;
	/** Interface class type identifier mask. */
	public static final int CT_INTERFACE = 1 << 4;
	/** Local class class type identifier mask. */
	public static final int CT_LOCALCLASS = 1 << 5;
	/** Member class class type identifier mask. */
	public static final int CT_MEMBERCLASS = 1 << 6;
	/** Primitive class type identifier mask. */
	public static final int CT_PRIMITIVE = 1 << 7;
	/** Synthetic class type identifier mask. */
	public static final int CT_SYNTHETIC = 1 << 8;
	/** Class class type identifier mask. */
	public static final int CT_CLASS = 1 << 9;

	/*
	 * Class modifiers.
	 */

	/** Abstract class modifier identifier mask. */
	public static final int CM_ABSTRACT = 1 << 16;
	/** Final class modifier identifier mask. */
	public static final int CM_FINAL = 1 << 17;
	/** Native class modifier identifier mask. */
	public static final int CM_NATIVE = 1 << 18;
	/** Private class modifier identifier mask. */
	public static final int CM_PRIVATE = 1 << 19;
	/** Protected class modifier identifier mask. */
	public static final int CM_PROTECTED = 1 << 20;
	/** Public class modifier identifier mask. */
	public static final int CM_PUBLIC = 1 << 21;
	/** Static class modifier identifier mask. */
	public static final int CM_STATIC = 1 << 22;
	/** Strict class modifier identifier mask. */
	public static final int CM_STRICT = 1 << 23;
	/** Synchronized class modifier identifier mask. */
	public static final int CM_SYNCHRONIZED = 1 << 24;
	/** Transient class modifier identifier mask. */
	public static final int CM_TRANSIENT = 1 << 25;
	/** Volatile class modifier identifier mask. */
	public static final int CM_VOLATILE = 1 << 26;

	/**
	 * Returns a bitfield mask containing the class types and modifiers for the given Class.
	 * @param clazz the class to examine
	 * @return a bitfield mask containing the class types and modifiers for the given Class
	 */
	public static int getClassTypeModifiersMask(Class<?> clazz) {
		int mask = 0;
		/*
		 * Type.
		 */
		if ( clazz.isAnnotation() ) {
			mask |= CT_ANNOTATION;
		}
		if ( clazz.isAnonymousClass() ) {
			mask |= CT_ANONYMOUSCLASS;
		}
		if ( clazz.isArray() ) {
			mask |= CT_ARRAY;
		}
		if ( clazz.isEnum() ) {
			mask |= CT_ENUM;
		}
		if ( clazz.isInterface() ) {
			mask |= CT_INTERFACE;
		}
		if ( clazz.isLocalClass() ) {
			mask |= CT_LOCALCLASS;
		}
		if ( clazz.isMemberClass() ) {
			mask |= CT_MEMBERCLASS;
		}
		if ( clazz.isPrimitive() ) {
			mask |= CT_PRIMITIVE;
		}
		if ( clazz.isSynthetic() ) {
			mask |= CT_SYNTHETIC;
		}
		/*
		 * Class.
		 */
		if ( (mask & (CT_ANNOTATION | CT_ANONYMOUSCLASS | CT_ARRAY | CT_ENUM | CT_INTERFACE | CT_LOCALCLASS | CT_MEMBERCLASS | CT_PRIMITIVE)) == 0 ) {
			mask |= CT_CLASS;
		}
		/*
		 * Modifiers.
		 */
		return mask | getModifiersMask( clazz.getModifiers() );
	}

	public static int getFieldModifiersMask(Field field) {
		return getModifiersMask( field.getModifiers() );
	}

	public static int getModifiersMask(int mod) {
		int mask = 0;
		if ( Modifier.isAbstract( mod ) ) {
			mask |= CM_ABSTRACT;
		}
		if ( Modifier.isFinal( mod ) ) {
			mask |= CM_FINAL;
		}
		if ( Modifier.isInterface( mod ) ) {
			mask |= CT_INTERFACE;
		}
		if ( Modifier.isNative( mod ) ) {
			mask |= CM_NATIVE;
		}
		if ( Modifier.isPrivate( mod) ) {
			mask |= CM_PRIVATE;
		}
		if ( Modifier.isProtected( mod ) ) {
			mask |= CM_PROTECTED;
		}
		if ( Modifier.isPublic( mod) ) {
			mask |= CM_PUBLIC;
		}
		if ( Modifier.isStatic( mod ) ) {
			mask |= CM_STATIC;
		}
		if ( Modifier.isStrict( mod ) ) {
			mask |= CM_STRICT;
		}
		if ( Modifier.isSynchronized( mod ) ) {
			mask |= CM_SYNCHRONIZED;
		}
		if ( Modifier.isTransient( mod ) ) {
			mask |= CM_TRANSIENT;
		}
		if ( Modifier.isVolatile( mod ) ) {
			mask |= CM_VOLATILE;
		}
		return mask;
	}

	public static String toString(int mask) {
		int[] masks = new int[] {
				CT_ANNOTATION,
				CT_ANONYMOUSCLASS,
				CT_ARRAY,
				CT_ENUM,
				CT_INTERFACE,
				CT_LOCALCLASS,
				CT_MEMBERCLASS,
				CT_PRIMITIVE,
				CT_SYNTHETIC,
				CT_CLASS,
				CM_ABSTRACT,
				CM_FINAL,
				CM_NATIVE,
				CM_PRIVATE,
				CM_PROTECTED,
				CM_PUBLIC,
				CM_STATIC,
				CM_STRICT,
				CM_SYNCHRONIZED,
				CM_TRANSIENT,
				CM_VOLATILE
		};
		String[] names = new String[] {
				"Annotation",
				"Anonymous",
				"Array",
				"Enum",
				"Interface",
				"Local",
				"Member",
				"Primitive",
				"Synthetic",
				"Class",
				"Abstract",
				"Final",
				"Native",
				"Private",
				"Protected",
				"Public",
				"Static",
				"Strict",
				"Synchronized",
				"Transient",
				"Volative"
		};
		StringBuilder sb = new StringBuilder();
		for ( int i=0; i<masks.length; ++i ) {
			if ( (mask & masks[ i ]) != 0 ) {
				if ( sb.length() > 0 ) {
					sb.append( ", " );
				}
				sb.append( names[ i ] );
			}
		}
		return sb.toString();
	}

}
