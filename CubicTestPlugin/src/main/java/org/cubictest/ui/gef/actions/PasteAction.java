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
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.AddExtensionPointCommand;
import org.cubictest.ui.gef.command.AddSubTestCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.command.MoveNodeCommand;
import org.cubictest.ui.utils.UserInteractionDialogUtil;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CompoundCommand;
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
		List<TransitionNode> clipboardNodes = getNodesOnClipboard();
		Map<TransitionNode, TransitionNode> clonedClipboardNodes = new HashMap<TransitionNode, TransitionNode>();
		
		Test test = ViewUtil.getSurroundingTest(targetPart);

		try {
			for (EditPart clipboardPart : getClipboardParts()) {
				if (clipboardPart.getModel() instanceof UrlStartPoint || clipboardPart.getModel() instanceof ExtensionStartPoint) {
					//Start points should not be pasted
					continue;
				}
				else if (clipboardPart.getModel() instanceof PageElement && !ViewUtil.parentPageIsOnClipboard(clipboardPart, getClipboardParts())) {
					//Page elements
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
					//Pages and commons
					AbstractPage page = (AbstractPage) clipboardPart.getModel();
	
					AbstractPage pageClone = (AbstractPage) page.clone();
					AddAbstractPageCommand pageAddCommand = new AddAbstractPageCommand();
					pageAddCommand.setTest(test);
					pageAddCommand.setPage(pageClone);
					compoundCmd.add(pageAddCommand);
					
					addMoveNodeCommand(compoundCmd, page, pageClone);
					clonedClipboardNodes.put(page, pageClone);
							
				}
				else if (clipboardPart.getModel() instanceof ExtensionPoint){
					//ExtensionPoint
					ExtensionPoint exPoint = (ExtensionPoint) clipboardPart.getModel();
	
					ExtensionPoint exPointClone = (ExtensionPoint) exPoint.clone();
					AddExtensionPointCommand addExCommand = new AddExtensionPointCommand();
					addExCommand.setTest(test);
					addExCommand.setExtensionPoint(exPointClone);
					compoundCmd.add(addExCommand);
					clonedClipboardNodes.put(exPoint, exPointClone);
					
					addMoveNodeCommand(compoundCmd, exPoint, exPointClone);
				}
				else if (clipboardPart.getModel() instanceof SubTest){
					//SubTest
					SubTest subtest = (SubTest) clipboardPart.getModel();
	
					SubTest subtestClone = (SubTest) subtest.clone();
					AddSubTestCommand addSubTestCmd = new AddSubTestCommand();
					addSubTestCmd.setTest(test);
					addSubTestCmd.setSubTest(subtestClone);
					compoundCmd.add(addSubTestCmd);
					clonedClipboardNodes.put(subtest, subtestClone);
					
					addMoveNodeCommand(compoundCmd, subtest, subtestClone);
				}
			} //end foreach
		
		
			//cloning transitions if needed
			for (TransitionNode node : clipboardNodes) {
				if (sourceFromInTransitionIsOnClipboard(clipboardNodes, node)) {
					
					Transition transClone = (Transition) node.getInTransition().clone();
					TransitionNode sourceClone = clonedClipboardNodes.get(node.getInTransition().getStart());
					transClone.setStart(sourceClone);
					transClone.setEnd(clonedClipboardNodes.get(node));
					
					if (transClone instanceof UserInteractionsTransition) {
						
						//we need to connect the action elements back to the clone's elements
						for (UserInteraction action : ((UserInteractionsTransition) transClone).getUserInteractions()) {
							IActionElement actionElement = action.getElement();
							if (actionElement instanceof PageElement) {
								boolean elementFound = false;
								for (PageElement pe : UserInteractionDialogUtil.getFlattenedPageElements(((AbstractPage) sourceClone).getElements())) {
									if (pe.isEqualTo(actionElement)) {
										action.setElement(pe);
										elementFound = true;
									}
								}
								if (!elementFound) {
									ErrorHandler.showErrorDialog("Unable to copy User Interactions from page \"" + sourceClone.getName() +
											"\". Check that it does not have user interaction elements from a Common.\n\n" +
											"Paste operation cancelled.");
									return;
								}
							}
						}
					}
					
					CreateTransitionCommand transCmd = new CreateTransitionCommand();
					transCmd.setAutoCreateTargetPage(false);
					transCmd.setSource(clonedClipboardNodes.get(node.getInTransition().getStart()));
					transCmd.setTarget(clonedClipboardNodes.get(node));
					transCmd.setTest(test);
					transCmd.setTransition(transClone);
					if (transCmd.canExecute()) {
						compoundCmd.add(transCmd);
					}
				}
			}
		}		
		catch (CloneNotSupportedException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		
		getCommandStack().execute(compoundCmd);
	}

	private MoveNodeCommand addMoveNodeCommand(CompoundCommand compoundCmd, TransitionNode node, TransitionNode nodeClone) {
		MoveNodeCommand moveCmd = new MoveNodeCommand();
		moveCmd.setNode(nodeClone);
		moveCmd.setOldPosition(node.getPosition());
		moveCmd.setNewPosition(new Point(node.getPosition().x + 250, node.getPosition().y));
		compoundCmd.add(moveCmd);
		return moveCmd;
	}

	private boolean sourceFromInTransitionIsOnClipboard(List<TransitionNode> clipboardPages, TransitionNode page) {
		if (page.getInTransition() != null) {
			return clipboardPages.contains(page.getInTransition().getStart());
		}
		return false;
	}

	
	private List<TransitionNode> getNodesOnClipboard() {
		List<TransitionNode> result = new ArrayList<TransitionNode>();

		for (EditPart part : getClipboardParts()) {
			if (part.getModel() instanceof TransitionNode) {
				result.add((TransitionNode) part.getModel());
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
