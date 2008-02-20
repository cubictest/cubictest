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
package org.cubictest.exporters.selenium.runner.converters;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.customstep.CustomTestStep;
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
		CustomTestStep ts = ((CustomTestStepHolder)cts).getCustomTestStep();
		
		throw new ExporterException("Custom test step not supported in Selenium runner yet");
	}

}
