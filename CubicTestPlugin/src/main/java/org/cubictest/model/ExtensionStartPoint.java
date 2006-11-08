/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import org.cubictest.common.exception.CubicException;


public class ExtensionStartPoint extends SubTest{

	private String sourceExtensionPointPageId;
	private String sourceExtensionPointName;
	
	public ExtensionStartPoint(String filePath) {
		super(filePath);
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
}
