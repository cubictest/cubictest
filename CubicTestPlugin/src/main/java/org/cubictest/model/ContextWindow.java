package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

public class ContextWindow implements IActionElement{
	
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.SWITCH_BY_TITLE);
		actions.add(ActionType.SWITCH_BY_URL);
		actions.add(ActionType.CLOSE);
		return actions;
	}

	public ActionType getDefaultAction() {
		return ActionType.SWITCH_BY_TITLE;
	}

	public String getDescription() {
		return "Window";
	}

	public String getType() {
		// TODO Auto-generated method stub
		return "Context";
	}


}
