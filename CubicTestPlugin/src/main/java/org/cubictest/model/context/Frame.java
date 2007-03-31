package org.cubictest.model.context;

import java.util.List;

import org.cubictest.model.IdentifierType;


public class Frame extends AbstractContext {
	
	@Override
	public String getType(){
		return "Frame";
	}
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = super.getIdentifierTypes();
		list.add(IdentifierType.NAME);
		list.add(IdentifierType.INDEX);
		return list;
	}

}
