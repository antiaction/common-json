package com.antiaction.common.classmapper;

public class JSONAnnotationsFactory extends AnnotationsFactory<JSONAnnotations> {

	@Override
	public JSONAnnotations getInstance() {
		return new JSONAnnotations();
	}

}
