/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.export.converters;

import org.cubictest.export.holders.IResultHolder;
import org.cubictest.model.UrlStartPoint;


/**
 * Interface for converters of a start points to test step(s).
 *  
 * @author chr_schwarz
 *
 */
public interface IUrlStartPointConverter<T extends IResultHolder> {
	
	/**
	 * Convert a StartPoint to a test step (e.g. invoke a specific URL).
	 * @param resultHolder the holder sent to each converter
	 * @param urlStartPoint the UrlStartPoint to convert.
	 * @param firstUrl is true for the initial UrlStartPoint, false for others
	 * @return a test step representing the start point.
	 */
	public void handleUrlStartPoint(T resultHolder, UrlStartPoint urlStartPoint, boolean firstUrl);

}
