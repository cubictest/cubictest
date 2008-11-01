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
package org.cubictest.export.utils.exported;

import static org.cubictest.model.IdentifierType.ALT;
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

import java.io.IOException;
import java.net.ServerSocket;

import org.cubictest.common.exception.CubicException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.SubTest;
import org.cubictest.model.SubTestStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;
import org.cubictest.model.UrlStartPoint;


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
		else if (id.getType().equals(ALT)) {
			return "alt";
		}
		return null;
	}
	
	public static synchronized int findAvailablePort() {
		try {
			ServerSocket s = new ServerSocket();
			s.bind(null);
			int port = s.getLocalPort();
			s.close();
			return port;
		}
		catch (IOException e) {
			throw new CubicException(e);
		}
	}
	
	
	/**
	 * Get the initial URL start point of the test (expands subtests).
	 */
	public static UrlStartPoint getInitialUrlStartPoint(Test test) {
		if (test.getStartPoint() instanceof UrlStartPoint) {
			return (UrlStartPoint) test.getStartPoint();
		} else if (test.getStartPoint() instanceof ExtensionStartPoint) {
			// Get url start point recursively:
			return getInitialUrlStartPoint(((ExtensionStartPoint) test
					.getStartPoint()).getTest(true));
		} else if (test.getStartPoint() instanceof TestSuiteStartPoint) {
			// Get url start point of first sub-test:
			if (!(test.getFirstNodeAfterStartPoint() instanceof SubTest)) {
				ErrorHandler
						.logAndShowErrorDialogAndThrow("Test suite is empty.\n\n"
								+ "To add a test, drag it from the package explorer into the test suite editor.");
			}
			return getInitialUrlStartPoint(((SubTest) test
					.getFirstNodeAfterStartPoint()).getTest(true));
		}
		return null;
	}
}
