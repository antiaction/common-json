package com.antiaction.common.classmapper;

public class FieldDataFactory extends FieldDataFactoryAbstract<FieldData> {

	@Override
	public FieldData getInstance() {
		return new FieldData();
	}

}
