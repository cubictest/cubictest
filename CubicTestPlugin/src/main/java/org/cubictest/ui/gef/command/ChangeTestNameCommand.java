/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;


/**
 * Command for changing test name.
 * 
 * @author Christian Schwarz
 *
 */
public class ChangeTestNameCommand extends Command {

	private Test test;
	
	private String oldName;
	
	private String newName;

	
	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public void setTest(Test test) {
		this.test = test;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		super.execute();
		test.setName(newName);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		test.setName(oldName);
	}
}
