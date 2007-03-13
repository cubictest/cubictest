/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeMultiSelectCommand extends Command {

	private Identifier identifier;
	private boolean oldMulti;
	private boolean newMulti;

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setOldMulti(boolean oldMulti) {
		this.oldMulti = oldMulti;
	}

	public void setNewMulti(boolean newMulti) {
		this.newMulti = newMulti;
	}

	@Override
	public void execute() {
		identifier.setValue(newMulti + "");
	}
	
	@Override
	public void undo() {
		identifier.setValue(oldMulti + "");
	}
}
