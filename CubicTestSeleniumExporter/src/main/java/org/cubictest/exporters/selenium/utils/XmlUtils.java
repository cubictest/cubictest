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
package org.cubictest.exporters.selenium.utils;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlUtils {
	public static XMLOutputter getNewXmlOutputter() {
		Format format = Format.getPrettyFormat();
		format.setEncoding("ISO-8859-1");		
		return new XMLOutputter(format);
	}
	
}
