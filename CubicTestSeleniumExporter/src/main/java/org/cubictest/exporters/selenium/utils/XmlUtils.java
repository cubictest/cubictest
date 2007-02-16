/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.utils;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlUtils {
	public static XMLOutputter getNewXmlOutputter() {
		Format format = Format.getPrettyFormat();
		format.setEncoding("ISO-8859-1");		
		return new XMLOutputter(format);
	}
	
}
