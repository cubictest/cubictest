/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
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
import org.cubictest.model.customstep.CustomTestStepParameterList;
import org.eclipse.gef.commands.Command;

public class DeleteCustomTestStepParameterCommand extends Command {

	private CustomTestStepParameterList parameters;
	private CustomTestStepParameter parameter;

	public void setParameters(CustomTestStepParameterList parameters) {
		this.parameters = parameters;
	}

	public void setParameter(CustomTestStepParameter parameter) {
		this.parameter = parameter;	
	}
	
	@Override
	public void execute() {
		parameters.remove(parameter);
	}
	
	@Override
	public void undo() {
		parameters.add(parameter);
	}
}
