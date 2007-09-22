/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.runner;


/** 
 * Class that monitors the Proces object and detects when it shuts down.
 * @author Christian Schwarz
 */
public class ProcessTerminationDetector extends Thread {
	Process process;
	TestRunner runner;
	
	public ProcessTerminationDetector(Process process, TestRunner runner) {
		this.process = process;
		this.runner = runner;
	}
	
	public void run() {
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			runner.setProcessDone(true);
		}
		runner.setProcessDone(true);
	}
}
