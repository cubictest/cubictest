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

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.view.AddElementContextMenuList;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action for adding page elements from context menu of IContext (e.g. Page).
 *  
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class AddPageElementAction extends SelectionAction {

	private Object selectedObj;
	private Class<? extends PageElement> actionPageElementClass;

	public AddPageElementAction(IWorkbenchPart part,Class<? extends PageElement> newElement) {
		super(part);
		this.actionPageElementClass = newElement;
		setActionText();
	}

	/**
	 * The Actions ID which can be used to set and get it from acion registers.
	 */
	public static String getActionId(Class<? extends PageElement> element) {
		return "cubicTestPlugin.action.add" + AddElementContextMenuList.getType(element); 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		if(selectedObj instanceof EditPart){
			Object selectedModel = ((EditPart)selectedObj).getModel();
			if (selectedModel instanceof Select) {
				return actionPageElementClass.equals(Option.class);
			}
			else if (actionPageElementClass.equals(Option.class)) {
				return selectedModel instanceof Select;
			}
			else if(selectedModel instanceof IContext || selectedModel instanceof PageElement){
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
	 */
	protected void setActionText() {
		try {
			setText("Add new " + actionPageElementClass.newInstance().getType());
		} catch (InstantiationException e) {
			Logger.error("Could not set action text.", e);
		} catch (IllegalAccessException e) {
			Logger.error("Could not set action text.", e);
		}
    	setId(getActionId(actionPageElementClass));
	}

	@Override
	public void runWithEvent(Event event){
		if(selectedObj instanceof EditPart){
			EditPart editPart = (EditPart)selectedObj;
			CreatePageElementCommand command = new CreatePageElementCommand();
			PageElement pe;
			try {
				if(actionPageElementClass == null)
					return;
				pe = actionPageElementClass.newInstance();
			} catch (Exception e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow("Problems with adding the new pageElement",e);
				return;
			}
			command.setPageElement(pe);
			Object model = (editPart).getModel();
			if(model instanceof IContext){
				command.setIndex(((IContext)model).getRootElements().size());
				command.setContext((IContext)model);
			}else if(model instanceof PageElement){
				editPart = editPart.getParent();
				IContext context = (IContext)editPart.getModel();
				command.setContext(context);
				command.setIndex(context.getRootElements().indexOf(model)+1);

			}else return;

			Command compoundCommand =
				ViewUtil.getCompoundCommandWithResize(command, ViewUtil.ADD, editPart);
			execute(compoundCommand);


			for(Object obj : editPart.getChildren()){
				if (obj instanceof EditPart) {
					EditPart ep = (EditPart) obj;
					if(ep.getModel().equals(pe)){
						//The pageElement needs to be selected to start direct edit: 
						if (ep instanceof PageElementEditPart) {
							((PageElementEditPart) ep).startDirectEdit();
						}
						break;
					}
				}
			}

		}
	}

	protected void handleSelectionChanged() {
		ISelection s = getSelection();
		if (!(s instanceof IStructuredSelection))
			return;
		IStructuredSelection selection = (IStructuredSelection)s;
		selectedObj = null;
		if (selection != null && selection.size() > 0) {
			selectedObj = selection.toList().get(0);
		}
		refresh();
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		String type = AddElementContextMenuList.getType(actionPageElementClass);
		type = type.substring(0, 1).toLowerCase() + type.substring(1, type.length());
		return CubicTestImageRegistry.getDescriptor(type);
	}
}
