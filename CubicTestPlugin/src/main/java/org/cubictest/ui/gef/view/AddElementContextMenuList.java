/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.view;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.exception.CubicException;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.context.Row;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;

/**
 * Class for holding list over page elements that should go into the "Add page element" context menu.
 * 
 * @author chr_schwarz
 */
public class AddElementContextMenuList {

	private static final long serialVersionUID = 1L;

	private static List<Class<? extends PageElement>> list = new ArrayList<Class<? extends PageElement>>();
	
	static {
		list.add(Text.class);
		list.add(Link.class);
		list.add(Button.class);
		list.add(SimpleContext.class);
		list.add(TextField.class);
		list.add(Checkbox.class);
		list.add(RadioButton.class);
	}

	public static List<Class<? extends PageElement>> getList() {
		return list;
	}
	
	
	public static String getType(Class<? extends PageElement> element) {
		try {
			return element.newInstance().getType();
		} 
		catch (Exception e) {
			throw new CubicException(e);
		}
	}
}
