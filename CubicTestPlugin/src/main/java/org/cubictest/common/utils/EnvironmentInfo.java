/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
