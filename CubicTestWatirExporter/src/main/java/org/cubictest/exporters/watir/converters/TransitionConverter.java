/*
 * Created on Apr 27, 2005
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters;

import static org.cubictest.model.ActionType.CHECK;
import static org.cubictest.model.ActionType.CLEAR_ALL_TEXT;
import static org.cubictest.model.ActionType.CLICK;
import static org.cubictest.model.ActionType.ENTER_PARAMETER_TEXT;
import static org.cubictest.model.ActionType.ENTER_TEXT;
import static org.cubictest.model.ActionType.GO_BACK;
import static org.cubictest.model.ActionType.GO_FORWARD;
import static org.cubictest.model.ActionType.NEXT_WINDOW;
import static org.cubictest.model.ActionType.PREVIOUS_WINDOW;
import static org.cubictest.model.ActionType.REFRESH;
import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.ActionType.UNCHECK;
import static org.cubictest.model.IdentifierType.LABEL;

import java.util.Iterator;
import java.util.List;

import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.IStepList;
import org.cubictest.exporters.watir.holders.ITestStep;
import org.cubictest.exporters.watir.holders.TestStep;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.FormElement;
import org.cubictest.model.IActionElement;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Class to convert transitions to watir test code.
 * 
 * @author chr_schwarz
 */
public class TransitionConverter implements ITransitionConverter<IStepList> {
	
	
	/**
	 * Converts a user interactions transition to a list of Watir steps.
	 * 
	 * @param transition The transition to convert.
	 */
	public void handleUserInteractions(IStepList steps, UserInteractionsTransition transition) {
		List actions = transition.getUserInteractions();
		Iterator it = actions.iterator();
		while(it.hasNext()) {
			UserInteraction action = (UserInteraction) it.next();
			IActionElement actionElement = action.getElement();
			
			if (actionElement == null) {
				Logger.warn("Action element was null. Skipping user interaction: " + transition);
				continue;
			}
			
			if (actionElement instanceof PageElement) {
				handlePageElementAction(steps, action);
			}
			else if (actionElement instanceof WebBrowser) {
				handleWebBrowserAction(steps, action);
			}
		}
	}
	
	
	/**
	 * Converts a UserInteraction on a page element to a Watir Step.
	 */
	private void handlePageElementAction(IStepList steps, UserInteraction userInteraction) {

		PageElement element = (PageElement) userInteraction.getElement();
		String elementType = WatirUtils.getElementType(element);
		String idType = WatirUtils.getIdType(element);
		String idText = element.getText();
		ActionType actionType = userInteraction.getActionType();
		ITestStep step = null;

		//If Label, inject watir code to get the ID from the label and modify variables with the injected value:
		
		if (element.getIdentifierType().equals(LABEL) && element instanceof FormElement && 
				!(element instanceof Button) && !(element instanceof Option)) {
			StringBuffer buff = new StringBuffer();
			WatirUtils.appendGetLabelTargetId(buff, element, element.getDescription());
			idText = "\" + labelTargetId + \"";
			idType = ":id";
			steps.add(new TestStep(buff.toString()).setDescription("Getting label with text = '" + element.getText()));
		}

		//Handle the different action types:
		
		if(actionType.equals(CLICK)){
			step = new TestStep("ie." + elementType + "(" + idType + ", \"" + idText + "\").click");
			step.setDescription("Clicking on " + elementType + " with " + idType + " = '" + idText + "'");
		}
		
		else if (actionType.equals(SELECT)) {
			selectOptionInSelectList(steps, (Option) element, idType, idText);	
		}
		
		else if(actionType.equals(CHECK)){
			step = new TestStep("ie." + elementType + "(" + idType + " , \"" + idText + "\").set");
			step.setDescription("Setting " + element.getType() + " with " + idType + " = '" + idText + "' to checked");
		} 
		
		else if(actionType.equals(UNCHECK)){
			step = new TestStep("ie." + elementType + "(" + idType + " , \"" + idText + "\").clear");
			step.setDescription("Setting " + element.getType() + " with " + idType + " = '" + idText + "' to NOT checked");
		}
		
		else if(actionType.equals(ENTER_TEXT) || actionType.equals(ENTER_PARAMETER_TEXT)){
			String textualInput = userInteraction.getTextualInput();
			step = new TestStep("ie." + elementType + "(" + idType + ", \"" + idText + "\").set(\"" + textualInput +"\")");
			step.setDescription("Inserting value '" + textualInput + "' into " + element.getType() + " with " + idType + " = '" + idText + "'");
		}
		
		else if(actionType.equals(CLEAR_ALL_TEXT)){
			step = new TestStep("ie." + elementType + "(" + idType + " , \"" + idText + "\").clear");
			step.setDescription("Clearing " + element.getType() + " with " + idType + " = '" + idText + "'");
		}
		
		else{
			//Handle all other events
			String eventType = WatirUtils.getEventType(actionType);
			step = new TestStep("ie." + elementType + "(" + idType + ", \"" + idText + "\").fireEvent(\"" + eventType + "\")");
			step.setDescription("FireEvent '" + eventType + "' on " + element.getType() + " with " + idType + " = '" + idText + "'");
		}
		steps.add(step);
	}

	
	
	/**
	 * Selects the specified option in a select list.
	 */
	private void selectOptionInSelectList(IStepList steps, Option option, String idType, String idText) {
		ITestStep step = null;
		Select select = (Select) option.getParent();
		String selectIdText = select.getText();
		String selectIdType = WatirUtils.getIdType(select);
		
		//Handle Label id type:
		
		if (select.getIdentifierType().equals(IdentifierType.LABEL)) {
			//If label, inject script to get the ID from the label and modify variables with the injected value:
			StringBuffer buff = new StringBuffer();
			WatirUtils.appendGetLabelTargetId(buff, select, select.getDescription());
			selectIdText = "\" + labelTargetId + \"";
			selectIdType = ":id";
			steps.add(new TestStep(buff.toString()).setDescription("Getting select list with text = '" + select.getText()));
		}
		
		//Select the option:
		
		String selectList = "ie.select_list(" + selectIdType + ", \"" + selectIdText + "\")";
		
		if (option.getIdentifierType().equals(LABEL)) {
			step = new TestStep(selectList + ".select(\"" + idText + "\")");
		}
		else {
			step = new TestStep(selectList + ".option(" + idType + ", \"" + idText + "\").select");
		}

		String ctxMessage = steps.getPrefix().equals(ContextConverter.ROOT_CONTEXT) ? "" : " ( in " + ContextConverter.ROOT_CONTEXT + ")";
		step.setDescription("Selecting " + option.getType() + " with " + idType + " = '" + idText + "'" + ctxMessage);
	}
	
	
	
	/**
	 * Converts a Web browser user interaction to a Watir step.
	 */
	private void handleWebBrowserAction(IStepList steps, UserInteraction userInteraction) {

		ActionType actionType = userInteraction.getActionType();
		ITestStep step = null;
			
		if (actionType.equals(GO_BACK)) {
			step = new TestStep("ie.back()");
			step.setDescription("Pressing browser back-button");
		}
		else if (actionType.equals(GO_FORWARD)) {
			step = new TestStep("ie.forward()");
			step.setDescription("Pressing browser forward-button");
		}
		else if (actionType.equals(REFRESH)){
			step = new TestStep("ie.refresh()");
			step.setDescription("Pressing browser refresh button");
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
		
		steps.add(step);
	}
}
