/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

/**
 * Class representing a single action from a user, on a single element (IActionElement).
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class UserInteraction extends PropertyAwareObject 
			implements SationObserver{
	
	/** The element upon which "the user" performs an action */
	private IActionElement element;
	
	/** The textual user input (only applicable to certain actions, like "enter text"-action) */
	protected String input = "";
	
	/** The action the user performs */
	private  ActionType action = ActionType.NO_ACTION;
	
	private String i18nKey = "";
	private String paramKey = "";
	private boolean useI18n;
	private boolean useParam;
	
	public UserInteraction(){}
	
	/**
	 * Construct a new UserInteraction representing a single user interaction on a single page element.
	 * @param element The action element (e.g. page element) that the action is applied to.
	 * @param action The action the user performs
	 * @param input The user textual input (only applicable to certain actions, like "enter text"-action).
	 */
	public UserInteraction(IActionElement element, ActionType action, String input){
		this.element = element;
		this.action = action;
		this.input = input;
	}
	
	/** 
	 * Get the action element (e.g. page element) that the action is applied to.
	 * @return the action element (e.g. page element) that the action is applied to.
	 */
	public IActionElement getElement() {
		return element;
	}
	
	/** 
	 * Set the action element (e.g. page element) that the action is applied to.
	 * @param element The action element (e.g. page element) that the action is applied to.
	 */
	public void setElement(IActionElement element) {
		IActionElement oldPE = this.element;
		this.element = element;
		firePropertyChange(CHILD, oldPE, element);
		firePropertyChange(LAYOUT, oldPE, element);
		if (element == null) {
			setActionType(ActionType.NO_ACTION);
		}
		else {
			setActionType(element.getDefaultAction());
		}
	}
	
	/**
	 * Get the user textual input (only applicable to certain actions, like the "enter text"-action).
	 * @return the user textual input (only applicable to certain actions, like the "enter text"-action).
	 */
	public String getTextualInput(){
		return input;
	}
	
	/**
	 * Set the user textual input (only applicable to certain actions, like the "enter text"-action).
	 * @param input the user textual input (only applicable to certain actions, like the "enter text"-action).
	 */
	public void setTextualInput(String input){
		String oldInput = this.input;
		this.input = input;
		firePropertyChange(PropertyAwareObject.NAME, oldInput, input);
		firePropertyChange(PropertyAwareObject.LAYOUT, oldInput, input);
	}
	
	/**
	 * Get the ActionType the user performs, e.g. Click. 
	 * @return the ActionType the user performs, e.g. Click.
	 */
	public ActionType getActionType(){
		return action;
	}

	/**
	 * Set the ActionType the user performs, e.g. Click. 
	 * @param action the ActionType the user performs, e.g. Click.
	 */
	public void setActionType(ActionType action){
		ActionType oldAction = this.action;
		this.action = action;
		firePropertyChange(PropertyAwareObject.NAME, oldAction, action);
		firePropertyChange(PropertyAwareObject.LAYOUT, oldAction, action);
	}
		
	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);
	}

	public void setValue(String value){
		setTextualInput(value);
	}
	public String getValue() {
		return getTextualInput();
	}

	public String getI18nKey() {
		return i18nKey;
	}
	public void setI18nKey(String i18nKey){
		this.i18nKey = i18nKey;
	}
	
	public String getParamKey() {
		return paramKey;
	}
	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}
	
	@Override
	public String toString() {
		String result = new String();
		result += action.getText();
		if(action.acceptsInput()) {
			result += " '" + input + "'";
		}
		if (element == null)
			result += " " + "null element";			
		else
			result += " " + element.toString();
		return result;
	}

	public void setUseI18n(boolean useI18n) {
		this.useI18n = useI18n;
	}

	public void setUseParam(boolean useParam) {
		this.useParam = useParam;
	}

	public boolean useI18n() {
		return useI18n;
	}

	public boolean useParam() {
		return useParam;
	}
}