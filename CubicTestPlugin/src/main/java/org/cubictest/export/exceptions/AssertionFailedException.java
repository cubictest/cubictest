package org.cubictest.export.exceptions;

import org.cubictest.common.exception.CubicException;

/**
 * Exception indicating that an assertion has failed.
 * 
 * @author Christian Schwarz
 *
 */
public class AssertionFailedException extends CubicException {


	public AssertionFailedException(String message) {
		super(message);
	}

	public AssertionFailedException(String message, Exception cause) {
		super(cause, message);
	}

	private static final long serialVersionUID = 1L;

}
