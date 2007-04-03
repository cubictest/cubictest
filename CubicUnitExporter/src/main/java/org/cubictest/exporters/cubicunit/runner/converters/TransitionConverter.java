/*
 * Created on 4.aug. 2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.exporters.cubicunit.runner.converters;

import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubicunit.Browser;
import org.cubicunit.Checkbox;
import org.cubicunit.Element;
import org.cubicunit.TextInput;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TransitionConverter implements ITransitionConverter<Holder> {

	public void handleUserInteractions(Holder holder, UserInteractionsTransition userInteractions) {

		if(userInteractions instanceof UserInteractionsTransition){
			UserInteractionsTransition actions = (UserInteractionsTransition) userInteractions;
			for(UserInteraction action : actions.getUserInteractions()){
				try{
					Element element = holder.getElement((PageElement) action.getElement());
					switch(action.getActionType()){
					case CLICK:
						element.click();
						break;
					case DBLCLICK:
						element.dblClick();
						break;
					case ENTER_TEXT:
					case ENTER_PARAMETER_TEXT:
						((TextInput)element).enter(action.getTextualInput());
						break;
					case CLEAR_ALL_TEXT:
						((TextInput)element).enter("");
						break;
					case BLUR:
						element.blur();
						break;
					case FOCUS:
						element.focus();
						break;
					case CHECK:
						((Checkbox)element).check();
						break;
					case UNCHECK:
						((Checkbox)element).unCheck();
						break;
					case GO_BACK:
						holder.getBrowser().goBack();
						break;
					case GO_FORWARD:
						holder.getBrowser().goForward();
						break;
					case REFRESH:
						holder.getBrowser().refresh();
						break;
					case MOUSE_OVER:
						element.moveMouseOver();
						break;
					case MOUSE_OUT:
						element.moveMouseOut();
						break;
					case NEXT_WINDOW:
						Browser b = holder.getBrowser().getLastNewWindow();
						if(b != null){
							holder.addBrowser(b);
						}
						break;
					case PREVIOUS_WINDOW:
						holder.getPreviousBrowser();
						break;
					case DRAG_END:
					case DRAG_START:
						throw new NotImplementedException();
					case NO_ACTION:
						break;
					default:
						break;
					}
					action.setStatus(TestPartStatus.PASS);
				}catch(RuntimeException e){
					action.setStatus(TestPartStatus.EXCEPTION);
					throw e;
				}catch(Error e){
					action.setStatus(TestPartStatus.FAIL);
					throw e;
				}

			}
		}
	}

}