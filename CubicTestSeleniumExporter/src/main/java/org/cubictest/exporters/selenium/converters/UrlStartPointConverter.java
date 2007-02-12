/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.selenium.converters;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.exporters.selenium.holders.SeleneseDocument;
import org.cubictest.exporters.selenium.holders.ITestStep;
import org.cubictest.exporters.selenium.holders.Command;
import org.cubictest.model.UrlStartPoint;

/**
 * Class for converting Connection points to Watir steps.
 * 
 * @author chr_schwarz
 */
public class UrlStartPointConverter implements IUrlStartPointConverter<SeleneseDocument> {
	
	
	public void handleUrlStartPoint(SeleneseDocument steps, UrlStartPoint cp) {
		UrlStartPoint sp = (UrlStartPoint) cp;
		String start = "ie.goto(\"" + sp.getBeginAt() + "\")";
		ITestStep step = new Command(start).setDescription("Invoking page: " + sp.getBeginAt());
		steps.add(step);
	}
}
