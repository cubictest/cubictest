/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model.formElement;

import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.IdentifierType.LABEL;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IdentifierType;

public class Option extends Checkable{

	private Select parent;
	
	@Override
	public String getType() {
		return "Option";
	}
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		list.add(IdentifierType.VALUE);
		list.add(IdentifierType.CHECKED);
		//ID type is not supported in Watir, so not added here.
		return list;
	}
	
	
	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(SELECT);
		return actions;
	}

	public Select getParent() {
		return parent;
	}

	public void setParent(Select parent) {
		this.parent = parent;
	}
}
