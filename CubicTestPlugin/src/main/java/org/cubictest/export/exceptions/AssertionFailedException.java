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
