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
package org.cubictest.export.converters;

import org.cubictest.export.holders.IResultHolder;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.customstep.data.CustomTestStepData;

public interface ICustomTestStepConverter<T extends IResultHolder> {
	public void handleCustomStep(T t, CustomTestStepHolder cts, CustomTestStepData data);

	public String getDataKey();
	

}
