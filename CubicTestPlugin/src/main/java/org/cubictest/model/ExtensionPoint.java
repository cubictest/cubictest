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
 * An exposed state/node that other tests can continue from.
 *  
 * @author Christian Schwarz
 *
 */
public class ExtensionPoint extends ConnectionPoint implements Comparable<ExtensionPoint>{

	public String getPageId() {
		return getPage().getId();
	}
	
	public Page getPage() {
		if (this.getInTransition() != null) {
			return (Page) this.getInTransition().getStart();
		}
		else {
			return null;
		}
	}
	@Override
	public String getName() {
		if (getPage() != null) {
			return getPage().getName() + " Extension Point";
		}
		else {
			return "Extension Point";
		}
	}
	
	@Override
	public void addOutTransition(Transition transition) {
		transition.disconnect();
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": Name = " + getName();
	}

	public int compareTo(ExtensionPoint other) {
		return getName().compareTo(other.getName());
	}
}