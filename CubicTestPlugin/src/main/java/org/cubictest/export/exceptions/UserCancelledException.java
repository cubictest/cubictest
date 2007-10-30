/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.exceptions;

import org.cubictest.common.exception.CubicException;

/**
 * Exception indicating user cancel.
 * 
 * @author Christian Schwarz
 */
public class UserCancelledException extends CubicException {

	private static final long serialVersionUID = 1L;

	public UserCancelledException(String message) {
		super(message);
	}
}
