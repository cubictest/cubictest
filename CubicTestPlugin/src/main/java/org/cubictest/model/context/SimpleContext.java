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
package org.cubictest.model.context;

import java.util.List;

import org.cubictest.model.IdentifierType;

/**
 * Generic context element, default implementation of AbstractContext.
 * Can be any parent HTML element.
 * 
 * @author Christian Schwarz
 *
 */
public class SimpleContext extends AbstractContext {
	
	@Override
	public String getType(){
		return "Context";
	}
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {
		List<IdentifierType> list = super.getIdentifierTypes();
		return list;
	}
}
