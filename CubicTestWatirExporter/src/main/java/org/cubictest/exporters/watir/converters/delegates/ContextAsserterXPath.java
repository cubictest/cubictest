/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.converters.delegates;

import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.PageElement;

/**
 * Asserts contexts present using XPath.
 * 
 * @author Christian Schwarz
 */
public class ContextAsserterXPath {

	
	public static void handle(WatirHolder watirHolder, PageElement pe) {

		watirHolder.add("pass = 0", 3);

		if (WatirUtils.getElementType(pe).equals("*")){
			String xpath = watirHolder.getFullContextWithAllElements(pe);
			watirHolder.add(watirHolder.getVariableName(pe) + " = " + watirHolder.getActiveContainer() + ".element_by_xpath(\"" + xpath + "\")", 3);
			String not = pe.isNot() ? "" : "not "; 
			watirHolder.add("while " + not + watirHolder.getVariableName(pe) + ".methods.member?(\"ole_get_methods\")", 3);
		}
		else {
			//context elements that have class in watir
			String xpath = watirHolder.getFullContextWithAllElements(pe);
			watirHolder.add(watirHolder.getVariableName(pe) + " =  " + watirHolder.getActiveContainer() + "." + WatirUtils.getElementType(pe) + "(:xpath, \"" + xpath + "\")", 3);
			String not = pe.isNot() ? "" : "not "; 
			watirHolder.add("while " + not + watirHolder.getVariableName(pe) + ".methods.member?(\"display\")", 3);
		}
		
		watirHolder.add("if (pass > 20)", 4);
		watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 5);
		watirHolder.add("end", 4);
		watirHolder.add("sleep 0.1", 4);
		watirHolder.add("pass += 1", 4);
		watirHolder.add("end", 3);
	}
}
