/*
 * Created on 28.may.2005
 *
 */
package org.cubictest;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Stein Kåre Skytteren
 *
 */
public class CubicTestPerspectiveFactory implements IPerspectiveFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewTestWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewSubTestWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewCubicTestProjectWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewParamWizard");
		layout.addNewWizardShortcut("org.cubictest.ui.wizards.NewPropertiesWizard");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard");
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); 
  		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); 
  		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewEnumCreationWizard");
		
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		
	
		//layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
  		layout.addActionSet(JavaUI.ID_ACTION_SET);
  		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
  		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		
		String editorArea = layout.getEditorArea();
		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.20f,
			editorArea);
		topLeft.addView("org.eclipse.jdt.ui.PackageExplorer");
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		
		// Bottom left: Outline view 
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f,
			"topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		
		// Bottom right: Property Sheet view
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.66f, editorArea);
		
		layout.addPerspectiveShortcut("cubicTestPlugin.CubicTest");
		
	}

}
