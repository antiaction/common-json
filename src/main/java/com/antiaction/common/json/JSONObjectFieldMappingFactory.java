package com.antiaction.common.json;

import com.antiaction.common.classmapper.FieldDataFactory;

// Abstract<JSONObjectFieldMapping>
public class JSONObjectFieldMappingFactory extends FieldDataFactory {

	@Override
	public JSONObjectFieldMapping getInstance() {
		return new JSONObjectFieldMapping();
	}

}
