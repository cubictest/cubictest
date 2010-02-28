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
package org.cubictest.export.holders;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.SubTest;

/**
 * Holds the exported test.
 * Has method for getting the final result of the export as a string.
 * 
 * @author chr_schwarz 
 *
 */
public interface IResultHolder {

	/**
	 * Get the final String representation of the result holder (after export is done).
	 */
	public String toResultString();
	

	/**
	 * Update status on the passed in sub test.
	 * @param subTest
	 */
	public void updateStatus(SubTest subTest, boolean hadException, ConnectionPoint targetConnectionPoint);
	
	
	/**
	 * Get whether runner should fail on assertion failure.
	 * @return
	 */
	public boolean shouldFailOnAssertionFailure();
	
	
	/**
	 * Add element to test walker breadcrumbs (trackbacks). 
	 * @param element
	 */
	public void pushBreadcrumb(PropertyAwareObject element);

	/**
	 * Remove element from test walker breadcrumbs (trackbacks).
	 */ 
	public void popBreadcrumb();
	
	/**
	 * Reset test run status for element.
	 */
	public void resetStatus(PropertyAwareObject object);

	public void setTestName(String testName);
	
	public void setSettings(CubicTestProjectSettings settings);
}
