/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder;

import static org.cubictest.model.ActionType.CLICK;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.ActionType;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.ChangeAbstractPageNameCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.layout.AutoLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.CommandStack;

public class CubicRecorder implements IRecorder {
	private Test test;
	private AbstractPage cursor;
	private UserInteractionsTransition userInteractionsTransition;
	private final CommandStack commandStack;
	private final AutoLayout autoLayout;
	private boolean enabled;
	
	public CubicRecorder(Test test, CommandStack comandStack, AutoLayout autoLayout) {
		this.test = test;
		this.commandStack = comandStack;
		this.autoLayout = autoLayout;
		//reuse empty start page if present:
		for(Transition t : test.getStartPoint().getOutTransitions()) {
			if(t.getEnd() instanceof Page && ((Page)t.getEnd()).getRootElements().size() == 0) {
				setCursor((Page) t.getEnd());
			}
		}
		
		//if no empty start page, create new
		if(this.cursor == null) {
			setCursor(this.createNewUserInteractionTransition(test.getStartPoint()));
		}
		//reset active transition to force creation of a new one for the user inputs
		this.userInteractionsTransition = null;
	}
	
	public CubicRecorder(Test test, Page cursor, CommandStack commandStack, AutoLayout autoLayout) {
		this.test = test;
		setCursor(cursor);
		this.commandStack = commandStack;
		this.autoLayout = autoLayout;
	}
	
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#setCursor(org.cubictest.model.AbstractPage)
	 */
	public void setCursor(AbstractPage page) {
		this.cursor = page;
		autoLayout.setPageSelected(this.cursor);
	}

	
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#addPageElement(org.cubictest.model.PageElement)
	 */
	public void addPageElement(PageElement element, PageElement parent) {
		if (!enabled) return;
		
		CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
		if(parent != null && parent instanceof IContext){
			createElementCmd.setIndex(((IContext) parent).getRootElements().size());
			createElementCmd.setContext((IContext) parent);
		} else {
			createElementCmd.setIndex(cursor.getRootElements().size());
			createElementCmd.setContext(this.cursor);
		}
		createElementCmd.setPageElement(element);
		
		this.commandStack.execute(createElementCmd);
		this.autoLayout.layout(cursor);
	}
	
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#addUserInput(org.cubictest.model.UserInteraction)
	 */
	public void addUserInput(UserInteraction action, PageElement parent) {
		if (!enabled) return;
		
		if(this.userInteractionsTransition == null) {
			createNewUserInteractionTransition(this.cursor);
		}
		
		boolean elementExists = false;
		for(PageElement element : cursor.getRootElements()) {
			if(action.getElement() == element) {
				elementExists = true;
			}
		}
		if(!elementExists) {
			this.addPageElement((PageElement) action.getElement(), parent);
		}

		this.userInteractionsTransition.addUserInteraction(action);
		this.autoLayout.layout(cursor);

		
		ActionType lastActionType = action.getActionType();
		if (lastActionType.equals(CLICK)) {
			IActionElement pe = action.getElement();
			if (pe instanceof Link || pe instanceof Button || pe instanceof Image) {
				advanceCursor();
			}
		}
	}

	/**
	 * Advance the cursor, creating a new user interaction transition if needed.
	 */
	private void advanceCursor() {
		if (this.userInteractionsTransition == null) {
			createNewUserInteractionTransition(this.cursor);
		}
		//advance the cursor:
		setCursor((AbstractPage) this.userInteractionsTransition.getEnd());
		this.userInteractionsTransition = null;
	}
	
	/**
	 * Create a new Page and a UserInteractionsTransition transition to it
	 */
	private AbstractPage createNewUserInteractionTransition(TransitionNode from) {
		Page page = new Page();
		if (from instanceof UrlStartPoint || from instanceof ExtensionStartPoint) {
			int num = from.getOutTransitions().size();
			page.setPosition(new Point(ITestEditor.INITIAL_PAGE_POS_X + (290 * num), ITestEditor.INITIAL_PAGE_POS_Y));
		}
				
		UserInteractionsTransition ua = new UserInteractionsTransition(from, page);
		
		/* Add Page */
		AddAbstractPageCommand addPageCmd = new AddAbstractPageCommand();
		addPageCmd.setPage(page);
		addPageCmd.setTest(test);
		commandStack.execute(addPageCmd);

		/* Change Page Name */
		ChangeAbstractPageNameCommand changePageNameCmd = new ChangeAbstractPageNameCommand();
		changePageNameCmd.setAbstractPage(page);
		changePageNameCmd.setOldName("");
		if (from instanceof UrlStartPoint) {
			changePageNameCmd.setName("First Page");
		}
		else {
			changePageNameCmd.setName("next page");
		}
		commandStack.execute(changePageNameCmd);
		
		/* Add Transition */
		CreateTransitionCommand createTransitionCmd = new CreateTransitionCommand();
		if (!(from instanceof ExtensionStartPoint)) {
			createTransitionCmd.setTransition(ua);
		}
		createTransitionCmd.setTest(test);
		createTransitionCmd.setSource(from);
		createTransitionCmd.setTarget(page);
		createTransitionCmd.setAutoCreateTargetPage(false);
		commandStack.execute(createTransitionCmd);

		userInteractionsTransition = ua;
		return page;
	}

	public void setStateTitle(String title) {
		ChangeAbstractPageNameCommand changePageNameCmd = new ChangeAbstractPageNameCommand();
		if(userInteractionsTransition != null) {
			changePageNameCmd.setAbstractPage((AbstractPage) userInteractionsTransition.getEnd());
			changePageNameCmd.setOldName(userInteractionsTransition.getEnd().getName());
		} else {
			changePageNameCmd.setAbstractPage(cursor);
			changePageNameCmd.setOldName(cursor.getName());
		}
		changePageNameCmd.setName(title);	
		this.commandStack.execute(changePageNameCmd);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
