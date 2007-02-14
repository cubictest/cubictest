package org.cubictest.exporters.selenium.holders;

import org.jdom.Comment;
import org.jdom.Element;


/**
 * Selenium command (selenese table row) representing a test step.
 * 
 * @author chr_schwarz
 */
public class Command extends Element {

	private static final long serialVersionUID = 1L;

	
	/**
	 * Create a new command (selenese table row).
	 */
	public Command(String commandName, String target, String value) {
		super("tr");
		addContent(new Element("td").setText(commandName));
		addContent(new Element("td").setText(target));
		addContent(new Element("td").setText(value));
	}
	
	
	/**
	 * Set description for command (comment as content of row).
	 * @param description
	 * @return
	 */
	public Command setDescription(String description) {
		addContent(new Comment(description));
		return this;
	}
}
