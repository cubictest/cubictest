/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.runner.cubicunit.delegates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.custom.IElementContext;
import org.cubictest.export.IResultHolder;
import org.cubictest.model.PageElement;
import org.cubicunit.Browser;
import org.cubicunit.Container;
import org.cubicunit.Document;
import org.cubicunit.Element;

public class Holder implements IResultHolder {

	private Container container;
	
	private Map<PageElement, Element> elementMap = new HashMap<PageElement, Element>();
	private List<Browser> browsers = new ArrayList<Browser>();
	private int browserIndex;
	private IElementContext context; 
	
	public Container getContainer() {
		return container;
	}
	
	public void setContainer(Container container) {
		this.container = container;
	}
	
	public void put(PageElement pe, Element element){
		elementMap.put(pe, element);
	}
	
	public Element getElement(PageElement element){
		return elementMap.get(element);
	}


	public Document getDocument() {
		return getBrowser().getDocument();
	}

	public Browser getBrowser() {
		return browsers.get(browserIndex);
	}
	public Browser getNextBrowser(){
		if(browsers.size() >= ++browserIndex){
			browserIndex = 0;
		}
		return browsers.get(browserIndex);
	}
	
	public Browser getPreviousBrowser() {
		if(0 == browserIndex){
			browserIndex = browsers.size();
		}
		return browsers.get(--browserIndex);
	}
	
	public void addBrowser(Browser browser) {
		if(browsers.indexOf(browser) == -1){
			browsers.add(browser);
		}
		browserIndex = browsers.indexOf(browser);
	}

	public IElementContext getContext() {
		if (context == null) {
			context = new ElementContext();
		}
		return context;
	}

	public String toResultString() {
		return "runner";
	}
}
