/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.IDescription;
import org.eclipse.gef.commands.Command;


public class ChangeDescriptionCommand extends Command {

	private IDescription description;
	private String oldDescription, newDescription;
	
	public void setDesctription(IDescription description) {
		this.description = description;
		
	}
	public void setOldDescription(String oldDescription) {
		this.oldDescription = oldDescription;
	}
	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}
	
	@Override
	public void execute() {
		super.execute();
		description.setDescription(newDescription);
	}
	
	@Override
	public void undo() {
		super.undo();
		description.setDescription(oldDescription);
	}

}
