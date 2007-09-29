/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.holders;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.holders.RunnerResultHolder;
import org.cubictest.exporters.watir.utils.RubyBuffer;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
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
	
	private static final String UTIL_VARIABLES_PLACEHOLDER = "$$$UTIL_VARIABLES";
	public static final String TEST_DONE = "[Test done]";;
	private RubyBuffer rubyBuffer;
	private boolean browserStarted = false;
	public static final String TEST_STEP_FAILED = "TestStepFailed";
	public static final String TEST_CASE_NAME = "CubicTestExport_";
	public Map<String, PageElement> pageElementIdMap;
	public Map<PageElement, String> idMap;
	public Map<PageElement, String> elementVariableMap;
	public Map<String, Integer> elementTypeCountMap;
	public static String PASS = "[pass]>";
	public static String FAIL = "[-FAIL-]>";
	public static String EXCEPTION = "[-EXCEPTION-]>";
	/** Initial prefix */
	private boolean runnerMode;
	public Map<PageElement, Boolean> pageElementInContextMap;
	
	/** Prefix for non-xpath contexts */
	private String prefix = "ie";
	private int counter;
	
	
	public WatirHolder() {
		this(false, null, null);
	}
	
	/**
	 * Constructor that sets up the Watir script.
	 * @param testName
	 */
	public WatirHolder(boolean runnerMode, Display display, CubicTestProjectSettings settings) {
		super(display, settings);
		this.runnerMode = runnerMode;
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
		rubyBuffer.add("class " + TEST_CASE_NAME + System.currentTimeMillis(), 0);
		rubyBuffer.add("failedSteps = 0", 2);
		rubyBuffer.add("passedSteps = 0", 2);
		rubyBuffer.add(UTIL_VARIABLES_PLACEHOLDER, 0);
		rubyBuffer.add("puts \"Starting test\"", 0);
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
		rubyBuffer.add("end", 0);
		String res = rubyBuffer.toString();
		res = StringUtils.replace(res, UTIL_VARIABLES_PLACEHOLDER, getWatirElementDefinitions());
		return res;
	}


	private String getWatirElementDefinitions() {
		RubyBuffer b = new RubyBuffer();
		b.add("# temp variables for indirect (e.g. 'label') reference, where applicable:", 2);
		for (String s : elementVariableMap.values()) {
			b.add(s + " = nil", 2);
		}
		return b.toString();
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


	public void updateStatus(SubTest theSubTest, boolean hadException) {
		//not possible to track subtests without putting logic in generated watir file
	}
	
	public void registerPageElement(PageElement pe) {
		if (pe instanceof SimpleContext) {
			for (PageElement p : ((SimpleContext) pe).getElements()) {
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
		elementVariableMap.put(pe, type + count);
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
		else if (pe instanceof Option) {
			if (!hasMoreThanOneIdentifier(pe) && (pe.getMainIdentifierType().equals(IdentifierType.LABEL) || 
					pe.getMainIdentifierType().equals(IdentifierType.VALUE) ||
					pe.getMainIdentifierType().equals(IdentifierType.INDEX))) {
				return false;
			}
			throw new ExporterException(pe.toString() + ":\n\nWatir does not support more than one identifier on Options in SelectLists. " +
					"In addition, only \"Value\", \"Index\" and \"Label\" are supported identifiers.");
		}
		
		if (Boolean.TRUE.equals(pageElementInContextMap.get(pe))) {
			return true;
		}
		
		return hasMoreThanOneIdentifier(pe);
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
	
	public PageElement getPageElement(String id) {
		return pageElementIdMap.get(id);
	}

	public String getId(PageElement pe) {
		return idMap.get(pe);
	}
	
	/**
	 * Set prefix to use (e.g. for contexts).
	 * Default prefix is "ie" (internet explorer root).
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Get prefix to use (e.g. for contexts).
	 * Default prefix is "ie" (internet explorer root).
	 */
	public String getPrefix() {
		return prefix;
	}
}