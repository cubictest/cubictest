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
 * Command for changing the Sation key of an Page Element.
 * 
 * @author chr_schwarz
 */
public class ChangePageElementSationKeyCommand extends Command {

	private PageElement element;
	private String oldKey, newKey;
	
	public void setPageElement(PageElement element) {
		this.element = element;
		
	}
	public void setOldKey(String oldKey) {
		this.oldKey = oldKey;
	}
	public void setNewKey(String newKey) {
		this.newKey = newKey;
	}
	
	@Override
	public void execute() {
		super.execute();
		element.setKey(newKey);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setKey(oldKey);
	}

}
