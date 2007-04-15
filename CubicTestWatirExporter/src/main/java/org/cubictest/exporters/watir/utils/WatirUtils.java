/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
import static org.cubictest.model.ActionType.UNCHECK;
import static org.cubictest.model.IdentifierType.*;

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.RubyBuffer;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.model.ActionType;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.Frame;
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

	public static String getIdType(PageElement pe) {
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
			throw new ExporterException("Identifier type \"" + idType + "\" not recognized. Page element: " + pe);
		}
	}
	
	/**
	 * Get the identifier value of the most significant identifier.
	 * @param pe
	 * @return
	 */
	public static String getIdText(PageElement pe) {
		return pe.getMainIdentifierValue();
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
		if (pe instanceof Frame)
			return "frame";
		if (pe instanceof AbstractContext)
			return "div";
		if (pe instanceof Image)
			return "image";
		if (pe instanceof Text)
			throw new ExporterException("Text is not a supported element type for identification.");
		
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

		throw new ExporterException("Unknown ActionType");
	}

	
	/**
	 * Gets the element ID that the label is for and stores it in ruby variable "labelTargetId".
	 */
	public static String getLabelTargetId(PageElement pe) {
		String label = pe.getIdentifier(IdentifierType.LABEL).getValue();

		RubyBuffer buff = new RubyBuffer();
		buff.add("# getting element associated with label '" + label + "'", 3);
		buff.add("labelTargetId = nil", 3);
		buff.add("ie.labels.each do |label|", 3);
		buff.add("if (label.text == \"" + label + "\")", 4);
		buff.add("labelTargetId = label.for", 5);
		buff.add("end", 4);
		buff.add("end", 3);
		buff.add("if (labelTargetId == nil)", 3);
		buff.add("raise " + StepList.TEST_STEP_FAILED, 4);
		buff.add("end", 3);
		return buff.toString();
	}
	
	
	public static boolean shouldGetLabelTargetId(PageElement pe) {
		//Link, Text, Button and Option has label accessible in Watir directly, 
		//so getLabelTarget ID is not necessary for these elements
		return pe.getMainIdentifierType().equals(LABEL) && !(pe instanceof Link) && !(pe instanceof Text) && !(pe instanceof Button) && !(pe instanceof Option);
	}
	
}
