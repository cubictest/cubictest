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

	private boolean newElementSet;
	private boolean newActionTypeSet;
	private boolean newTextInputSet;
	private boolean oldElementSet;
	private boolean oldActionTypeSet;
	private boolean oldTextInputSet;


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
		
		if (newElementSet) {
			if (!oldElementSet) {
				ErrorHandler.logAndThrow("Old element was not set. Cannot execute.");
			}
			userInteraction.setElement(newElement);
		}
		
		if (newActionTypeSet) {
			if (!oldActionTypeSet) {
				ErrorHandler.logAndThrow("Old ActionType was not set. Cannot execute.");
			}
			userInteraction.setActionType(newActionType);
		}
		
		if (newTextInputSet) {
			if (!oldTextInputSet) {
				ErrorHandler.logAndThrow("Old text input was not set. Cannot execute.");
			}
			userInteraction.setTextualInput(newTextInput);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		if (newElementSet) {
			userInteraction.setElement(oldElement);
		}
		
		if (newActionTypeSet) {
			userInteraction.setActionType(oldActionType);
		}
		
		if (newTextInputSet) {
			userInteraction.setTextualInput(oldTextInput);
		}
	}

	
	public void setNewTextInput(String newInput) {
		newTextInputSet = true;
		this.newTextInput = newInput;
	}
	
	public void setNewActionType(ActionType type) {
		newActionTypeSet = true;
		this.newActionType = type;
	}

	public void setNewElement(IActionElement element) {
		newElementSet = true;
		this.newElement = element;
	}

	
	public void setOldTextInput(String newInput) {
		oldTextInputSet = true;
		this.oldTextInput = newInput;
	}
	
	public void setOldActionType(ActionType type) {
		oldActionTypeSet = true;
		this.oldActionType = type;
	}

	public void setOldElement(IActionElement element) {
		oldElementSet = true;
		this.oldElement = element;
	}
	

}
