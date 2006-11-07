/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.formElement.Select;
import org.eclipse.gef.commands.Command;

public class ChangeMultiSelectCommand extends Command {

	private Select model;
	private boolean oldMulti;
	private boolean newMulti;

	public void setSelect(Select model) {
		this.model = model;
	
	}

	public void setOldMulti(boolean oldMulti) {
		this.oldMulti = oldMulti;
	}

	public void setNewMulti(boolean newMulti) {
		this.newMulti = newMulti;
	}

	@Override
	public void execute() {
		model.setMultiselect(newMulti);
	}
	
	@Override
	public void undo() {
		model.setMultiselect(oldMulti);
	}
}
