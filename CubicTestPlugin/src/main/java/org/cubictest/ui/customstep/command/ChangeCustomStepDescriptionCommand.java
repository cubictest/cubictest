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

import org.cubictest.model.customstep.CustomTestStep;
import org.eclipse.gef.commands.Command;

public class ChangeCustomStepDescriptionCommand extends Command {

	private String description;
	private CustomTestStep customStep;
	private String oldDescription;

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCustomStep(CustomTestStep customStep) {
		this.customStep = customStep;	
	}

	@Override
	public void execute() {
		this.oldDescription = customStep.getDescription();
		customStep.setDescription(description);
	}
	
	@Override
	public void undo() {
		super.undo();
		customStep.setDescription(oldDescription);
	}
}
