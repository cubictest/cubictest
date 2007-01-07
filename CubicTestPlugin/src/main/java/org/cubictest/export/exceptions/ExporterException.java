package org.cubictest.export.exceptions;

/**
 * Base class for all CubicTest export exceptions.
 * 
 * @author Christian Schwarz
 *
 */
public class ExporterException extends RuntimeException {


	public ExporterException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
