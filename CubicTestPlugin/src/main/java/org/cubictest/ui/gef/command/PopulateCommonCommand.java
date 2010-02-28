/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.cubictest.model.Common;
import org.cubictest.model.FormElement;
import org.cubictest.model.Identifier;
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
		elements = new HashSet<PageElement>(p.getRootElements());
		
		List<PageElement> forRemoval = new ArrayList<PageElement>();
		for (Page page : pages){
			elements = getCommonElements(elements,page.getRootElements(), forRemoval);
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
			if(e2.getIdentifiers().size() != e3.getIdentifiers().size()){
				return false;
			}
			for(int i = 0; i < e2.getIdentifiers().size(); i++){
				Identifier id2 = e2.getIdentifiers().get(i);
				Identifier id3 = e3.getIdentifiers().get(i);
				if(!StringUtils.equals(id2.getActual(),id3.getActual())||
						(id2.getProbability() != id3.getProbability()) ||
						!StringUtils.equals(id2.getI18nKey(),id3.getI18nKey()) ||
						!StringUtils.equals(id2.getParamKey(),id3.getParamKey()) ||
						(id2.useI18n() != id3.useI18n()) ||
						(id2.useParam() != id3.useParam()) ||
						(id2.getType() != id3.getType()) ||
						!StringUtils.equals(id2.getValue(),id3.getValue()))
					return false;
			}
			return (StringUtils.equals(e2.getDescription(), e3.getDescription()) &&
					e2.isNot() == e3.isNot());
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
