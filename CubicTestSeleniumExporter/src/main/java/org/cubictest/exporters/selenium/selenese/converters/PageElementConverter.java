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
package org.cubictest.exporters.selenium.selenese.converters;

import static org.cubictest.model.IdentifierType.LABEL;

import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;

/**
 * This class is responsible for converting PageElements to Selenese rows 
 * asserting that elements are present. 
 * 
 * @author Christian Schwarz
 */
public class PageElementConverter implements IPageElementConverter<SeleneseDocument> {	

	
	/**
	 * Asserts that page element is present on HTML page. 
	 * @param pe The Page element to convert to Selenese row.
	 */
	public void handlePageElement(SeleneseDocument doc, PageElement pe) {

		if (pe instanceof Title) {
			doc.addCommand("verifyTitle", pe.getIdentifier(LABEL).getValue()).setDescription("Check present = " + pe);
		}
		else if (doc.isInRootContext() && pe instanceof Text) {
			//texts in root context have bug in firefox xpath, use selenium's own function:
			if (pe.isNot()) {
				doc.addCommand("waitForTextNotPresent", pe.getText()).setDescription("Check NOT present: " + pe);
			}
			else {
				doc.addCommand("waitForTextPresent", pe.getText()).setDescription("Check present: " + pe);
			}
		}
		else {
			//all other elements
			String locator = "xpath=" + doc.getFullContextWithAllElements(pe);
			if (pe.isNot()) {
				doc.addCommand("waitForElementNotPresent", locator).setDescription("Check NOT present: " + pe);
			}
			else {
				doc.addCommand("waitForElementPresent", locator).setDescription("Check present: " + pe);
			}
		}
	}
}
