package org.cubictest.ui.gef.view;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;

public class AddElementContextMenuList extends ArrayList<Class>{

	private static final long serialVersionUID = 1L;

	private static List<Class<? extends PageElement>> list = new ArrayList<Class<? extends PageElement>>();
	
	static {
		list.add(Text.class);
		list.add(Link.class);
		list.add(Image.class);
	}

	public static List<Class<? extends PageElement>> getList() {
		return list;
	}
}
