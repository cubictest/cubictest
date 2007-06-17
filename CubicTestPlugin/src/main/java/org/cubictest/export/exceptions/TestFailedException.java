package org.cubictest.export.exceptions;

import org.cubictest.common.exception.CubicException;

/**
 * Base class for all CubicTest test failure exceptions.
 * 
 * @author Christian Schwarz
 *
 */
public class TestFailedException extends CubicException {


	public TestFailedException(String message) {
		super(message);
	}

	public TestFailedException(String message, Exception cause) {
		super(cause, message);
	}

	private static final long serialVersionUID = 1L;

}
