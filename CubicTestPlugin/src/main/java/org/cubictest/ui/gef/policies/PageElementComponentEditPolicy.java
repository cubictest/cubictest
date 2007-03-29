/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.DeletePageElementCommand;
import org.cubictest.ui.utils.ViewUtil;
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
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		DeletePageElementCommand deleteCmd = new DeletePageElementCommand();
		deleteCmd.setContext((IContext)getHost().getParent().getModel());
		deleteCmd.setPageElement((PageElement)getHost().getModel());
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
