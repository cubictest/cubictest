/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Test;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Option;
import org.cubictest.ui.gef.command.AddExtensionPointCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.controller.AbstractPageEditPart;
import org.cubictest.ui.gef.controller.ContextEditPart;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

public class SelectContextContainerEditPolicy extends
		ContextContainerEditPolicy {

	private ContextLayoutEditPolicy flowPolicy;

	public SelectContextContainerEditPolicy(ContextLayoutEditPolicy flowPolicy) {
		super(flowPolicy);
		this.flowPolicy = flowPolicy;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		
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
		
		Object obj = request.getNewObject();
		if(!(getHost().getModel() instanceof IContext)) {
			return null;
		}
		
		if(obj instanceof ExtensionPoint) {
			PageEditPart pageEditPart = (PageEditPart) ViewUtil.getSurroundingPagePart((PropertyChangePart) getHost());
			AddExtensionPointCommand cmd = new AddExtensionPointCommand();
			cmd.setPageEditPart(pageEditPart);
			cmd.setTest((Test) pageEditPart.getParent().getModel());
			return cmd;
		}
		
		if (!(obj instanceof Option))
			return null;
		Option option = (Option) obj;
			
		CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
		createElementCmd.setContext((IContext)getHost().getModel());
		createElementCmd.setPageElement(option);
		createElementCmd.setIndex(index);
		
		IContext node = (IContext) getHost().getModel(); 
		return ViewUtil.getCompoundCommandWithResize(createElementCmd, ViewUtil.ADD, getHost());
	}
}
