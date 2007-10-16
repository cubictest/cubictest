/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.exception;

/**
 * Base class for all CubicTest runtime exceptions.
 * 
 * @author chr_schwarz
 */
public class CubicException extends RuntimeException {

	
	private static final long serialVersionUID = 1L;

	public CubicException(Throwable cause) {
		super(cause);
	}

	public CubicException(String message) {
		super(message);
	}

	public CubicException(Throwable cause, String message) {
		super(message, cause);
	}
	
}
