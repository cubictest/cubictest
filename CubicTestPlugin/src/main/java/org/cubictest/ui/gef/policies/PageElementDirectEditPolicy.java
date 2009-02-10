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

import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.PageElement;
import org.cubictest.ui.gef.command.ChangePageElementTextCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.CellEditor;


public class PageElementDirectEditPolicy extends DirectEditPolicy{

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		
		
		ChangePageElementTextCommand changeDescriptionCmd = new ChangePageElementTextCommand();
		PageElement element = (PageElement) getHost().getModel();
		changeDescriptionCmd.setPageElement(element);
		changeDescriptionCmd.setOldText(element.getText());
		CellEditor cellEditor = request.getCellEditor();
		changeDescriptionCmd.setNewText((String) cellEditor.getValue());
		

		//make "undo" an atomic operation for a newly created page element:

		CommandStack commandStack = getHost().getViewer().getEditDomain().getCommandStack();
		
		if(ViewUtil.pageElementHasJustBeenCreated(commandStack, element)) {
			CompoundCommand compoundCommand = new CompoundCommand();
			compoundCommand.add(commandStack.getUndoCommand()); //idempotent, can be redone
			compoundCommand.add(changeDescriptionCmd);
			return compoundCommand;
		}
		else {
			return changeDescriptionCmd;
		}
	}
	
	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
		
	}

}
