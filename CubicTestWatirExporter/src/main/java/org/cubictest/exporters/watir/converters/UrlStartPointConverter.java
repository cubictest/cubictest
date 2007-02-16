/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.watir.converters;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.exporters.watir.holders.IStepList;
import org.cubictest.exporters.watir.holders.ITestStep;
import org.cubictest.exporters.watir.holders.TestStep;
import org.cubictest.model.UrlStartPoint;

/**
 * Class for converting UrlStartPoint to Watir steps.
 * 
 * @author chr_schwarz
 */
public class UrlStartPointConverter implements IUrlStartPointConverter<IStepList> {
	
	
	public void handleUrlStartPoint(IStepList steps, UrlStartPoint sp) {
		String start = "ie.goto(\"" + sp.getBeginAt() + "\")";
		ITestStep step = new TestStep(start).setDescription("Opening " + sp);
		steps.add(step);
	}
}
