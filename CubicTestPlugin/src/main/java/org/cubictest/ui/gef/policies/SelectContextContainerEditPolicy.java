/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Option;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

/**
 * EditPolicy for Select list.
 * 
 * @author chr_schwarz 
 */
public class SelectContextContainerEditPolicy extends
		ContextContainerEditPolicy {

	public SelectContextContainerEditPolicy(ContextLayoutEditPolicy flowPolicy) {
		super(flowPolicy);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		
		int index = super.getIndex(request);
		
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

}
