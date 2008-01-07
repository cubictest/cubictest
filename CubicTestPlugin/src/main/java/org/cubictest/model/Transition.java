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
 * Abstract base class for all transitions.
 * 
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
		if (getEnd().hasInTransition()) {
			getEnd().removeInTransition();
		}
	}
}
