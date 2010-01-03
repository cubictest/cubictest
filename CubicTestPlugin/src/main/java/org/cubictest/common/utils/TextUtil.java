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
package org.cubictest.common.utils;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringEscapeUtils;
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
		//we can't save in svn the characters we want, so we get it from unescaping html entities
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&aring;"), "a");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&oslash;"), "o");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&aelig;"), "a");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&Aring;"), "A");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&Oslash;"), "O");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&AElig;"), "A");

		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&auml;"), "a");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&ouml;"), "o");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&uuml;"), "u");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&Auml;"), "A");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&Ouml;"), "O");
		s = StringUtils.replace(s, StringEscapeUtils.unescapeHtml("&Uuml;"), "U");
		return s;
	}
	
	/**
	 * Normalizes space, i.e. trims and removes all excessive white space 
	 * in string except single spaces.
	 * @param s
	 * @return
	 */
	public static String normalizeAndTrim(String s) {
		if (StringUtils.isBlank(s)) {
			return "";
		}
		
		String res = "";
		char[] chars = s.toCharArray();
		boolean hasWhite = true;
		for (int i = 0; i < chars.length; i++) {
			if (Character.isWhitespace(chars[i])) {
				if (hasWhite) {
					continue;
				}
				else {
					res += chars[i];
					hasWhite = true;
				}
			}
			else {
				res += chars[i];
				hasWhite = false;
			}
		}
		return res.trim();
	}

	public static String stripHtmlTags(String s) {
		return s.replaceAll("<[^>]+>", ""); 
	}

	public static String normalizeWithPossibleSingleTrailingSpace(String s) {
		if (StringUtils.isBlank(s)) {
			return "";
		}
		char originalLastChar = StringUtils.right(s, 1).toCharArray()[0];
		return normalizeAndTrim(s) + (Character.isWhitespace(originalLastChar) ? " " : "");
	}
}
