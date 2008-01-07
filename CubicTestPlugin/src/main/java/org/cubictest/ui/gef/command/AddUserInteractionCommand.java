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

import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.eclipse.gef.commands.Command;


/**
 * Command for adding a user interaction to a user interactions transition.
 * I.e. adding an "input" to the list of inputs consituting the user interactions.
 * 
 * @author chr_schwarz
 */
public class AddUserInteractionCommand extends Command {

	private UserInteraction userInteraction;
	private UserInteractionsTransition transition;


	public void setUserInteractionsTransition(UserInteractionsTransition transition) {
		this.transition = transition;
	}

	public void setNewUserInteraction(UserInteraction userInteraction) {
		this.userInteraction = userInteraction;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		transition.addUserInteraction(userInteraction);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		transition.removeUserInteraction(userInteraction);
	}




}
