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
import org.cubictest.common.utils.UserInfo;
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
	ExporterException error;
	
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
			error = new ExporterException(errorBuffer.toString());
		}
		else if (line.toLowerCase().contains("watir") && line.toLowerCase().contains("loaderror")) {
			//error loading Watir
			isInError = true;
			error = new ExporterException("Could not start Watir (but Ruby was OK). Check that Watir is installed on your system.");
		}
		else if (line.startsWith(WatirHolder.EXCEPTION) || isInError || line.contains(WatirHolder.TEST_CASE_NAME) ||
				(line.toLowerCase().contains("error") && line.contains(TestRunner.RUNNER_TEMP_FILENAME)) ||
				line.contains(WatirHolder.UNEXPECTED_ERROR_FROM_WATIR_RUNNER)) {
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
		else if(line.startsWith(WatirHolder.EXCEPTION)) {
			String pattern = "] -- ";
			int messagePos = line.lastIndexOf(pattern);
			messagePos = (messagePos < 0) ? line.length() : messagePos + 1;
			PageElement pe = watirHolder.getPageElement(line.substring(line.indexOf(WatirHolder.EXCEPTION) + WatirHolder.EXCEPTION.length(), messagePos));
			watirHolder.addResult(pe, TestPartStatus.EXCEPTION);
		}
		else if (line.startsWith(WatirHolder.SUBTEST_DONE)) {
			runner.display.asyncExec(new Runnable() {
				public void run() {
					runner.getTest().updateAndGetStatus(null);
				}
			});
		}
		else if (line.startsWith(WatirHolder.TEST_DONE)) {
			runner.testRunning = false;
		}
		
		if (line.contains("REXML::ParseException")) {
			watirHolder.getDisplay().asyncExec(new Runnable() {
    			public void run() {
    				UserInfo.showErrorDialog("Watir failed to parse a page/state probably due to malformed HTML. If the HTML page cannot be fixed, " +
    						"try the following to prevent the need for parsing the page and using XPath:\n\n" +
    						"1. Avoid using Contexts and SelectLists\n" +
    						"2. Have page elements use only one identifier.\n" +
    						"3. Only use \"Be equal to\" in identifier properties\n");
    			}
    		});
		}
	}
	
	public void verify() {
		if (error != null) {
			throw error;
		}
		if (StringUtils.isNotBlank(errorBuffer.toString())) {
			throw new ExporterException(errorBuffer.toString());
		}
	}

	public Exception getWatirException() {
		return error;
	}

}
