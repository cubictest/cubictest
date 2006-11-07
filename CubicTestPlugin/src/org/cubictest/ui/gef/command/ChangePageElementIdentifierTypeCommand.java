/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;

/**
 * Command that operates on a PageElements identifyer type.
 * 
 * @author chr_schwarz
 */
public class ChangePageElementIdentifierTypeCommand extends Command {

	private PageElement element;
	private IdentifierType oldIdentifierType, newIdentifierType;
	
	public void setPageElement(PageElement element) {
		this.element = element;
		
	}
	
	public void setOldIdentifierType(IdentifierType oldIdentifierType) {
		this.oldIdentifierType = oldIdentifierType;
	}
	
	public void setNewIdentifierType(IdentifierType newIdentifierType) {
		this.newIdentifierType = newIdentifierType;
	}
	
	@Override
	public void execute() {
		super.execute();
		element.setIdentifierType(newIdentifierType);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setIdentifierType(oldIdentifierType);
	}

}
