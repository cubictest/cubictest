/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.ui.gef.command.DeleteTransitionCommand;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;



/**
 * @author SK Skytteren
 *
 */
public class TransitionConnectionEditPolicy extends ConnectionEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConnectionEditPolicy#getDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		Transition transition = (Transition) getHost().getModel();
		Test test = (Test)((RootEditPart)getHost().getParent()).getContents().getModel();
		DeleteTransitionCommand deleteCmd = new DeleteTransitionCommand(transition, test);
		return deleteCmd;
	}

}
