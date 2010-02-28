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
package org.cubictest.ui.gef.interfaces.exported;

import org.cubictest.model.Test;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.commands.CommandStack;

/**
 * Interface for test editors.
 * 
 * @author Christian Schwarz
 */
public interface ITestEditor {

	public static final int INITIAL_PAGE_POS_Y = 100;
	public static final int INITIAL_PAGE_POS_X = 170;
	public static final int INITIAL_PAGE_WIDTH = 150;
	public static final int INITIAL_PAGE_HEIGHT = 95;
	public static final int NEW_PATH_OFFSET = 250;

	public Test getTest();
	
	public void addDisposeListener(IDisposeListener listener);
	
	public CommandStack getCommandStack();
	
	public IProject getProject();
	
}
