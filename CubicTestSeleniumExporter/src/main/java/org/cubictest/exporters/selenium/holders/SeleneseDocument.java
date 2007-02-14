/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.holders;

import java.util.Stack;

import org.cubictest.common.utils.XmlUtils;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Selenium (Selenese) step list. HTML document with test steps (commands / rows).
 * 
 * @author chr_schwarz
 */
public class SeleneseDocument {

	/** The root element of the document */
	private Element rootElement;
	
	/** The table to add commands (rows) to */
	private Element table;
	
	/** Context is the XPath starting point (path) for lookup of elements */
	private Stack<String> context = new Stack<String>(); 

	/**
	 * Public constructor.
	 */
	public SeleneseDocument() {

		rootElement = new Element("html");

		Element header = new Element("head");
		Element title = new Element("title");
		title.setText("CubicTest exported test");
		header.addContent(title);
		rootElement.addContent(header);

		Element body = new Element("body");
		rootElement.addContent(body);

		table = new Element("table");
		body.addContent(table);
		
		//root context is anywhere in the document ("//" in XPath)
		context.push("//");
	}

	
	/**
	 * Add command-row to command table (with value).
	 */
	public Command addCommand(String commandName, String target, String value) {
		Command command = new Command(commandName, target, value);
		table.addContent(command);
		return command;
	}

	/**
	 * Add command-row to command table (without value).
	 */
	public Command addCommand(String commandName, String target) {
		Command command = new Command(commandName, target, "");
		table.addContent(command);
		return command;
	}
	
	
	/**
	 * Get string representation of the document (for e.g. file write).
	 */
	public String toString() {
		Document document = new Document(rootElement);
		return XmlUtils.getNewXmlOutputter().outputString(document);
	}
	
	
	public void pushContext(IContext ctx) {
		if (ctx instanceof Select) {
			Select select = (Select) ctx;
			context.push("select[@id=\"" + select.getText() + "\"][1]//");
		}
		else if (ctx instanceof AbstractContext) {
			AbstractContext abstractContext = (AbstractContext) ctx;
			context.push("div[@id=\"" + abstractContext.getText() + "\"][1]//");
		}
	}
	
	public void popContext() {
		context.pop();
	}
	
	
	public String getContextString() {
		StringBuffer buff = new StringBuffer();
		for (String element : context) {
			buff.append(element);
		}
		return buff.toString();
	}
}