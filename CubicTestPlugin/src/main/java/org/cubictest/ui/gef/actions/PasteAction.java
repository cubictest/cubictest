/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.command.MovePageCommand;
import org.cubictest.ui.gef.controller.PropertyChangePart;
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
		
		EditPart targetPart = getTargetPart();
		CompoundCommand compoundCmd = new CompoundCommand();
		List<AbstractPage> clipboardPages = getPagesOnClipboard();
		Map<AbstractPage, AbstractPage> clonedClipboardPages = new HashMap<AbstractPage, AbstractPage>();
		
		Test test = ViewUtil.getSurroundingTest(targetPart);

		try {
			for (EditPart clipboardPart : getClipboardParts()) {
				if (clipboardPart.getModel() instanceof ExtensionStartPoint || clipboardPart.getModel() instanceof UrlStartPoint) {
					continue;
				}
				else if (clipboardPart.getModel() instanceof PageElement && !ViewUtil.parentPageIsOnClipboard(clipboardPart, getClipboardParts())) {
					PageElement clipboardElement = (PageElement) clipboardPart.getModel();
					if (targetPart.getModel() instanceof PageElement && !(targetPart.getModel() instanceof AbstractContext))
						targetPart = targetPart.getParent();
					if (targetPart.getModel() instanceof IContext){
						try {
							CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
							createElementCmd.setContext((IContext)targetPart.getModel());
							createElementCmd.setPageElement((PageElement) clipboardElement.clone());
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
	
					AbstractPage pageClone = (AbstractPage) page.clone();
					AddAbstractPageCommand pageAddCommand = new AddAbstractPageCommand();
					pageAddCommand.setTest(test);
					pageAddCommand.setPage(pageClone);
					compoundCmd.add(pageAddCommand);
					
					MovePageCommand moveCmd = new MovePageCommand();
					moveCmd.setPage(pageClone);
					moveCmd.setOldPosition(page.getPosition());
					moveCmd.setNewPosition(new Point(page.getPosition().x + 180, page.getPosition().y));
					compoundCmd.add(moveCmd);
					clonedClipboardPages.put(page, pageClone);
							
				}
			} //end foreach
		
		
			//cloning transitions if needed
			for (AbstractPage page : clipboardPages) {
				if (sourceFromInTransitionIsOnClipboard(clipboardPages, page)) {
					
					Transition transClone = (Transition) page.getInTransition().clone();
					transClone.setStart(clonedClipboardPages.get(page.getInTransition().getStart()));
					transClone.setEnd(clonedClipboardPages.get(page));
					
					CreateTransitionCommand transCmd = new CreateTransitionCommand();
					transCmd.setAutoCreateTargetPage(false);
					transCmd.setSource(clonedClipboardPages.get(page.getInTransition().getStart()));
					transCmd.setTarget(clonedClipboardPages.get(page));
					transCmd.setTest(test);
					transCmd.setTransition(transClone);
					compoundCmd.add(transCmd);
				}
			}
		}		
		catch (CloneNotSupportedException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		
		getCommandStack().execute(compoundCmd);
	}

	private boolean sourceFromInTransitionIsOnClipboard(List<AbstractPage> clipboardPages, AbstractPage page) {
		if (page.getInTransition() != null) {
			return clipboardPages.contains(page.getInTransition().getStart());
		}
		return false;
	}

	
	private List<AbstractPage> getPagesOnClipboard() {
		List<AbstractPage> result = new ArrayList<AbstractPage>();

		for (EditPart part : getClipboardParts()) {
			if (part.getModel() instanceof AbstractPage) {
				result.add((AbstractPage) part.getModel());
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


	private Object getClipboardContents() {
		return Clipboard.getDefault().getContents();
	}
	
	
	private List<EditPart> getClipboardParts() {
		List<EditPart> result = new ArrayList<EditPart>();
		
		Object clipContents = getClipboardContents();
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

}
