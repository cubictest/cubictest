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
package org.cubictest.ui.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.utils.ModelUtil;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
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
	private boolean actionElementDeletionInfoShowed;
	private List<UserInteraction> oldUserInteractions;

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

		if (transition instanceof UserInteractionsTransition) {
			
			UserInteractionsTransition userInteractionsTransition = (UserInteractionsTransition) transition;
			oldUserInteractions = new ArrayList<UserInteraction>(userInteractionsTransition.getUserInteractions());
			userInteractionsTransition.removeAllUserInteractions();
			
			if (!actionElementDeletionInfoShowed) {
				String msg = "Transition source change: All action elements of the user interaction were removed. Undo to revert.";
				UserInfo.setStatusLine(msg);
				actionElementDeletionInfoShowed = true;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		
		if (transition instanceof UserInteractionsTransition) {
			UserInteractionsTransition userInteractionsTransition = (UserInteractionsTransition) transition;
			userInteractionsTransition.setUserInteractions(oldUserInteractions);
		}
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
