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
package org.cubictest.ui.customstep.command;

import org.cubictest.model.customstep.CustomTestStepParameter;
import org.eclipse.gef.commands.Command;

public class ChangeCustomTestStepParameterDescriptionCommand extends Command {

	private CustomTestStepParameter parameter;
	private String newDescription;
	private String oldDescription;

	public void setParameter(CustomTestStepParameter parameter) {
		this.parameter = parameter;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	@Override
	public void execute() {
		oldDescription = parameter.getDescription();
		parameter.setDescription(newDescription);
	}
	
	@Override
	public void undo() {
		parameter.setDescription(oldDescription);
	}
	
	@Override
	public void redo() {
		parameter.setDescription(newDescription);
	}
}
