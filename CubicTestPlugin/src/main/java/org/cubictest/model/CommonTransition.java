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
 * Transition to connect a Common to a Page.
 * 
 * @author SK Skytteren
 */
public class CommonTransition extends Transition {

	public CommonTransition() {}
	
	/**
	 * @param start
	 * @param end
	 */
	public CommonTransition(Common start, Page end) {
		super(start, end);
	}

	@Override
	public void connect() {
		((Common)getStart()).addOutTransition(this);
		((Page)getEnd()).addCommonTransition(this);
	}
	
	@Override
	public void disconnect() {
		getStart().removeOutTransition(this);
		((Page)getEnd()).removeCommonTransition(this);
	}
}
