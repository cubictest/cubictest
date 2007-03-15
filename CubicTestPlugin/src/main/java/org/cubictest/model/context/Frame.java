package org.cubictest.model.context;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.IdentifierType;


public class Frame extends AbstractContext {
	
	public String getType(){
		return "Frame";
	}
	
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = super.getIdentifierTypes();
		list.add(IdentifierType.NAME);
		list.add(IdentifierType.INDEX);
		return list;
	}

}
