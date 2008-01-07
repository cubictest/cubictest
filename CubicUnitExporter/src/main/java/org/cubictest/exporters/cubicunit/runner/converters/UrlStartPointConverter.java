/*******************************************************************************
 * Copyright (c) 2005, 2008  Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
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
