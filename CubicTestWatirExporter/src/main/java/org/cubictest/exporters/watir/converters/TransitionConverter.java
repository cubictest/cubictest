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
package org.cubictest.exporters.watir.converters;

import static org.cubictest.exporters.watir.utils.WatirUtils.escape;

import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.exporters.watir.converters.delegates.TransitionHandler;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.IActionElement;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;

/**
 * Converts transitions to watir test code.
 * 
 * @author chr_schwarz
 */
public class TransitionConverter implements ITransitionConverter<WatirHolder> {
	
	
	/**
	 * Converts a user interactions transition to a list of Watir steps.
	 * @param transition The transition to convert.
	 */
	public void handleUserInteractions(WatirHolder watirHolder, UserInteractionsTransition transition) {

		for (UserInteraction action :  transition.getUserInteractions()) {
			IActionElement actionElement = action.getElement();
			if (actionElement == null) {
				Logger.warn("Action element was null. Skipping user interaction: " + transition);
				continue;
			}
			watirHolder.addSeparator();
			
			if (actionElement instanceof PageElement) {
				handlePageElementAction(watirHolder, action);
			}
		}
	}

	
	/**
	 * Converts a UserInteraction on a page element to a Watir Step.
	 */
	private void handlePageElementAction(WatirHolder watirHolder, UserInteraction userInteraction) {
		PageElement pe = (PageElement) userInteraction.getElement();

		watirHolder.add("# user interaction: " + userInteraction.toString());
		watirHolder.add("begin");
		
		TransitionHandler.handle(watirHolder, userInteraction);
		
		watirHolder.add("passedSteps += 1 ", 3);

		watirHolder.add("rescue", 2);
		watirHolder.add("puts " + "\"" + WatirHolder.EXCEPTION + escape(watirHolder.getId(pe)) + " -- \" + $!", 3);
		watirHolder.add("failedSteps += 1 ", 3);
		String quoteNotStartingWithABackslash = "([^\\\\])\"";
		String quoteStartingWithABackslash = "$1\\\\\"";
		String interactionType = WatirUtils.getInteraction(userInteraction).replaceAll(quoteNotStartingWithABackslash, quoteStartingWithABackslash);
		watirHolder.add("puts \"Could not " + interactionType + " " + escape(pe.toString()) + "\"", 3);
		watirHolder.add("raise " + WatirHolder.INTERACTION_FAILURE, 3);
		watirHolder.add("end", 2);
	}
}

