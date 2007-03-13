/*
 * Created on 11.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model;

import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.*;

import java.util.ArrayList;
import java.util.List;

public class Image extends PageElement {

	@Override
	public String getType() {
		return "Image";
	}


	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(ID);
		list.add(SRC);
		list.add(TITLE);
		return list;
	}
}
