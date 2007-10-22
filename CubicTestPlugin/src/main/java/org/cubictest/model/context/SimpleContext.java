/*
 * Created on 08.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model.context;

import java.util.List;

import org.cubictest.model.IdentifierType;

/**
 * Generic context element, default implementation of AbstractContext.
 * Can be any parent HTML element.
 * 
 * @author Christian Schwarz
 *
 */
public class SimpleContext extends AbstractContext {
	
	@Override
	public String getType(){
		return "Context";
	}
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {
		List<IdentifierType> list = super.getIdentifierTypes();
		return list;
	}
}
