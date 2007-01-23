/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.utils;

import junit.framework.TestCase;

import org.cubictest.model.Page;
import org.cubictest.model.Text;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.ui.gef.controller.ContextEditPart;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PageTextEditPart;

/**
 * Tests the ModelUtil class.
 * 
 * @author Christian Schwarz
 */
public class ModelUtilTest extends TestCase {

	
	public void testGetsPageSimple() {
		PageTextEditPart textPart = new PageTextEditPart(new Text());
		PageEditPart pagePart = new PageEditPart(new Page());
		textPart.setParent(pagePart);
		
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(textPart));
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(pagePart));
	}
	
	
	public void testGetsPageWithContext() {
		PageTextEditPart textPart = new PageTextEditPart(new Text());

		ContextEditPart contextPart = new ContextEditPart(new SimpleContext());
		textPart.setParent(contextPart);

		PageEditPart pagePart = new PageEditPart(new Page());
		contextPart.setParent(pagePart);
		
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(textPart));
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(contextPart));
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(pagePart));
	}
	
	
	public void testGetsPageWithTwoContexts() {
		PageTextEditPart textPart = new PageTextEditPart(new Text());

		ContextEditPart innerContextPart = new ContextEditPart(new SimpleContext());
		textPart.setParent(innerContextPart);

		ContextEditPart outerContextPart = new ContextEditPart(new SimpleContext());
		innerContextPart.setParent(outerContextPart);

		PageEditPart pagePart = new PageEditPart(new Page());
		
		outerContextPart.setParent(pagePart);
		
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(textPart));
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(innerContextPart));
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(outerContextPart));
		assertEquals(pagePart, ModelUtil.getSurroundingPagePart(pagePart));
	}
}
