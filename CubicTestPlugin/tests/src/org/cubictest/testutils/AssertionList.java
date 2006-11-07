/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.testutils;

import java.util.ArrayList;

import junit.framework.AssertionFailedError;

/**
 * List used for asserting that object is present.
 * Also provides methods to check that the order is correct.
 * 
 * @author chr_schwarz
 */
public class AssertionList<T> extends ArrayList<T> {
	
	private static final long serialVersionUID = 1L;
	private int knownMinPosition = 0;

	public void resetCounter() {
		knownMinPosition = 0;
	}
	/**
	 * Checks that object is after the other objects asserted before this object. 
	 * @param obj the object to check
	 * @return
	 */
	public void assertContainsInOrder(Object obj) {
		
		if (!contains(obj))
			throw new AssertionFailedError("\"" + obj + "\" is not in list." + getDebugInfo()); 
		
		int currentPosition = indexOf(obj);
		if (currentPosition < knownMinPosition) {
			throw new AssertionFailedError("\"" + obj + "\" was before another object that was already asserted." + getDebugInfo());
		}
		knownMinPosition = currentPosition + 1;
	}
	
	
	/**
	 * Checks that object is present in list. 
	 * @param obj the object to check
	 * @return
	 */
	public void assertContains(Object obj) {
		
		if (!contains(obj))
			throw new AssertionFailedError("\"" + obj + "\" is not in list." + getDebugInfo()); 

		int currentPosition = indexOf(obj);
		if (currentPosition > knownMinPosition) {
			knownMinPosition = currentPosition + 1;
		}

	}
	
	/**
	 * Checks that object is <i>not</i> present in list. 
	 * @param obj the object to check
	 * @return
	 */
	public void assertNotContains(Object obj) {
		
		if (contains(obj))
			throw new AssertionFailedError("\"" + obj + "\" was present in list." + getDebugInfo()); 
	}
	
	
	public String getDebugInfo() {
		return "\nElements asserted in assertionList: " + subList(0, knownMinPosition) + 
			"\nMin pos: " + knownMinPosition +
			"\nContents in list: " + this;
	}
}
