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
	 * This method forces the element to be added to the current page, even
	 * if a transition to a new node has been created
	 * @param element
	 */
	public abstract void addPageElementToCurrentPage(PageElement element);

	/**
	 * Add a check for a page element. If a new UserInteractionsTransition transition has been
	 * created, we move the cursor to the next Page before adding
	 * @param element
	 */
	public abstract void addPageElement(PageElement element);

	/**
	 * Add a UserInteraction from the current page. Consecutive calls to this 
	 * function adds more PageElementActions to the same UserInteractionsTransition Transition 
	 * @param action
	 */
	public abstract void addUserInput(UserInteraction action);

	public abstract void setStateTitle(String title);

}