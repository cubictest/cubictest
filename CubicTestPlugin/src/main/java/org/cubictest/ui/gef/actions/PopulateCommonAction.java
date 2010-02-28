/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
