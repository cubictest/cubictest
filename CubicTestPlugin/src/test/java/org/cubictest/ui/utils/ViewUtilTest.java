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
package org.cubictest.ui.utils;

import junit.framework.TestCase;

import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.Page;
import org.cubictest.model.Text;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.ui.gef.controller.ContextEditPart;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PageTextEditPart;

/**
 * Tests the ViewUtil class.
 * 
 * @author Christian Schwarz
 */
public class ViewUtilTest extends TestCase {

	
	public void testGetsPageSimple() {
		PageTextEditPart textPart = new PageTextEditPart(new Text());
		PageEditPart pagePart = new PageEditPart(new Page());
		textPart.setParent(pagePart);
		
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(textPart));
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(pagePart));
	}
	
	
	public void testGetsPageWithContext() {
		PageTextEditPart textPart = new PageTextEditPart(new Text());

		ContextEditPart contextPart = new ContextEditPart(new SimpleContext());
		textPart.setParent(contextPart);

		PageEditPart pagePart = new PageEditPart(new Page());
		contextPart.setParent(pagePart);
		
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(textPart));
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(contextPart));
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(pagePart));
	}
	
	
	public void testGetsPageWithTwoContexts() {
		PageTextEditPart textPart = new PageTextEditPart(new Text());

		ContextEditPart innerContextPart = new ContextEditPart(new SimpleContext());
		textPart.setParent(innerContextPart);

		ContextEditPart outerContextPart = new ContextEditPart(new SimpleContext());
		innerContextPart.setParent(outerContextPart);

		PageEditPart pagePart = new PageEditPart(new Page());
		
		outerContextPart.setParent(pagePart);
		
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(textPart));
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(innerContextPart));
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(outerContextPart));
		assertEquals(pagePart, ViewUtil.getSurroundingPagePart(pagePart));
	}

}
