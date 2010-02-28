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

import org.cubictest.common.utils.ViewUtil;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action for copying selected test elements to the clipboard. 
 * 
 * @author Christian Schwarz
 */
public class CopyAction extends BaseEditorAction {
		
	public CopyAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected void init() {
		super.init();
		setId(ActionFactory.COPY.getId());
		setText("Copy");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages(); 
		setImageDescriptor(sharedImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_COPY_DISABLED));
		setEnabled(false);
	}
	
	@Override
	protected boolean calculateEnabled() {
		if (getParts() == null) {
			return false;
		}
		else if(getParts().size() == 1 && getParts().get(0) instanceof AbstractConnectionEditPart) {
			return false;
		}
		else if (getParts().size() == 1 && getParts().get(0) instanceof PropertyChangePart) {
			return ((PropertyChangePart) getParts().get(0)).isCopyable();
		}
		else {
			return true;
		}
	}
	
	@Override
	public void run() {
		List<EditPart> newClips = ViewUtil.getPartsForClipboard(getParts());
		Clipboard.getDefault().setContents(newClips);
	}


	
	

}
