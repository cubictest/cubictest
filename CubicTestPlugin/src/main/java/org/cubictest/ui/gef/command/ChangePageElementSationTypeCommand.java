/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.PageElement;
import org.cubictest.model.SationObserver.SationType;
import org.eclipse.gef.commands.Command;

/**
 * Command for changing the Sation type of an Page Element.
 * 
 * @author chr_schwarz
 */
public class ChangePageElementSationTypeCommand extends Command {

	private PageElement element;
	private SationType oldType, newType;
	
	public void setPageElement(PageElement element) {
		this.element = element;
	}

	public void setOldSationType(SationType oldType) {
		this.oldType = oldType;
	}
	
	public void setNewSationType(SationType newType) {
		this.newType = newType;
	}
	
	@Override
	public void execute() {
		super.execute();
		element.setSationType(newType);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setSationType(oldType);
	}

}
