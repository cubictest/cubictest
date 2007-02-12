package org.cubictest.exporters.selenium.holders;

import org.cubictest.common.utils.XmlUtils;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Selenium (Selenese) step list.
 * 
 * @author chr_schwarz
 */
public class SeleneseDocument {

	private Element rootElement;
	private Element table;

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
	}

	public Element addRow(Element row) {
		table.addContent(row);
		return row;
	}
	
	public Command addCommand(String commandName, String target, String value) {
		Command command = new Command(commandName, target, value);
		table.addContent(command);
		return command;
	}

	public Command addCommand(String commandName, String target) {
		Command command = new Command(commandName, target);
		table.addContent(command);
		return command;
	}
	
	public String toString() {
		Document document = new Document(rootElement);
		return XmlUtils.getNewXmlOutputter().outputString(document);
	}
}