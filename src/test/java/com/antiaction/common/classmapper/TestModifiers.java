package com.antiaction.common.classmapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestModifiers {

	/*
	public static Enum TestEnum {

	}
	*/

	public interface TestInterface {
	}

	public abstract class TestAbstractClaas {
	}

	public class InnerClass {
	}

	public final class FinalInnerClass {
	}

	public static final List<?> list = new LinkedList<>();

	public static final ArrayList<?> arraylist = new ArrayList<>();;

	@Test
	@Ignore
	public void  test_modifiers() {
		Class<?> c;
		Field f;
		int m;

		c = TestInterface.class;
		m = c.getModifiers();
		System.out.println(Modifier.toString(m));

		c = TestAbstractClaas.class;

		c = InnerClass.class;
		m = c.getModifiers();
		System.out.println(Modifier.toString(m));

		c = FinalInnerClass.class;
		m = c.getModifiers();
		System.out.println(Modifier.toString(m));

		c = TestModifiers.class;
		m = c.getModifiers();
		System.out.println(Modifier.toString(m));

		try {
			f = c.getField("list");
			m = f.getModifiers();
			System.out.println(Modifier.toString(m));

			f = c.getField("arraylist");
			m = f.getModifiers();
			System.out.println(Modifier.toString(m));
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
	}

}
