/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.htmlPrototype.utils;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * Util class for model objects.
 * 
 * @author chr_schwarz
 */
public class TextUtil {

	/**
	 * Converts a text string to camelCase notation with initial letter set to small.
	 * See also the JUnit test case.
	 * 
	 * @param name The string to convert
	 * @return The camelCase version.
	 */
	public static String camel(String name) {
		StringBuffer b = new StringBuffer(name.length());
		StringTokenizer t = new StringTokenizer(name);
		if (!t.hasMoreTokens())
			return convertToAscii(name);;
		
		String firstToken = t.nextToken();

		if (!t.hasMoreTokens()) {
			String result = firstToken.substring(0, 1).toLowerCase() + firstToken.substring(1);
			return convertToAscii(result);
		}
		
		b.append(firstToken.substring(0, 1).toLowerCase());
		b.append(firstToken.substring(1).toLowerCase());

		while (t.hasMoreTokens()) {
			String token = t.nextToken();
			b.append(token.substring(0, 1).toUpperCase());
			b.append(token.substring(1).toLowerCase());
		}

		return convertToAscii(b.toString());
	}

	public static String convertToAscii(String s) {
		s = StringUtils.replace(s, "æ", "a");
		s = StringUtils.replace(s, "ø", "o");
		s = StringUtils.replace(s, "å", "a");
		s = StringUtils.replace(s, "Æ", "A");
		s = StringUtils.replace(s, "Ø", "O");
		s = StringUtils.replace(s, "Å", "A");

		s = StringUtils.replace(s, "ä", "a");
		s = StringUtils.replace(s, "ö", "o");
		s = StringUtils.replace(s, "ü", "u");
		s = StringUtils.replace(s, "Ä", "A");
		s = StringUtils.replace(s, "Ö", "O");
		s = StringUtils.replace(s, "Ü", "U");
		return s;
	}
}
