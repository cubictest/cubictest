/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.holders;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.holders.RunnerResultHolder;
import org.cubictest.exporters.watir.utils.RubyBuffer;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Moderator;
import org.cubictest.model.PageElement;
import org.cubictest.model.SubTest;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.Option;
import org.eclipse.swt.widgets.Display;


/**
 * Stores the watir test as a series of test steps.
 * Provides methods for adding steps to list and getting the final list. 
 * 
 * @author chr_schwarz
 */
public class WatirHolder extends RunnerResultHolder {
	
	public static final String UNEXPECTED_ERROR_FROM_WATIR_RUNNER = "Unexpected error from Watir runner";
	public static final String TEST_DONE = "[Test done]";;
	public static final String SUBTEST_DONE = "[Subtest done]> ";;
	private RubyBuffer rubyBuffer;
	private boolean browserStarted = false;
	public static final String TEST_STEP_FAILED = "TestStepFailed";
	public static final String TEST_CASE_NAME = "CubicTestExport_";
	public static final String INTERACTION_FAILURE = "InteractionFailure";
	public Map<String, PageElement> pageElementIdMap;
	public Map<PageElement, String> idMap;
	public Map<PageElement, String> elementVariableMap;
	public Map<String, Integer> elementTypeCountMap;
	public static String PASS = "[-PASS-]>";
	public static String FAIL = "[-FAIL-]>";
	public static String EXCEPTION = "[-EXCEPTION-]>";
	
	/** Container to get elements from. Initial value is "ie" (watir root object), can be set to frame objects */
	private Stack<String> containers = new Stack<String>();
	
	public Map<PageElement, Boolean> pageElementInContextMap;
	
	public WatirHolder() {
		this(null, null);
	}
	
	/**
	 * Constructor that sets up the Watir script.
	 * @param testName
	 */
	public WatirHolder(Display display, CubicTestProjectSettings settings) {
		super(display, settings);
		this.containers.push("ie");
		this.rubyBuffer = new RubyBuffer();
		pageElementIdMap = new HashMap<String, PageElement>();
		elementVariableMap = new LinkedHashMap<PageElement, String>();
		elementTypeCountMap = new HashMap<String, Integer>();
		idMap = new HashMap<PageElement, String>();
		pageElementInContextMap = new HashMap<PageElement, Boolean>();
		setUpWatirTest();
	}

	
	/**
	 * Adds string to the step list.
	 * If not indented, adds with default indent.
	 */
	public void add(String s) {
		if (!s.startsWith("\t")) {
			//use default indent:
			rubyBuffer.add(s, 2);
		}
		else {
			rubyBuffer.add(s);
		}
	}

	/**
	 * Adds string to the step list with the secified indent.
	 */
	public void add(String s, int indent) {
		rubyBuffer.add(s, indent);
	}


	private void setUpWatirTest() {
		rubyBuffer.add("require 'rubygems'", 0);
		rubyBuffer.add("require 'watir'", 0);
		rubyBuffer.add("class " + TEST_STEP_FAILED + " < RuntimeError", 0);
		rubyBuffer.add("end", 0);
		rubyBuffer.add("class " + INTERACTION_FAILURE + " < RuntimeError", 0);
		rubyBuffer.add("end", 0);
		rubyBuffer.add("class " + TEST_CASE_NAME + System.currentTimeMillis(), 0);
		rubyBuffer.add("begin", 1);
		rubyBuffer.add("failedSteps = 0", 2);
		rubyBuffer.add("passedSteps = 0", 2);
		rubyBuffer.add("puts \"Starting test..\"", 2);
	}
	
	
	/**
	 * Get the String representation of the step list.
	 * This can be stored to file and excecuted.
	 */
	public String toResultString() {
		rubyBuffer.add("", 0);
		rubyBuffer.add("", 0);
		rubyBuffer.add("puts \"\"", 2);
		
		rubyBuffer.add("if (failedSteps == 0)", 2);
		rubyBuffer.add("puts (passedSteps.to_s + \" steps passed, no steps failed.\")", 3);
		rubyBuffer.add("else", 2);
		rubyBuffer.add("puts(passedSteps.to_s + \" steps passed, \" + failedSteps.to_s + \" steps failed!\")", 3);
		rubyBuffer.add("end", 2);
		
		rubyBuffer.add("puts \"" + TEST_DONE + "\"", 2);
		rubyBuffer.add("puts \"Press enter to exit\"", 2);
		rubyBuffer.add("STDOUT.flush", 2);
		rubyBuffer.add("gets", 2);
		rubyBuffer.add("puts \"Closing browser..\"", 2);
		rubyBuffer.add("STDOUT.flush", 2);
		rubyBuffer.add("ie.close", 2);
		rubyBuffer.add("rescue " + INTERACTION_FAILURE, 1);
		rubyBuffer.add("puts \"There were failures during user interactions. Test was stopped.\"", 2);
		rubyBuffer.add("rescue", 1);
		rubyBuffer.add("puts \"" + UNEXPECTED_ERROR_FROM_WATIR_RUNNER + ": \" + $!", 2);
		rubyBuffer.add("end", 1);
		rubyBuffer.add("end", 0);
		rubyBuffer.add("", 0);
		String res = rubyBuffer.toString();
		return res;
	}


