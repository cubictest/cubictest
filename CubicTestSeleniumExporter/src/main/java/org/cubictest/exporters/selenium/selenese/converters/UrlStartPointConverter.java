/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.selenium.selenese.converters;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.cubictest.model.UrlStartPoint;

/**
 * Class for converting UrlStartPoint to Selenese row.
 * 
 * @author chr_schwarz
 */
public class UrlStartPointConverter implements IUrlStartPointConverter<SeleneseDocument> {
	
	
	public void handleUrlStartPoint(SeleneseDocument doc, UrlStartPoint sp) {
		doc.addCommand("open", sp.getBeginAt()).setDescription("Opening " + sp);
	}
}
