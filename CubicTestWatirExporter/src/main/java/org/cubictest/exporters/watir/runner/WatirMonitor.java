/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.runner;

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;


/**
 * Monitors a Watir process and updates the GUI 
 * @author Christian Schwarz
 *
 */
public class WatirMonitor {

	private StepList stepList;
	
	public WatirMonitor(StepList stepList) {
		this.stepList = stepList;
	}



	public void handle(String line) {
		if(line.startsWith(StepList.PASS)) {
			PageElement pe = stepList.getPageElement(line.substring(line.indexOf(StepList.PASS) + StepList.PASS.length()));
			stepList.addResult(pe, TestPartStatus.PASS);
		}
		else if(line.startsWith(StepList.FAIL)) {
			PageElement pe = stepList.getPageElement(line.substring(line.indexOf(StepList.FAIL) + StepList.FAIL.length()));
			stepList.addResult(pe, TestPartStatus.FAIL);
		}
		else if(line.startsWith(StepList.EXCEPTION)) {
			throw new ExporterException("Exception when running test: " + 
					line.substring(line.indexOf(StepList.EXCEPTION) + StepList.EXCEPTION.length()));
		}
	}
	
}
