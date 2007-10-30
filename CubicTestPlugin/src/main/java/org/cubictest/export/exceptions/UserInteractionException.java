package org.cubictest.export.exceptions;


/**
 * Exception indicating that a user interaction has failed and test cannot continue.
 * 
 * @author Christian Schwarz
 *
 */
public class UserInteractionException extends ExporterException {


	public UserInteractionException(String message) {
		super(message);
	}

	public UserInteractionException(String message, Exception cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
