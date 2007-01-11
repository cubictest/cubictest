package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.PageElementAction;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserActions;

public class CubicRecorder implements IRecorder {
	private Test test;
	private AbstractPage cursor;
	private UserActions userActions;
	
	public CubicRecorder(Test test) {
		this.test = test;
		for(Transition t : test.getStartPoint().getOutTransitions()) {
			if(t.getEnd() instanceof Page && ((Page)t.getEnd()).getElements().size() == 0) {
				this.cursor = (Page) t.getEnd();
			}
		}
		
		if(this.cursor == null) {
			this.cursor = this.addUserActions(test.getStartPoint());
		}
	}
	
	public CubicRecorder(Test test, Page cursor) {
		this.test = test;
		this.cursor = cursor;
	}
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#setCursor(org.cubictest.model.AbstractPage)
	 */
	public void setCursor(AbstractPage page) {
		this.cursor = page;
	}
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#addPageElementToCurrentPage(org.cubictest.model.PageElement)
	 */
	public void addPageElementToCurrentPage(PageElement element) {
		this.cursor.addElement(element);		
	}
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#addPageElement(org.cubictest.model.PageElement)
	 */
	public void addPageElement(PageElement element) {
		if(this.userActions != null) {
			// Advance the cursor
			this.cursor = (AbstractPage) this.userActions.getEnd();
			this.userActions = null;
		}
		this.addPageElementToCurrentPage(element);
	}
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#addUserInput(org.cubictest.model.PageElementAction)
	 */
	public void addUserInput(PageElementAction action) {
		if(this.userActions == null) {
			addUserActions(this.cursor);
		}
		this.userActions.addInput(action);
	}
	
	/**
	 * Create a new Page and a UserActions transition to it
	 */
	private AbstractPage addUserActions(TransitionNode from) {
		Page page = new Page();
		page.setName("untitled");
		this.test.addPage(page);
		UserActions ua = new UserActions(from, page);
		this.test.addTransition(ua);
		this.userActions = ua;
		return page;
	}
}
