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

import org.cubictest.common.exception.CubicException;
import org.eclipse.core.resources.IProject;

/**
 * Start point in test that includes another test before the current test.
 * The test must continue from an extension point is the preceding test.
 * 
 * @author Christian Schwarz
 */
public class ExtensionStartPoint extends SubTest implements IStartPoint {

	private String sourceExtensionPointPageId;
	private String sourceExtensionPointName;
	
	public ExtensionStartPoint(String filePath, IProject project) {
		super(filePath, project);
	}
	
	@Override
	public void setInTransition(Transition inTransition) {
		throw new CubicException("Setting of in transition of ExtensionStartPoint is not allowed");
	}

	public String getSourceExtensionPointName() {
		return sourceExtensionPointName;
	}

	public void setSourceExtensionPointName(String sourceExtensionPointName) {
		this.sourceExtensionPointName = sourceExtensionPointName;
	}

	public String getSourceExtensionPointPageId() {
		return sourceExtensionPointPageId;
	}

	public void setSourceExtensionPointPageId(String sourceExtensionPointPageId) {
		this.sourceExtensionPointPageId = sourceExtensionPointPageId;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": SourceExtensionPointPageId = " + sourceExtensionPointPageId +
			", sourceExtensionPointName = " + sourceExtensionPointName + ", SubTest = " + super.toString();
	}
}
