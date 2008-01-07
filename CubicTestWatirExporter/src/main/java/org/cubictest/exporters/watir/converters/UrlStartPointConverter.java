/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
