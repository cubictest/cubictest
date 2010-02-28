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
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.controller.AbstractNodeEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;


/**
 * @author Stein K. Skytteren
 *
 */
public class PageNodeEditPolicy extends AbstractNodeEditPolicy {
	
	/*
	 * @see GraphicalNodeEditPolicy#getConnectionCompleteCommand(CreateConnectionRequest)
	 */
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request){
		Test test = (Test)getHost().getParent().getModel();
		CreateTransitionCommand cmd = (CreateTransitionCommand) request.getStartCommand();
		AbstractNodeEditPart part = (AbstractNodeEditPart) request.getTargetEditPart();
		cmd.setTarget((TransitionNode)part.getModel());
		cmd.setTest(test);
		return cmd;
	}
}
