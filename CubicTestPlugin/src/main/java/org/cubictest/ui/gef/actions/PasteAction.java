/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;


/**
 * Action for pasting from clipboard.
 * 
 * @author Christian Schwarz
 */
public class PasteAction extends SelectionAction {
	
	/**
	 * The Actions ID which can be used to set and get it from acion registers.
	 */
	public static final String ACTION_ID = "cubicTestPlugin.action.present";
	
	public PasteAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		Object selectionObj = getSelection();
		if (selectionObj == null || !(selectionObj instanceof StructuredSelection)){
			return false;
		}
		
		StructuredSelection selection = (StructuredSelection) selectionObj;
		if (!(selection.getFirstElement() instanceof EditPart)) {
			return false;
		}
		return true;
	}
	
	@Override
	protected void init(){
		setId(ActionFactory.PASTE.getId());
		setText("Paste");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages(); 
		setImageDescriptor(sharedImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setEnabled(false);
	}
	
	@Override
	public void run() {
		
		try {
			Test test = ViewUtil.getSurroundingTest(getTargetPart());
			ViewUtil.cloneAndAddNodesToTest(test, getNodesOnClipboard(), getCommandStack(), true);
			ViewUtil.cloneAndAddPageElementsToTarget(getTargetPart(), getAllClipboardParts());
		}		
		catch (CloneNotSupportedException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		
	}
	
	
	private List<TransitionNode> getNodesOnClipboard() {
		List<TransitionNode> result = new ArrayList<TransitionNode>();

		for (EditPart part : getAllClipboardParts()) {
			if (part.getModel() instanceof TransitionNode) {
				result.add((TransitionNode) part.getModel());
			}
		}
		return result;
	}
	

	private List<EditPart> getAllClipboardParts() {
		List<EditPart> result = new ArrayList<EditPart>();
		
		Object clipContents = Clipboard.getDefault().getContents();
		if (clipContents instanceof List) {
			List clipboardList = (List) clipContents;
	
			for (Iterator iter = clipboardList.iterator(); iter.hasNext();){
				Object currentClip = iter.next();
				if(currentClip instanceof EditPart) {
					result.add((EditPart) currentClip);		
				}
			}
		}
		return result;
	}
	
	
	private EditPart getTargetPart() {
		StructuredSelection selection = (StructuredSelection) getSelection();
		Object selectedObj = selection.getFirstElement();
		EditPart targetPart = (EditPart) selectedObj;
		return targetPart;
	}



}
