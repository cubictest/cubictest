/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.utils;

import org.cubictest.persistence.CubicTestXStream;


/**
 * Creates a deep copy of an object using XStream.
 * 
 * @author Christian Schwarz
 */
public class CubicCloner {

	public static CubicTestXStream xStream;
	
	/**
	 * Returns a deep copy of an object using XStream.
	 */
	public static Object deepCopy(Object oldObj) {

		if (xStream == null) {
			xStream = new CubicTestXStream();
		}
		String xml = xStream.toXML(oldObj);
		Object newObj = xStream.fromXML(xml);

		return newObj;
	}

}
