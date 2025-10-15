package com.antiaction.common.classmapper;

public class ClassModifier {

	/*
	 * THE CLASS FILE FORMAT The ClassFile Structure 4.1
	 * Table 4.1-B. Class access and property modifiers
	 */

	/** Declared public; may be accessed from outside its package. */
	public static final int ACC_PUBLIC = 0x0001;

	/** Declared final; no subclasses allowed. */
	public static final int ACC_FINAL = 0x0010;

	/** Treat superclass methods specially when invoked by the invokespecial instruction. */
	public static final int ACC_SUPER = 0x0020;

	/** Is an interface, not a class. */
	public static final int ACC_INTERFACE = 0x0200;

	/** Declared abstract; must not be instantiated. */
	public static final int ACC_ABSTRACT = 0x0400;

	/** Declared synthetic; not present in the source code. */
	public static final int ACC_SYNTHETIC = 0x1000;

	/** Declared as an annotation interface. */
	public static final int ACC_ANNOTATION = 0x2000;

	/** Declared as an enum class. */
	public static final int ACC_ENUM = 0x4000;

	/** Is a module, not a class or interface. */
	public static final int ACC_MODULE = 0x8000;

}
