/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.model;

import java.util.List;

import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.CustomTestStepValue;

public interface ICustomTestStepHolder {
	
	public List<CustomTestStepParameter> getCustomTestStepParameters();
	
	public CustomTestStepValue getValue(CustomTestStepParameter parameter);

	public String getDisplayText();
	
	public void setStatus(TestPartStatus status);

}
