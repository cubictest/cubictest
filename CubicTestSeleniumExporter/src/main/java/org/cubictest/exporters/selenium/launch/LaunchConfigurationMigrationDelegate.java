/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class LaunchConfigurationMigrationDelegate implements
		ILaunchConfigurationMigrationDelegate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate#isCandidate()
	 */
	public boolean isCandidate(ILaunchConfiguration candidate) throws CoreException {
		IResource[] mapped = candidate.getMappedResources();
		IResource target = getResource(candidate);
		if (target == null) {
			return mapped == null;
		} else {
			if (mapped == null) {
				return true;
			} else {
				if (mapped.length != 1) {
					return true;
				} else {
					return !target.equals(mapped[0]);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate#migrate(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void migrate(ILaunchConfiguration candidate) throws CoreException {
		ILaunchConfigurationWorkingCopy wc= candidate.getWorkingCopy();
		mapResources(wc);
		wc.doSave();
	}
	
	/**
	 * Maps a resource for the given launch configuration.
	 * 
	 * @param config working copy
	 * @throws CoreException if an exception occurs mapping resource
	 */
	public static void mapResources(ILaunchConfigurationWorkingCopy config) throws CoreException {
		IResource resource = getResource(config);
		if (resource == null) {
			config.setMappedResources(null);
		} else {
			config.setMappedResources(new IResource[]{resource});
		}
	}
	
	/**
	 * Returns a resource mapping for the given launch configuration, or <code>null</code>
	 * if none.
	 * 
	 * @param config working copy
	 * @returns resource or <code>null</code>
	 * @throws CoreException if an exception occurs mapping resource
	 */
	private static IResource getResource(ILaunchConfiguration config) throws CoreException {
		String projName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		String testName = config.getAttribute(SeleniumRunnerTab.CUBIC_TEST_NAME, (String)null);
		if (projName != null && Path.ROOT.isValidSegment(projName)) {
			IProject project = ResourcesPlugin.getWorkspace().
				getRoot().getProject(projName);
			if (project.exists()) {
				if (testName != null) {
					return (IResource)project.getFile(testName);
				}
			}
		}
		return null;
	}
	
}
