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

	/** The edit parts that are selected in the editor */
	private List<Object> parts;

	public BaseEditorAction(IWorkbenchPart part) {
		super(part);
	}

	
	@Override
	protected void handleSelectionChanged() {
		ISelection s = getSelection();
		if (!(s instanceof IStructuredSelection))
			return;
		IStructuredSelection selection = (IStructuredSelection)s;
		setParts(null);
		if (selection != null && selection.size() > 0) {
			setParts(selection.toList());
		}
		refresh();
	}


	public void setParts(List<Object> parts) {
		this.parts = parts;
	}


	public List<Object> getParts() {
		return parts;
	}
}
