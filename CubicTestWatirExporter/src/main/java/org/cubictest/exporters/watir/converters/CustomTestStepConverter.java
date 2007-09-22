/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.converters;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.customstep.data.CustomTestStepData;

/**
 * Watir custom test step converter.
 * 
 * @author chr_schwarz
 */
public class CustomTestStepConverter implements ICustomTestStepConverter<WatirHolder> {

	public String getDataKey() {
		return null;
	}

	public void handleCustomStep(WatirHolder t, ICustomTestStepHolder cts,
			CustomTestStepData data) {
		throw new ExporterException("Custom test step not supported in Watir exporter");
	}

}
