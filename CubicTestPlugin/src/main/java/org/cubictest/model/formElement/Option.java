/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model.formElement;

import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.IdentifierType.INDEX;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.SELECTED;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.Identifier;
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
		list.add(SELECTED);
		list.add(INDEX);
		list.add(IdentifierType.ID); //Watir may have problems with ID
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
	
	@Override
	protected void setDefaultIdentifierValues() {
		getIdentifiers().get(0).setProbability(Identifier.MAX_PROBABILITY);
	}

}
