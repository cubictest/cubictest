/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * Monitors a Watir process and updates the GUI.
 * Sets testDone property when test is done.
 * 
 * @author Christian Schwarz
 */
public class WatirMonitor extends Thread {

	WatirHolder watirHolder;
	boolean isInError;
	StringBuffer errorBuffer = new StringBuffer();
	IProgressMonitor monitor;
	TestRunner runner;
	Process process;
	
	public WatirMonitor(WatirHolder watirHolder, Process process, IProgressMonitor monitor, TestRunner runner) {
		this.watirHolder = watirHolder;
		this.process = process;
		this.monitor = monitor;
		this.runner = runner;
	}

	public void run() {
		try {
			// monitor process output:
			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()), 1);
			String line;
			while (runner.processAlive && (line = output.readLine()) != null) {
				System.out.println(line);
				handle(line);
			}
			runner.testRunning = false;
			output.close();
		} catch (Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
	}
	
	
	public void handle(String line) {
		if (isInError && (line.startsWith(WatirHolder.PASS) || line.startsWith(WatirHolder.FAIL))) {
			//test is continuing after an exception, stop it:
			throw new ExporterException(errorBuffer.toString());
		}
		else if (isInError || line.contains(WatirHolder.TEST_CASE_NAME) ||
				(line.toLowerCase().contains("error") && line.contains(TestRunner.RUNNER_TEMP_FILENAME)) ||
				line.startsWith(WatirHolder.EXCEPTION)) {
			isInError = true;
			errorBuffer.append(line + "\n");
		}
		
		if(line.startsWith(WatirHolder.PASS)) {
			PageElement pe = watirHolder.getPageElement(line.substring(line.indexOf(WatirHolder.PASS) + WatirHolder.PASS.length()));
			watirHolder.addResult(pe, TestPartStatus.PASS);
		}
		else if(line.startsWith(WatirHolder.FAIL)) {
			PageElement pe = watirHolder.getPageElement(line.substring(line.indexOf(WatirHolder.FAIL) + WatirHolder.FAIL.length()));
			watirHolder.addResult(pe, TestPartStatus.FAIL);
		}
		else if (line.startsWith(WatirHolder.TEST_DONE)) {
			runner.testRunning = false;
		}
	}
	
	public void verify() {
		if (StringUtils.isNotBlank(errorBuffer.toString())) {
			throw new ExporterException(errorBuffer.toString());
		}
	}

}
