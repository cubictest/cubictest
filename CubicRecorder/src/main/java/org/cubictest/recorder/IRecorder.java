/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
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
	 */
	public void addUserInput(UserInteraction action);

	/**
	 * Set title of the current page to the specified string.
	 * @param title
	 */
	public void setStateTitle(String title);

}