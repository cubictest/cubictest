/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.export.holders;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.utils.exported.XPathBuilder;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.PageElement;
import org.cubictest.model.SubTest;
import org.cubictest.model.context.IContext;

/**
 * Holder for context info for runners and exporters.
 * 
 * @author Christian Schwarz
 */
public class ContextHolder implements IResultHolder {


	private Stack<IContext> contextStack = new Stack<IContext>(); 
	private Map<PageElement, PageElement> elementParentMap = new HashMap<PageElement, PageElement>();
	private boolean shouldFailOnAssertionFailure;
	
	/**
	 * Set new context, i.e. XPath starting point (path) for lookup of elements.
	 * @param ctx
	 */
	public void pushContext(IContext ctx) {

		//all interesting contexts are page elements
		if (ctx instanceof PageElement) {
			contextStack.push(ctx);
			
			for (PageElement pe : ctx.getRootElements()) {
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
	 * Gets "smart context": Asserts all elements in context present.
	 */
	public String getFullContextWithAllElements(PageElement pageElement) {
		return getSmartContext(pageElement, pageElement);
	}
	
	
	
	/**
	 * Recursive private utility method. Gets "smart context": Asserts all or previous elements in context present.
	 */
	private String getSmartContext(PageElement pageElement, PageElement orgElement) {
		String res = "";
		if (pageElement == null) {
			return "";
		}
		
		String axis = "/descendant-or-self::";
		if (isInRootContext()) {
			axis = "//";
		}
		res += axis + XPathBuilder.getXPath(pageElement);
		
		if (pageElement instanceof IContext && ((IContext) pageElement).getRootElements().size() > 1) {
			String assertion = getElementsInContextXPath((IContext) pageElement, orgElement);
			if (StringUtils.isNotBlank(assertion)) {
				res += "[" + assertion + "]";
			}
		}
		
		PageElement parent = elementParentMap.get(pageElement);
		return getSmartContext(parent, orgElement) + res;
	}
	


	private String getElementsInContextXPath(IContext context, PageElement orgElement) {
		String res = "";
		int i = 0;
		for (PageElement pe : context.getRootElements()) {
			if (pe.equals(orgElement)) {
				continue; //skip current element
			}
			if (pe.isNot()) {
				continue; //skip elements that are not there
			}
			if (i > 0) {
				res += "][";
			}
			res += ".//" + XPathBuilder.getXPath(pe);
			i++;
		}
		return res;
	}


	public void updateStatus(SubTest subTest, boolean hadException, ConnectionPoint targetConnectionPoint) {
		//Empty. Can be overridden if exporters want to update sub test statuses.
	}


	public boolean shouldFailOnAssertionFailure() {
		return shouldFailOnAssertionFailure;
	}

	public void setShouldFailOnAssertionFailure(boolean shouldFailOnAssertionFailure) {
		this.shouldFailOnAssertionFailure = shouldFailOnAssertionFailure;
	}
}
