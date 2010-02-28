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
package org.cubictest.ui.customstep.section;

import org.cubictest.model.customstep.data.CustomTestStepData;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Composite;

public abstract class CustomStepSection {

	public final static int STANDARD_LABEL_WIDTH = 200;
	private CommandStack commandStack;
	
	public abstract void createControl(Composite parent);

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}
	
	public abstract void setData(CustomTestStepData data);
	
	public abstract String getDataKey();
	
	public abstract void setProject(IProject project);
	
}
