/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;


public class ChangePageElementDescriptionCommand extends Command {

	private PageElement element;
	private String oldDescription, newDescription;
	
	public void setPageElement(PageElement element) {
		this.element = element;
		
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
		element.setDescription(newDescription);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setDescription(oldDescription);
	}

}
