/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.Common;
import org.cubictest.model.CustomTestStepHolder;
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
import org.cubictest.ui.gef.command.DeleteCommonCommand;
import org.cubictest.ui.gef.command.DeleteCustomTestStepCommand;
import org.cubictest.ui.gef.command.DeleteExtensionPointCommand;
import org.cubictest.ui.gef.command.DeletePageCommand;
import org.cubictest.ui.gef.command.DeletePageElementCommand;
import org.cubictest.ui.gef.command.DeleteSubTestCommand;
import org.cubictest.ui.gef.command.MoveNodeCommand;
import org.cubictest.ui.gef.command.PageResizeCommand;
import org.cubictest.ui.gef.controller.AbstractPageEditPart;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;


/**
 * Util class for view layer.
 * 
 * @author chr_schwarz
 */
public class ViewUtil {

	public static final String ADD = "add";
	public static final String REMOVE = "remove";
	public static final int LABEL_HEIGHT = 22;

	
	/**
	 * Opens file for viewing.
	 * @param filePath the file to open.
	 */
	public static void openFileForViewing(String filePath) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(filePath));
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditor(page, ResourceUtil.getFile(resource), true);
		} catch (PartInitException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error opening file", e);
		}
	}
	
	/**
	 * Get whether the page element has just been created.
	 * @param stack
	 * @param pageElement
	 * @return
	 */
	public static boolean pageElementHasJustBeenCreated(CommandStack stack, PageElement pageElement) {
		boolean isCreatePageElementCommand = false;
		PageElement cmdPageElement = null;
		
		Command undoCommand = stack.getUndoCommand();
		if (undoCommand instanceof CompoundCommand) {
			for (Object command : ((CompoundCommand) undoCommand).getCommands()) {
				if (command instanceof CreatePageElementCommand) {
					isCreatePageElementCommand = true;
					cmdPageElement = ((CreatePageElementCommand)command).getPageElement();
					break;
				}
			}
		}
		else
		{
			isCreatePageElementCommand = undoCommand instanceof CreatePageElementCommand;
			if (isCreatePageElementCommand) {
				cmdPageElement = ((CreatePageElementCommand)undoCommand).getPageElement();
			}
		}
		return (stack.isDirty() && isCreatePageElementCommand && cmdPageElement.equals(pageElement));
	}
	
	/**
	 * Get whether the page has just been created.
	 */
	public static boolean pageHasJustBeenCreated(CommandStack stack, AbstractPage page) {
		Command undoCommand = stack.getUndoCommand();
		
		if (undoCommand instanceof AddAbstractPageCommand) {
			return stack.isDirty() && ((AddAbstractPageCommand) stack.getUndoCommand()).getPage().equals(page);
		}
		else if (undoCommand instanceof CreateTransitionCommand && ((CreateTransitionCommand) undoCommand).isAutoCreateTargetPage()) {
			return stack.isDirty() && ((CreateTransitionCommand) stack.getUndoCommand()).getTarget().equals(page);
		}
		return false;
	}
	
	
	public static FlowLayout getFlowLayout() {
		FlowLayout layout = new FlowLayout();
		layout.setMinorAlignment(FlowLayout.ALIGN_LEFTTOP);
		layout.setStretchMinorAxis(false);
		layout.setHorizontal(false);
		return layout;
		
	}
	
	
	public static Command getCompoundCommandWithResize(Command originatingCommand, String mode, EditPart host) {
		
		//get surrounding page:
		AbstractPage page = getSurroundingPage(host);
		
		int height = page.getDimension().height;
		int width = page.getDimension().width;
		
		int elementsHeight = (getElementHeight((IContext) page) + LABEL_HEIGHT);
		
		int num = 1;
		boolean isContext = false;
		if (originatingCommand instanceof CreatePageElementCommand) {
			PageElement pe  = ((CreatePageElementCommand) originatingCommand).getPageElement();
			if (pe instanceof IContext) {
				num = ((IContext) pe).getRootElements().size() + 1;
				isContext = true;
			}
		}
		
		int heightToAdd;
		if (isContext) {
			heightToAdd = (LABEL_HEIGHT + 2) * num;
		}
		else {
			heightToAdd = LABEL_HEIGHT * num;
		}

		if(height > (elementsHeight + LABEL_HEIGHT)) {
			//there are leftover height
			heightToAdd = 0;
		} else if (height + LABEL_HEIGHT < elementsHeight){
			//probably more than one column
			heightToAdd = 0;
		}
		
		PageResizeCommand resizeCmd = new PageResizeCommand();
		resizeCmd.setNode(page);
		resizeCmd.setOldDimension(new Dimension(width, height));

		if (mode.equals(ADD))
			resizeCmd.setNewDimension(new Dimension(width, height + heightToAdd));
		else
			resizeCmd.setNewDimension(new Dimension(width, height - LABEL_HEIGHT));
			
		CompoundCommand compoundCmd = new CompoundCommand();
		compoundCmd.add(originatingCommand);
		compoundCmd.add(resizeCmd);
		return compoundCmd;
	}

	private static int getElementHeight(IContext node) {
		List<PageElement> elements = ((IContext) node).getRootElements();
		int height = elements.size() * LABEL_HEIGHT; 
		for (PageElement element : elements) {
			if (element instanceof IContext) {
				height = height + 2 + getElementHeight((IContext) element);
			}
		}
		return height;
	}

	public static AbstractPage getSurroundingPage(EditPart host) {
		AbstractPage node = null;
		int i = 0;
		while(i < 42) {
			if (host.getModel() instanceof AbstractPage) {
				node = (AbstractPage) host.getModel();
				break;
			}
			host = host.getParent();
			i++;
		}
		return node;
	}

	public static Test getSurroundingTest(EditPart host) {
		Test test = null;
		int i = 0;
		while(i < 42) {
			if (host.getModel() instanceof Test) {
				test = (Test) host.getModel();
				break;
			}
			host = host.getParent();
			i++;
		}
		return test;
	}
	
	
	public static List<TransitionNode> cloneAndAddNodesToTest(Test test, List<TransitionNode> originalNodes, 
			CommandStack commandStack, boolean moveClonesToRight) throws CloneNotSupportedException {
		
		Map<TransitionNode, TransitionNode> clonedNodesForAddition = new HashMap<TransitionNode, TransitionNode>();

		CompoundCommand compoundCmd = new CompoundCommand();
		for (TransitionNode originalNode : originalNodes) {
			if (originalNode instanceof UrlStartPoint || originalNode instanceof ExtensionStartPoint) {
				//Start points should not be copy/pasted
				continue;
			}
			else if (originalNode instanceof AbstractPage){
				//Pages and commons
				AbstractPage page = (AbstractPage) originalNode;

				AbstractPage pageClone = (AbstractPage) page.clone();
				AddAbstractPageCommand pageAddCommand = new AddAbstractPageCommand();
				pageAddCommand.setTest(test);
				pageAddCommand.setPage(pageClone);
				compoundCmd.add(pageAddCommand);
				
				if (moveClonesToRight)
					addMoveNodeCommand(compoundCmd, page, pageClone);
				
				clonedNodesForAddition.put(page, pageClone);
						
			}
			else if (originalNode instanceof ExtensionPoint){
				//ExtensionPoint
				ExtensionPoint exPoint = (ExtensionPoint) originalNode;

				ExtensionPoint exPointClone = (ExtensionPoint) exPoint.clone();
				AddExtensionPointCommand addExCommand = new AddExtensionPointCommand();
				addExCommand.setTest(test);
				addExCommand.setExtensionPoint(exPointClone);
				compoundCmd.add(addExCommand);
				clonedNodesForAddition.put(exPoint, exPointClone);
				
				if (moveClonesToRight)
					addMoveNodeCommand(compoundCmd, exPoint, exPointClone);
			}
			else if (originalNode instanceof SubTest){
				//SubTest
				SubTest subtest = (SubTest) originalNode;

				SubTest subtestClone = (SubTest) subtest.clone();
				AddSubTestCommand addSubTestCmd = new AddSubTestCommand();
				addSubTestCmd.setTest(test);
				addSubTestCmd.setSubTest(subtestClone);
				compoundCmd.add(addSubTestCmd);
				clonedNodesForAddition.put(subtest, subtestClone);
				
				if (moveClonesToRight)
					addMoveNodeCommand(compoundCmd, subtest, subtestClone);
			}
		} //end foreach


		//cloning transitions if needed
		for (TransitionNode node : originalNodes) {
			if (sourceFromInTransitionIsOnClipboard(originalNodes, node)) {
				
				Transition transClone = (Transition) node.getInTransition().clone();
				TransitionNode sourceClone = clonedNodesForAddition.get(node.getInTransition().getStart());
				transClone.setStart(sourceClone);
				transClone.setEnd(clonedNodesForAddition.get(node));
				
				if (transClone instanceof UserInteractionsTransition) {
					
					//we need to connect the action elements back to the clone's elements
					for (UserInteraction action : ((UserInteractionsTransition) transClone).getUserInteractions()) {
						IActionElement actionElement = action.getElement();
						if (actionElement instanceof PageElement) {
							boolean elementFound = false;
							for (PageElement pe : UserInteractionDialogUtil.getFlattenedPageElements(((AbstractPage) sourceClone).getRootElements())) {
								if (pe.isEqualTo(actionElement)) {
									action.setElement(pe);
									elementFound = true;
								}
							}
							if (!elementFound) {
								ErrorHandler.logAndShowErrorDialogAndThrow("Unable to copy User Interactions from page \"" + sourceClone.getName() +
										"\". Check that it does not have user interaction elements from a Common.\n\n" +
										"Paste operation cancelled.");
							}
						}
					}
				}
				
				CreateTransitionCommand transCmd = new CreateTransitionCommand();
				transCmd.setAutoCreateTargetPage(false);
				transCmd.setSource(clonedNodesForAddition.get(node.getInTransition().getStart()));
				transCmd.setTarget(clonedNodesForAddition.get(node));
				transCmd.setTest(test);
				transCmd.setTransition(transClone);
				if (transCmd.canExecute()) {
					compoundCmd.add(transCmd);
				}
			}
		}
		commandStack.execute(compoundCmd);
		return new ArrayList<TransitionNode>(clonedNodesForAddition.values());
	}
	
	
	public static void cloneAndAddPageElementsToTarget(EditPart targetPart, List<EditPart> allClipboardParts) throws CloneNotSupportedException {
		CompoundCommand compoundCmd = new CompoundCommand();
		for (EditPart clipboardPart : allClipboardParts) {
			if (clipboardPart.getModel() instanceof PageElement && !ViewUtil.parentPageIsOnClipboard(clipboardPart, allClipboardParts)) {
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
		}
		getCommandStackFromActivePage().execute(compoundCmd);
	}
	
	
	private static MoveNodeCommand addMoveNodeCommand(CompoundCommand compoundCmd, TransitionNode node, TransitionNode nodeClone) {
		MoveNodeCommand moveCmd = new MoveNodeCommand();
		moveCmd.setNode(nodeClone);
		moveCmd.setOldPosition(node.getPosition());
		moveCmd.setNewPosition(new Point(node.getPosition().x + 250, node.getPosition().y));
		compoundCmd.add(moveCmd);
		return moveCmd;
	}

	private static boolean sourceFromInTransitionIsOnClipboard(List<TransitionNode> clipboardPages, TransitionNode page) {
		if (page.getInTransition() != null) {
			return clipboardPages.contains(page.getInTransition().getStart());
		}
		return false;
	}

	
	

	
	/**
	 * Get the AbstractPage edit part surronding an PropertyChangePart.
	 * @param editPart
	 * @return
	 */
	public static AbstractPageEditPart getSurroundingPagePart(EditPart editPart) {
		if (!(editPart instanceof PropertyChangePart)) {
			return null;
		}
		
		if (editPart instanceof TestEditPart) {
			return null;
		}
		if (editPart instanceof AbstractPageEditPart) {
			return (AbstractPageEditPart) editPart;
		}
		
		editPart = (PropertyChangePart) editPart.getParent();
		
		while (!(editPart.getModel() instanceof AbstractPage)) {
			editPart = (PropertyChangePart) editPart.getParent();
		}
		
		
		if (!(editPart instanceof AbstractPageEditPart)) {
			throw new CubicException("Did not find surronding AbstractPage of edit part: " + editPart);
		}
		
		return (AbstractPageEditPart) editPart;
		
	}
	
	
	/**
	 * Get the TestEditPart edit part surronding an PropertyChangePart.
	 * @param editPart
	 * @return
	 */
	public static TestEditPart getSurroundingTestPart(EditPart editPart) {
		if (editPart == null) {
			Logger.warn("Could not get test part. Supplied EditPart was null");
			return null;
		}
		if (editPart instanceof TestEditPart) {
			return (TestEditPart) editPart;
		}
		
		editPart = (EditPart) editPart.getParent();
		
		while (editPart != null && !(editPart instanceof TestEditPart)) {
			editPart = (EditPart) editPart.getParent();
		}
		
		if (!(editPart instanceof TestEditPart)) {
			throw new CubicException("Did not find surronding TestEditPart for edit part: " + editPart);
		}
		
		return (TestEditPart) editPart;
	}
	
	public static boolean parentPageIsOnClipboard(EditPart clipboardPart, List clipboardList) {
		EditPart parent = clipboardPart.getParent();
		while(parent != null && parent.getModel() instanceof PageElement) {
			parent = parent.getParent();
		}
		return clipboardList.contains(parent);
	}
	
	public static List<EditPart> getPartsForClipboard(List<Object> parts) {
		List<EditPart> newClips = new ArrayList<EditPart>();
		
		for (Iterator<Object> iter = parts.iterator(); iter.hasNext();){
			Object obj = iter.next();
			if(!(obj instanceof EditPart)) {
				continue;
			}

			EditPart part = (EditPart) obj;			
			if (part.getModel() instanceof PageElement) {
				if (ViewUtil.parentPageIsOnClipboard(part, parts)) {
					continue;
				}
				else if (containsAPage(parts)) {
					//Clip is a "loose element" and clipboard contains other pages
					//Skip element (not mix loose elements and other pages)
					continue;
				}
			}
			newClips.add(part);
		}
		return newClips;
	}
	
	public static boolean containsAPage(List parts) {
		for (Iterator iter = parts.iterator(); iter.hasNext();){
			Object selected = iter.next();
			if (selected instanceof AbstractPageEditPart || selected instanceof AbstractPage) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean containsAPageElement(Object o) {
		if (o instanceof PageElementEditPart || o instanceof PageElement)
			return true;
		
		if (o instanceof List) {
			List parts = (List) o;
			for (Iterator iter = parts.iterator(); iter.hasNext();){
				Object selected = iter.next();
				if (selected instanceof PageElementEditPart || selected instanceof PageElement) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static IProject getProjectFromActivePage() {
		IProject project = null;
		IEditorPart editorPart = CubicTestPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editorPart != null && editorPart instanceof ITestEditor) {
			project = ((ITestEditor) editorPart).getProject();
		}
		return project;
	}
	
	public static CommandStack getCommandStackFromActivePage() {
		IEditorPart editorPart = CubicTestPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editorPart != null && editorPart instanceof ITestEditor) {
			return ((ITestEditor) editorPart).getCommandStack();
		}
		else {
			return null;
		}
	}
	
	
	public static void deleteParts(List<EditPart> parts, CommandStack commandStack) {
		
		CompoundCommand compoundCmd = new CompoundCommand();

		for (EditPart part : parts) {
			if (part.getModel() instanceof PageElement) {
				DeletePageElementCommand deleteCmd = new DeletePageElementCommand();
				deleteCmd.setElementParent((IContext) part.getParent().getModel());
				deleteCmd.setElement((PageElement) part.getModel());
				deleteCmd.setPage(ViewUtil.getSurroundingPage(part));
				compoundCmd.add(deleteCmd);
			}
			else if (part.getModel() instanceof Page) {
				DeletePageCommand deleteCmd = new DeletePageCommand();
				deleteCmd.setTest((Test) part.getParent().getModel());
				deleteCmd.setTransitionNode((TransitionNode) part.getModel());
				compoundCmd.add(deleteCmd);
			}
			else if (part.getModel() instanceof CustomTestStepHolder) {
				DeleteCustomTestStepCommand deleteCmd = new DeleteCustomTestStepCommand();
				deleteCmd.setTest((Test) part.getParent().getModel());
				deleteCmd.setTransitionNode((TransitionNode) part.getModel());
				compoundCmd.add(deleteCmd);
			}
			else if (part.getModel() instanceof ExtensionPoint) {
				DeleteExtensionPointCommand deleteCmd = new DeleteExtensionPointCommand();
				deleteCmd.setTest((Test) part.getParent().getModel());
				deleteCmd.setTransitionNode((TransitionNode) part.getModel());
				compoundCmd.add(deleteCmd);
			}
			else if (part.getModel() instanceof SubTest) {
				DeleteSubTestCommand deleteCmd = new DeleteSubTestCommand();
				deleteCmd.setTest((Test) part.getParent().getModel());
				deleteCmd.setTransitionNode((TransitionNode) part.getModel());
				compoundCmd.add(deleteCmd);
			}
			else if (part.getModel() instanceof Common) {
				DeleteCommonCommand deleteCmd = new DeleteCommonCommand();
				deleteCmd.setTest((Test) part.getParent().getModel());
				deleteCmd.setTransitionNode((AbstractPage) part.getModel());
				compoundCmd.add(deleteCmd);
			}
		}
		commandStack.execute(compoundCmd);
	}
}
