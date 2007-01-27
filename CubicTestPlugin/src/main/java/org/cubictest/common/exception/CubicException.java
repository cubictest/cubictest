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
	private Throwable cause;
	
	private CubicException() {
	}

	public CubicException(Throwable cause) {
		super(cause.toString());
		this.cause = cause;
	}

	public CubicException(String message) {
		super(message);
	}

	public CubicException(Throwable cause, String message) {
		super(message);
		this.cause = cause;
	}
	
	public String toString() {
		if (cause == null) {
			return this.getMessage();
		}
		return this.getMessage() + " (cause: " + cause.toString() + ")";
	}
}
