/*
 * Created on 25.mar.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.parameterization.ParameterList;
import org.eclipse.gef.commands.Command;


public class ChangeParameterListIndexCommand extends Command {

	private ParameterList list;
	private int index;
	private int oldIndex;

	public void setNewIndex(int index) {
		this.index = index;
	}

	public void setParameterList(ParameterList list) {
		this.list = list;
	}
	
	@Override
	public void execute() {
		oldIndex = list.getParameterIndex();
		list.setParameterIndex(index);
	}
	
	@Override
	public void undo() {
		list.setParameterIndex(oldIndex);
	}

}
