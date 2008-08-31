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
package org.cubictest.export.holders;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.utils.exported.XPathBuilder;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.SubTest;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.context.Frame;
import org.cubictest.model.context.IContext;

/**
 * Holder for context info for runners and exporters.
 * 
 * @author Christian Schwarz
 */
public abstract class ContextHolder implements IResultHolder {


	protected Stack<PropertyAwareObject> breadCrumbs = new Stack<PropertyAwareObject>(); 
	private Stack<IContext> contextStack = new Stack<IContext>(); 
	private Stack<Stack<IContext>> frameStack = new Stack<Stack<IContext>>(); 
	private Map<PageElement, PageElement> elementParentMap = new HashMap<PageElement, PageElement>();
	private boolean shouldFailOnAssertionFailure;
	private boolean useNamespace = false;
	
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
	 * Gets "smart context" assertion for a page element. 
	 * Asserts all sibling elements in context present, and recurses into parent contexts to
	 * give a precise XPath to identify the element.
	 */
	public String getFullContextWithAllElements(PageElement pageElement) {
		return getSmartContext(pageElement, pageElement);
	}
	
	/**
	 * Recursive private utility method. Gets "smart context" for a page element.
	 * Asserts all sibling elements in context present.
	 * Recurses into parent contexts.
	 * @param currentElement Current page element to get XPath for.
	 * @param orgElement The original element for the query.
	 */
	private String getSmartContext(PageElement currentElement, PageElement orgElement) {
		String res = "";
		if (currentElement == null || (currentElement instanceof Frame && !orgElement.equals(currentElement))) {
			return "";
		}
		
		String axis = "/descendant-or-self::";
		if (isInRootContext()) {
			axis = "//";
		}
		
		res += axis + XPathBuilder.getXPathForSingleElement(currentElement, useNamespace);
		
		if (currentElement instanceof IContext && !(currentElement instanceof Frame) 
				&& ((IContext) currentElement).getRootElements().size() > 1) {
			String assertion = getSiblingElementsXPathAssertion(orgElement, (IContext) currentElement);
			if (StringUtils.isNotBlank(assertion)) {
				res += "[" + assertion + "]";
			}
		}
		
		//recurse into parent contexts, appending them to beginning of XPath:
		PageElement parent = elementParentMap.get(currentElement);
		return getSmartContext(parent, orgElement) + res;
	}
	


	private String getSiblingElementsXPathAssertion(PageElement orgElement, IContext context) {
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
			res += ".//" + XPathBuilder.getXPathForSingleElement(pe, useNamespace);
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
	
	public void popBreadcrumb() {
		breadCrumbs.pop();
	}

	public void pushBreadcrumb(PropertyAwareObject element) {
		breadCrumbs.push(element);
	}

	public String getCurrentBreadcrumbs() {
		String res = "";
		for (PropertyAwareObject obj : breadCrumbs) {
			if (StringUtils.isNotBlank(res)) {
				res += " --> ";
			}
			res += obj.getClass().getSimpleName() + ": " + obj.getName();
		}
		return res;
	}
	
	public void pushFrame(Frame frame) {
		frameStack.push(contextStack);
		contextStack = new Stack<IContext>();
		for (PageElement pe : frame.getRootElements()) {
			//setting current context as parent of each page element within context
			elementParentMap.put(pe, frame);
		}
	}

	public void popFrame() {
		contextStack = frameStack.pop();
	}
	
	public boolean isPageElementWithinFrame(PageElement element){
		return getParentFrame(element) != null;
	}
	
	public Frame getParentFrame(PageElement element) {
		PageElement parent = elementParentMap.get(element);
		if(parent == null)
			return null;
		if(parent instanceof Frame)
			return (Frame) parent;
		return getParentFrame(parent);
	}
	
	public void setUseNamespace(boolean useNamespace) {
		this.useNamespace = useNamespace;
	}
	
	public boolean useNamespace() {
		return useNamespace;
	}
}
