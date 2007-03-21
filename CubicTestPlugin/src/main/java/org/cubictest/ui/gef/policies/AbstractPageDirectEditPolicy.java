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
		ChangeAbstractPageNameCommand cmd = new ChangeAbstractPageNameCommand();
		AbstractPage page = (AbstractPage) getHost().getModel();
		cmd.setAbstractPage(page);
		cmd.setOldName(page.getName());
		CellEditor cellEditor = request.getCellEditor();
		cmd.setName((String) cellEditor.getValue());

		// make "undo" an atomic operation for a newly created page:

		CommandStack commandStack = getHost().getViewer().getEditDomain().getCommandStack();
		
		if(ViewUtil.pageHasJustBeenCreated(commandStack, page)) {
			if (page.getInTransition() != null && (page.getInTransition().getStart() instanceof ExtensionStartPoint)) {
				//do not nest when extensionStart point:
				return cmd;
			}
			else {
				Command addPageCmd = (AddAbstractPageCommand) commandStack.getUndoCommand();
				
				Test test = ViewUtil.getSurroundingTest(getHost());
				if (test.getPages().size() == 1 && page instanceof Page){
					CreateTransitionCommand createTransitionCmd = new CreateTransitionCommand();
					createTransitionCmd.setSource(test.getStartPoint());
					createTransitionCmd.setTarget(((AddAbstractPageCommand) addPageCmd).getPage());
					createTransitionCmd.setTest(test);
					addPageCmd = addPageCmd.chain(createTransitionCmd);
				}
				
				return addPageCmd.chain(cmd);
			}
		}
		else {
			return cmd;
		}
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request){

	}

}
