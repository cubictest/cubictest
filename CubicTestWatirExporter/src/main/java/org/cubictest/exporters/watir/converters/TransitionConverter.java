/*
 * Created on Apr 27, 2005
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.exporters.watir.converters.delegates.TransitionHandlerPlain;
import org.cubictest.exporters.watir.converters.delegates.TransitionHandlerXPath;
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
	public void handleUserInteractions(WatirHolder stepList, UserInteractionsTransition transition) {
		List actions = transition.getUserInteractions();
		Iterator it = actions.iterator();
		while(it.hasNext()) {
			UserInteraction action = (UserInteraction) it.next();
			IActionElement actionElement = action.getElement();
			
			if (actionElement == null) {
				Logger.warn("Action element was null. Skipping user interaction: " + transition);
				continue;
			}
			stepList.addSeparator();
			
			if (actionElement instanceof PageElement) {
				handlePageElementAction(stepList, action);
			}
		}
	}
	
	
	/**
	 * Converts a UserInteraction on a page element to a Watir Step.
	 */
	private void handlePageElementAction(WatirHolder stepList, UserInteraction userInteraction) {
		PageElement pe = (PageElement) userInteraction.getElement();

		stepList.add("# user interaction: " + userInteraction.toString());
		stepList.add("begin");
		
		
		if (stepList.requiresXPath(pe)) {
			TransitionHandlerXPath.handle(stepList, userInteraction);
		}
		else {
			TransitionHandlerPlain.handle(stepList, userInteraction);
		}
		
		
		stepList.add("passedSteps += 1 ", 3);

		stepList.add("rescue", 2);
		stepList.add("puts " + "\"" + WatirHolder.EXCEPTION + " \" + $!", 3);
		stepList.add("failedSteps += 1 ", 3);
		String interactionType = StringUtils.replace(WatirUtils.getInteraction(userInteraction) ,"\"", "\\\"");
		stepList.add("puts \"Could not " + interactionType + " " + escape(pe.toString()) + "\"", 3);
		stepList.add("end", 2);
	}

	
	private String escape(String s) { 
		return StringUtils.replace(s, "\"", "\\\"");
	}
}

