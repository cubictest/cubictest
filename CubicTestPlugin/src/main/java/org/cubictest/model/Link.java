/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import static org.cubictest.model.IdentifierType.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author skyttere
 *
 */
public class Link extends PageElement {
	
	@Override
	public String getType(){
		return "Link";
	}

	
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		list.add(ID);
		list.add(HREF);
		list.add(TITLE);
		list.add(INDEX);
		return list;
	}	
}
