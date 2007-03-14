/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cubictest.common.exception.CubicException;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.PageResizeCommand;
import org.cubictest.ui.gef.controller.AbstractPageEditPart;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;


/**
 * Util class for view layer.
 * 
 * @author chr_schwarz
 */
public class ViewUtil {

	public static final String ADD = "add";
	public static final String REMOVE = "remove";
	public static final int LABEL_HEIGHT = 21;

	
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
	 * @param stack
	 * @param pageElement
	 * @return
	 */
	public static boolean pageHasJustBeenCreated(CommandStack stack, AbstractPage page) {
		return stack.isDirty() && 
			stack.getUndoCommand() instanceof AddAbstractPageCommand &&
			((AddAbstractPageCommand)stack.getUndoCommand()).getPage().equals(page);
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
		
		int heightToAdd = LABEL_HEIGHT;

		if(height > (elementsHeight + LABEL_HEIGHT)) {
			//there are leftover height
			heightToAdd = 0;
		} else if (height + LABEL_HEIGHT < elementsHeight){
			//probably more than one column
			heightToAdd = 0;
		}
		
		PageResizeCommand resizeCmd = new PageResizeCommand();
		resizeCmd.setPage(page);
		resizeCmd.setOldDimension(new Dimension(width, height));

		if (mode.equals(ADD))
			resizeCmd.setNewDimension(new Dimension(width, height + heightToAdd));
		else
			resizeCmd.setNewDimension(new Dimension(width, height - LABEL_HEIGHT));
			
		CompoundCommand compoundCmd = new CompoundCommand();
		compoundCmd.add(resizeCmd);
		compoundCmd.add(originatingCommand);
		return compoundCmd;
	}

	private static int getElementHeight(IContext node) {
		List<PageElement> elements = ((IContext) node).getElements();
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
	
	/**
	 * Get the AbstractPage model object surronding an PropertyChangePart.
	 * @param editPart
	 * @return
	 */
	public static AbstractPageEditPart getSurroundingPagePart(PropertyChangePart editPart) {
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
	
	
	public static boolean parentPageIsOnClipboard(EditPart clipboardPart, List clipboardList) {
		EditPart parent = clipboardPart.getParent();
		while(parent != null && parent.getModel() instanceof PageElement) {
			parent = parent.getParent();
		}
		return clipboardList.contains(parent);
	}
	
	public static List<EditPart> getPartsForClipboard(List parts) {
		List<EditPart> newClips = new ArrayList<EditPart>();
		
		for (Iterator iter = parts.iterator(); iter.hasNext();){
			Object selected = iter.next();
			if(!(selected instanceof EditPart)) {
				continue;
			}

			EditPart part = (EditPart) selected;			
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
}
