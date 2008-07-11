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
package org.cubictest.exporters.selenium.common;

import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * @author Christian Schwarz
 *
 */
public class BrowserTypeUtils {

	public static BrowserType getPreferredBrowserType(AbstractUIPlugin plugin, String browserPreferenceKey) {
		BrowserType browserType = null;
		try {
			int storedBrowserTypeIndex = plugin.getDialogSettings().getInt(browserPreferenceKey);
			if (storedBrowserTypeIndex < 0 || storedBrowserTypeIndex > BrowserType.values().length - 1) {
				storedBrowserTypeIndex = 0;
			}
			browserType = BrowserType.values()[storedBrowserTypeIndex];
		} 
		catch(Exception ignore) {
		}
		return browserType;
	}
}
