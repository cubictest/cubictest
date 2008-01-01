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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action for cutting elements.
 * 
 * @author Christian Schwarz
 */
public class CutAction extends BaseEditorAction {
	
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
		if (getParts() == null) {
			return false;
		}
		else if(getParts().size() == 1 && getParts().get(0) instanceof AbstractConnectionEditPart) {
			return false;
		}
		else if (getParts().size() == 1 && getParts().get(0) instanceof PropertyChangePart) {
			return ((PropertyChangePart) getParts().get(0)).isCuttable();
		}
		else {
			return true;
		}
	}
	
	@Override
	public void run() {
		List<EditPart> newClips = ViewUtil.getPartsForClipboard(getParts());
		Clipboard.getDefault().setContents(newClips);
		ViewUtil.deleteParts(newClips, ViewUtil.getCommandStackFromActivePage());
	}
}
