/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - bug fixes and usability improvements
 *******************************************************************************/
package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.Transition;
import org.cubictest.model.UserInteraction;

public interface IRecorder {

	/**
	 * Set the cursor to a specific Page.
	 * @param page
	 */
	public void setCursor(AbstractPage page);

	/**
	 * Add element to the current page.
	 * @param element
	 * @param parent 
	 */
	public void addPageElement(PageElement element, PageElement parent);

	/**
	 * Add a UserInteraction to the current page, creating a new transition if needed.
	 * If action if of type CLICK, a the cursor is moved to the next page. 
	 * @param action
	 * @param parent 
	 */
	public void addUserInput(UserInteraction action, PageElement parent);

	/**
	 * Set title of the current page to the specified string.
	 * @param title
	 */
	public void setStateTitle(String title);

	
	public void setEnabled(boolean enabled);
	
	public boolean isEnabled();
	
	public void addToTest(Transition transition, AbstractPage endPage);

}