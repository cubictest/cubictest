/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.ChangeAbstractPageNameCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.CellEditor;


public class AbstractPageDirectEditPolicy extends DirectEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.DirectEditPolicy#getDirectEditCommand(org.eclipse.gef.requests.DirectEditRequest)
	 */
	protected Command getDirectEditCommand(DirectEditRequest request) {
		ChangeAbstractPageNameCommand editCommand = new ChangeAbstractPageNameCommand();
		AbstractPage page = (AbstractPage) getHost().getModel();
		editCommand.setAbstractPage(page);
		editCommand.setOldName(page.getName());
		CellEditor cellEditor = request.getCellEditor();
		editCommand.setName((String) cellEditor.getValue());

		// make "undo" an atomic operation for a newly created page:

		CommandStack commandStack = getHost().getViewer().getEditDomain().getCommandStack();
		
		if(ViewUtil.pageHasJustBeenCreated(commandStack, page)) {
			if (page.getInTransition() != null && (page.getInTransition().getStart() instanceof ExtensionStartPoint)) {
				//do not nest when extensionStart point:
				return editCommand;
			}
			else {
				Command previousCmd = (Command) commandStack.getUndoCommand();
				
				Test test = ViewUtil.getSurroundingTest(getHost());
				if (test.getPages().size() == 1 && page instanceof Page && test.getSubTests().size() == 0){
					//"auto setup" page (first page in test)
					CreateTransitionCommand createTransitionCmd = new CreateTransitionCommand();
					createTransitionCmd.setSource(test.getStartPoint());
					createTransitionCmd.setTarget(((AddAbstractPageCommand) previousCmd).getPage());
					createTransitionCmd.setTest(test);
					previousCmd = previousCmd.chain(createTransitionCmd);
				}
				
				//previous command must be idempotent
				return previousCmd.chain(editCommand);
			}
		}
		else {
			return editCommand;
		}
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request){

	}

}
