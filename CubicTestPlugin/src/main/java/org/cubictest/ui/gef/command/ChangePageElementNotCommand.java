/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;

/**
 * Command for changing the not field of an Page Element.
 * 
 * @author chr_schwarz
 */
public class ChangePageElementNotCommand extends Command {

	private PageElement element;
	private boolean oldNot, newNot;
	
	public void setPageElement(PageElement element) {
		this.element = element;
		
	}
	public void setOldNot(boolean oldNot) {
		this.oldNot = oldNot;
	}
	public void setNewNot(boolean newNot) {
		this.newNot = newNot;
	}
	
	@Override
	public void execute() {
		super.execute();
		element.setNot(newNot);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setNot(oldNot);
	}

}
