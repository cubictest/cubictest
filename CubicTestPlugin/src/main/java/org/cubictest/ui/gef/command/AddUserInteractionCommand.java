/*
 * Created on 26.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
 
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
