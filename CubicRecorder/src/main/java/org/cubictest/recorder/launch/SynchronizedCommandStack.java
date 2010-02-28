/*******************************************************************************
 * Copyright (c) 2005, 2010 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - enhanced features, bug fixes and usability improvements
 *******************************************************************************/
package org.cubictest.recorder.launch;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Display;

public class SynchronizedCommandStack  {

	private Display display;
	private CommandStack commandStack;
	
	
	@SuppressWarnings("unused")
	private SynchronizedCommandStack() {
	}

	public SynchronizedCommandStack(Display display, CommandStack commandStack) {
		this.display = display;
		this.commandStack = commandStack;
	}

	
	public void execute(final Command command) {
		display.syncExec(new Runnable() {
			public void run() {
				commandStack.execute(command);
			}
		});
	}
	
	public void execute(Runnable runnable) {
		display.syncExec(runnable);
	}
	
}
