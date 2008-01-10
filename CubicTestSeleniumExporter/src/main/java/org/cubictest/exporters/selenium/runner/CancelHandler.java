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
package org.cubictest.exporters.selenium.runner;

import org.cubictest.common.utils.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/** 
 * Class that handles user cancel and stops selenium.
 * @author Christian Schwarz
 */
public class CancelHandler extends Thread {
	IProgressMonitor monitor;
	TestRunner runner;
	
	public CancelHandler(IProgressMonitor progressMon, TestRunner runner) {
		this.monitor = progressMon;
		this.runner = runner;
	}
	
	public void run() {
		try {
			while (runner.seleniumStarter != null || runner.seleniumHolder.isSeleniumStarted()) {
				if (monitor.isCanceled()) {
					runner.stopSelenium();
				}
				Thread.sleep(100);
			}
		} catch (Exception e) {
			Logger.warn("Exception when stopping selenium.", e);
		}
	}
}
