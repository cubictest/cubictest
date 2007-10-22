/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
