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
