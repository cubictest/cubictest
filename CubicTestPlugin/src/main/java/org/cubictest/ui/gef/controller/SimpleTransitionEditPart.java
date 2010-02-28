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
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;

import org.cubictest.model.SimpleTransition;


/**
 * @author SK Skytteren
 * Contoller for the <code>SimpleTransition</code> model.
 *
 */
public class SimpleTransitionEditPart extends TransitionEditPart {

	/**
	 * Constructor for <code>TransitionEditPart</code>.
	 * @param transition the model.
	 */
	public SimpleTransitionEditPart(SimpleTransition transition) {
		super(transition);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		refreshVisuals();
	}
	

}
