/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.export.exceptions;


/**
 * Exception indicating that a test suite was empty.
 * 
 * @author Christian Schwarz
 *
 */
public class EmptyTestSuiteException extends ExporterException {

	private static final long serialVersionUID = 1L;


	public EmptyTestSuiteException() {
		super("Test suite was empty");
	}


}
