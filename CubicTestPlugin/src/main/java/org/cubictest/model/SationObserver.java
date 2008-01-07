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

/**
 * Interface for parameterization observers.
 * 
 * @author SK Skytteren
 *
 */
public interface SationObserver{

	public void setUseParam(boolean useParam);
	public boolean useParam();
	
	public void setUseI18n(boolean useI18n);
	public boolean useI18n();
	
	public void setValue(String value);
	
	public String getParamKey();
	
	public String getI18nKey();

	public String getValue();
}