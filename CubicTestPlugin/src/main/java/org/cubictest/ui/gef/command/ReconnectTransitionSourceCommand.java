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
package org.cubictest.ui.gef.command;

import org.cubictest.common.utils.ModelUtil;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.eclipse.gef.commands.Command;


/**
 * Command for reconnection transition source.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class ReconnectTransitionSourceCommand extends Command {

	private Transition transition;
	private TransitionNode newSource;
	private TransitionNode oldSource;

	public ReconnectTransitionSourceCommand(TransitionNode newSource, Transition transition) {
		super();
		this.newSource = newSource;
		this.transition = transition;
		this.oldSource = transition.getStart();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		super.execute();
		transition.disconnect();
		oldSource.removeOutTransition(transition);
		transition.setStart(newSource);
		transition.connect();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		transition.disconnect();
		newSource.removeOutTransition(transition);
		transition.setStart(oldSource);
		transition.connect();
	}
	
	/*
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if (transition instanceof ExtensionTransition) {
			return false;
		}
		return (ModelUtil.isLegalTransition(newSource, transition.getEnd(), true, false) == ModelUtil.TRANSITION_EDIT_VALID);
	}
}
