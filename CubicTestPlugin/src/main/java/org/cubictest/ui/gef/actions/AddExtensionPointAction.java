/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.Iterator;
import java.util.List;

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.command.AddExtensionPointCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Context menu action for adding an extension point to a page.
 * 
 * @author Christian Schwarz
 *
 */
public class AddExtensionPointAction extends SelectionAction {

	public static final String ACTION_ID = "CubicTestPlugin.action.addExtensionPoint";
	private List model;
	
	public AddExtensionPointAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		if(model != null) {
			for(Object element : model) {
				if(element instanceof PageEditPart || element instanceof PageElementEditPart) {
					return true;
				}
			}			
		}
		return false;
	}

	@Override
	protected void init() {
		super.init();
		setText("Add Extension Point");
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		for (Iterator iter = this.model.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if(element instanceof PageEditPart) {
				PageEditPart pageEditPart = (PageEditPart) element;
				Page page = (Page) pageEditPart.getModel();
				Test test = (Test) pageEditPart.getParent().getModel();
				ExtensionPoint exPoint = new ExtensionPoint();

				CompoundCommand compoundCmd = new CompoundCommand();

				AddExtensionPointCommand cmd = new AddExtensionPointCommand();
				cmd.setPage(page);
				cmd.setExtensionPoint(exPoint);
				cmd.setTest(test);
				compoundCmd.add(cmd);
				
				CreateTransitionCommand transitionCreateCommand = new CreateTransitionCommand();
				transitionCreateCommand.setSource(page);
				transitionCreateCommand.setTarget(exPoint);
				transitionCreateCommand.setTest(test);
				transitionCreateCommand.setPageEditPart(pageEditPart);
				compoundCmd.add(transitionCreateCommand);

				getCommandStack().execute(compoundCmd);
			}
		}
	}
	
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
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.EXTENSION_POINT_IMAGE);
	}
}
