/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.exception.CubicException;


/**
 * Util for exception handling.
 * 
 * @author chr_schwarz
 *
 */
public class ExceptionUtil {

	public static void rethrow(Exception e) {
		e.printStackTrace();
		
		throw new CubicException(e.toString(), e);
	}

	public static void rethrow(Exception e, String message) {
		e.printStackTrace();
		
		if (StringUtils.isBlank(message)) {
			throw new CubicException(e.toString(), e);
		} else {
			throw new CubicException(message, e);
		}
	}
}
