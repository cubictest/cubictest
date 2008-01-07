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
package org.cubictest.exporters.htmlPrototype.interfaces;

import java.io.File;
import java.util.Stack;

import org.cubictest.exporters.htmlPrototype.delegates.HtmlPageCreator;
import org.cubictest.model.Page;

public interface IPageConverter {

	public abstract HtmlPageCreator convert(Page page, HtmlPageCreator startOfPage, Stack<HtmlPageCreator> breadcrumbs, File outFolder, String prefix);

}