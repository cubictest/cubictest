/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.converters;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.CustomTestStep;

/**
 * Selenium custom test step converter.
 * 
 * @author chr_schwarz
 */
public class CustomTestStepConverter implements ICustomTestStepConverter<SeleniumHolder> {

	public void handleCustomStep(SeleniumHolder seleniumHolder, CustomTestStep cts) {
		throw new ExporterException("Custom test step not supported in Selenium runner yet");
	}

}
