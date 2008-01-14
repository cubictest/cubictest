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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate;

public class LaunchConfigurationMigrationDelegate implements
		ILaunchConfigurationMigrationDelegate {

	public boolean isCandidate(ILaunchConfiguration candidate)
			throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	public void migrate(ILaunchConfiguration candidate) throws CoreException {
		// TODO Auto-generated method stub

	}

}
