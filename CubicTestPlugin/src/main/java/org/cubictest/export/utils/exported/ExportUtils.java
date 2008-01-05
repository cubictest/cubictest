/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.utils.exported;

import static org.cubictest.model.IdentifierType.CHECKED;
import static org.cubictest.model.IdentifierType.CLASS;
import static org.cubictest.model.IdentifierType.HREF;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.INDEX;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.MULTISELECT;
import static org.cubictest.model.IdentifierType.NAME;
import static org.cubictest.model.IdentifierType.SELECTED;
import static org.cubictest.model.IdentifierType.SRC;
import static org.cubictest.model.IdentifierType.TITLE;
import static org.cubictest.model.IdentifierType.VALUE;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.model.Identifier;
import org.cubictest.model.SubTestStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;


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
	
	
	/**
	 * Get the Selenium ID type based on the specified Identifier.
	 * Also works for HTML ID's except for LABEL.
	 * @param id
	 * @return
	 */
	public static String getHtmlIdType(Identifier id) {
		if (id.getType().equals(LABEL)) {
			return "value";
		}
		else if (id.getType().equals(NAME)) {
			return "name";
		}
		else if (id.getType().equals(ID)) {
			return "id";
		}
		else if (id.getType().equals(VALUE)) {
			return "value";
		}
		else if (id.getType().equals(HREF)) {
			return "href";
		}
		else if (id.getType().equals(SRC)) {
			return "src";
		}
		else if (id.getType().equals(TITLE)) {
			return "title";
		}
		else if (id.getType().equals(CHECKED)) {
			return "checked";
		}
		else if (id.getType().equals(SELECTED)) {
			return "selected";
		}
		else if (id.getType().equals(MULTISELECT)) {
			return "multiple";
		}
		else if (id.getType().equals(INDEX)) {
			return "index";
		}
		else if (id.getType().equals(CLASS)) {
			return "class";
		}
		return null;
	}
}
