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
 * Start point that invokes/opens a URL.
 * 
 * @author skyttere
 *
 */
public class UrlStartPoint extends ConnectionPoint implements IStartPoint {
	private String beginAt = "";
	
	/**
	 * @return Returns the beginAt.
	 */
	public String getBeginAt() {
		return beginAt;
	}
	
	/**
	 * @param beginAt The beginAt to set.
	 */
	public void setBeginAt(String beginAt) {
		String oldBeginAt = this.beginAt;
		this.beginAt = beginAt;
		firePropertyChange(VALUE, oldBeginAt, beginAt);
	}
	
	@Override
	public void setInTransition(Transition inTransition) {
		inTransition.getStart().removeOutTransition(inTransition);
	}
	
	@Override
	public String toString() {
		return "URL: " + getBeginAt();
	}
}
