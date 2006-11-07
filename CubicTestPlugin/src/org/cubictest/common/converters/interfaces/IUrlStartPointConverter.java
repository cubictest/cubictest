/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.converters.interfaces;

import org.cubictest.model.UrlStartPoint;


/**
 * Interface for converters of a start points to test step(s).
 *  
 * @author chr_schwarz
 *
 */
public interface IUrlStartPointConverter<T> {
	
	/**
	 * Convert a StartPoint to a test step (e.g. invoke a specific URL).
	 * @param urlStartPoint the UrlStartPoint to convert.
	 * @return a test step representing the start point.
	 */
	public void handleUrlStartPoint(T t, UrlStartPoint urlStartPoint);

}
