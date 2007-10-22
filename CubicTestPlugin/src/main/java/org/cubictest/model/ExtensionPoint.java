/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/

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