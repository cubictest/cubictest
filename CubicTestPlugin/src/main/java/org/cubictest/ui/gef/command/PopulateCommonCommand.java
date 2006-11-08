/*
 * Created on 21.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.cubictest.model.Common;
import org.cubictest.model.FormElement;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Transition;
import org.cubictest.model.context.AbstractContext;
import org.eclipse.gef.commands.Command;


public class PopulateCommonCommand extends Command{

	private Common common;
	private Set<PageElement> elements = new HashSet<PageElement>();
	private List<Page> pages;

	public void setCommon(Common common) {
		this.common = common;
	}
	
	@Override
	public void execute() {
		pages = new ArrayList<Page>();
		
		for(Transition t : common.getOutTransitions())
			pages.add((Page)t.getEnd());
		
		if (pages.size() == 0) 
			return;
		
		Page p = pages.remove(0);
		elements = new HashSet<PageElement>(p.getElements());
		
		List<PageElement> forRemoval = new ArrayList<PageElement>();
		for (Page page : pages){
			elements = getCommonElements(elements,page.getElements(), forRemoval);
		}
		
		pages.add(p);
		
		for (PageElement element : elements){
			common.addElement(element);
		}
		for (PageElement element : forRemoval){
			for (Page page : pages) {
					page.removeElement(element);
			}
		}
	}

	private Set<PageElement> getCommonElements(Set<PageElement> elements2, List<PageElement> elements3, List<PageElement> forRemoval) {
		Set<PageElement> resultSet = new HashSet<PageElement>();
		for (PageElement e2 : elements2){
			for (PageElement e3 : elements3){
				if(equalElements(e2,e3)){
					resultSet.add(e3);
					forRemoval.add(e2);
					forRemoval.add(e3);
				}
			}
		}
		return resultSet;
	}
	
	private boolean equalElements(PageElement e2, PageElement e3) {
		if (!e2.getClass().equals(e3.getClass())) {
			//not same class. never equal for current purpose.
			return false;
		}
		if (e2 instanceof AbstractContext || e3 instanceof AbstractContext) {
			return false;
		}
		else if (e2 instanceof FormElement || e3 instanceof FormElement) {
			return false;
		}
		else {
			//other page elements such as Text, Link, Image and Title
			return (StringUtils.equals(e2.getDescription(), e3.getDescription()) &&
					e2.isNot() == e3.isNot() &&
					StringUtils.equals(e2.getKey(), e3.getKey()) &&
					e2.getSationType().equals(e3.getSationType()));
		}
	}

	@Override
	public void undo() {
		for (PageElement element : elements){
			for (Page page : pages)
				page.addElement(element);
			common.removeElement(element);
		}
	}
}
