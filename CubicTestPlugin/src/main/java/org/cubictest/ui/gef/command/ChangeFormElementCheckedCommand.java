/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.formElement.Checkable;
import org.eclipse.gef.commands.Command;

/**
 * Command that operates on a Checkable type.
 * 
 * @author chr_schwarz
 */
public class ChangeFormElementCheckedCommand extends Command {

	private Checkable element;
	private boolean oldChecked, newChecked;
	
	public void setCheckable(Checkable element) {
		this.element = element;
		
	}
	public void setOldChecked(boolean oldChecked) {
		this.oldChecked = oldChecked;
	}
	
	public void setNewChecked(boolean newChecked) {
		this.newChecked = newChecked;
	}
	
	@Override
	public void execute() {
		super.execute();
		element.setChecked(newChecked);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setChecked(oldChecked);
	}

}
