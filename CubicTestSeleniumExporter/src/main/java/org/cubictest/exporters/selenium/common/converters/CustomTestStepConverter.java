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
package org.cubictest.exporters.selenium.common.converters;

import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;

public abstract class CustomTestStepConverter implements ICustomTestStepConverter<SeleniumHolder> {

	public final String getDataKey() {
		return "org.cubictest.seleniumexporter";
	}

}
