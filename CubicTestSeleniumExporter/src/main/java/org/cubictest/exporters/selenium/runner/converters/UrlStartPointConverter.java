/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.selenium.runner.converters;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.UrlStartPoint;

/**
 * Class for converting UrlStartPoint to Selenese row.
 * 
 * @author chr_schwarz
 */
public class UrlStartPointConverter implements IUrlStartPointConverter<SeleniumHolder> {
	
	
	public void handleUrlStartPoint(SeleniumHolder seleniumHolder, UrlStartPoint sp) {
		seleniumHolder.getSelenium().open(sp.getBeginAt());
	}
}
