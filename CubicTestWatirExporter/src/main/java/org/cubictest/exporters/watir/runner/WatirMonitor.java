/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.runner;

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

	private WatirHolder watirHolder;
	
	public WatirMonitor(WatirHolder watirHolder) {
		this.watirHolder = watirHolder;
	}



	public void handle(String line) {
		if(line.contains("error") && line.contains(TestRunner.RUNNER_TEMP_FILENAME)) {
			throw new ExporterException(line);
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
			throw new ExporterException(line.substring(line.indexOf(WatirHolder.EXCEPTION) + WatirHolder.EXCEPTION.length()));
		}
	}
	
}
