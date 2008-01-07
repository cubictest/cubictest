/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.view;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.exception.CubicException;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
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
		list.add(TextField.class);
		list.add(Checkbox.class);
		list.add(RadioButton.class);
		list.add(Select.class);
		list.add(TextArea.class);
		list.add(Password.class);
		list.add(Image.class);
		list.add(Title.class);
		list.add(SimpleContext.class);
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
