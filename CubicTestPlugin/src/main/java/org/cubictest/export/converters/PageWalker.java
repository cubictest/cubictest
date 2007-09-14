/*
 * Created on Apr 21, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
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

	public void handlePage(T t, Page page) {
		super.handleContext(t, page);
		for (CommonTransition at : page.getCommonTransitions()) {
			Common common = (Common) at.getStart();
			super.handleContext(t, common);
		}
	}
}
