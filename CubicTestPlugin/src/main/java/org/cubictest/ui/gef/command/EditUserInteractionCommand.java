/*
 * Created on 26.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
 
package org.cubictest.ui.gef.command;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.UserInteraction;
import org.eclipse.gef.commands.Command;


/**
 * Command for editing a user interaction of a user interactions transition.
 * 
 * @author chr_schwarz
 */
public class EditUserInteractionCommand extends Command {

	private UserInteraction userInteraction;
	
	private IActionElement newElement;
	private ActionType newActionType;
	private String newTextInput;

	private IActionElement oldElement;
	private ActionType oldActionType;
	private String oldTextInput;


	public void setUserInteraction(UserInteraction userInteraction) {
		this.userInteraction = userInteraction;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (userInteraction == null) {
			ErrorHandler.logAndThrow("Cannot excecute command. UserInteraction was null.");
		}
		
		if (newElement != null)
			userInteraction.setElement(newElement);
		if (newActionType != null)
			userInteraction.setActionType(newActionType);
		if (newTextInput != null)
			userInteraction.setTextualInput(newTextInput);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		if (newElement != null)
			userInteraction.setElement(oldElement);
		if (newActionType != null)
			userInteraction.setActionType(oldActionType);
		if (newTextInput != null)
			userInteraction.setTextualInput(oldTextInput);
	}

	public void setNewTextInput(String newInput) {
		this.newTextInput = newInput;
	}
	
	public void setNewActionType(ActionType type) {
		this.newActionType = type;
	}

	public void setNewElement(IActionElement element) {
		this.newElement = element;
	}

	
	public void setOldTextInput(String newInput) {
		this.oldTextInput = newInput;
	}
	
	public void setOldActionType(ActionType type) {
		this.oldActionType = type;
	}

	public void setOldElement(IActionElement element) {
		this.oldElement = element;
	}
	

}
