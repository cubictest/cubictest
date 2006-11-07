/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.export.watir.delegates;

import org.cubictest.common.converters.interfaces.IUrlStartPointConverter;
import org.cubictest.export.watir.TestStep;
import org.cubictest.export.watir.interfaces.IStepList;
import org.cubictest.export.watir.interfaces.ITestStep;
import org.cubictest.model.UrlStartPoint;

/**
 * Class for converting Connection points to Watir steps.
 * 
 * @author chr_schwarz
 */
public class UrlStartPointConverter implements IUrlStartPointConverter<IStepList> {
	
	
	public void handleUrlStartPoint(IStepList steps, UrlStartPoint cp) {
		UrlStartPoint sp = (UrlStartPoint) cp;
		String start = "ie.goto(\"" + sp.getBeginAt() + "\")";
		ITestStep step = new TestStep(start).setDescription("Invoking page: " + sp.getBeginAt());
		steps.add(step);
	}
}
