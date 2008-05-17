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
package org.cubictest.testutils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.cubictest.export.holders.IResultHolder;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.SubTest;
import org.cubictest.model.TestPartStatus;

/**
 * List used for asserting that object is present.
 * Also provides methods to check that the order is correct.
 * 
 * @author chr_schwarz
 */
public class AssertionList<T> extends ArrayList<T> implements IResultHolder {
	
	private static final long serialVersionUID = 1L;
	private int currentPosition = 0;
	private List<Object> assertedElements = new ArrayList<Object>();



	/**
	 * Checks that object is after the other objects asserted before this object. 
	 * @param obj the object to check
	 * @return
	 */
	public void assertContainsInOrder(Object obj) {
		assertedElements.add(obj);
		
		if (!get(currentPosition).equals((obj))) {
			throw new AssertionFailedError("\"" + obj + "\" is not in list with index " + currentPosition + getDebugInfo()); 
		}
		
		currentPosition++;
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
		return 
			"\nRequired position : " + currentPosition +
			"\nContents in list: " + this +
			"\nAsserted elements: " + assertedElements;
	}

	public void verifySize() {
		if (currentPosition < this.size()) {
			throw new AssertionFailedError("There were more elements in the converted list than were asserted in test."
					+ getDebugInfo());
		}		
	}


	public String toResultString() {
		return toString();
	}
	
	public void updateStatus(SubTest subTest, boolean hadException, ConnectionPoint targetConnectionPoint) {
	}

	public void updateStatus(ICustomTestStepHolder ctsh, TestPartStatus newStatus) {
	}

	public boolean shouldFailOnAssertionFailure() {
		return false;
	}

	public void popBreadcrumb() {
	}

	public void pushBreadcrumb(PropertyAwareObject element) {
	}



	public void resetStatus(PropertyAwareObject object) {
		// TODO Auto-generated method stub
	}

}
