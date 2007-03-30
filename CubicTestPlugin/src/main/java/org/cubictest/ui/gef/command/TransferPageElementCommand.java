/*
 * Created on 19.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;
import org.eclipse.gef.commands.Command;


/**
 * Transfers a page element to a new IContext (new page or other context on same page).
 * 
 * @author Christian Schwarz
 */
public class TransferPageElementCommand extends Command {

	private PageElement element;
	private IContext originalContext;
	private IContext newContext;
	private int newIndex;
	private AbstractPage sourcePage;
	private AbstractPage targetPage;
	
	//util fields:
	private boolean delegateCommandsCreated = false;
	private DeletePageElementCommand deleteCmd;
	private CreatePageElementCommand createCmd;
	private int oldIndex;
	
	/**
	 * @param childModel
	 */
	public void setElement(PageElement element) {
		this.element = element;
	}

	/**
	 * @param originalContext
	 */
	public void setOriginalContext(IContext originalContext) {
		this.originalContext = originalContext;
	}

	/**
	 * @param newContext
	 */
	public void setNewContext(IContext newContext) {
		this.newContext = newContext;
	}


	/**
	 * @param newIndex
	 */
	public void setNewIndex(int newIndex) {
		this.newIndex = newIndex;
		
	}

	public void setSourcePage(AbstractPage sourcePage) {
		this.sourcePage = sourcePage;
	}

	
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute(){
		if (element instanceof Option && !(newContext instanceof Select)) {
			//do not move Option outside Select
			return false;
		}
		else if (!(element instanceof Option) && newContext instanceof Select) {
			//do not move non-Option inside Select
			return false;
		}
		return true;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (sourcePage.equals(targetPage)) {
			oldIndex = originalContext.getElementIndex(element);
			originalContext.removeElement(element);
			newContext.addElement(element,newIndex);
		}
		else {
			//move between different pages. Must clean up user interaction tranisitions
			if (!delegateCommandsCreated) {
				setUpDelegateCommands();
			}
			deleteCmd.execute();
			createCmd.execute();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		if (sourcePage.equals(targetPage)) {
			newContext.removeElement(element);
			originalContext.addElement(element,oldIndex);
		}
		else {
			//different page. Move and reset user interactions transition
			createCmd.undo();
			deleteCmd.undo();
		}
	}

	
	
	private void setUpDelegateCommands() {
		deleteCmd = new DeletePageElementCommand();
		deleteCmd.setElementParent(originalContext);
		deleteCmd.setPage(sourcePage);
		deleteCmd.setElement(element);

		createCmd = new CreatePageElementCommand();
		createCmd.setContext(newContext);
		createCmd.setIndex(newIndex);
		createCmd.setPageElement(element);
		
		delegateCommandsCreated = true;
	}

	public void setTargetPage(AbstractPage targetPage) {
		this.targetPage = targetPage;
	}
}
