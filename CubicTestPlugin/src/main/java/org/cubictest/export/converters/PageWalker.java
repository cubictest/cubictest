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
import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.Page;

/**
 * Converts a page to a list of test stpes. Uses an common converter and a page
 * element converter to create the test steps.
 * 
 * @author ovstetun
 * @author chr_schwarz
 * @author skyt
 * 
 */
public class PageWalker<T extends IResultHolder> extends ContextWalker<T> {



	public PageWalker(Class<? extends IPageElementConverter<T>> pec,
			Class<? extends IContextConverter<T>> cc) {
		super(pec, cc);
	}

	public void handlePage(T resultHolder, Page page) {
		super.handleContext(resultHolder, page);
		for (CommonTransition at : page.getCommonTransitions()) {
			Common common = (Common) at.getStart();
			resultHolder.resetStatus(common);
			super.handleContext(resultHolder, common);
		}
	}
}
