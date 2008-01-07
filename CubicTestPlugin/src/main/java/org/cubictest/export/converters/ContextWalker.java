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

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.holders.IResultHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;

/**
 * Converter a common to a list of test steps. Uses a page element converter to
 * create the actual test steps.
 * 
 * @author ovstetun
 * @author chr_schwarz
 * @author skyt
 * 
 */
public class ContextWalker<T extends IResultHolder> {

	private Class<? extends IPageElementConverter<T>> pec;

	private final Class<? extends IContextConverter<T>> cc;

	public ContextWalker(Class<? extends IPageElementConverter<T>> pec,
			Class<? extends IContextConverter<T>> cc) {
		this.pec = pec;
		this.cc = cc;
	}

	public void handleContext(T t, IContext context) {
		try{
			IContextConverter<T> cc = this.cc.newInstance();
			cc.handlePreContext(t, context);
			for (PageElement pe : context.getRootElements()) {
	
				if (pe instanceof IContext) {
					new ContextWalker<T>(pec, this.cc).handleContext(t,
							(IContext) pe);
				} else {
					pec.newInstance().handlePageElement(t, pe);
				}
	
			}
			cc.handlePostContext(t, context);
		} catch (InstantiationException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		} catch (IllegalAccessException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		
	}
}
