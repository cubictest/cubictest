/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.model;

import java.util.HashMap;

/**
 * The action types for user interaction with a web page.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public enum ActionType {

	//params: text, description, acceptsTextInput
	
	CLICK("Click", "Click on the element", false),
	CHECK("Check", "Checks an checkbox", false),
	UNCHECK("Uncheck", "Unchecks an checkbox", false),
	ENTER_TEXT("Enter text", "Enter text into the field", true),
	KEY_PRESSED("Key pressed", "Fire key pressed event", false),
	CLEAR_ALL_TEXT("Clear all text", "Clears the text in the field", false),
	MOUSE_OVER("Mouse over", "Fire mouse over event", false),
	MOUSE_OUT("Mouse out", "Fire mouse out event", false),
	DBLCLICK("Double click", "Double click on the element", false),
	FOCUS("Set focus", "Set focus on the element", false),
	BLUR("Remove focus", "Remove focus from the element(blur)", false),
	DRAG_DROP("Drag n' Drop", "Start a drag'n drop action. Value example: -200,+80", false),
	//DRAG_END("Drag n' Drop end", "End a drag'n drop action", false), 
	NO_ACTION("No action", "Take no action", false), 
	GO_BACK("Go back", "Go back on the browser", false),
	//GO_FORWARD("Go forward", "Go forward on the browser", false),
	REFRESH("Refresh", "Refreshes the browser", false),
	ENTER_PARAMETER_TEXT("Enter parameter text", "Enter a text from the parameter field specified", true),
	SELECT("Select", "Select this element", false),
	SWITCH_WINDOW_BY_NAME("Switch Browser Window by name", "Switch to a another window by using its name", true),
//	SWITCH_BY_TITLE("Switch by Title", "", true),
//	SWITCH_BY_URL("Switch by URL", "", true),
	CLOSE("Close Window", "Closes the window", false);
	
	private String text;
	private boolean acceptsInput;
	private String description;
	
	static HashMap<String, ActionType> actions;
	
	private ActionType(String text, String description, boolean acceptsInput) {
		this.text = text;
		this.description = description;
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
	
	/**
	 * Get the description for this action type
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	public boolean acceptsInput() {
		return acceptsInput;
	}
	
	public static ActionType getActionType(String action) {
		return ActionType.actions.get(action);
	}
}
