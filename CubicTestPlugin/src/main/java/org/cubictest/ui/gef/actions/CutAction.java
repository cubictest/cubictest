/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.actions;

import java.util.Iterator;
import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.DeletePageCommand;
import org.cubictest.ui.gef.command.DeletePageElementCommand;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action for cutting elements.
 * @author chr_schwarz
 */
public class CutAction extends SelectionAction{
	
	private List model = null;
	
	public CutAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected void init() {
		super.init();
		setId(ActionFactory.CUT.getId());
		setText("Cut");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages(); 
		setImageDescriptor(sharedImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_CUT));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_CUT_DISABLED));
		setEnabled(false);
	}
	
	@Override
	protected boolean calculateEnabled() {
		if (model != null)
			return true;
		return false;
	}
	
	@Override
	public void run() {
		Clipboard.getDefault().setContents(model);
		Iterator iter = model.iterator();
		while(iter.hasNext()) {
			EditPart item = (EditPart) iter.next();
			if (item.getModel() instanceof PageElement) {
				DeletePageElementCommand deleteCmd = new DeletePageElementCommand();
				deleteCmd.setContext((IContext) item.getParent().getModel());
				deleteCmd.setPageElement((PageElement) item.getModel());
				getCommandStack().execute(deleteCmd);
			}
			else if (item.getModel() instanceof AbstractPage) {
				DeletePageCommand deleteCmd = new DeletePageCommand();
				deleteCmd.setTest((Test) item.getParent().getModel());
				deleteCmd.setTransitionNode((AbstractPage) item.getModel());
				getCommandStack().execute(deleteCmd);
			}
		}
	}
	@Override
	protected void handleSelectionChanged() {
		ISelection s = getSelection();
		if (!(s instanceof IStructuredSelection))
			return;
		IStructuredSelection selection = (IStructuredSelection)s;
		model = null;
		if (selection != null && selection.size() > 0) {
			model = selection.toList();
		}
		refresh();
	}
}
