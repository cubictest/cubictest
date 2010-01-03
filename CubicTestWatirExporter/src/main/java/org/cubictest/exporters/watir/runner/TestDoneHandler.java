/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.runner;

import java.io.BufferedOutputStream;

import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.core.runtime.IProgressMonitor;

/** 
 * Class that closes IE when the test is done.
 * @author Christian Schwarz
 */
public class TestDoneHandler extends Thread {
	Process process;
	IProgressMonitor monitor;
	WatirTestRunner runner;
	boolean closeBrowser;
	
	public TestDoneHandler(Process process, IProgressMonitor progressMon, WatirTestRunner runner) {
		this.process = process;
		this.monitor = progressMon;
		this.runner = runner;
	}
	
	public void run() {
		try {
			while (runner.processAlive && !closeBrowser) {
				Thread.sleep(100);
			}
			//close browser
			if (runner.processAlive && process != null && process.getOutputStream() != null) {
				BufferedOutputStream input = new BufferedOutputStream(process.getOutputStream());
				//close process and browser (waits for "press enter"):
				input.write('\n');
				input.flush();
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
		finally {
			runner.processAlive = false;
		}
	}

	public void setCloseBrowser(boolean closeBrowser) {
		this.closeBrowser = closeBrowser;
	}
}
