/*
 * Created on 25.mar.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.SubTest;
import org.eclipse.gef.commands.Command;


/**
 * Command for changing param index on subtest.
 * 
 * @author Christian Schwarz
 *
 */
public class ChangeSubTestParamIndexCommand extends Command {

	private int index;
	private int oldIndex;
	private SubTest subtest;

	public void setNewIndex(int index) {
		this.index = index;
	}

	public void setTest(SubTest subtest){
		this.subtest = subtest;
	}
	
	@Override
	public void execute() {
		oldIndex = subtest.getParameterIndex();
		subtest.setParameterIndex(index);
	}
	
	@Override
	public void undo() {
		subtest.setParameterIndex(oldIndex);
	}
}
