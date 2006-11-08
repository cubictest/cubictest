/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.exception;

/**
 * Exception for encountered unknown extension point.
 * @author chr_schwarz
 */
public class UnknownExtensionPointException extends CubicException {

	private static final long serialVersionUID = 1L;

	public UnknownExtensionPointException(String message) {
		super(message);
	}

}
