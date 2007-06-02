/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.utils;

import static org.cubictest.model.IdentifierType.INDEX;
import static org.cubictest.model.IdentifierType.LABEL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.IResultHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;

/**
 * Holder for context info for runner and exporter.
 * 
 * @author Christian Schwarz
 */
public class ContextHolder implements IResultHolder {

	private static final String ROOT_CONTEXT = "//";
	/** The XPath starting point (path) for lookup of elements. */
	private Stack<String> context = new Stack<String>(); 
	private Map<PageElement, String> contextMap = new HashMap<PageElement, String>();
	private Map<PageElement, AbstractContext> elementParentMap = new HashMap<PageElement, AbstractContext>();
	
	public ContextHolder() {
		//Setting default context to be anywhere in the document ("//" in XPath)
		context.push(ROOT_CONTEXT);
	}
	
	
	/**
	 * Set new context, i.e. XPath starting point (path) for lookup of elements.
	 * @param ctx
	 */
	public void pushContext(IContext ctx) {
		if (ctx instanceof Select) {
			Select select = (Select) ctx;
			if (select.getMainIdentifierType().equals(LABEL)) {
				context.push("select[@id=(//label[text()=\"" + select.getMainIdentifierValue() + "\"]/@for)]//");
			}
			else if (select.getMainIdentifierType().equals(INDEX)) {
				context.push("select[@id=(//label[text()=\"" + select.getMainIdentifierValue() + "\"]/@for)]//");
			}
			else {
				String idType = SeleniumUtils.getIdType(select.getMainIdentifier());
				context.push("select[@" + idType + "=\"" + select.getMainIdentifierValue() + "\"]//");
			}
		}
		else if (ctx instanceof AbstractContext) {
			context.push(SeleniumUtils.getXPath((AbstractContext) ctx) + "/descendant-or-self::");
		}
		
		for (PageElement pe : ctx.getElements()) {
			//saving the context of each page element for use in user interactions:
			contextMap.put(pe, getFullContext());
			
			//setting current context as parent of each page element within context
			if(ctx instanceof AbstractContext) {
				elementParentMap.put(pe, (AbstractContext) ctx);
			}
		}
	}

	
	/**
	 * Get the previous context, i.e. the previous XPath starting point (path) for lookup of elements.
	 */
	public void popContext() {
		context.pop();
	}
	
	
	/**
	 * Get the currenct active context, appending all known contexts into an XPath string 
	 * that can be used for lookup of elements.
	 * @return 
	 */
	public String getFullContext() {
		StringBuffer buff = new StringBuffer();
		
		//Concatenate known contexts to a single XPath line:
		//First in First Out: 
		for (String contextPart : context) {
			buff.append(contextPart);
		}
		
		//return the full, concatenated context:
		return buff.toString();
	}


	public String toResultString() {
		return "";
	}
	
	/**
	 * Get the context of a previously asserted page element.
	 * @param pe
	 * @return
	 */
	public String getFullContext(PageElement pe) {
		String ctx = contextMap.get(pe);
		if(StringUtils.isNotBlank(ctx)) {
			return ctx;
		}
		else {
			//did not find element in map. Default to the current context.
			return getFullContext();
		}
		
	}
	
	public boolean isInRootContext() {
		return getFullContext().equals(ROOT_CONTEXT);
	}
	
	
	
	public String getXPathWithFullContextAndPreviousElements(PageElement pageElement) {
		return getFullContextWithOtherElements(pageElement, true, pageElement);
	}

	public String getFullContextWithAllElements(PageElement pageElement) {
		return getFullContextWithOtherElements(pageElement, false, pageElement);
	}
	
	
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
		
		if (pageElement instanceof AbstractContext && ((AbstractContext) pageElement).getElements().size() > 1) {
			String assertion = getContextElementsAssertion((AbstractContext) pageElement, assertOnlyPrevElements, orgElement);
			if (StringUtils.isNotBlank(assertion)) {
				res += "[" + assertion + "]";
			}
		}
		
		AbstractContext parent = elementParentMap.get(pageElement);
		return getFullContextWithOtherElements(parent, assertOnlyPrevElements, orgElement) + res;
	}


	private String getContextElementsAssertion(AbstractContext context, boolean assertOnlyPrevElements, PageElement orgElement) {
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
