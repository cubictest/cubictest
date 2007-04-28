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

	
	/**
	 * Returns a deep copy of an object using XStream.
	 */
	public static Object deepCopy(Object oldObj) {

		String xml = new CubicTestXStream().toXML(oldObj);
		Object newObj = new CubicTestXStream().fromXML(xml);

		return newObj;
	}

}
