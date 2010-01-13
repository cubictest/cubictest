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
package org.cubictest.common.exception;

/**
 * Base class for all CubicTest runtime exceptions.
 * 
 * @author chr_schwarz
 */
public class CubicException extends RuntimeException {

	
	private static final long serialVersionUID = 1L;

	public CubicException(Throwable cause) {
		super(cause);
	}

	public CubicException(String message) {
		super(message);
	}

	public CubicException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
