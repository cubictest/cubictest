/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import java.util.List;

/**
 * Interface for elements that can participate in a user interaction.
 * 
 * @author chr_schwarz
 */
public interface IActionElement {
	
	/**
	 * Get the action types (e.g. Click, MouseOver, etc) supported by the element.
	 * @return the action types (e.g. Click, MouseOver, etc) supported by the element.
	 */
	public List<ActionType> getActionTypes();
	
	/**
	 * Get the default action for the element.
	 * @return the default action for the element.
	 */
	public ActionType getDefaultAction();
	
	/**
	 * Get the text identifying the action element in the GUI/page (e.g. "Username")
	 * @return the text identifying the action element in the GUI/page
	 */
	public String getDescription();

	/**
	 * Get the element type used for GUI display (e.g. "TextField").
	 * @return the element type used for GUI display.
	 */
	public String getType();
	
}
