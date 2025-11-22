package com.antiaction.common.classmapper;

public class AnnotationsFactory extends AnnotationsFactoryAbstract<Annotations> {

	@Override
	public Annotations getInstance() {
		return new Annotations();
	}

}
