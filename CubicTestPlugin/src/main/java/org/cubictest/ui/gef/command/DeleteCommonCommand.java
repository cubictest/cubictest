/*
 * Created on 07.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;


/**
 * A command that deletes an <code>AbstractPage</code>.
 * Does not inherit from DeleteTransitionNodeCommand as it is too special 
 * (must update user interactions of target pages etc.)
 * 
 * @author Christian Schwarz
 */
public class DeleteCommonCommand extends DeleteAbstractPageCommand {

	private List<DeletePageElementCommand> deleteCommands = new ArrayList<DeletePageElementCommand>();
	private AbstractPage abstractPage; 
	
	private boolean redoing;
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		//clean up target user interactions
		abstractPage = (AbstractPage) transitionNode;
		List<PageElement> elementsToDelete = new ArrayList<PageElement>();
		elementsToDelete.addAll(abstractPage.getElements());
		for (PageElement element : elementsToDelete) {
			DeletePageElementCommand deleteCmd = new DeletePageElementCommand();
			if (redoing) {
				deleteCmd.setInfoDialogShowed(true);
			}
			deleteCmd.setElement(element);
			deleteCmd.setElementParent(abstractPage);
			deleteCmd.setPage(abstractPage);
			deleteCmd.execute();
			deleteCommands.add(deleteCmd);
		}
		//remove page and transitions (done in parent command)
		super.execute();
	}
		
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		test.addPage(abstractPage);
		for (DeletePageElementCommand deleteCmd : deleteCommands) {
			deleteCmd.undo();
		}
		super.undo();
	}
	
	public void redo() {
		redoing = true;
		super.redo();
	}

}
