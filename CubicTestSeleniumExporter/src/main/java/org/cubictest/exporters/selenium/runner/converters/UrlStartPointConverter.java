/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner.converters;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.UrlStartPoint;

/**
 * Class for converting UrlStartPoint to Selenium commands.
 * 
 * @author chr_schwarz
 */
public class UrlStartPointConverter implements IUrlStartPointConverter<SeleniumHolder> {
	
	
	public void handleUrlStartPoint(SeleniumHolder seleniumHolder, UrlStartPoint sp, boolean firstUrl) {
		if (seleniumHolder.getHandledUrlStartPoint() != null &&
				sp.getBeginAt().equals(seleniumHolder.getHandledUrlStartPoint().getBeginAt())) {
			//initial start point is opened by the SeleniumController. Reset it to make other paths in test open it again.
			seleniumHolder.setHandledUrlStartPoint(null);
			return;
		}
		
		//open URL:
		seleniumHolder.getSelenium().open(sp.getBeginAt(), "true");
	}
}
