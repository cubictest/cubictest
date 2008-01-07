/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - bug fixes and usability improvements
 *******************************************************************************/
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
