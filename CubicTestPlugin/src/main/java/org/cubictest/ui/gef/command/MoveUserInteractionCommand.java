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
 * Command for moving a user interaction of a user interactions transition
 * (move to other rowindex in table).
 * 
 * @author chr_schwarz
 */
public class MoveUserInteractionCommand extends Command {

	public Direction direction;
	private UserInteraction userInteraction;
	private UserInteractionsTransition transition;
	private int oldIndex;
	private int newIndex;
	
	public enum Direction {
		UP,
		DOWN;
	}
	
	public void setUserInteractionsTransition(UserInteractionsTransition transition) {
		this.transition = transition;
	}

	public void setUserInteraction(UserInteraction userInteraction) {
		this.userInteraction = userInteraction;
		oldIndex = transition.getUserInteractions().indexOf(userInteraction);
		if (direction == Direction.UP)
			newIndex = oldIndex - 1;
		else
			newIndex = oldIndex + 1;
		
		//ensure valid index:
		if (newIndex >= transition.getUserInteractions().size()) {
			newIndex = transition.getUserInteractions().size() - 1;
		}
		if (newIndex < 0) {
			newIndex = 0;
		}
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		transition.removeUserInteraction(userInteraction);
		transition.addUserInteraction(newIndex, userInteraction);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		transition.removeUserInteraction(userInteraction);
		transition.addUserInteraction(oldIndex, userInteraction);
	}


	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
