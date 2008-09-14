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
package org.cubictest;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.pde.internal.runtime.PDERuntimePlugin;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Stein K. Skytteren
 *
 */
public class CubicTestPerspectiveFactory implements IPerspectiveFactory {

	private static final String LOG_VIEW_ID = "org.eclipse.pde.runtime.LogView";

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewTestWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewSubTestWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewTestSuiteWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewCubicTestProjectWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewParamWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewPropertiesWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewCustomTestStepWizard");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard");
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); 
  		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); 
  		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewEnumCreationWizard");
		
  		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(JavaUI.ID_PACKAGES); //package explorer
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(LOG_VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);
		
		layout.addPerspectiveShortcut(JavaUI.ID_PERSPECTIVE);
		layout.addPerspectiveShortcut(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);

		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
  		layout.addActionSet(JavaUI.ID_ACTION_SET);
  		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
  		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		
		String editorArea = layout.getEditorArea();
		
		// Top left: Package explorer view and Bookmarks view placeholder
		String topLeftId = "topLeft";
		IFolderLayout topLeft = layout.createFolder(topLeftId, IPageLayout.LEFT, 0.20f, editorArea);
		topLeft.addView(JavaUI.ID_PACKAGES);
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		
		// Bottom left: Outline view 
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, topLeftId);
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		

		// Bottom right: Property Sheet view and Log view
		IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.66f, editorArea);
		bottomRight.addView(IPageLayout.ID_PROP_SHEET);
		bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottomRight.addView(LOG_VIEW_ID);
		bottomRight.addView(IProgressConstants.PROGRESS_VIEW_ID);
		
		layout.addPerspectiveShortcut("cubicTestPlugin.CubicTest");
		
		try {
			// Set "activate on errors" property to false on logview (only works if it's hidden)
			Preferences preferences = PDERuntimePlugin.getDefault().getPluginPreferences();
			preferences.setValue("activate", false);
			PDERuntimePlugin.getDefault().savePluginPreferences();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
