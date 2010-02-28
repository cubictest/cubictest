/*******************************************************************************
 * Copyright (c) 2005, 2010  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.runner;

import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.core.runtime.IProgressMonitor;

/** 
 * Class that handles user cancel and destroys the process.
 * @author Christian Schwarz
 */
public class CancelHandler extends Thread {
	Process process;
	IProgressMonitor monitor;
	WatirTestRunner runner;
	
	public CancelHandler(Process process, IProgressMonitor progressMon, WatirTestRunner runner) {
		this.process = process;
		this.monitor = progressMon;
		this.runner = runner;
	}
	
	public void run() {
		try {
			while (runner.processAlive) {
				if (monitor.isCanceled()) {
					process.destroy();
					runner.testRunning = false;
					runner.processAlive = false;
				}
				Thread.sleep(100);
			}
		} catch (Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
	}
}
