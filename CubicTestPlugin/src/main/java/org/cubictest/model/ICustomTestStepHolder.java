package org.cubictest.model;

import java.util.List;

import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.CustomTestStepValue;

public interface ICustomTestStepHolder {
	
	public List<CustomTestStepParameter> getCustomTestStepParameters();
	
	public CustomTestStepValue getValue(CustomTestStepParameter parameter);

	public String getDisplayText();

}
