/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.PageElement;
import org.cubictest.ui.gef.command.ChangePageElementTextCommand;
import org.cubictest.ui.utils.ViewUtil;
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
