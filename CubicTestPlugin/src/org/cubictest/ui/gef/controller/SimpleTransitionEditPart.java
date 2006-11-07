/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
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
		// TODO Auto-generated method stub
		
	}
	

}
