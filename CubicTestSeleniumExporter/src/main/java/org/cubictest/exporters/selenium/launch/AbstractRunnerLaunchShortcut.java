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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.Logger;
import org.cubictest.exporters.selenium.common.BrowserType;
import org.cubictest.exporters.selenium.common.SeleniumExporterProjectSettings;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public abstract class AbstractRunnerLaunchShortcut implements ILaunchShortcut {

	protected BrowserType[] getSupportedBrowsers() {
		return BrowserType.values();
	}
	
	public void launch(ISelection selection, String mode) {
		if(selection instanceof StructuredSelection) {
			launch((IFile) ((StructuredSelection) selection).getFirstElement(), mode);
		}
	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IFile file = (IFile)input.getAdapter(IFile.class);
		if(file != null){
			launch(file, mode);
		}
	}

	private void launch(IFile element, String mode) {
		try {
			element.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			ILaunchConfigurationWorkingCopy temparary = createLaunchConfiguration(element);
			ILaunchConfiguration config = findExistingLaunchConfiguration(temparary, mode);
			if (config == null) {
				// no existing found: create a new one
				config= temparary.doSave();
			}
			DebugUITools.launch(config, mode);
		} catch (CoreException e) {
			Logger.error(e);
		} catch (InterruptedException e) {
			Logger.error(e);
		}
	}
	

	private ILaunchConfigurationWorkingCopy createLaunchConfiguration(IFile file) throws CoreException {
		ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(
			getLaunchConfigurationTypeId());
		
		CubicTestProjectSettings settings = CubicTestProjectSettings.getInstanceFromActivePage();
		
		ILaunchConfigurationWorkingCopy wc= configType.newInstance(file.getParent(), 
				getLaunchManager().generateUniqueLaunchConfigurationNameFrom(file.getName()));
		
		BrowserType preferredBrowser = SeleniumExporterProjectSettings.getPreferredBrowser(settings);
		if (asList(getSupportedBrowsers()).indexOf(preferredBrowser) < 0) {
			preferredBrowser = getSupportedBrowsers()[0];
		}
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_BROWSER, preferredBrowser.getId());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
				file.getProject().getName());
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_NAME, 
				file.getProjectRelativePath().toPortableString());
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_NAMESPACE_XPATH, false);
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_SELENIUM_SERVER_HOST, "localhost");
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_SELENIUM_SERVER_PORT, "4444");
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_SELENIUM_SERVER_MULTI_WINDOW, false);
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_SELENIUM_TAKE_SCREENSHOTS, false);
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_SELENIUM_CAPTURE_HTML, false);
		wc.setAttribute(SeleniumRunnerTab.CUBIC_TEST_SELENIUM_SERVER_AUTO_HOST_AND_PORT, true);
		LaunchConfigurationMigrationDelegate.mapResources(wc);
		return wc;
	}

	protected abstract String getLaunchConfigurationTypeId();
	
	private ILaunchConfiguration findExistingLaunchConfiguration(
			ILaunchConfigurationWorkingCopy temporary, String mode) throws CoreException, InterruptedException {
		ILaunchConfigurationType configType= temporary.getType();

		ILaunchConfiguration[] configs= getLaunchManager().getLaunchConfigurations(configType);
		String[] attributeToCompare= getAttributeNamesToCompare();
		
		List<ILaunchConfiguration> candidateConfigs = 
			new ArrayList<ILaunchConfiguration>(configs.length);
		for (int i= 0; i < configs.length; i++) {
			ILaunchConfiguration config= configs[i];
			if (hasSameAttributes(config, temporary, attributeToCompare)) {
				candidateConfigs.add(config);
			}
		}

		// If there are no existing configs associated with the IType, create
		// one.
		// If there is exactly one config associated with the IType, return it.
		// Otherwise, if there is more than one config associated with the
		// IType, prompt the
		// user to choose one.
		int candidateCount= candidateConfigs.size();
		if (candidateCount == 0) {
			return null;
		} else if (candidateCount == 1) {
			return (ILaunchConfiguration) candidateConfigs.get(0);
		} else {
			// Prompt the user to choose a config. A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching
			// anything.
			ILaunchConfiguration config = chooseConfiguration(candidateConfigs, mode);
			if (config != null) {
				return config;
			}
		}
		return null;
	}

	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}
	
	protected String[] getAttributeNamesToCompare() {
		return new String[] {
				//SeleniumRunnerTab.CUBIC_TEST_BROWSER,
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				SeleniumRunnerTab.CUBIC_TEST_NAME/*,
				SeleniumRunnerTab.CUBIC_TEST_NAMESPACE_XPATH,
				SeleniumRunnerTab.CUBIC_TEST_SELENIUM_SERVER_HOST,
				SeleniumRunnerTab.CUBIC_TEST_SELENIUM_SERVER_PORT,
				SeleniumRunnerTab.CUBIC_TEST_SELENIUM_SERVER_MULTI_WINDOW*/
		};
	}
	
	private static boolean hasSameAttributes(ILaunchConfiguration config1, ILaunchConfiguration config2, String[] attributeToCompares) {
		try {
			for (String attributeToCompare : attributeToCompares) {
				if(SeleniumRunnerTab.CUBIC_TEST_NAMESPACE_XPATH.equals(attributeToCompare) ||
						SeleniumRunnerTab.CUBIC_TEST_SELENIUM_SERVER_MULTI_WINDOW.equals(attributeToCompare)){
					boolean val1 = config1.getAttribute(attributeToCompare, false);
					boolean val2 = config2.getAttribute(attributeToCompare, false);
					if (val1 != val2) {
						return false;
					}
				}else{
					String val1 = config1.getAttribute(attributeToCompare, "");
					String val2 = config2.getAttribute(attributeToCompare, "");
					if (!val1.equals(val2)) {
						return false;
					}
				}
			}
			return true;
		} catch (CoreException e) {
			// ignore access problems here, return false
		}
		return false;
	}
	
	private ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList, String mode) throws InterruptedException {
		IDebugModelPresentation labelProvider= DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(new Shell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("Select configuration");
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			dialog.setMessage("Select debug configuration");
		} else {
			dialog.setMessage("Select run configuration");
		}
		dialog.setMultipleSelection(false);
		int result= dialog.open();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		throw new InterruptedException(); // cancelled by user
	}
}
