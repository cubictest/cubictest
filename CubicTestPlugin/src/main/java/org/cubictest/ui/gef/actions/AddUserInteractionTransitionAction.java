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

import org.cubictest.common.utils.ModelUtil;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Context menu action for adding a user interaction transition to a page.
 * 
 * @author Christian Schwarz
 *
 */
public class AddUserInteractionTransitionAction extends BaseEditorAction {

	public static final String ACTION_ID = "CubicTestPlugin.action.addUserInteractionTransition";

	
	public AddUserInteractionTransitionAction(IWorkbenchPart part) {
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
		setText("Add User Interaction");
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		for (Iterator iter = this.getParts().iterator(); iter.hasNext();) {
			Object element = iter.next();
			PageEditPart pageEditPart = null;
			PageElement selectedPageElement = null;
			if (element instanceof PageElementEditPart) {
				selectedPageElement = ((PageElementEditPart) element).getModel();
				pageEditPart = (PageEditPart) ViewUtil.getSurroundingPagePart((EditPart) element);
			}
			if(element instanceof PageEditPart) {
				pageEditPart = (PageEditPart) element;
			}
			
			Page page = (Page) pageEditPart.getModel();
			List<IActionElement> elements = ModelUtil.getActionElements(page);
			if (elements.size() == 0) {
				UserInfo.showWarnDialog("Cannot create user interaction. Page/State must have at least one page element that can accept user interactions.");
				return;
			}
			CreateTransitionCommand cmd = new CreateTransitionCommand();
			cmd.setTest((Test) pageEditPart.getParent().getModel());
			cmd.setSource((TransitionNode) pageEditPart.getModel());
			cmd.setPageEditPart(pageEditPart);
			cmd.setAutoCreateTargetPage(true);
			cmd.setSelectedPageElement(selectedPageElement);

			getCommandStack().execute(cmd);
		}
	}
	
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.USER_INTERACTION_IMAGE);
	}
}
