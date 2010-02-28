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
package org.cubictest.exporters.selenium.selenese.holders;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.EntityRef;


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
		addCell(commandName);
		addCell(target);
		addCell(value);
	}

	/**
	 * Create a new command (selenese table row).
	 */
	public Command(String commandName, String target) {
		this(commandName, target, "");
	}
	
	/**
	 * Set description for command (comment as content of row).
	 * @param description
	 * @return
	 */
	public Command setDescription(String description) {
		int pos = ((Element) getParent()).indexOf(this);
		Element comment = new Element("td").setText(description + ":");
		comment.setAttribute("class", "comment");
		comment.setAttribute("colspan", "3");
		((Element) getParent()).addContent(pos, new Element("tr").addContent(comment));
		return this;
	}
	
	
	private void addCell(String text) {
		if (StringUtils.isBlank(text)) {
			addContent(new Element("td").addContent(new EntityRef("nbsp")));
		}
		else {
			addContent(new Element("td").setText(text));
		}
	}
}
