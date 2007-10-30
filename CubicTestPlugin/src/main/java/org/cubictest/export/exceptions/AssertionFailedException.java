package org.cubictest.export.exceptions;


/**
 * Exception indicating that an assertion  of a page elemenet has failed.
 * 
 * @author Christian Schwarz
 *
 */
public class AssertionFailedException extends ExporterException {


	public AssertionFailedException(String message) {
		super(message);
	}

	public AssertionFailedException(String message, Exception cause) {
		super(message, cause);
	}

	public AssertionFailedException(Exception cause) {
		super(cause);
	}
	
	private static final long serialVersionUID = 1L;

}
