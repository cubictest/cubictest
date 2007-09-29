/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
	TestRunner runner;
	boolean closeBrowser;
	
	public TestDoneHandler(Process process, IProgressMonitor progressMon, TestRunner runner) {
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
