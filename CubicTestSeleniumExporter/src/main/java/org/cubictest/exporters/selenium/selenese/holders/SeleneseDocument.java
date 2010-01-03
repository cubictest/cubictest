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
package org.cubictest.exporters.selenium.selenese.holders;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.holders.ContextHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.exporters.selenium.utils.XmlUtils;
import org.cubictest.model.PropertyAwareObject;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Selenium (Selenese) step list. HTML document with test steps (commands / rows).
 * 
 * @author chr_schwarz
 */
public class SeleneseDocument extends ContextHolder {

	/** The root element of the document */
	private Element rootElement;
	
	/** The table to add commands (rows) to */
	private Element table;

	private Element htmlPageTitle;
	
	
	/**
	 * Public constructor.
	 */
	public SeleneseDocument() {
		super();
		setUpHtmlPage();
	}

	@Override
	public void setSettings(CubicTestProjectSettings settings) {
		super.setSettings(settings);
		table.addContent(new Command("setTimeout", (SeleniumUtils.getTimeout(settings) * 1000) + ""));
	}

	
	/**
	 * Add command (with value).
	 */
	public Command addCommand(String commandName, String target, String value) {
		Command command = new Command(commandName, target, value);
		table.addContent(command);
		return command;
	}

	
	/**
	 * Add command (without value).
	 */
	public Command addCommand(String commandName, String target) {
		return addCommand(commandName, target, "");
	}
	
	
	/**
	 * Get string representation of the document (for e.g. file write).
	 */
	@Override
	public String toResultString() {
		Document document = new Document(rootElement);
		return XmlUtils.getNewXmlOutputter().outputString(document);
	}
	

	
	
	/**
	 * Sets up a HTML page for the tests, including a table for the commands.
	 */
	private void setUpHtmlPage() {
		rootElement = new Element("html");
		
		Element header = new Element("head");
		htmlPageTitle = new Element("title");
		header.addContent(htmlPageTitle);
		
		Element style = new Element("style");
		style.setAttribute("type", "text/css");
		style.setText(
				"td {padding-right: 25px}\n" + 
				".comment {color: blue; padding-top: 10px;}\n");
		header.addContent(style);
		
		rootElement.addContent(header);
		
		Element body = new Element("body");
		rootElement.addContent(body);
		table = new Element("table");
		table.setAttribute("border", "1");
		table.setAttribute("cellspacing", "0");
		body.addContent(table);
	}



	public void resetStatus(PropertyAwareObject object) {
		//not applicable
	}


	public void setTestName(String testName) {
		htmlPageTitle.setText(testName);
	}
}