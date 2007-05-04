/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.exception;

public class TestNotFoundException extends CubicException {
	private static final long serialVersionUID = 1L;
	
	public TestNotFoundException(String message) {
		super(message);
	}
	
}
