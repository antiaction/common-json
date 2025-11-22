package com.antiaction.common.classmapper;

public class ClassDataFactory extends ClassDataFactoryAbstract<ClassData> {

	@Override
	public ClassData getInstance() {
		return new ClassData();
	}

}
