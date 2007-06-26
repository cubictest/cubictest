/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.utils.exported;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.SubTestStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;
import org.cubictest.ui.utils.ModelUtil;


/**
 * Utils for export plugins.
 * 
 * @author Christian Schwarz
 *
 */
public class ExportUtils {

	public static String SUBTEST_START_POINT_MSG = "Tests with SubTest start points cannot be exported on their own. They must be part of a normal Test (drag and drop subtest into a normal test).";
	
	
	public static void throwTestNotOkForExportException(Test test) {
		ErrorHandler.logAndThrow(SUBTEST_START_POINT_MSG);
	}

	public static void showTestNotOkForExportMsg(Test test) {
		ErrorHandler.logAndShowErrorDialog(SUBTEST_START_POINT_MSG);
	}
	
	public static boolean testIsOkForExport(Test test) {
		if (test.getStartPoint() instanceof SubTestStartPoint) {
			return false;
		}
		return true;
	}
	
	public static boolean isTestFile(String fileName) {
		return ModelUtil.isTestFile(fileName);
	}
	
	public static boolean testIsOkForRecord(Test test) {
		if (test.getStartPoint() instanceof SubTestStartPoint) {
			ErrorHandler.logAndShowErrorDialog("It is not possible to record from tests that start with a sub test start point.");
			return false;
		}
		else if (test.getStartPoint() instanceof TestSuiteStartPoint) {
			ErrorHandler.logAndShowErrorDialog("It is not possible to record from a test suite start point.\n\n" + 
					"To add tests to the suite, drag and drop test-files from the package explorer into the test suite editor.");
			return false;
		}
		return true;
	}
}
