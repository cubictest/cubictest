/*
 * Created on 9.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model;

/**
 * The action types for user interaction with a web page.
 * 
 * @author skyt
 * @author chr_schwarz
 */
public enum ActionType {

	//params: text, acceptsInput
	
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
	;
	
	private String text;
	private boolean acceptsInput;

	private ActionType(String text, boolean acceptsInput){
		this.text = text;
		this.acceptsInput = acceptsInput;
	}
	
	public String getText(){
		return text;
	}
	
	public boolean acceptsInput() {
		return acceptsInput;
	}
}
