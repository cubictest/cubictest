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
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.utils.exported.XPathBuilder;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.SubTest;
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
	protected CubicTestProjectSettings settings;
	
	public CubicTestProjectSettings getSettings() {
		return settings;
	}


	public void setSettings(CubicTestProjectSettings settings) {
		this.settings = settings;
	}


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
	 * Appends child element XPaths.
	 */
	public String getFullContextWithAllElements(PageElement pageElement) {
		String axis = "/descendant-or-self::";
		if (!isInAContext(pageElement)) {
			axis = "//";
		}
		return getFullContextWithAllElements(pageElement, axis, true, null);
	}
	
	 private String getFullContextWithAllElements(PageElement pageElement, String axis, boolean traverseParents, PageElement elementToIgnore) {
		
		String elementExp = axis + XPathBuilder.getXPathForSingleElement(pageElement, useNamespace);
		
		if (pageElement instanceof IContext && !(pageElement instanceof Frame)) {
			IContext context = (IContext) pageElement;
			for (PageElement child : context.getRootElements()) {
				if (child == elementToIgnore) {
					continue;
				}
				elementExp = elementExp + "[" + getFullContextWithAllElements(child, "descendant-or-self::", false, null) + "]";
			}
		}
		
		if (traverseParents && isInAContext(pageElement) && !(getParent(pageElement) instanceof Frame)) {
			elementExp = getFullContextWithAllElements(getParent(pageElement), "/descendant-or-self::", true, pageElement) + elementExp;
		}
		
		return elementExp;
	}

	

	private PageElement getParent(PageElement pageElement) {
		return elementParentMap.get(pageElement);
	}


	private boolean isInAContext(PageElement pageElement) {
		return elementParentMap.get(pageElement) != null;
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
