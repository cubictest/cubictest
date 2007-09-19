/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.selenese.converters;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.customstep.data.CustomTestStepData;

/**
 * Selenium custom test step converter.
 * 
 * @author chr_schwarz
 */
public class CustomTestStepConverter implements ICustomTestStepConverter<SeleneseDocument> {

	public String getDataKey() {
		return null;
	}

	public void handleCustomStep(SeleneseDocument t, ICustomTestStepHolder cts,
			CustomTestStepData data) {
		throw new ExporterException("Custom test step not supported in Selenium exporter yet");
	}

}
