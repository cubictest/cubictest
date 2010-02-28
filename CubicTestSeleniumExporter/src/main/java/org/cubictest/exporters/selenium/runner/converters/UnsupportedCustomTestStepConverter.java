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

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.common.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.customstep.data.CustomTestStepData;

/**
 * Selenium custom test step converter.
 * 
 * @author chr_schwarz
 */
public class UnsupportedCustomTestStepConverter extends CustomTestStepConverter {
	
	public void handleCustomStep(SeleniumHolder t, CustomTestStepHolder cts, CustomTestStepData data) {
		throw new ExporterException("Custom test step not supported in standard Selenium runner. Use the new Runner: Right click on test -> Run As -> Run Test with Selenium");
	}

}
