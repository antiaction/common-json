package com.antiaction.common.json;

import com.antiaction.common.classmapper.AnnotationsFactory;

public class JSONAnnotationsFactory extends AnnotationsFactory {

	@Override
	public JSONAnnotations getInstance() {
		return new JSONAnnotations();
	}

}
