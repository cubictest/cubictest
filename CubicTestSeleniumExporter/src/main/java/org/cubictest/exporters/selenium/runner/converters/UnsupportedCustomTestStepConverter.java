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

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.common.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.model.customstep.data.CustomTestStepData;

/**
 * Selenium custom test step converter.
 * 
 * @author chr_schwarz
 */
public class UnsupportedCustomTestStepConverter extends CustomTestStepConverter {
	
	public void handleCustomStep(SeleniumHolder t, ICustomTestStepHolder cts,
			CustomTestStepData data) {
		CustomTestStep ts = ((CustomTestStepHolder)cts).getCustomTestStep(false);
		
		throw new ExporterException("Custom test step not supported in standard Selenium runner. Use the new Runner: Right click on test -> Run As -> Run CubicTest with Selenium");
	}

}
