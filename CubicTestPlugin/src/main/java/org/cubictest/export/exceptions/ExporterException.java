package org.cubictest.export.exceptions;

import org.cubictest.common.exception.CubicException;

/**
 * Base class for all CubicTest export exceptions.
 * 
 * @author Christian Schwarz
 *
 */
public class ExporterException extends CubicException {


	public ExporterException(String message) {
		super(message);
	}

	public ExporterException(String message, Exception cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
