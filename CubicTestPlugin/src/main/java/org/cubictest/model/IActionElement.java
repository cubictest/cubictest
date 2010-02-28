/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
