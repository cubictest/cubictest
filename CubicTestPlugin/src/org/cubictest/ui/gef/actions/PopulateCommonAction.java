/*
 * Created on 21.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.actions;

import org.cubictest.model.Common;
import org.cubictest.ui.gef.command.PopulateCommonCommand;
import org.cubictest.ui.gef.controller.CommonEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;


public class PopulateCommonAction extends SelectionAction {

	public static final String ID = "PopulateCommonAction"; 
	private Object selected = null;
	
	
	public PopulateCommonAction(IWorkbenchPart part) {
		super(part);
		setId(ID);
	}

	@Override
	protected boolean calculateEnabled() {
		if (selected != null && selected instanceof CommonEditPart)
			return true;
		return false;
	}
	
	@Override
	protected void init() {
		super.init();
		setId(ID);
		setText("Populate Common");
	}
	@Override
	public void run() {
		PopulateCommonCommand command = new PopulateCommonCommand();
		command.setCommon((Common)((CommonEditPart) selected).getModel());
		getCommandStack().execute(command);
	}
	
	@Override
	protected void handleSelectionChanged() {
		ISelection s = getSelection();
		if (!(s instanceof IStructuredSelection))
			return;
		IStructuredSelection selection = (IStructuredSelection)s;
		selected = selection.getFirstElement();
		refresh();
	}

}
