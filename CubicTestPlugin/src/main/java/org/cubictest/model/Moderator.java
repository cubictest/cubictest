/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

/**
 * Identifier moderator (for constraints / attributes of the identifier).
 *
 * @author SK Skytteren
 */
public enum Moderator {
	END("ends with"),
	CONTAIN("contains"),
	BEGIN("begins with"),
	EQUAL("=");
	
	private String displayText;

	private Moderator(String displayText) {
		this.displayText = displayText;
	}
	
	public String getDisplayText() {
		return displayText;
	}

}
