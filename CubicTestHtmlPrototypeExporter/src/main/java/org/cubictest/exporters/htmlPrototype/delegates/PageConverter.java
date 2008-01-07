/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Erlend S. Halvorsen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Erlend S. Halvorsen - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.htmlPrototype.delegates;

import java.io.File;
import java.util.Stack;

import org.cubictest.exporters.htmlPrototype.interfaces.IPageConverter;
import org.cubictest.exporters.htmlPrototype.interfaces.IPageElementConverter;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.UserInteractionsTransition;


public class PageConverter implements IPageConverter {
	private IPageElementConverter pageElementConverter;
	private String extension;
	
	public PageConverter(String extension, IPageElementConverter pageElementConverter) {
		this.pageElementConverter = pageElementConverter;
		this.extension = extension;
	}
	
	public static boolean isNewPageTransition(Transition transition) {
		if(transition instanceof UserInteractionsTransition) {
			return ((UserInteractionsTransition) transition).hasUserInteractions();
		}
		return false;
	}
		
	public HtmlPageCreator convert(Page page, HtmlPageCreator pageStart, Stack<HtmlPageCreator> breadcrumbs, File outFolder, String prefix) {
		if(pageStart == null || isNewPageTransition(page.getInTransition())) {
			File pageFile = new File(outFolder, HtmlPageCreator.filenameFor(page, extension, prefix));
			pageStart = new HtmlPageCreator(pageFile, pageElementConverter, extension, prefix, page.getName());
		}
		
		pageStart.convert(page, breadcrumbs);
		return pageStart;
	}
}
