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

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.eclipse.gef.commands.Command;


/**
 * Command for deleting a user interaction of a user interactions transition.
 * Does not delete the transition, only the user interaction "row" of it.
 * 
 * @author chr_schwarz
 */
public class DeleteUserInteractionCommand extends Command {

	private UserInteraction userInteraction;
	private UserInteractionsTransition transition;
	private int index;
	private boolean indexSet;

	public void setUserInteractionsTransition(UserInteractionsTransition transition) {
		this.transition = transition;
	}

	public void setUserInteraction(UserInteraction userInteraction) {
		this.userInteraction = userInteraction;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (!indexSet) {
			ErrorHandler.logAndThrow("Index not set. Cannot delete user interaction");
		}
		transition.removeUserInteraction(userInteraction);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		transition.addUserInteraction(index, userInteraction);
	}

	
	public void setIndex(int index) {
		indexSet = true;
		this.index = index;
	}




}
