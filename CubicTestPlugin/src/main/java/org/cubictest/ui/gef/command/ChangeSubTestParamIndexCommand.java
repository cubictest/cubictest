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
