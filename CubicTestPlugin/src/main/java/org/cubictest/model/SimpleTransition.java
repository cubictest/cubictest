/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

public class SimpleTransition extends Transition {
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
