/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.exception;

public class ResourceNotCubicTestFileException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() {
		return "Resource is not an .aat file!";
	}
}
