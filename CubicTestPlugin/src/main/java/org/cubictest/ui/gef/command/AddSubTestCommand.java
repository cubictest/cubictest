/*
 * Created on 09.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;

public class AddSubTestCommand extends Command {

	private Test test;
	private SubTest subTest;

	public void setTest(Test test) {
		this.test = test;
	}

	public void setSubTest(SubTest subTest) {
		this.subTest = subTest;
	}

	@Override
	public void execute() {
		super.execute();
		test.addSubTest(subTest);
	}
	
	@Override
	public void undo() {
		super.undo();
		test.removeSubTest(subTest);
	}
}
