package org.cubictest.exporters.selenium.runner.converters;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.exporters.selenium.common.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.data.CustomTestStepData;
import org.cubictest.runner.selenium.server.internal.ElementContext;
import org.cubictest.selenium.custom.ICustomTestStep;

public class SameVMCustomTestStepConverter extends CustomTestStepConverter {

	private ElementContext context;
	
	public void handleCustomStep(SeleniumHolder t, ICustomTestStepHolder cts,
			CustomTestStepData data) {
		
		Map<String, String> arguments = new HashMap<String, String>();
		
		for(CustomTestStepParameter param : cts.getCustomTestStepParameters()){
			arguments.put(param.getKey(), cts.getValue(param).getValue());
		}
		
		if(context == null){
			context = new ElementContext();
		}
		
		try{
			ICustomTestStep testStep = (ICustomTestStep) Class.forName(data.getDisplayText()).newInstance();
			testStep.execute(arguments, context, t.getSelenium().getSelenium());
			t.updateStatus(cts,TestPartStatus.PASS);
		}catch (Exception e) {
			t.updateStatus(cts, TestPartStatus.EXCEPTION);
		}catch (AssertionError e) {
			t.updateStatus(cts, TestPartStatus.FAIL);
		}
	}

}
