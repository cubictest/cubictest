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
package org.cubictest.export.holders;

import static org.junit.Assert.assertEquals;

import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.TextArea;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Context Holder.
 * 
 * @author Christian Schwarz
 *
 */
public class ContextHolderTest {

	ContextHolder holder;
	SimpleContext outerContext;
	SimpleContext innerContext1;
	SimpleContext innerContext2;
	Link link;
	TextArea textArea;
	Image image;
	
	@Before
	public void setUp() throws CoreException {
		holder = new ContextHolder() {
			public void resetStatus(PropertyAwareObject object) {
			}
			public void setTestName(String testName) {
			}
		};

		outerContext = new SimpleContext();
		Identifier id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("outerId");
		id.setProbability(100);
		outerContext.addIdentifier(id);

		innerContext1 = new SimpleContext();
		id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("inner1Id");
		id.setProbability(100);
		innerContext1.addIdentifier(id);

		innerContext2 = new SimpleContext();
		id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("inner2Id");
		id.setProbability(100);
		innerContext2.addIdentifier(id);

		link = new Link();
		id = new Identifier();
		id.setType(IdentifierType.HREF);
		id.setValue("linkHref");
		id.setProbability(100);
		link.addIdentifier(id);
		
		textArea = new TextArea();
		id = new Identifier();
		id.setType(IdentifierType.NAME);
		id.setValue("textAreaName");
		id.setProbability(100);
		textArea.addIdentifier(id);
		
		image = new Image();
		id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("imageId");
		id.setProbability(100);
		image.addIdentifier(id);
	}
		
	@Test
	public void testSingleElement() {
		String exp = "//a[@href=\'linkHref\']";
		assertEquals(exp, holder.getFullContextWithAllElements(link));
	}
	
	@Test
	public void testContextWithOneElement() {
		outerContext.addElement(link);
		holder.pushContext(outerContext);
		String exp = "/descendant-or-self::*[@id=\'outerId\']/descendant-or-self::a[@href=\'linkHref\']";
		assertEquals(exp, holder.getFullContextWithAllElements(link));
	}
	
	@Test
	public void testNestedContextWithOneElement() {
		outerContext.addElement(innerContext1);
		innerContext1.addElement(link);
		holder.pushContext(outerContext);
		holder.pushContext(innerContext1);
		String exp = "/descendant-or-self::*[@id=\'outerId\']/descendant-or-self::*[@id=\'inner1Id\']/descendant-or-self::a[@href=\'linkHref\']";
		assertEquals(exp, holder.getFullContextWithAllElements(link));
	}
	
	

	
	@Test
	public void testContextWithThreeElements() {
		outerContext.addElement(image);
		outerContext.addElement(textArea);
		holder.pushContext(outerContext);

		String expAll = "/descendant-or-self::*[@id=\'outerId\'][descendant-or-self::textarea[@name=\'textAreaName\']]/descendant-or-self::img[@id=\'imageId\']";
		assertEquals(expAll, holder.getFullContextWithAllElements(image));
	}
	
	
	@Test
	public void testNestedContextWithThreeElements() {
		outerContext.addElement(innerContext1);
		innerContext1.addElement(image);
		innerContext1.addElement(textArea);
		innerContext1.addElement(link);
		holder.pushContext(outerContext);
		holder.pushContext(innerContext1);

		String expAll = "/descendant-or-self::*[@id=\'outerId\']/descendant-or-self::*[@id=\'inner1Id\'][descendant-or-self::textarea[@name=\'textAreaName\']][descendant-or-self::a[@href=\'linkHref\']]/descendant-or-self::img[@id=\'imageId\']";
		assertEquals(expAll, holder.getFullContextWithAllElements(image));
	}
	
	@Test
	public void testNestedContextWithThreeInnerContexts() throws CloneNotSupportedException {
		SimpleContext outerContext1 = (SimpleContext) outerContext.clone();
		outerContext1.addElement(innerContext1);
		outerContext1.addElement(link);

		SimpleContext outerContext2 = (SimpleContext) outerContext.clone();
		outerContext2.addElement(innerContext2);
		outerContext2.addElement(image);

		Link innerLink = new Link();
		Identifier id = new Identifier();
		id.setType(IdentifierType.ID);
		id.setValue("innerLinkId");
		id.setProbability(100);
		innerLink.addIdentifier(id);
		
		innerContext2.addElement(innerLink);
		innerContext2.addElement(textArea);

		SimpleContext outerContext3 = (SimpleContext) outerContext.clone();
		SimpleContext innerContext3 = (SimpleContext) innerContext1.clone();
		outerContext3.addElement(innerContext3);
		outerContext3.addElement(textArea);

		holder.pushContext(outerContext2);
		holder.pushContext(innerContext2);

		String expAll =  "/descendant-or-self::*[@id=\'outerId\'][descendant-or-self::img[@id=\'imageId\']]/descendant-or-self::*[@id=\'inner2Id\'][descendant-or-self::textarea[@name=\'textAreaName\']]/descendant-or-self::a[@id=\'innerLinkId\']";
		assertEquals(expAll, holder.getFullContextWithAllElements(innerLink));
	}
	
	@Test
	public void testSingleChildElementInclusionWhenRootContextIsTargeted() {
		outerContext.addElement(innerContext1);
		innerContext1.addElement(link);
		String exp = "//*[@id=\'outerId\'][descendant-or-self::*[@id=\'inner1Id\'][descendant-or-self::a[@href=\'linkHref\']]]";
		assertEquals(exp, holder.getFullContextWithAllElements(outerContext));
	}

	@Test
	public void testMultipleChildElementsInclusionWhenRootContextIsTargeted() {
		outerContext.addElement(link);
		outerContext.addElement(textArea);
		String exp = "//*[@id=\'outerId\'][descendant-or-self::a[@href=\'linkHref\']][descendant-or-self::textarea[@name=\'textAreaName\']]";
		assertEquals(exp, holder.getFullContextWithAllElements(outerContext));
	}
	
	@Test
	public void testMultipleRecursiveChildElementsInclusionWhenRootContextIsTargeted() {
		outerContext.addElement(image);
		outerContext.addElement(innerContext1);
		innerContext1.addElement(textArea);
		innerContext1.addElement(link);
		String exp = "//*[@id=\'outerId\'][descendant-or-self::img[@id=\'imageId\']][descendant-or-self::*[@id=\'inner1Id\'][descendant-or-self::textarea[@name=\'textAreaName\']][descendant-or-self::a[@href=\'linkHref\']]]";
		assertEquals(exp, holder.getFullContextWithAllElements(outerContext));
	}

}
