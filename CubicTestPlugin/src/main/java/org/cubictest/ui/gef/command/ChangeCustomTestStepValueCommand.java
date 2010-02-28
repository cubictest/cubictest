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

import org.cubictest.model.customstep.CustomTestStepValue;
import org.eclipse.gef.commands.Command;

public class ChangeCustomTestStepValueCommand extends Command{

	private final CustomTestStepValue paramValue;
	private final String text;
	private String oldText;

	public ChangeCustomTestStepValueCommand(CustomTestStepValue paramValue, String text) {
		this.paramValue = paramValue;
		this.text = text;
	}

	@Override
	public void execute() {
		this.oldText = paramValue.getValue();
		paramValue.setValue(text);
	}
	
	@Override
	public void undo() {
		paramValue.setValue(oldText);
	}
	
	@Override
	public void redo() {
		paramValue.setValue(text);
	}
}
