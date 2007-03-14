/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.watir.converters;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.model.UrlStartPoint;

/**
 * Converts a UrlStartPoint to a Watir step.
 * 
 * @author chr_schwarz
 */
public class UrlStartPointConverter implements IUrlStartPointConverter<StepList> {
	
	
	public void handleUrlStartPoint(StepList stepList, UrlStartPoint sp) {
		if (!stepList.isBrowserStarted()) {
			stepList.add("ie = Watir::IE.new");
			stepList.setBrowserStarted(true);
		}

		stepList.addSeparator();
		stepList.add("# URL start point");
		stepList.add("ie.goto(\"" + sp.getBeginAt() + "\")");
	}
	
}
