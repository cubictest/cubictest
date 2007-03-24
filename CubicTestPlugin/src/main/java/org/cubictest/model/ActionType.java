/*
 * Created on 9.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model;

import java.util.HashMap;

/**
 * The action types for user interaction with a web page.
 * 
 * @author skyt
 * @author chr_schwarz
 */
public enum ActionType {

	//params: text, acceptsTextInput
	
	CLICK("Click", false),
	CHECK("Check", false),
	UNCHECK("Uncheck", false),
	ENTER_TEXT("Enter text", true),
	KEY_PRESSED("Key pressed", false),
	CLEAR_ALL_TEXT("Clear all text", false),
	MOUSE_OVER("Mouse over", false),
	MOUSE_OUT("Mouse out", false),
	DBLCLICK("Double click", false),
	FOCUS("Set focus", false),
	BLUR("Remove focus", false),
	DRAG_START("Drag n' Drop start", false),
	DRAG_END("Drag n' Drop end", false), 
	NO_ACTION("No action", false), 
	GO_BACK("Go back", false),
	GO_FORWARD("Go forward", false),
	REFRESH("Refresh", false),
	NEXT_WINDOW("Next window", false),
	PREVIOUS_WINDOW("Previous window", false),
	ENTER_PARAMETER_TEXT("Enter parameter text", true),
	SELECT("Select", false),
	//Added by Genesis Campos
	SWITCH_BY_TITLE("Switch by Title", true),
	SWITCH_BY_URL("Switch by URL", true),
	CLOSE("Close Window", false)
	//End;	
	;
	
	private String text;
	private boolean acceptsInput;
	
	static HashMap<String, ActionType> actions;
	
	private ActionType(String text, boolean acceptsInput) {
		this.text = text;
		this.acceptsInput = acceptsInput;
		if(ActionType.actions == null) {
			ActionType.actions = new HashMap<String, ActionType>();
		}
		ActionType.actions.put(text, this);
	}
	
	/**
	 * Get the text describing the action type, e.g. "Click".
	 * @return
	 */
	public String getText(){
		return text;
	}
	
	public boolean acceptsInput() {
		return acceptsInput;
	}
	
	public static ActionType getActionType(String action) {
		return ActionType.actions.get(action);
	}
}
