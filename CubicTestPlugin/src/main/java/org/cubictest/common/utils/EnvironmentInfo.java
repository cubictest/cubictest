/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *    Mao YE - version up, new feature extended
 *******************************************************************************/
package org.cubictest.common.utils;


/**
 * Class containing plugin environment info.
 * 
 * @author Christian Schwarz
 */
public class EnvironmentInfo {

	private static Boolean runningInEclipse;

	/**
	 * Get whether CubicTest is running in Eclipse or not.
	 * @return
	 */
	public static boolean isRunningInEclipse() {
		if (runningInEclipse == null) {
			try {
				Class<?> plugin = Class.forName("org.cubictest.CubicTestPlugin");
				if (plugin == null) {
					runningInEclipse = false;
				}
				else {
					runningInEclipse = true;
				}
				
			}
			catch (Throwable e) {
				runningInEclipse = false;
			}
		}
		return runningInEclipse;
	}
}
