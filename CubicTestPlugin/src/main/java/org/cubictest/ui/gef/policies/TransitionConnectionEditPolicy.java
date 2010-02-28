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
