package org.cubictest.exporters.selenium.common.converters;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;

public abstract class CustomTestStepConverter implements ICustomTestStepConverter<SeleniumHolder> {

	public final String getDataKey() {
		return "org.cubictest.seleniumexporter";
	}

}
