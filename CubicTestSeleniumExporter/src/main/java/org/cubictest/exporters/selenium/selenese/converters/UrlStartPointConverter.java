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
	
	
	public void handleUrlStartPoint(SeleneseDocument doc, UrlStartPoint sp, boolean firstUrl) {
		doc.addCommand("open", sp.getBeginAt()).setDescription("Opening " + sp);
	}
}
