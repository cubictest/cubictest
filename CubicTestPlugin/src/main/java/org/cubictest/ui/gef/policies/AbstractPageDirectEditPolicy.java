/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.policies;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.ChangeNameCommand;
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
	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		ChangeNameCommand editCommand = new ChangeNameCommand();
		AbstractPage page = (AbstractPage) getHost().getModel();
		editCommand.setNamePropertyObject(page);
		editCommand.setOldName(page.getName());
		CellEditor cellEditor = request.getCellEditor();
		editCommand.setNewName((String) cellEditor.getValue());

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
