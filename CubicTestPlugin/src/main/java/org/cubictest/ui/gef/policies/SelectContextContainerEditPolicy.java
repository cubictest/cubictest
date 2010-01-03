/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
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
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Option;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.controller.formElement.FormSelectEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * EditPolicy for Select list.
 * 
 * @author chr_schwarz 
 */
public class SelectContextContainerEditPolicy extends ContextContainerEditPolicy {

	public SelectContextContainerEditPolicy(ContextLayoutEditPolicy flowPolicy) {
		super(flowPolicy);
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
		
		if ((obj instanceof Option)) {
			Option option = (Option) obj;
			CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
			createElementCmd.setContext((IContext)getHost().getModel());
			createElementCmd.setPageElement(option);
			createElementCmd.setIndex(index);
			
			return ViewUtil.getCompoundCommandWithResize(createElementCmd, ViewUtil.ADD, getHost());
		}
		return null;
		
	}
	
	protected int getIndex(CreateRequest request) {
		int index = flowPolicy.getIndex(request);
		
		if (index < 0){
			if (getHost() instanceof FormSelectEditPart) {
				index = ((FormSelectEditPart)getHost()).getChildren().size();
			}
			else {
				index = 0;
			}
		}
		return index;
	}

}
