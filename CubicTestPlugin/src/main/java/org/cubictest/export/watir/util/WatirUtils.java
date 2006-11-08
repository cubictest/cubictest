/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.watir.util;

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
import static org.cubictest.model.ActionType.GO_BACK;
import static org.cubictest.model.ActionType.GO_FORWARD;
import static org.cubictest.model.ActionType.KEY_PRESSED;
import static org.cubictest.model.ActionType.MOUSE_OUT;
import static org.cubictest.model.ActionType.MOUSE_OVER;
import static org.cubictest.model.ActionType.NEXT_WINDOW;
import static org.cubictest.model.ActionType.NO_ACTION;
import static org.cubictest.model.ActionType.PREVIOUS_WINDOW;
import static org.cubictest.model.ActionType.REFRESH;
import static org.cubictest.model.ActionType.UNCHECK;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.NAME;
import static org.cubictest.model.IdentifierType.VALUE;

import org.cubictest.common.exception.CubicException;
import org.cubictest.export.watir.TestStep;
import org.cubictest.export.watir.interfaces.IStepList;
import org.cubictest.export.watir.interfaces.ITestStep;
import org.cubictest.model.ActionType;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;

public class WatirUtils {

	public static String getIdType(PageElement pe) {
		IdentifierType type = pe.getIdentifierType();
		if (type.equals(ID))
			return ":id";
		if (type.equals(NAME))
			return ":name";
		if (type.equals(VALUE))
			return ":value";
		if (type.equals(LABEL))
			if (pe instanceof Link)
				return ":text";
			else
				return ":value";
		else
			throw new CubicException("Identifier type not recognized.");
	}
	
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
		if (pe instanceof AbstractContext)
			return "div";
		if (pe instanceof Image)
			return "image";
		if (pe instanceof Text)
			throw new CubicException("Text is not a supported element type for identification.");
		
		throw new CubicException("Unknown element type");
	}
	
	public static String getEventType(ActionType a) {
		if (a.equals(CLICK))
			throw new CubicException("Should not be used as event");
		if (a.equals(CHECK))
			throw new CubicException("Should not be used as event");
		if (a.equals(UNCHECK))
			throw new CubicException("Should not be used as event");
		if (a.equals(ENTER_TEXT))
			throw new CubicException("Should not be used as event");
		if (a.equals(KEY_PRESSED))
			return "onkeypress";
		if (a.equals(CLEAR_ALL_TEXT))
			throw new CubicException("Should not be used as event");
		if (a.equals(MOUSE_OVER))
			return "onmouseover";
		if (a.equals(MOUSE_OUT))
			return "onmouseout";
		if (a.equals(DBLCLICK))
			return "ondblclick";
		if (a.equals(FOCUS))
			return "onfocus";
		if (a.equals(BLUR))
			return "onblur";
		if (a.equals(DRAG_START))
			throw new CubicException(a.getText() + " is not a supported action type");
		if (a.equals(DRAG_END))
			throw new CubicException(a.getText() + " is not a supported action type");
		if (a.equals(NO_ACTION))
			throw new CubicException(a.getText() + " is not a supported action type");
		if (a.equals(GO_BACK))
			throw new CubicException(a.getText() + " is not a supported action type");
		if (a.equals(GO_FORWARD))
			throw new CubicException(a.getText() + " is not a supported action type");
		if (a.equals(REFRESH))
			throw new CubicException(a.getText() + " is not a supported action type");
		if (a.equals(NEXT_WINDOW))
			throw new CubicException(a.getText() + " is not a supported action type");
		if (a.equals(PREVIOUS_WINDOW))
			throw new CubicException(a.getText() + " is not a supported action type");
		if (a.equals(ENTER_PARAMETER_TEXT))
			throw new CubicException(a.getText() + " is not a supported action type");
		

		throw new CubicException("Unknown ActionType type");
	}

	
	public static void appendGetLabelTargetId(StringBuffer buff, PageElement pe, String idText) {
		append(buff, "labelTargetId = \"\"", 3);
		append(buff, "ie.labels.each do |label|", 3);
			append(buff, "if(label.innerText() == \"" + idText + "\")", 4);
				append(buff, "labelTargetId = label.for()", 5);
			append(buff, "end", 4);
		append(buff, "end", 3);
		append(buff, "if (labelTargetId.length == 0)", 3);
			append(buff, "puts \"Did not find label with text '" + idText + "'\"", 4);
		append(buff, "end", 3);
	}
	
	
	public static void appendCheckOptionPresent(StringBuffer buff, String selectList, String text) {
		append(buff, "optionFound = false", 3);
		append(buff, selectList + ".getAllContents().each do |opt|", 3);
			append(buff, "if(opt.to_s() == \"" + text + "\")", 4);
				append(buff, "optionFound = true", 5);
			append(buff, "end", 4);
		append(buff, "end", 3);
		append(buff, "if (!optionFound)", 3);
			append(buff, "puts \"Did not find option with text '" + text + "' in select list " + selectList.replace("\"", "'") + "\"", 4);
		append(buff, "end", 3);
	}
	
	private static void append(StringBuffer buff, String s, int indent) {
		for (int i = 0; i < indent; i++) {
			buff.append("\t");			
		}
		buff.append(s);
		buff.append("\n");
	}

}
