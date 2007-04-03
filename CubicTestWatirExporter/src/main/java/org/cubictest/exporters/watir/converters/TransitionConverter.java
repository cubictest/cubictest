/*
 * Created on Apr 27, 2005
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters;

import static org.cubictest.model.ActionType.GO_BACK;
import static org.cubictest.model.ActionType.GO_FORWARD;
import static org.cubictest.model.ActionType.NEXT_WINDOW;
import static org.cubictest.model.ActionType.PREVIOUS_WINDOW;
import static org.cubictest.model.ActionType.REFRESH;
import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.ActionType.SWITCH_BY_TITLE;
import static org.cubictest.model.ActionType.SWITCH_BY_URL;
import static org.cubictest.model.ActionType.CLOSE;
import static org.cubictest.model.IdentifierType.LABEL;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.ContextWindow;
import org.cubictest.model.IActionElement;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Converts transitions to watir test code.
 * 
 * @author chr_schwarz
 */
public class TransitionConverter implements ITransitionConverter<StepList> {
	
	
	/**
	 * Converts a user interactions transition to a list of Watir steps.
	 * 
	 * @param transition The transition to convert.
	 */
	public void handleUserInteractions(StepList stepList, UserInteractionsTransition transition) {
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
			else if (actionElement instanceof WebBrowser) {
				handleWebBrowserAction(stepList, action);
			}
			//Added by Genesis Campos
			else if (actionElement instanceof ContextWindow){
				handleContextWindowAction(stepList, action);
			}
			//End;
		}
	}
	
	
	/**
	 * Converts a UserInteraction on a page element to a Watir Step.
	 */
	private void handlePageElementAction(StepList stepList, UserInteraction userInteraction) {
		PageElement pe = (PageElement) userInteraction.getElement();
		String idType = WatirUtils.getIdType(pe);
		String idText = "\"" + WatirUtils.getIdText(pe) + "\"";

		stepList.add("# user interaction");
		stepList.add("begin");

		//Handle Label identifier:
		if (WatirUtils.shouldGetLabelTargetId(pe)) {
			stepList.add(WatirUtils.getLabelTargetId(pe));
			stepList.addSeparator();
			idText = "labelTargetId";
			idType = ":id";
		}
		
		if (userInteraction.getActionType().equals(SELECT) && userInteraction.getElement() instanceof Option) {
			//select option in select list:
			selectOptionInSelectList(stepList, (Option) pe, idType, idText);	
		}
		else {
			//handle all other interaction types:
			stepList.add("ie." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idText + ")." + WatirUtils.getInteraction(userInteraction), 3);
			stepList.add("passedSteps += 1 ", 3);

			stepList.add("rescue", 2);
			stepList.add("failedSteps += 1 ", 3);
			String interactionType = StringUtils.replace(WatirUtils.getInteraction(userInteraction) ,"\"", "\\\"");
			stepList.add("puts \"Could not " + interactionType + " " + 
					WatirUtils.getElementType(pe) + " with " + pe.getMainIdentifierType() + " = '" + WatirUtils.getIdText(pe) + "'\"", 3);
	
			stepList.add("end", 2);
		}
	}

	
	
	/**
	 * Selects the specified option in a select list.
	 */
	private void selectOptionInSelectList(StepList stepList, Option option, String idType, String idText) {
		Select select = (Select) option.getParent();
		String selectIdText = "\"" + WatirUtils.getIdText(select) + "\"";
		String selectIdType = WatirUtils.getIdType(select);
		
		if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
			//Handle label:
			stepList.add(WatirUtils.getLabelTargetId(select));
			selectIdText = "labelTargetId";
			selectIdType = ":id";
		}
		
		String selectList = "ie.select_list(" + selectIdType + ", " + selectIdText + ")";
		
		//Select the option:
		if (option.getMainIdentifierType().equals(LABEL)) {
			stepList.add(selectList + ".select(" + idText + ")", 3);
		}
		else {
			stepList.add(selectList + ".option(" + idType + ", " + idText + ").select", 3);
		}
		
		stepList.add("rescue",2);
		stepList.add("failedSteps += 1 ",3);
		stepList.add("puts \"Step failed: Select option with " + idType + " = '" + WatirUtils.getIdText(option) + "'\"", 3);		
		stepList.add("end", 2);

	}
	
	
	
	/**
	 * Converts a Web browser user interaction to a Watir step.
	 */
	private void handleWebBrowserAction(StepList steps, UserInteraction userInteraction) {

		ActionType actionType = userInteraction.getActionType();
			
		if (actionType.equals(GO_BACK)) {
			steps.add("ie.back()");
		}
		else if (actionType.equals(GO_FORWARD)) {
			steps.add("ie.forward()");
		}
		else if (actionType.equals(REFRESH)){
			steps.add("ie.refresh()");
		}
		else if (actionType.equals(NEXT_WINDOW)){
			// TODO: should call the IE.attach method in Watir, it requires either an URL or the name of the window.
			// probably best to just use the name, in order to make it work for more frameworks
			throw new ExporterException("Previous window not supported by Watir");
		}
		else if (actionType.equals(PREVIOUS_WINDOW)) {
			// TODO:  should call the IE.attach method in Watir, it requires either an URL or the name of the window.
			// probably best to just use the name, in order to make it work for more frameworks
			throw new ExporterException("Previous window not supported by Watir");
		}
	}
	
	//Added by Genesis Campos	
	/**
	 * Converts a Context Window user interaction to a Watir step.
	 */
	private void handleContextWindowAction(StepList steps, UserInteraction userInteraction) {

		String input = userInteraction.getTextualInput();
		ActionType action = userInteraction.getActionType();
		String atribute = null;
		
		steps.add("# user interaction");
		steps.add("begin");
		
		if(action.equals(CLOSE)){
				steps.add("# closing the last context window",3);
				steps.add("if (ie2 == nil)",3);
					steps.add("raise TestStepFailed",4);
				steps.add("end",3);
				steps.add("ie2.close()",3);
				steps.add("passedSteps += 1",3);
				steps.add("rescue TestStepFailed",2);
					steps.add("failedSteps += 1 ",3);
					steps.add("puts \"Step failed: You must switch context window before\"",3);		
			steps.add("end", 2);
			return;
		}
		else{
				steps.add("# storing the last context window",3);
				steps.add("ie2 = ie",3);
			if (action.equals(SWITCH_BY_TITLE)) {
				atribute = "title";
			}
			else if (action.equals(SWITCH_BY_URL)) {
				atribute = "url";
			}
			steps.add("# changing context to the window with "+ atribute +" equals \""+ input +"\"",3);
			steps.add("ie = Watir::IE.attach(:"+ atribute +", '"+input+"')",3);
			steps.add("if (ie == nil)",3);
				steps.add("raise TestStepFailed",4);
			steps.add("end",3);
			steps.add("passedSteps += 1",3);
			steps.add("rescue TestStepFailed",2);
				steps.add("failedSteps += 1 ",3);
				steps.add("puts \"Step failed: Check context window present with " + atribute + " equals '" + input + "'\"",3);		
		}
		steps.add("end", 2);
	}
	//End;
}
