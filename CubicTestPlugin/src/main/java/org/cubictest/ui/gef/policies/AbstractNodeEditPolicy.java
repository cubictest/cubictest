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

import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.command.ReconnectTransitionSourceCommand;
import org.cubictest.ui.gef.command.ReconnectTrasitionTargetCommand;
import org.cubictest.ui.gef.controller.AbstractNodeEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;



/**
 * Abstract class for the nodes in the graphical test editor.
 * 
 * @author SK Skytteren
 */
public abstract class AbstractNodeEditPolicy extends GraphicalNodeEditPolicy {

	/*
	 * @see GraphicalNodeEditPolicy#getConnectionCreateCommand(CreateConnectionRequest)
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request){
		CreateTransitionCommand cmd = new CreateTransitionCommand();
		AbstractNodeEditPart part = (AbstractNodeEditPart) getHost();
		cmd.setSource((TransitionNode)part.getModel());
		request.setStartCommand(cmd);
		return cmd;
	}

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

	/*
	 * @see GraphicalNodeEditPolicy#getReconnectSourceCommand(ReconnectRequest)
	 */
	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request){
		TransitionNode newSource = ((TransitionNode)((AbstractNodeEditPart) getHost()).getModel());
		Transition transition = ((Transition) request.getConnectionEditPart().getModel());
		ReconnectTransitionSourceCommand cmd = new ReconnectTransitionSourceCommand(newSource, transition);
		return cmd;
	}

	/*
	 * @see GraphicalNodeEditPolicy#getReconnectTargetCommand(ReconnectRequest)
	 */
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request){
		ReconnectTrasitionTargetCommand cmd = new ReconnectTrasitionTargetCommand();
		cmd.setTransition((Transition)request.getConnectionEditPart().getModel());
		AbstractNodeEditPart abstractNodeEditPart = (AbstractNodeEditPart) getHost();
		cmd.setNewTarget((TransitionNode)abstractNodeEditPart.getModel());
		return cmd;
	}
}
