/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.model;

/**
 * Status of test element (e.g. page element) after running a test.
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
