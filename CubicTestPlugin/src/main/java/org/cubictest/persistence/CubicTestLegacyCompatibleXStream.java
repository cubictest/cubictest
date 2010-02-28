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
package org.cubictest.persistence;

/**
 * XStream subclass that adds support for reading old test files.
 * I.e. maps old elements to new classes.
 * Not feasible to use for objects in collections, as they will have 
 * the wrong name in the setter method in the class.
 * 
 * @author chr_schwarz
 */
public class CubicTestLegacyCompatibleXStream extends CubicTestXStream {

	
}
