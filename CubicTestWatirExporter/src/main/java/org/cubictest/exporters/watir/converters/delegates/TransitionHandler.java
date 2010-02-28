/*******************************************************************************
 * Copyright (c) 2005, 2010  Christian Schwarz
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
import org.cubictest.model.UserInteraction;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Transition converter that uses XPath.
 * 
 * @author Christian Schwarz
 */
public class TransitionHandler {

	
	public static void handle(WatirHolder watirHolder, UserInteraction userInteraction) {
		PageElement pe = (PageElement) userInteraction.getElement();

		if (pe instanceof Option) {
			Select select = ((Option) pe).getParent();
			watirHolder.add("if (" + watirHolder.getVariableName(pe) + ".class == String)", 3);
			watirHolder.add(watirHolder.getVariableName(select) + "." + WatirUtils.getInteraction(userInteraction) + "(" + watirHolder.getVariableName(pe) + ")", 4);
			watirHolder.add("else", 3);
			watirHolder.add(watirHolder.getVariableName(pe) + "." + WatirUtils.getInteraction(userInteraction), 4);
			watirHolder.add("end", 3);
		}
		else {
			//handle all other elements
			watirHolder.add(watirHolder.getVariableName(pe) + "." + WatirUtils.getInteraction(userInteraction), 3);
		}
	}
}
