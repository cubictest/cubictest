/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.utils;

/**
 * Exception indicating user cancel.
 * 
 * @author Christian Schwarz
 */
public class UserCancelledException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserCancelledException(String message) {
		super(message);
	}
}
