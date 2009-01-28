package org.cubictest.exporters.selenium.runner.converters;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.common.utils.Logger;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.common.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.data.CustomTestStepData;
import org.cubictest.runner.selenium.server.internal.ElementContext;
import org.cubictest.selenium.custom.ICustomTestStep;

public class SameVMCustomTestStepConverter extends CustomTestStepConverter {

	private static ElementContext context = new ElementContext();
	
	public void handleCustomStep(SeleniumHolder t, CustomTestStepHolder cts,
			CustomTestStepData data) {
		
		Map<String, String> arguments = new HashMap<String, String>();
		
		for(CustomTestStepParameter param : cts.getCustomTestStepParameters()){
			arguments.put(param.getKey(), cts.getValue(param).getValue());
		}
		
		String ctsName = null;
		if (cts != null) {
			ctsName = cts.getName();
		}

		try{
			ICustomTestStep testStep = (ICustomTestStep) Class.forName(data.getDisplayText()).newInstance();
			testStep.execute(arguments, context, t.getSelenium().getSelenium());
			t.addResult(cts,TestPartStatus.PASS);
		}catch (Exception e) {
			Logger.error("Error handling custom step " + ctsName, e);
			t.addResult(cts, TestPartStatus.EXCEPTION);
			throw new ExporterException(e);
		}catch (AssertionError e) {
			Logger.error("AssertionError in custom step " + ctsName + ": " + e.getMessage());
			t.addResult(cts, TestPartStatus.FAIL);
		}
	}

	public static void initializeElementContext() {
		context = new ElementContext();
	}

}
