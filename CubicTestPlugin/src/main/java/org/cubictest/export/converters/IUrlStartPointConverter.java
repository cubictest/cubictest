/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
