/*
 * Created on 04.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.exporters.cubicunit.runner.converters;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.model.UrlStartPoint;
import org.cubicunit.Browser;
import org.cubicunit.Document;
import org.cubicunit.WebTester;
import org.cubicunit.internal.selenium.SeleniumBrowserType;

public class UrlStartPointConverter implements IUrlStartPointConverter<Holder> {

	public void handleUrlStartPoint(Holder holder, UrlStartPoint urlStartPoint, boolean firstUrl) {
		if (!firstUrl)
			return;
		holder.setHandledUrlStartPoint(urlStartPoint);
		String url = urlStartPoint.getBeginAt();
		int port = holder.getPort();
		SeleniumBrowserType browserType = holder.getBrowserType();
		
		Browser browser = WebTester.selenium(browserType,port);
		browser.setVisible(true);
		holder.addBrowser(browser);
		
		Document document = browser.goTo(url);
		holder.setContainer(document);
		
	}
}
