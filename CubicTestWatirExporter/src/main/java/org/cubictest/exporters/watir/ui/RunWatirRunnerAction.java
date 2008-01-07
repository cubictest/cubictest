/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.ui;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.ITestRunner;
import org.cubictest.export.ui.BaseRunnerAction;
import org.cubictest.exporters.watir.WatirExporterPlugin;
import org.cubictest.exporters.watir.runner.TestRunner;
import org.cubictest.model.Test;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Action for running a test using the Watir runner.
 * 
 * @author Christian Schwarz
 */
public class RunWatirRunnerAction extends BaseRunnerAction {



	@Override
	public Shell getShell() {
		return WatirExporterPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	}


	@Override
	public ITestRunner getTestRunner(Test test, Display display, CubicTestProjectSettings settings) {
		return new TestRunner(test, display, settings);
	}

	@Override
	protected void finalCleanUp() {
		((TestRunner) testRunner).closeBrowser();
	}
}
