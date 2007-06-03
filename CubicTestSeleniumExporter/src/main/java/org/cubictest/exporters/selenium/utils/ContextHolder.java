/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.IResultHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;

/**
 * Holder for context info for runner and exporter.
 * 
 * @author Christian Schwarz
 */
public class ContextHolder implements IResultHolder {

	private Stack<IContext> contextStack = new Stack<IContext>(); 
	private Map<PageElement, PageElement> elementParentMap = new HashMap<PageElement, PageElement>();
	
	
	/**
	 * Set new context, i.e. XPath starting point (path) for lookup of elements.
	 * @param ctx
	 */
	public void pushContext(IContext ctx) {

		//all interesting contexts are page elements
		if (ctx instanceof PageElement) {
			contextStack.push(ctx);
			
			for (PageElement pe : ctx.getElements()) {
				//setting current context as parent of each page element within context
				elementParentMap.put(pe, (PageElement) ctx);
			}
		}
	}

	
	/**
	 * Get the previous context, i.e. the previous XPath starting point (path) for lookup of elements.
	 */
	public void popContext() {
		contextStack.pop();
	}
	

	public String toResultString() {
		//should be subclassed
		return "";
	}
	
	
	public boolean isInRootContext() {
		return contextStack.size() == 0;
	}
	
	
	
	/**
	 * Gets "smart context": Asserts all previous elements in context present.
	 */
	public String getXPathWithFullContextAndPreviousElements(PageElement pageElement) {
		return getFullContextWithOtherElements(pageElement, true, pageElement);
	}

	/**
	 * Gets "smart context": Asserts all elements in context present.
	 */
	public String getFullContextWithAllElements(PageElement pageElement) {
		return getFullContextWithOtherElements(pageElement, false, pageElement);
	}
	
	
	
	/**
	 * Private utility method. Gets "smart context": Asserts all or previous elements in context present.
	 */
	private String getFullContextWithOtherElements(PageElement pageElement, boolean assertOnlyPrevElements, PageElement orgElement) {
		String res = "";
		if (pageElement == null) {
			return "";
		}
		
		String axis = "/descendant-or-self::";
		if (isInRootContext()) {
			axis = "//";
		}
		res += axis + SeleniumUtils.getXPath(pageElement);
		
		if (pageElement instanceof IContext && ((IContext) pageElement).getElements().size() > 1) {
			String assertion = getContextElementsAssertion((IContext) pageElement, assertOnlyPrevElements, orgElement);
			if (StringUtils.isNotBlank(assertion)) {
				res += "[" + assertion + "]";
			}
		}
		
		PageElement parent = elementParentMap.get(pageElement);
		return getFullContextWithOtherElements(parent, assertOnlyPrevElements, orgElement) + res;
	}


	private String getContextElementsAssertion(IContext context, boolean assertOnlyPrevElements, PageElement orgElement) {
		String res = "";
		int i = 0;
		for (PageElement pe : context.getElements()) {
			if (pe.equals(orgElement) && assertOnlyPrevElements) {
				break;
			}
			if (i > 0) {
				res += " and ";
			}
			res += ".//" + SeleniumUtils.getXPath(pe);
			i++;
		}
		return res;
	}
}
