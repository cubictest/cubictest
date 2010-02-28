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

import org.cubictest.model.Test;
import org.cubictest.model.parameterization.ParameterList;
import org.eclipse.gef.commands.Command;


public class ChangeParameterListIndexCommand extends Command {

	private ParameterList list;
	private int index;
	private int oldIndex;
	private Test test;

	public void setNewIndex(int index) {
		this.index = index;
	}

	public void setParameterList(ParameterList list) {
		this.list = list;
	}
	
	public void setTest(Test test){
		this.test = test;
	}
	
	@Override
	public void execute() {
		oldIndex = list.getParameterIndex();
		list.setParameterIndex(index);
		test.updateObservers();
	}
	
	@Override
	public void undo() {
		list.setParameterIndex(oldIndex);
		test.updateObservers();
	}
}
