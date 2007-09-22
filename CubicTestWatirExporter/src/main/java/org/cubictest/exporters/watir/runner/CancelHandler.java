/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
	TestRunner runner;
	
	public CancelHandler(Process process, IProgressMonitor progressMon, TestRunner runner) {
		this.process = process;
		this.monitor = progressMon;
		this.runner = runner;
	}
	
	public void run() {
		while (!runner.processDone) {
			if (monitor.isCanceled()) {
				process.destroy();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				ErrorHandler.logAndRethrow(e);
			}
		}
	}
}
