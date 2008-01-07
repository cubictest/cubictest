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
package org.cubictest.model;

/**
 * Class for holding track of incompatible model changes.
 * Use integer numbers.
 * 
 * The version number is independant of release versions, and can change frequently.
 * Should be updated as soon as incompatible changes are made.
 * Please also update the <code>LegacySupport</code> class to auto-convert old files to new standard.
 * 
 * @author chr_schwarz
 */
public class ModelInfo {

	public static String getCurrentModelVersion() {
		return "9";
	}
}
