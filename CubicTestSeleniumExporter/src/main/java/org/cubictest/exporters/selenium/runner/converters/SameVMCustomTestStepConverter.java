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
package org.cubictest.exporters.selenium.runner.converters;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.common.utils.Logger;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.ElementContext;
import org.cubictest.exporters.selenium.common.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.data.CustomTestStepData;
import org.cubictest.selenium.custom.ICustomTestStep;
import org.cubictest.selenium.custom.IElementContext;

public class SameVMCustomTestStepConverter extends CustomTestStepConverter {

	private static final ThreadLocal<IElementContext> threadElementContext = new ThreadLocal<IElementContext>();

	
	public void handleCustomStep(SeleniumHolder t, CustomTestStepHolder cts,
			CustomTestStepData data) {

		boolean waitForPageToLoad = SeleniumUtils.getWaitForPageToLoadValue(data);
		if (waitForPageToLoad) {
			t.getSelenium().waitForPageToLoad((t.getNextPageElementTimeout() * 1000) + "");
		}
		
		if (threadElementContext.get() == null) {
			throw new ExporterException("Custom step converter thread ElementContext was null. Please set an ElementContext on the SeleniumRunner or this converter.");
		}
		
		Map<String, String> arguments = new HashMap<String, String>();
		
		for(CustomTestStepParameter param : cts.getCustomTestStepParameters()){
			arguments.put(param.getKey(), cts.getValue(param).getValue());
		}
		
		String ctsName = null;
		if (cts != null) {
			ctsName = cts.getName();
		}

		try{
			ICustomTestStep testStep = (ICustomTestStep) Class.forName(data.getDisplayText()).newInstance();
			testStep.execute(arguments, threadElementContext.get(), t.getSelenium().getSelenium());
			t.addResult(cts,TestPartStatus.PASS);
		}catch (Exception e) {
			Logger.error("Error handling custom step " + ctsName, e);
			t.addResult(cts, TestPartStatus.EXCEPTION);
			throw new ExporterException(e);
		}catch (AssertionError e) {
			Logger.error("AssertionError in custom step " + ctsName + ": " + e.getMessage());
			t.addResult(cts, TestPartStatus.FAIL);
		}
	}

	/** Retrieves the ElementContext of this thread. */
	public static IElementContext getThreadElementContext() {
		return threadElementContext.get();
	}

	/** Stores the ElementContext of this thread. */
	public static void setThreadElementContext(IElementContext elementContext) {
		threadElementContext.set(elementContext);
	}
	
	public static void resetElementContext() {
		setThreadElementContext(new ElementContext());
	}
	
}
