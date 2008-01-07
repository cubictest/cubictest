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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Context menu action for adding an extension point to a page.
 * 
 * @author Christian Schwarz
 *
 */
public class AddExtensionPointAction extends BaseEditorAction {

	public static final String ACTION_ID = "CubicTestPlugin.action.addExtensionPoint";
	
	public AddExtensionPointAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		if(getParts() != null) {
			for(Object element : getParts()) {
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
		for (Iterator iter = this.getParts().iterator(); iter.hasNext();) {
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
	
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.EXTENSION_POINT_IMAGE);
	}
}
