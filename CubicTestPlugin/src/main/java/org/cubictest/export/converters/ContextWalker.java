/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
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
			for (PageElement pe : context.getElements()) {
	
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
