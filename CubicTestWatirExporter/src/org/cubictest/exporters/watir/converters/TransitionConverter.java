/*
 * Created on Apr 27, 2005
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
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

import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.IStepList;
import org.cubictest.exporters.watir.holders.TestStep;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.FormElement;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Class to convert transitions between pages to runnable ITestStep objects.
 * 
 * @author chr_schwarz
 */
public class TransitionConverter implements ITransitionConverter<IStepList> {
	
	
	/**
	 * Converts a user interactions transition to a list of Watir steps.
	 * 
	 * @param userInteractions The transition to convert.
	 */
	public void handleUserInteractions(IStepList steps, UserInteractionsTransition userInteractions) {
		List inputs = userInteractions.getUserInteractions();
		Iterator it = inputs.iterator();
		while(it.hasNext()) {
			UserInteraction userInput = (UserInteraction) it.next();
			handlePageElementAction(steps, userInput);
		}
	}
	
	
	/**
	 * Converts a UserInteraction to a Watir Step.
	 * 
	 * @param peAction The FormInput to convert.
	 */
	private void handlePageElementAction(IStepList steps, UserInteraction peAction) {
		Object obj = peAction.getElement();
		if (obj == null) {
			return;
		}
		ActionType action = peAction.getActionType();
		TestStep step = null;
		if (obj instanceof PageElement) {
			String input = peAction.getTextualInput();
			PageElement element = (PageElement)peAction.getElement();
			String elementType = WatirUtils.getElementType(element);
			String idType = WatirUtils.getIdType(element);
			String idText = element.getText();
	
			//If label, inject script to get the ID from the label and modify variables with the injected value:
			if (element.getIdentifierType().equals(LABEL) && element instanceof FormElement && 
					!(element instanceof Button) && !(element instanceof Option)) {
				StringBuffer buff = new StringBuffer();
				WatirUtils.appendGetLabelTargetId(buff, element, element.getDescription());
				idText = "\" + labelTargetId + \"";
				idType = ":id";
				steps.add(new TestStep(buff.toString()).setDescription("Getting label with text = '" + element.getText()));
			}

			if(action.equals(CLICK)){
				step = new TestStep("ie." + elementType + "(" + idType + ", \"" + idText + "\").click");
				step.setDescription("Clicking on " + elementType + " with " + idType + " = '" + idText + "'");
			}
			if (action.equals(SELECT) && element instanceof Option) {
				//Option in SelectList:
				Select parent = (Select) ((Option) element).getParent();
				String parentIdText = parent.getText();
				String parentIdType = WatirUtils.getIdType(parent);
				
				if (parent.getIdentifierType().equals(IdentifierType.LABEL)) {
					//If label, inject script to get the ID from the label and modify variables with the injected value:
					StringBuffer buff = new StringBuffer();
					WatirUtils.appendGetLabelTargetId(buff, parent, parent.getDescription());
					parentIdText = "\" + labelTargetId + \"";
					parentIdType = ":id";
					steps.add(new TestStep(buff.toString()).setDescription("Getting select list with text = '" + parent.getText()));
				}
				String selectList = "ie.select_list(" + parentIdType + ", \"" + parentIdText + "\")";
				
				if (element.getIdentifierType().equals(LABEL)) {
					step = new TestStep(selectList + ".select(\"" + idText + "\")");
				}
				else {
					step = new TestStep(selectList + ".option(" + idType + ", \"" + idText + "\").select");
				}

				String ctxMessage = steps.getPrefix().equals(ContextConverter.ROOT_CONTEXT) ? "" : " ( in " + ContextConverter.ROOT_CONTEXT + ")";
				step.setDescription("Selecting " + element.getType() + " with " + idType + " = '" + idText + "'" + ctxMessage);	
			}
			else if(action.equals(CHECK)){
				step = new TestStep("ie." + elementType + "(" + idType + " , \"" + idText + "\").set");
				step.setDescription("Setting " + element.getType() + " with " + idType + " = '" + idText + "' to checked");
			} 
			else if(action.equals(UNCHECK)){
				step = new TestStep("ie." + elementType + "(" + idType + " , \"" + idText + "\").clear");
				step.setDescription("Setting " + element.getType() + " with " + idType + " = '" + idText + "' to NOT checked");
			}
			else if(action.equals(ENTER_TEXT) || action.equals(ENTER_PARAMETER_TEXT)){
				if (element instanceof Select) {
					step = new TestStep("ie." + elementType + "(" + idType + ", \"" + idText + "\").option(:value, \"" + input +"\").select");
					step.setDescription("Selecting option with value '" + input + "' in " + element.getType() + " with " + idType + " = '" + idText + "'");
				}
				else {
					step = new TestStep("ie." + elementType + "(" + idType + ", \"" + idText + "\").set(\"" + input +"\")");
					step.setDescription("Inserting value '" + input + "' into " + element.getType() + " with " + idType + " = '" + idText + "'");
				}
			}
			else if(action.equals(CLEAR_ALL_TEXT)){
				step = new TestStep("ie." + elementType + "(" + idType + " , \"" + idText + "\").clear");
				step.setDescription("Clearing " + element.getType() + " with " + idType + " = '" + idText + "'");
			}
			else{
				//Handle all other events
				String eventType = WatirUtils.getEventType(action);
				step = new TestStep("ie." + elementType + "(" + idType + ", \"" + idText + "\").fireEvent(\"" + eventType + "\")");
				step.setDescription("FireEvent '" + eventType + "' on " + element.getType() + " with " + idType + " = '" + idText + "'");
			}
		}
		else if (obj instanceof WebBrowser) {
			if (action.equals(GO_BACK)) {
				step = new TestStep("ie.back()");
				step.setDescription("Pressing browser back-button");
			}
			else if (action.equals(GO_FORWARD)) {
				step = new TestStep("ie.forward()");
				step.setDescription("Pressing browser forward-button");
			}
			else if (action.equals(REFRESH)){
				step = new TestStep("ie.refresh()");
				step.setDescription("Pressing browser refresh button");
			}
			else if (action.equals(NEXT_WINDOW)){
				// TODO: should call the IE.attach method in Watir, it requires either an URL or the name of the window.
				// probably best to just use the name, in order to make it work for more frameworks
				throw new ExporterException("Previous window not supported by Watir");
			}
			else if (action.equals(PREVIOUS_WINDOW)) {
				// TODO:  should call the IE.attach method in Watir, it requires either an URL or the name of the window.
				// probably best to just use the name, in order to make it work for more frameworks
				throw new ExporterException("Previous window not supported by Watir");
			}
		}
		steps.add(step);
	}
}
