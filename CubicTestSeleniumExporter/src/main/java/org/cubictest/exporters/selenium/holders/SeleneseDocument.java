/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.holders;

import java.util.Stack;

import org.cubictest.exporters.selenium.utils.XmlUtils;
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
	
	/** The XPath starting point (path) for lookup of elements. */
	private Stack<String> context = new Stack<String>(); 

	
	/**
	 * Public constructor.
	 */
	public SeleneseDocument() {

		setUpHtmlPage();
		
		//Setting default context to be anywhere in the document ("//" in XPath)
		context.push("//");
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
	public String toString() {
		Document document = new Document(rootElement);
		return XmlUtils.getNewXmlOutputter().outputString(document);
	}
	
	
	/**
	 * Set new context, i.e. XPath starting point (path) for lookup of elements.
	 * @param ctx
	 */
	public void pushContext(IContext ctx) {
		if (ctx instanceof Select) {
			Select select = (Select) ctx;
			context.push("select[@id=\"" + select.getText() + "\"]//");
		}
		else if (ctx instanceof AbstractContext) {
			AbstractContext abstractContext = (AbstractContext) ctx;
			context.push("*[@id=\"" + abstractContext.getText() + "\"]//");
		}
	}

	
	/**
	 * Get the previous context, i.e. the previous XPath starting point (path) for lookup of elements.
	 */
	public void popContext() {
		context.pop();
	}
	
	
	/**
	 * Get the currenct active context, appending all known contexts into an XPath string 
	 * that can be used for lookup of elements.
	 * @return 
	 */
	public String getFullContext() {
		StringBuffer buff = new StringBuffer();
		
		//Concatenate known contexts to a single XPath line:
		//First in First Out: 
		for (String contextPart : context) {
			buff.append(contextPart);
		}
		
		//return the full, concatenated context:
		return buff.toString();
	}
	
	
	/**
	 * Sets up a HTML page for the tests, including a table for the commands.
	 */
	private void setUpHtmlPage() {
		rootElement = new Element("html");
		
		Element header = new Element("head");
		Element title = new Element("title");
		title.setText("CubicTest exported test");
		header.addContent(title);
		
		Element style = new Element("style");
		style.setAttribute("type", "text/css");
		style.setText(
				"td {padding-right: 25px}\n" + 
				".comment {color: #AAF; padding-top: 10px;}\n");
		header.addContent(style);
		
		rootElement.addContent(header);
		
		Element body = new Element("body");
		rootElement.addContent(body);
		table = new Element("table");
		table.setAttribute("border", "1");
		table.setAttribute("cellspacing", "0");
		body.addContent(table);
		
		table.addContent(new Command("setTimeout", "10000"));
	}
}