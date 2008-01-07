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
package org.cubictest.export.converters;

import org.cubictest.export.holders.IResultHolder;
import org.cubictest.model.context.IContext;


/**
 * Interface for converters of an common to a list of test steps.
 * 
 * @author chr_schwarz
 */
public interface IContextConverter<T extends IResultHolder> {
	
	/**
	 * Handle entry into a new context.
	 */
	public PreContextHandle handlePreContext(T t,IContext a);
	
	
	/**
	 * Handle exit from context.
	 */
	public PostContextHandle handlePostContext(T t,IContext a);
}
