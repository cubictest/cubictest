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
package org.cubictest.model.formElement;

import java.util.List;

import org.cubictest.model.FormElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;


/**
 * A button on the page. Can be any type of button, including image input button.
 * 
 * @author Christian Schwarz
 *
 */
public class Button extends FormElement {

	@Override
	public String getType() {
		return "Button";
	}

	@Override
	protected void setDefaultIdentifierValues() {
		getIdentifiers().get(0).setProbability(Identifier.MAX_PROBABILITY);
	}
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = super.getIdentifierTypes();
		list.add(IdentifierType.SRC);
		list.add(IdentifierType.ALT);
		return list;
	}

}
