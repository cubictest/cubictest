/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

/**
 * @author skyttere
 *
 */
public class PageElementAction extends PropertyAwareObject 
			implements SationObserver{
	
	private IActionElement element; // the element upon which "the user" performs an action
	protected String input = ""; // the simulated "user" input
	private  ActionType action = ActionType.NO_ACTION; //the action "the user" perform
	private SationType sationType = SationType.NONE;
	private String key = "";
	
	public PageElementAction(){}
	
	public PageElementAction( IActionElement element, ActionType action, 
			String input){
		this.element = element;
		this.action = action;
		this.input = input;
	}
	public IActionElement getElement() {
		return element;
	}
	/** 
	 * @param element
	 */
	public void setElement(IActionElement element) {
		IActionElement oldPE = this.element;
		this.element = element;
		firePropertyChange(CHILD, oldPE, element);
		firePropertyChange(LAYOUT, oldPE, element);
		if (element != null) {
			setAction(element.getDefaultAction());
		}
	}
	
	public String getInput(){
		if((action !=null && element != null) && 
				(action!= null || !element.equals(null)))
			return input;
		return null;
	}
	/**
	 * @param value
	 */
	public void setInput(String input){
		String oldInput = this.input;
		this.input = input;
		firePropertyChange(PropertyAwareObject.NAME, oldInput, input);
		firePropertyChange(PropertyAwareObject.LAYOUT, oldInput, input);
	}
	public ActionType getAction(){
		return action;
	}
	/**
	 * @param action
	 */
	public void setAction(ActionType action){
		ActionType oldAction = this.action;
		this.action = action;
		firePropertyChange(PropertyAwareObject.NAME, oldAction, action);
		firePropertyChange(PropertyAwareObject.LAYOUT, oldAction, action);
	}
		
	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);
	}
	
	public void setSationType(SationType type) {
		SationType oldType = sationType;
		sationType = type;
		firePropertyChange(PropertyAwareObject.NAME, oldType, type);
	}
	
	public SationType getSationType() {
		return sationType;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg) {
		if (arg.toString().endsWith("-ACTION"))
			return toString().concat("-ACTION").equals(arg.toString());
		else if (arg.toString().endsWith("-INPUT"))
			return toString().concat("-INPUT").equals(arg.toString());
		else if (arg.toString().endsWith("-ELEMENT"))
			return toString().concat("-ELEMENT").equals(arg.toString());
		else
			return toString().equals(arg.toString()); 
	}

	
	public void setText(String text){
		setInput(text);
	}
	public String getText() {
		return getInput();
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key){
		this.key = key;
	}
	
	public String toString() {
		String result = new String();
		result += action.getText();
		if(action.acceptsInput()) {
			result += " '" + input + "'";
		}
		if (element == null)
			result += " @ " + "null element";			
		else
			result += " @ " + element.getDescription();
		return result;
	}
}
