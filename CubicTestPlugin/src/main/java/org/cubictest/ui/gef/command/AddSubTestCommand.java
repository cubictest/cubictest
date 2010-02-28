/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.command;

import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;

public class AddSubTestCommand extends Command {

	private Test test;
	private SubTest subTest;

	public AddSubTestCommand(SubTest subTest, Test test) {
		this.subTest = subTest;
		this.test = test;
	}

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
