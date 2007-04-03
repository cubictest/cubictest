/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.runner.util;

import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;

/**
 * Utils for runner.
 * 
 * @author Christian Schwarz
 */
public class RunnerUtils {

	
	public static void setStatus(PageElement pe, String actual, String expected) {
		if (actual.equals(expected)) {
			pe.setStatus(TestPartStatus.PASS);
		}
		else {
			pe.setStatus(TestPartStatus.FAIL);
		}
	}
}
