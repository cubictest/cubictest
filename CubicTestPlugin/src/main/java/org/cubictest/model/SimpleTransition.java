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
 * A transition that does nothing except connect two pages/states.
 * 
 * @author Christian Schwarz
 *
 */
public class SimpleTransition extends Transition {
	
	public SimpleTransition() {}
	
	public SimpleTransition(ConnectionPoint start, TransitionNode end) {
		super(start, end);
	}

	public SimpleTransition(TransitionNode start, TransitionNode end) {
		super(start, end);
	}
	
	@Override
	public void resetStatus() {
	}
}
