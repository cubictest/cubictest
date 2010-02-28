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
package org.cubictest.export.converters;

import org.cubictest.export.holders.IResultHolder;
import org.cubictest.model.PageElement;


/**
 * Interface for converters of page elements of a page to test steps. 
 * The test steps produced should verify that the particular page element is present.
 *
 * @author chr_schwarz
*/
public interface IPageElementConverter<T extends IResultHolder> {

	/**
	 * Converts a page element to a list of test steps verifying that the page element is present.
	 * @param e The page element
	 * @return a list of test steps verifying that the page element is present.
	 */
	public void handlePageElement(T t, PageElement e);
	
}
