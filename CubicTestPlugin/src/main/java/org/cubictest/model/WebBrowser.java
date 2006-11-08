/*
 * Created on 29.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

public class WebBrowser implements IActionElement{

	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.GO_BACK);
		actions.add(ActionType.GO_FORWARD);
		actions.add(ActionType.REFRESH);
		actions.add(ActionType.NEXT_WINDOW);
		actions.add(ActionType.PREVIOUS_WINDOW);
		actions.add(ActionType.NO_ACTION);
		return actions;
	}

	public ActionType getDefaultAction() {
		return ActionType.GO_BACK;
	}

	public String getDescription() {
		return "Browser";
	}

	public String getType() {
		// TODO Auto-generated method stub
		return "Web";
	}

}
