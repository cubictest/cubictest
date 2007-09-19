/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.cubicunit.runner.converters;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.custom.ICustomTestStep;
import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.customstep.data.CustomTestStepData;

public class CustomTestStepConverter implements ICustomTestStepConverter<Holder> {
	
	@Override
	public String getDataKey(){
		return "org.cubictest.cubicunitexporter";
	}

	public void handleCustomStep(Holder t, ICustomTestStepHolder cts,
			CustomTestStepData data) {
		try {
			Class<?> myClass = Class.forName(data.getPath());
			ICustomTestStep ctsimpl = (ICustomTestStep)myClass.newInstance();
			ctsimpl.execute(null, null, null);
		} catch (Exception e) {
			ErrorHandler.logAndRethrow(e, "File in error: " + data.getPath());
		}
	}
}
