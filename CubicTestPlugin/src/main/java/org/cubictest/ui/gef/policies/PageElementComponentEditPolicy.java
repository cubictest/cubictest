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
import org.cubictest.model.PageElement;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.DeletePageElementCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;


/**
 * @author SK Skytteren
 *
 * 
 */
public class PageElementComponentEditPolicy extends ComponentEditPolicy {
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		DeletePageElementCommand deleteCmd = new DeletePageElementCommand();
		deleteCmd.setElementParent((IContext)getHost().getParent().getModel());
		deleteCmd.setElement((PageElement)getHost().getModel());
		deleteCmd.setPage(ViewUtil.getSurroundingPage(getHost()));


		//only resize height if width has not been changed:
		//find page:
		TransitionNode node = ViewUtil.getSurroundingPage(getHost());

		if (node != null)
		{
			return ViewUtil.getCompoundCommandWithResize(deleteCmd, ViewUtil.REMOVE, getHost());
		}
		else
		{
			return deleteCmd;
		}
	}


	
}
