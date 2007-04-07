package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;

public interface IRecorder {

	/**
	 * Set the cursor to a specific Page.
	 * @param page
	 */
	public abstract void setCursor(AbstractPage page);

	/**
	 * Add element to the current page.
	 * @param element
	 */
	public abstract void addPageElement(PageElement element);

	/**
	 * Add a UserInteraction to the current page, creating a new transition if needed.
	 * If action if of type CLICK, a the cursor is moved to the next page. 
	 * @param action
	 */
	public abstract void addUserInput(UserInteraction action);

	/**
	 * Set title of the current page to the specified string.
	 * @param title
	 */
	public abstract void setStateTitle(String title);

}