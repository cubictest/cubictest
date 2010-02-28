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
package org.cubictest.ui.gef.policies;

import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Option;
import org.cubictest.ui.gef.command.AddExtensionPointCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.controller.AbstractPageEditPart;
import org.cubictest.ui.gef.controller.ContextEditPart;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Edit policy for objects implementing IContext. 
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 *
 */
public class ContextContainerEditPolicy extends ContainerEditPolicy {

	protected ContextLayoutEditPolicy flowPolicy;
	
	public ContextContainerEditPolicy(ContextLayoutEditPolicy flowPolicy) {
		this.flowPolicy = flowPolicy;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	@Override
	public EditPart getTargetEditPart(Request request) {
		if(REQ_CREATE.equals(request.getType()))
			return getHost();
		return super.getTargetEditPart(request);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		
		int index = getIndex(request);
		
		Object obj = request.getNewObject();
		if(!(getHost().getModel() instanceof IContext)) {
			return null;
		}
		
		if(obj instanceof ExtensionPoint) {
			AbstractPageEditPart abstractPageEditPart = (AbstractPageEditPart) ViewUtil.getSurroundingPagePart((PropertyChangePart) getHost());
			if (!(abstractPageEditPart instanceof PageEditPart))
				return null;
			
			ExtensionPoint exPoint = new ExtensionPoint();
			Test test = (Test) abstractPageEditPart.getParent().getModel();
			PageEditPart pagePart = (PageEditPart) abstractPageEditPart;
			Page page = (Page) pagePart.getModel();
			
			CompoundCommand compoundCmd = new CompoundCommand();
			
			CreateTransitionCommand transitionCreateCommand = new CreateTransitionCommand();
			transitionCreateCommand.setSource(page);
			transitionCreateCommand.setTarget(exPoint);
			transitionCreateCommand.setTest(test);
			compoundCmd.add(transitionCreateCommand);
			
			AddExtensionPointCommand addExPointCmd = new AddExtensionPointCommand();
			addExPointCmd.setExtensionPoint(exPoint);
			addExPointCmd.setPage(page);
			addExPointCmd.setTest(test);
			compoundCmd.add(addExPointCmd);
			
			return compoundCmd;
		}
		if(obj instanceof UserInteractionsTransition) {
			AbstractPageEditPart abstractPageEditPart = (AbstractPageEditPart) ViewUtil.getSurroundingPagePart((PropertyChangePart) getHost());
			if (!(abstractPageEditPart instanceof PageEditPart))
				return null;
			
			CreateTransitionCommand cmd = new CreateTransitionCommand();
			cmd.setTest((Test) abstractPageEditPart.getParent().getModel());
			cmd.setSource((TransitionNode) abstractPageEditPart.getModel());
			cmd.setPageEditPart((PageEditPart) abstractPageEditPart);
			cmd.setAutoCreateTargetPage(true);
			return cmd;
		}		
		if (obj instanceof Option)
			return null;
		
		if (!(obj instanceof PageElement))
			return null;
		PageElement pageElement = (PageElement) obj;
			
		CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
		createElementCmd.setContext((IContext)getHost().getModel());
		createElementCmd.setPageElement(pageElement);
		createElementCmd.setIndex(index);
		
		//IContext node = (IContext) getHost().getModel(); 
		return ViewUtil.getCompoundCommandWithResize(createElementCmd, ViewUtil.ADD, getHost());
	}
	
	protected int getIndex(CreateRequest request) {
		int index = flowPolicy.getIndex(request);
		
		if (index < 0){
			
			if (getHost() instanceof ContextEditPart) {
				index = ((ContextEditPart)getHost()).getChildren().size();
			}
			else if(getHost() instanceof AbstractPageEditPart) {
				index = ((AbstractPageEditPart)getHost()).getChildren().size();
			}
			else {
				index = 0;
			}
		}
		return index;
	}
	
	
}
