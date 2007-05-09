/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder.utils;

/**
 * Text utils.
 * 
 * @author Christian Schwarz
 *
 */
public class TextUtil {

	public static String normalize(String s) {
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
}
