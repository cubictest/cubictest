/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.Iterator;
import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.MovePageCommand;
import org.cubictest.ui.gef.controller.ExtensionStartPointEditPart;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.cubictest.ui.gef.controller.UrlStartPointEditPart;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
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
 * @author chr_schwarz
 *
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
		StructuredSelection selection = (StructuredSelection) getSelection();
		if (selection.size() > 1)
			return false;
		
		if (!(selection.getFirstElement() instanceof EditPart)) {
			return false;
		}

		EditPart selectedPart = (EditPart) selection.getFirstElement();

		if(selectedPart instanceof AbstractConnectionEditPart ||
				selectedPart instanceof PropertyChangePart && !((PropertyChangePart) selectedPart).canBeTargetForPaste() ||
				!(selectedPart.getModel() instanceof IContext) && ViewUtil.containsAPageElement(getClipboardContents())) {
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
		Object clipContents = getClipboardContents();
		if (!(clipContents instanceof List)) {
			Logger.warn("Clipboard contents was not a list: " + clipContents);
			return;
		}
		List clipboardList = (List) clipContents;
		
		StructuredSelection selection = (StructuredSelection) getSelection();
		Object selectedObj = selection.getFirstElement();
		EditPart targetPart = (EditPart) selectedObj;
		
		CompoundCommand compoundCmd = new CompoundCommand();
		
		for (Iterator iter = clipboardList.iterator(); iter.hasNext();){
			Object currentClip = iter.next();
			if(!(currentClip instanceof EditPart)) {
				Logger.warn("Clipboard item was not an editpart: " + currentClip);
				return;
			}
			EditPart clipboardPart = (EditPart) currentClip;			
			
			if (clipboardPart.getModel() instanceof ExtensionStartPoint || clipboardPart.getModel() instanceof UrlStartPoint) {
				continue;
			}
			else if (clipboardPart.getModel() instanceof PageElement && !ViewUtil.parentPageIsOnClipboard(clipboardPart, clipboardList)) {
				PageElement clipboardElement = (PageElement) clipboardPart.getModel();
				if (targetPart.getModel() instanceof PageElement && !(targetPart.getModel() instanceof AbstractContext))
					targetPart = targetPart.getParent();
				if (targetPart.getModel() instanceof IContext){
					try {
						CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
						createElementCmd.setContext((IContext)targetPart.getModel());
						createElementCmd.setPageElement(clipboardElement.clone());
						createElementCmd.setIndex(targetPart.getChildren().size());
						compoundCmd.add(ViewUtil.getCompoundCommandWithResize(createElementCmd, ViewUtil.ADD, targetPart));
					} catch (CloneNotSupportedException e) {
						ErrorHandler.logAndShowErrorDialogAndRethrow(e);
					}
				}
				else {
					Logger.warn("Parent of page element was not an IContext. Skipping it: " + targetPart.getModel());
				}
				
			}
			else if (clipboardPart.getModel() instanceof AbstractPage){
				AbstractPage page = (AbstractPage) clipboardPart.getModel();
				while (!(targetPart.getModel() instanceof Test))
					targetPart = targetPart.getParent();
				if (targetPart.getModel() instanceof Test){
					try {
						AbstractPage clone = page.clone();
						AddAbstractPageCommand pageAddCommand = new AddAbstractPageCommand();
						pageAddCommand.setTest((Test) targetPart.getModel());
						pageAddCommand.setPage(clone);
						compoundCmd.add(pageAddCommand);
						MovePageCommand moveCmd = new MovePageCommand();
						moveCmd.setPage(clone);
						moveCmd.setOldPosition(page.getPosition());
						moveCmd.setNewPosition(new Point(page.getPosition().x + 25, page.getPosition().y + 25));
						compoundCmd.add(moveCmd);
					} catch (CloneNotSupportedException e) {
						ErrorHandler.logAndShowErrorDialogAndRethrow(e);
					}
				}
				else {
					Logger.warn("Parent of selected item did not have path to test. Skipping it: " + targetPart.getModel());
				}
				
			}
		}
		getCommandStack().execute(compoundCmd);
	}


	private Object getClipboardContents() {
		return Clipboard.getDefault().getContents();
	}
	

}
