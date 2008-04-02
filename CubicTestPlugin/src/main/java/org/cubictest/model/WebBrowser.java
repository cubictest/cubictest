package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

public class WebBrowser extends PropertyAwareObject implements IActionElement{

	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.GO_BACK);
		actions.add(ActionType.GO_FORWARD);
		actions.add(ActionType.REFRESH);
		actions.add(ActionType.SWITCH_BY_NAME);
		actions.add(ActionType.NO_ACTION);
		return actions;
	}

	public ActionType getDefaultAction() {
		return ActionType.GO_BACK;
	}

	public String getDescription() {
		return "Window";
	}

	public String getType() {
		return "WebBrowser";
	}

	@Override
	public void resetStatus() {
	}

}