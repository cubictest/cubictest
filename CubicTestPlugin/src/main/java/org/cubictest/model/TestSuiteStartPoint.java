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
 * Start point that does not invoke any actions. Can only be used in test suites.
 * 
 * @author Christian Schwarz
 */
public class TestSuiteStartPoint extends ConnectionPoint implements IStartPoint {
	
	@Override
	public void setInTransition(Transition inTransition) {
		inTransition.getStart().removeOutTransition(inTransition);
	}
	
}
