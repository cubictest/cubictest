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

import org.cubictest.model.PageElement;
import org.cubictest.ui.gef.command.ChangePageElementNotCommand;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author Stein K. Skytteren
 *
 * An selection action that changes the "present"/"notPresent" feature 
 * of the <code>cubicTestPlugin.model.Link</code> and <code>cubicTestPlugin.model.Text</code>
 * classes in the <code>org.cubictest.ui.gef.editors.GraphicalTestEditor</code>.
 */
public class PresentAction extends SelectionAction {

	/**
	 * The Actions ID which can be used to set and get it from acion registers.
	 */
	public static final String ACTION_ID = "cubicTestPlugin.action.present";
	
	/**
	 * Creates a <code>PresentAction</code> and associates it with the given workbench part.
	 * @param part the workbench part to associate with.
	 */
	public PresentAction(IWorkbenchPart part) {
		super(part);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		if (!(getSelection() instanceof StructuredSelection )) return false;
		Object selection = ((StructuredSelection)getSelection()).getFirstElement();
		if (selection instanceof PageElementEditPart){
			setText((((PageElementEditPart)selection).getModel().isNot())?"should exist":"should NOT exist");
			setImageDescriptor(getImageDescriptor());
			return true;
		}
		return false;
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
	 */
	protected void init() {
		super.init();
		setText("");
    	setId(ACTION_ID);
    	
    	setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		
		if (!(getSelection() instanceof StructuredSelection )) return;
		Object selection = ((StructuredSelection)getSelection()).getFirstElement();
		if (selection instanceof PageElementEditPart){
			ChangePageElementNotCommand command = new ChangePageElementNotCommand();
			PageElement pe = (PageElement) ((PageElementEditPart)selection).getModel();
			command.setPageElement(pe);
			command.setNewNot(!pe.isNot());
			command.setOldNot(pe.isNot());
			getCommandStack().execute(command);
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		if (!(getSelection() instanceof StructuredSelection )) 
			return super.getImageDescriptor();
		Object selection = ((StructuredSelection)getSelection()).getFirstElement();
		if (selection instanceof PageElementEditPart){
			PageElement pe = (PageElement) ((PageElementEditPart)selection).getModel();
			String type = pe.getType();
			type = type.substring(0, 1).toLowerCase() + type.substring(1, type.length());
			return CubicTestImageRegistry.getDescriptor(type,!pe.isNot());
		}
		return super.getImageDescriptor();
	}
}
