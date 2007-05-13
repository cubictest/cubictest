/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

/**
 * Status of test part after running a test.
 * 
 * @author chr_schwarz
 */
public enum TestPartStatus {
	
	/** Status unknown / test not run */
	UNKNOWN,
	
	/** Test part passed test */
	PASS,
	
	/** Test part passed with some warning */
	WARN,

	/** Test part failed test */
	FAIL,
	
	/** Exception when running test / test part */
	EXCEPTION
}
