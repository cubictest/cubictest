/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner.converters;

import static org.cubictest.model.IdentifierType.LABEL;

import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.export.exceptions.AssertionFailedException;
import org.cubictest.export.utils.exported.RunnerUtils;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.FormElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.formElement.Option;

import com.thoughtworks.selenium.SeleniumException;

/**
 * This class is responsible for asserting PageElements present. 
 * 
 * @author chr_schwarz
 */
public class PageElementConverter implements IPageElementConverter<SeleniumHolder> {	

	
	/**
	 * Asserts that page element is present on HTML page. 
	 * @param pe The Page element to convert to Selenese row.
	 */
	public void handlePageElement(SeleniumHolder seleniumHolder, PageElement pe) {
		try {
			if (pe instanceof Title) {
				String actual = seleniumHolder.getSelenium().getTitle();
				Identifier identifier = pe.getIdentifier(LABEL);
				String expected = identifier.getValue();
	
				if (RunnerUtils.pass(expected, actual, identifier.getModerator())) {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.PASS, pe.isNot());
				}
				else {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.FAIL, pe.isNot());
				}
			}
			else if (pe instanceof FormElement && !(pe instanceof Option)){
				//html input elements: get value
				String locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(pe);
				String value = seleniumHolder.getSelenium().getValue(locator);
				if (value == null) {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.FAIL, pe.isNot());
				}
				else {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.PASS, pe.isNot());
				}
			}
			else if (seleniumHolder.isInRootContext() && pe instanceof Text) {
				//texts in root context have bug in firefox xpath, use selenium's own function:
				boolean present = seleniumHolder.getSelenium().isTextPresent(pe.getText());
				if (present) {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.PASS, pe.isNot());
				}
				else {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.FAIL, pe.isNot());
				}
			}
			else {
				//all other elements: get text
				String locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(pe);
				String text = seleniumHolder.getSelenium().getText(locator);
				if (text == null) {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.FAIL, pe.isNot());
				}
				else {
					seleniumHolder.addResultByIsNot(pe, TestPartStatus.PASS, pe.isNot());
				}
			}
		}
		catch (SeleniumException e) {
			seleniumHolder.addResultByIsNot(pe, TestPartStatus.FAIL, pe.isNot());
		}
	}
}
