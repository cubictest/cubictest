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
package org.cubictest.model.popup;

import org.cubictest.model.Text;


public class Confirm extends JavaScriptPopup{

	public Confirm() {
		elements.add(new Text());
		elements.add(new OKButton());
		elements.add(new CancelButton());
	}

	@Override
	public String getType() {
		return "Confirm";
	}
}
