/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.utils.exported;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.SubTestStartPoint;
import org.cubictest.model.Test;


/**
 * Utils for export plugins.
 * 
 * @author Christian Schwarz
 *
 */
public class ExportUtils {

	public static String SUBTEST_START_POINT_MSG = "Tests with SubTest start points cannot be exported on their own. They must be part of a normal Test (drag and drop subtest into a normal test).";
	
	
	public static void throwTestNotOkForExport(Test test) {
		if (test.getStartPoint() instanceof SubTestStartPoint) {
			ErrorHandler.logAndThrow(SUBTEST_START_POINT_MSG);
		}
	}

	public static void showTestNotOkForExportMsg(Test test) {
		if (test.getStartPoint() instanceof SubTestStartPoint) {
			ErrorHandler.logAndShowErrorDialog(SUBTEST_START_POINT_MSG);
		}
	}
	
	public static boolean testIsOkForExport(Test test) {
		if (test.getStartPoint() instanceof SubTestStartPoint) {
			return false;
		}
		return true;
	}
}
