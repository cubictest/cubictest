/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.launch;

import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.launch.TestRunner.RunnerParameters;
import org.cubictest.exporters.selenium.runner.SeleniumRunnerConfiguration;
import org.cubictest.model.Test;

public class RunnerLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	protected TestRunner getCubicTestRunnable(TestRunner.RunnerParameters parameters, SeleniumRunnerConfiguration config) {
		return new TestRunner(parameters, config);
	}


	@Override
	protected void verifyPreconditions(RunnerParameters parameters, SeleniumRunnerConfiguration config) {
		final Test test = parameters.test;
		if (!ExportUtils.testIsOkForExport(test)) {
			parameters.display.syncExec(new Runnable() {
				public void run() {
					ExportUtils.showTestNotOkForExportMsg(test);
				}
			});
			ExportUtils.throwTestNotOkForExportException(test);
		}
	}
}
