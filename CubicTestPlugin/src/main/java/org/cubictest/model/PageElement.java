/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import static org.cubictest.model.IdentifierType.LABEL;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.TextUtil;

/**
 * Base class for elements on a page.
 * 
 * @author skyttere
 * @author chr_schwarz
 *
 */
public abstract class PageElement extends PropertyAwareObject 
			implements Cloneable, SationObserver,IActionElement {
	
	private boolean not = false;
	protected String text = "";
	protected IdentifierType identifierType;
	private String description = "";
	private SationType sationType = SationType.NONE;
	private String key = "";
	
	
	/**
	 * Get the user interaction types that is possible on this page elemenent.
	 */
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.CLICK);
		actions.add(ActionType.MOUSE_OVER);
		actions.add(ActionType.MOUSE_OUT);
		actions.add(ActionType.DBLCLICK);
		actions.add(ActionType.NO_ACTION);
		return actions;
	}
	
	/**
	 * Get whether this is a element that should not be present.
	 */
	public boolean isNot() {
		return not;
	}
	
	/**
	 * Set whether this is a element that should not be present.
	 */
	public void setNot(boolean not) {
		this.not = not;
		firePropertyChange(PropertyAwareObject.NAME, null,new Boolean(not));
	}
	
	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);		
	}
	
	@Override
	public PageElement clone() throws CloneNotSupportedException {
		PageElement clone = (PageElement) super.clone();
		clone.setText(text);
		clone.setDescription(description);
		clone.setIdentifierType(identifierType);
		clone.setKey(key);
		clone.setNot(not);
		clone.setSationType(sationType);
		return clone;
	}
	
	
	/**
	 * Get the text that is shown in the CubicTest GUI for the page element.
	 * @return the text that is shown in the CubicTest GUI for the page element.
	 */
	public String getDescription() {
		if (description == null)
			return "";
		return description;
	}

	/**
	 * Set the text that is shown in the CubicTest GUI for the page element.
	 * @param text The text that is shown in the CubicTest GUI for the page element.
	 */
	public void setDescription(String desc) {
		String oldDesc = this.description;
		this.description = desc;
		firePropertyChange(PropertyAwareObject.NAME, oldDesc, desc);
	}
	
	/**
	 * Get the user friendly type-name of this page element.
	 */
	public abstract String getType();
	
	public String getKey(){
		return key;
	}
	
	
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * Set the current parameterization/internationalization type associated with this page element.
	 */
	public void setSationType(SationType type) {
		SationType oldType = sationType;
		sationType = type;
		firePropertyChange(PropertyAwareObject.NAME, oldType, type);
	}
	
	/**
	 * Get the current parameterization/internationalization type associated with this page element.
	 */
	public SationType getSationType() {
		return sationType;
	}
	
	/**
	 * Get the default action for user interaction with this page element.
	 */
	public ActionType getDefaultAction(){
		return getActionTypes().get(0);
	}
	
	/**
	 * Get the text used to identify the element in the HTML page.
	 * Can be e.g. text in label, ID or name, according to type from getIdentifierType().
	 * @return the text used to identify the element in the page.
	 */
	public String getText() {
		if(StringUtils.isBlank(text)) {
			if (getIdentifierType().equals(LABEL)) {
				return getDescription();
			}
			else {
				//camelling:
				return TextUtil.camel(getDescription());
			}
		}
		else {
			return text;
		}
	}
	
	/**
	 * Set the text used to identify the element in the HTML page.
	 * Can be e.g. text in label, ID or name, according to type in setIdentifierType().
	 */
	public void setText(String text) {
		String oldText = getText();
		this.text = text;
		if(getIdentifierType().equals(LABEL)){
			setDescription(text);
		}
		firePropertyChange(PropertyAwareObject.NAME, oldText, text);
	}
	
	/**
	 * Get the current identifier type (e.g. Label, ID or name) associated with this page element.
	 * @return the current identifier type (e.g. Label, ID or name) associated with this page element.
	 */
	public IdentifierType getIdentifierType() {
		if (identifierType == null) {
			//defaulting to first type:
			return getIdentifierTypes().get(0);
		}
		return identifierType;
	}

	
	/**
	 * Set the identifier types that this page elements supports.
	 */
	public void setIdentifierType(IdentifierType identifierType) {
		IdentifierType oldType = getIdentifierType();
		this.identifierType = identifierType;
		firePropertyChange(PropertyAwareObject.NAME, oldType, identifierType);
	}
	
	/**
	 * Get the identifier types that this page elements supports.
	 * @return the identifier types that this page elements supports.
	 */
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		return list;
	}

}
