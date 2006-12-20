package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.PageElementAction;
import org.cubictest.model.Test;
import org.cubictest.model.UserActions;

public class CubicRecorder {
	private Test test;
	private AbstractPage cursor;
	private UserActions userActions;
	
	public CubicRecorder(Test test, AbstractPage cursor) {
		this.test = test;
		this.cursor = cursor;
	}
	
	/**
	 * Set the cursor to a specific Page.
	 * @param page
	 */
	public void setCursor(AbstractPage page) {
		this.cursor = page;
	}
	
	/**
	 * This method forces the element to be added to the current page, even
	 * if a transition to a new node has been created
	 * @param element
	 */
	public void addPageElementToCurrentPage(PageElement element) {
		this.cursor.addElement(element);		
	}
	
	/**
	 * Add a check for a page element. If a new UserActions transition has been
	 * created, we move the cursor to the next Page before adding
	 * @param element
	 */
	public void addPageElement(PageElement element) {
		if(this.userActions != null) {
			// Advance the cursor
			this.cursor = (AbstractPage) this.userActions.getEnd();
			this.userActions = null;
		}
		this.addPageElementToCurrentPage(element);
	}
	
	/**
	 * Add a PageElementAction from the current page. Consecutive calls to this 
	 * function adds more PageElementActions to the same UserActions Transition 
	 * @param action
	 */
	public void addUserInput(PageElementAction action) {
		if(this.userActions == null) {
			addUserActions();
		}
		this.userActions.addInput(action);
	}
	
	/**
	 * Create a new Page and a UserActions transition to it
	 */
	private void addUserActions() {
		Page page = new Page();
		page.setName("untitled");
		this.test.addPage(page);
		UserActions ua = new UserActions(this.cursor, page);
		this.test.addTransition(ua);
		this.userActions = ua;			
	}
}
