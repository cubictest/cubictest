/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.actions;

import java.util.Iterator;
import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Common;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.DeleteCommonCommand;
import org.cubictest.ui.gef.command.DeleteCustomTestStepCommand;
import org.cubictest.ui.gef.command.DeleteExtensionPointCommand;
import org.cubictest.ui.gef.command.DeletePageCommand;
import org.cubictest.ui.gef.command.DeletePageElementCommand;
import org.cubictest.ui.gef.command.DeleteSubTestCommand;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
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
 * 
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
		if (model == null) {
			return false;
		}
		else if(model.size() == 1 && model.get(0) instanceof AbstractConnectionEditPart) {
			return false;
		}
		else if (model.size() == 1 && model.get(0) instanceof PropertyChangePart) {
			return ((PropertyChangePart) model.get(0)).isCuttable();
		}
		else {
			return true;
		}
	}
	
	@Override
	public void run() {
		List<EditPart> newClips = ViewUtil.getPartsForClipboard(model);
		Clipboard.getDefault().setContents(newClips);
		
		Iterator iter = newClips.iterator();
		CompoundCommand compoundCmd = new CompoundCommand();
		while(iter.hasNext()) {
			EditPart item = (EditPart) iter.next();
			if (item.getModel() instanceof PageElement) {
				DeletePageElementCommand deleteCmd = new DeletePageElementCommand();
				deleteCmd.setElementParent((IContext) item.getParent().getModel());
				deleteCmd.setElement((PageElement) item.getModel());
				deleteCmd.setPage(ViewUtil.getSurroundingPage(item));
				compoundCmd.add(deleteCmd);
			}
			else if (item.getModel() instanceof Page) {
				DeletePageCommand deleteCmd = new DeletePageCommand();
				deleteCmd.setTest((Test) item.getParent().getModel());
				deleteCmd.setTransitionNode((TransitionNode) item.getModel());
				compoundCmd.add(deleteCmd);
			}
			else if (item.getModel() instanceof CustomTestStep) {
				DeleteCustomTestStepCommand deleteCmd = new DeleteCustomTestStepCommand();
				deleteCmd.setTest((Test) item.getParent().getModel());
				deleteCmd.setTransitionNode((TransitionNode) item.getModel());
				compoundCmd.add(deleteCmd);
			}
			else if (item.getModel() instanceof ExtensionPoint) {
				DeleteExtensionPointCommand deleteCmd = new DeleteExtensionPointCommand();
				deleteCmd.setTest((Test) item.getParent().getModel());
				deleteCmd.setTransitionNode((TransitionNode) item.getModel());
				compoundCmd.add(deleteCmd);
			}
			else if (item.getModel() instanceof SubTest) {
				DeleteSubTestCommand deleteCmd = new DeleteSubTestCommand();
				deleteCmd.setTest((Test) item.getParent().getModel());
				deleteCmd.setTransitionNode((TransitionNode) item.getModel());
				compoundCmd.add(deleteCmd);
			}
			else if (item.getModel() instanceof Common) {
				DeleteCommonCommand deleteCmd = new DeleteCommonCommand();
				deleteCmd.setTest((Test) item.getParent().getModel());
				deleteCmd.setTransitionNode((AbstractPage) item.getModel());
				compoundCmd.add(deleteCmd);
			}
		}
		getCommandStack().execute(compoundCmd);
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
