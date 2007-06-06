/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.actions;

import java.util.List;

import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
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
 * Action for copying selected test elements to the clipboard. 
 * 
 * @author chr_schwarz
 */
public class CopyAction extends SelectionAction{
	
	private List parts = null;
	
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
		if (parts == null) {
			return false;
		}
		else if(parts.size() == 1 && parts.get(0) instanceof AbstractConnectionEditPart) {
			return false;
		}
		else if (parts.size() == 1 && parts.get(0) instanceof PropertyChangePart) {
			return ((PropertyChangePart) parts.get(0)).isCopyable();
		}
		else {
			return true;
		}
	}
	
	@Override
	public void run() {
		List<EditPart> newClips = ViewUtil.getPartsForClipboard(parts);
		Clipboard.getDefault().setContents(newClips);
	}


	
	

}
