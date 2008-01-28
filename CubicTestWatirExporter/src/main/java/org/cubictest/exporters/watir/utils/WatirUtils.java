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
package org.cubictest.exporters.watir.utils;

import static org.cubictest.model.ActionType.BLUR;
import static org.cubictest.model.ActionType.CHECK;
import static org.cubictest.model.ActionType.CLEAR_ALL_TEXT;
import static org.cubictest.model.ActionType.CLICK;
import static org.cubictest.model.ActionType.DBLCLICK;
import static org.cubictest.model.ActionType.DRAG_END;
import static org.cubictest.model.ActionType.DRAG_START;
import static org.cubictest.model.ActionType.ENTER_PARAMETER_TEXT;
import static org.cubictest.model.ActionType.ENTER_TEXT;
import static org.cubictest.model.ActionType.FOCUS;
import static org.cubictest.model.ActionType.KEY_PRESSED;
import static org.cubictest.model.ActionType.MOUSE_OUT;
import static org.cubictest.model.ActionType.MOUSE_OVER;
import static org.cubictest.model.ActionType.NO_ACTION;
import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.ActionType.UNCHECK;
import static org.cubictest.model.IdentifierType.ALT;
import static org.cubictest.model.IdentifierType.HREF;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.INDEX;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.NAME;
import static org.cubictest.model.IdentifierType.SRC;
import static org.cubictest.model.IdentifierType.TITLE;
import static org.cubictest.model.IdentifierType.VALUE;
import static org.cubictest.model.Moderator.EQUAL;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.ActionType;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.Moderator;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;

/**
 * Util class for watir export.
 *  
 * @author chr_schwarz
 */
public class WatirUtils {

	
	public static String getMainIdType(PageElement pe) {
		IdentifierType idType = pe.getMainIdentifierType();
		if (idType.equals(ID))
			return ":id";
		if (idType.equals(NAME))
			return ":name";
		if (idType.equals(VALUE))
			return ":value";
		if (idType.equals(INDEX))
			return ":index";
		if (idType.equals(SRC))
			return ":src";
		if (idType.equals(ALT))
			return ":alt";
		if (idType.equals(HREF))
			return ":url";
		if (idType.equals(TITLE))
			return ":title";
		if (idType.equals(LABEL)) {
			if (pe instanceof Link) {
				return ":text";
			}
			else {
				return ":value";
			}
		}
		else {
			throw new ExporterException("Page element: " + pe + "\n\nIdentifier type \"" + idType + "\" not supported by Watir as primary ID for elements.");
		}
	}
	
	
	/**
	 * Get the Watir element type corresponding to the page element.
	 * @param pe
	 * @return
	 */
	public static String getElementType(PageElement pe) {
		if (pe instanceof TextField || pe instanceof Password || pe instanceof TextArea)
			return "text_field";
		if (pe instanceof Checkbox)
			return "checkbox";
		if (pe instanceof RadioButton)
			return "radio";
		if (pe instanceof Button)
			return "button";
		if (pe instanceof Select)
			return "select_list";
		if (pe instanceof Option)
			return "option";
		if (pe instanceof Link)
			return "link";
		if (pe instanceof Image)
			return "image";
		if (pe instanceof Text)
			return "text";
		if (pe instanceof SimpleContext) {
			SimpleContext ctx = (SimpleContext) pe;
			String elementName = ctx.getIdentifier(IdentifierType.ELEMENT_NAME).getValue().toLowerCase();
			if (elementName.equals("div") || 
					elementName.equals("span") || 
					elementName.equals("li") || 
					elementName.equals("form") || 
					elementName.equals("table") || 
					elementName.equals("p")) {
				return elementName;
			}
			else {
				return "*";
			}
		}
		
		throw new ExporterException("Unknown element type");
	}
	
	/**
	 * Get the Watir interaction substring.
	 */
	public static String getInteraction(UserInteraction userInteraction) {
		ActionType a = userInteraction.getActionType();
		String textualInput = userInteraction.getTextualInput();
		
		if (a.equals(CLICK))
			return "click";
		if (a.equals(CHECK))
			return "set";
		if (a.equals(UNCHECK))
			return "clear";
		if (a.equals(SELECT))
			return "select";
		if (a.equals(ENTER_TEXT))
			return "set(\"" + textualInput +"\")";
		if (a.equals(ENTER_PARAMETER_TEXT))
			return "set(\"" + textualInput +"\")";
		if (a.equals(KEY_PRESSED))
			return "fireEvent(\"onkeypress\")";
		if (a.equals(CLEAR_ALL_TEXT))
			return "clear";
		if (a.equals(MOUSE_OVER))
			return "fireEvent(\"onmouseover\")";
		if (a.equals(MOUSE_OUT))
			return "fireEvent(\"onmouseout\")";
		if (a.equals(DBLCLICK))
			return "fireEvent(\"ondblclick\")";
		if (a.equals(FOCUS))
			return "fireEvent(\"onfocus\")";
		if (a.equals(BLUR))
			return "fireEvent(\"onblur\")";
		if (a.equals(DRAG_START))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(DRAG_END))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(NO_ACTION))
			throw new ExporterException(a.getText() + " is not a supported action type");

		throw new ExporterException("Unknown ActionType: " + a);
	}

	
	/**
	 * Gets the element ID that the label is for.
	 */
	public static String getLabelTargetId(PageElement pe, WatirHolder watirHolder) {
		String label = getIdValue(pe.getIdentifier(LABEL));
		Moderator moderator = pe.getIdentifier(LABEL).getModerator();
		
		RubyBuffer buff = new RubyBuffer();
		buff.add("# getting element associated with label '" + label + "'", 3);
		buff.add(watirHolder.getVariableName(pe) + " = nil", 3);
		buff.add("ie.labels.each do |label|", 3);
		if (moderator.equals(EQUAL)) {
			buff.add("if (label.text == " + label + ")", 4);
		}
		else {
			buff.add("if ((label.text =~ " + label + ") != nil)", 4);
		}
		buff.add(watirHolder.getVariableName(pe) + " = label.for", 5);
		buff.add("end", 4);
		buff.add("end", 3);
		buff.add("if (" + watirHolder.getVariableName(pe) + " == nil)", 3);
		buff.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
		buff.add("end", 3);
		return buff.toString();
	}
	
	
	public static boolean hasExternalLabel(PageElement pe) {
		//Link, Text, Button and Option has label accessible in Watir directly, 
		//so getLabelTarget ID is not necessary for these elements
		return pe.getMainIdentifierType().equals(LABEL) && !(pe instanceof Link) && !(pe instanceof Text) && !(pe instanceof Button) && !(pe instanceof Option);
	}
	
	
	public static String getIdValue(Identifier id) {
		String idValue = StringUtils.replace(id.getValue(), "\"", "\\\"");
		switch (id.getModerator()) {
		case CONTAIN:
			idValue = "/" + idValue + "/";
			break;
		case BEGIN:
			idValue = "/^" + idValue + "/";
			break;
		case END:
			idValue = "/" + idValue + "$/";
			break;
		case EQUAL:
			idValue = "\"" + idValue + "\"";
			break;
		}
		return idValue;
	}


}
