/*******************************************************************************
 * Copyright (c) 2005, 2008  Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.ui.command;

import org.cubictest.model.customstep.data.CustomTestStepData;
import org.eclipse.gef.commands.Command;

public class ChangeCustomStepWaitForPageToLoadCommand extends Command {

	public static final String WAIT_FOR_PAGE_TO_LOAD = "waitForPageToLoad";
	private CustomTestStepData customTestStepData;
	private Object oldWaitForPageToLoad;
	private boolean newWaitForPageToLoad;

	@Override
	public void execute() {
		super.execute();
		oldWaitForPageToLoad = customTestStepData.getExporterUserSetting(WAIT_FOR_PAGE_TO_LOAD);
		customTestStepData.setExporterUserSetting(WAIT_FOR_PAGE_TO_LOAD, newWaitForPageToLoad);
	}
	
	@Override
	public void undo() {
		super.undo();
		customTestStepData.setExporterUserSetting(WAIT_FOR_PAGE_TO_LOAD, oldWaitForPageToLoad);
	}

	public void setCustomTestStepData(CustomTestStepData customTestStepData) {
		this.customTestStepData = customTestStepData;
	}

	public void setNewWaitForPageToLoad(boolean newWaitForPageToLoad) {
		this.newWaitForPageToLoad = newWaitForPageToLoad;
	}
}
