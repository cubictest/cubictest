/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model.popup;

import static org.cubictest.model.ActionType.CLICK;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.formElement.Button;

public abstract class JavaScriptButton extends Button {

	@Override
	public abstract String getType();
	
	@Override
	public String getText() {
		return "";
	}
	
	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(CLICK);
		return actions;
	}

	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		return list;
	}
	
}
