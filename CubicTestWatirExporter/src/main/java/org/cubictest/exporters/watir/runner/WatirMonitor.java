/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.runner;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;


/**
 * Monitors a Watir process and updates the GUI 
 * @author Christian Schwarz
 *
 */
public class WatirMonitor {

	WatirHolder watirHolder;
	boolean isInError;
	StringBuffer errorBuffer = new StringBuffer();
	
	public WatirMonitor(WatirHolder watirHolder) {
		this.watirHolder = watirHolder;
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
	}
	
	public void verify() {
		if (StringUtils.isNotBlank(errorBuffer.toString())) {
			throw new ExporterException(errorBuffer.toString());
		}
	}
	
}
