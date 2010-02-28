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
package org.cubictest.exporters.selenium.runner.converters;

import static org.cubictest.exporters.selenium.runner.converters.ConverterUtils.waitForElement;
import static org.cubictest.model.IdentifierType.LABEL;

import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.export.utils.exported.RunnerUtils;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.CubicWait;
import org.cubictest.model.Identifier;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.Text;
import org.cubictest.model.Title;

import com.thoughtworks.selenium.Wait.WaitTimedOutException;

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
		if (pe instanceof Title) {
			Identifier identifier = pe.getIdentifier(LABEL);
			String expected = identifier.getValue();
			String actual = seleniumHolder.getSelenium().getTitle();

			if (RunnerUtils.pass(expected, actual, identifier.getModerator())) {
				seleniumHolder.addResultByIsNot(pe, TestPartStatus.PASS, pe.isNot());
			}
			else {
				seleniumHolder.addResultByIsNot(pe, TestPartStatus.FAIL, pe.isNot());
			}
		}
		else if (seleniumHolder.isInRootContext() && pe instanceof Text) {
			//texts in root context have bug in firefox xpath, use selenium's own function:
			try {
				waitForText(seleniumHolder, pe.getText(), pe.isNot());
				seleniumHolder.addResult(pe, TestPartStatus.PASS);
			}
			catch (WaitTimedOutException e) {
				seleniumHolder.addResult(pe, TestPartStatus.FAIL);
			}
		}
		else {
			//all other elements
			try {
				String locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(pe);
				waitForElement(seleniumHolder, locator, pe.isNot());
				seleniumHolder.addResult(pe, TestPartStatus.PASS);
			}
			catch (WaitTimedOutException e) {
				seleniumHolder.addResult(pe, TestPartStatus.FAIL);
			}
		}
	}
	
	private void waitForText(final SeleniumHolder seleniumHolder, final String text, final boolean isNot) {
		new CubicWait() {
			public boolean until() {
				if (isNot) {
					return !seleniumHolder.getSelenium().isTextPresent(text);
				}
				else {
					return seleniumHolder.getSelenium().isTextPresent(text);
				}
			}
		}.wait("Text not found: " + text, seleniumHolder.getNextPageElementTimeout() * 1000);
	}
}
