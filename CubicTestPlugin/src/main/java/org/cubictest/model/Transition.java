/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

/**
 * @author trond
 *
 */
public abstract class Transition extends PropertyAwareObject {
	private TransitionNode start;
	private TransitionNode end;
	
	protected Transition(){}
	
	protected Transition(TransitionNode start, TransitionNode end) {
		setStart(start);
		setEnd(end);
	}
	
	/**
	 * @return Returns the end.
	 */
	public TransitionNode getEnd() {
		return end;
	}
	/**
	 * @param end The end to set.
	 */
	public void setEnd(TransitionNode end) {
		this.end = end;
	}
	/**
	 * @return Returns the start.
	 */
	public TransitionNode getStart() {
		return start;
	}
	/**
	 * @param start The start to set.
	 */
	public void setStart(TransitionNode start) {
		this.start = start;
	}
	
	@Override
	public void resetStatus() {
	}

	public void connect() {
		getStart().addOutTransition(this);
		getEnd().setInTransition(this);
	}

	public void disconnect() {
		getStart().removeOutTransition(this);
		getEnd().removeInTransition();
	}
}
