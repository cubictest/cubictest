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
package org.cubictest.exporters.selenium.launch.converters;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.utils.Logger;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.common.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.data.CustomTestStepData;

/**
 * Selenium custom test step converter.
 */
public class LaunchCustomTestStepConverter extends CustomTestStepConverter {
	
	public void handleCustomStep(SeleniumHolder t, CustomTestStepHolder cts,
			CustomTestStepData data) {

		boolean waitForPageToLoad = SeleniumUtils.getWaitForPageToLoadValue(data);
		if (waitForPageToLoad) {
			t.getSelenium().waitForPageToLoad((t.getNextPageElementTimeout() * 1000) + "");
		}
		
		CubicTestRemoteRunnerClient runner = t.getCustomStepRunner();
		
		List<String> attributes = new ArrayList<String>();
		attributes.add(data.getDisplayText());
		
		for(CustomTestStepParameter param : cts.getCustomTestStepParameters()){
			attributes.add(param.getKey());
			attributes.add(cts.getValue(param).getValue());
		}
		
		String result = runner.executeOnServer("cubicTestCustomStep",
				attributes.toArray(new String[attributes.size()]));
		if(isBlank(result)){
			t.addResult(cts, TestPartStatus.EXCEPTION);
			throw new ExporterException("Unknown exception while executing Custom Test Step " + cts.getName());
		}
		else if(result.startsWith("Error")){
			t.addResult(cts, TestPartStatus.EXCEPTION);
			throw new ExporterException(result.replaceFirst("Error: ", ""));
		}
		if(result.startsWith("Failure")){
			Logger.error("AssertionError in custom step " + cts.getName() + ": " + result);
			t.addResult(cts, TestPartStatus.FAIL);
		}
		else 
			t.addResult(cts,TestPartStatus.PASS);
	}

}
