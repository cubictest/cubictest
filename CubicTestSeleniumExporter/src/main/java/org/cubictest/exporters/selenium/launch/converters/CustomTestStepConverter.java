/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.launch.converters;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.data.CustomTestStepData;

/**
 * Selenium custom test step converter.
 * 
 * @author chr_schwarz
 */
public class CustomTestStepConverter implements ICustomTestStepConverter<SeleniumHolder> {
	
	public String getDataKey() {
		return "org.cubictest.seleniumexporter";
	}

	public void handleCustomStep(SeleniumHolder t, ICustomTestStepHolder cts,
			CustomTestStepData data) {
		//throw new ExporterException("Custom test step not supported in Selenium runner yet");
		CubicTestRemoteRunnerClient runner = t.getCustomStepRunner();
		
		List<String> attributes = new ArrayList<String>();
		attributes.add(data.getDisplayText());
		
		for(CustomTestStepParameter param : cts.getCustomTestStepParameters()){
			attributes.add(param.getKey());
			attributes.add(cts.getValue(param).getValue());
		}
		
		String result = runner.executeOnServer("cubicTestCustomStep",
				attributes.toArray(new String[attributes.size()]));
		if(result.startsWith("Error")){
			t.updateStatus(cts, TestPartStatus.EXCEPTION);
			throw new ExporterException(result.replaceFirst("Error: ", result));
		}
		if(result.startsWith("Failure")){
			t.updateStatus(cts, TestPartStatus.FAIL);
			throw new ExporterException(result.replaceFirst("Failure: ", result));
		}
		else 
			t.updateStatus(cts,TestPartStatus.PASS);
	}

}
