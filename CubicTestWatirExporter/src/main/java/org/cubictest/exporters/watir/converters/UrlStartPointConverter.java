/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.watir.converters;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.UrlStartPoint;

/**
 * Converts a UrlStartPoint to a Watir step.
 * 
 * @author chr_schwarz
 */
public class UrlStartPointConverter implements IUrlStartPointConverter<WatirHolder> {
	
	
	public void handleUrlStartPoint(WatirHolder watirHolder, UrlStartPoint sp, boolean firstUrl) {
		if (!watirHolder.isBrowserStarted()) {
			watirHolder.add("ie = Watir::IE.new");
			watirHolder.setBrowserStarted(true);
		}

		watirHolder.addSeparator();
		watirHolder.add("# URL start point");
		watirHolder.add("ie.goto(\"" + sp.getBeginAt() + "\")");
	}
	
}