	public boolean isBrowserStarted() {
		return browserStarted;
	}


	public void setBrowserStarted(boolean browserStarted) {
		this.browserStarted = browserStarted;
	}
	
	public void addSeparator() {
		rubyBuffer.add("\n");
	}

	@Override
	public void updateStatus(SubTest subTest, boolean hadException, ConnectionPoint targetConnectionPoint) {
		rubyBuffer.add("puts \"" + SUBTEST_DONE + ": " + subTest.getName() + "\"", 2);
	}
	
	public void registerPageElement(PageElement pe) {
		if (pe instanceof SimpleContext) {
			for (PageElement p : ((SimpleContext) pe).getRootElements()) {
				pageElementInContextMap.put(p, true);
			}
		}
		String id = pe.toString();
		if (pageElementIdMap.get(id) != null) {
			id = id + "--" + pe.hashCode();
		}
		pageElementIdMap.put(id, pe);
		idMap.put(pe, id);
		
		String type = pe.getType().toLowerCase();
		int count = elementTypeCountMap.get(type) == null ? 0 : elementTypeCountMap.get(type);
		elementVariableMap.put(pe, "@" + type + count);
		elementTypeCountMap.put(type, ++count);
	}
	
	public String getVariableName(PageElement pe) {
		return elementVariableMap.get(pe);
	}
	
	/**
	 * XPath is slow in Watir, so only require it when necessary.
	 * @param pe
	 * @return
	 */
	public boolean requiresXPath(PageElement pe) {
		if (pe instanceof SimpleContext) {
			return true;
		}
		
		IdentifierType mainIdType = pe.getMainIdentifierType();
		if (mainIdType != null
				&& (mainIdType.equals(IdentifierType.SRC) || mainIdType.equals(IdentifierType.HREF))
				&& (!pe.getMainIdentifierValue().startsWith("http"))
				&& (!pe.getMainIdentifier().getModerator().equals(Moderator.EQUAL))) {
			return true;
		}
		
		if (pe instanceof Option) {
			Option option = (Option) pe;
			if (!hasMoreThanOneIdentifier(option) && 
					(option.getMainIdentifierType().equals(IdentifierType.LABEL) || 
					option.getMainIdentifierType().equals(IdentifierType.VALUE) ||
					option.getMainIdentifierType().equals(IdentifierType.INDEX))) {
				return false;
			}
			throw new ExporterException(pe.toString() + ":\n\nWatir does not support more than one identifier on Options in SelectLists. " +
					"In addition, only \"Value\", \"Index\" and \"Label\" are supported identifiers.");
		}
		
		if (Boolean.TRUE.equals(pageElementInContextMap.get(pe))) {
			return true;
		}
		
		return hasNoIdentifiers(pe) || hasMoreThanOneIdentifier(pe);
	}

	private boolean hasMoreThanOneIdentifier(PageElement pe) {
		int num = 0;
		for (Identifier id : pe.getIdentifiers()) {
			if (id.isNotIndifferent()) {
				num++;
			}
		}
		return num > 1;
	}

	private boolean hasNoIdentifiers(PageElement pe) {
		int num = 0;
		for (Identifier id : pe.getIdentifiers()) {
			if (id.isNotIndifferent()) {
				num++;
			}
		}
		return num == 0;
	}
	
	public PageElement getPageElement(String id) {
		return pageElementIdMap.get(id);
	}

	public String getId(PageElement pe) {
		return idMap.get(pe);
	}

	/** Set active Watir::Container object, e.g. a Watir::Frame */
	public void pushContainer(String container) {
		containers.push(container);
	}

	public void popContainer() {
		containers.pop();
	}
	
	public String getActiveContainer() {
		return containers.peek();
	}

	public void setTestName(String testName) {
		// not in use
	}
	
}