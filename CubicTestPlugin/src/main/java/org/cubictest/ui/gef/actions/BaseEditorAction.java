/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.List;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Base class for test editor actions.
 * 
 * @author Christian Schwarz
 */
public abstract class BaseEditorAction extends SelectionAction {

	/** The editparts that are selected in the editor */
	protected List parts;

	public BaseEditorAction(IWorkbenchPart part) {
		super(part);
	}

	
	@Override
	protected void handleSelectionChanged() {
		ISelection s = getSelection();
		if (!(s instanceof IStructuredSelection))
			return;
		IStructuredSelection selection = (IStructuredSelection)s;
		parts = null;
		if (selection != null && selection.size() > 0) {
			parts = selection.toList();
		}
		refresh();
	}
}
