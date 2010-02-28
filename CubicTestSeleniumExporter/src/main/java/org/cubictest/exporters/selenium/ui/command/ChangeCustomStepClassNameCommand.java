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
package org.cubictest.exporters.selenium.ui.command;

import org.cubictest.model.customstep.data.CustomTestStepData;
import org.eclipse.gef.commands.Command;

public class ChangeCustomStepClassNameCommand extends Command {

	private CustomTestStepData customTestStepData;
	private String oldDisplayText;
	private String path;
	private String displayText;
	private String oldPath;

	public void setCustomTestStepData(CustomTestStepData customTestStepData) {
		this.customTestStepData = customTestStepData;
	}

	@Override
	public void execute() {
		super.execute();
		oldDisplayText = customTestStepData.getDisplayText();
		customTestStepData.setDisplayText(displayText);
		oldPath = customTestStepData.getPath();
		customTestStepData.setPath(path);
	}
	
	@Override
	public void undo() {
		super.undo();
		customTestStepData.setDisplayText(oldDisplayText);
		customTestStepData.setPath(oldPath);
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
}
