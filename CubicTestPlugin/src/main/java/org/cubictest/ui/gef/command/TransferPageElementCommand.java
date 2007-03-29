/*
 * Created on 19.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;
import org.eclipse.gef.commands.Command;


/**
 * Transfers a page element to a new IContext (new page or other context on same page).
 * 
 * @author Christian Schwarz
 * @author SK Skytteren
 */
public class TransferPageElementCommand extends Command {

	private PageElement element;
	private IContext originalContext;
	private IContext newContext;
	private int newIndex;
	private Page sourcePage;
	
	//util fields:
	private boolean delegateCommandsCreated = false;
	private DeletePageElementCommand deleteCmd;
	private CreatePageElementCommand createCmd;
	
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

	public void setSourcePage(Page sourcePage) {
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
		if (!delegateCommandsCreated) {
			setUpDelegateCommands();
		}
		deleteCmd.execute();
		createCmd.execute();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		createCmd.undo();
		deleteCmd.undo();
	}

	
	
	private void setUpDelegateCommands() {
		deleteCmd = new DeletePageElementCommand();
		deleteCmd.setContext(originalContext);
		deleteCmd.setPage(sourcePage);
		deleteCmd.setPageElement(element);

		createCmd = new CreatePageElementCommand();
		createCmd.setContext(newContext);
		createCmd.setIndex(newIndex);
		createCmd.setPageElement(element);
		
		delegateCommandsCreated = true;
	}
}
