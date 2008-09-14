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
package org.cubictest.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.IBuildPathSupporter;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.ui.customstep.CustomStepEditor;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.views.navigator.ResourceNavigator;


public class NewCubicTestProjectWizard extends Wizard implements INewWizard {

	public static final String TESTS_FOLDER_NAME = "tests";
	public static final String TEST_SUITES_FOLDER_NAME = "test suites";
	private WizardNewProjectCreationPage namePage;
	private NewProjectSummaryPage summaryPage;
	
	private IWorkbench workbench;
	
	public NewCubicTestProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	@Override
	public void addPages() {
		super.addPages();
		namePage = new WizardNewProjectCreationPage("newCubicTestProjectNamepage");
		namePage.setTitle("New CubicTest project");
		namePage.setDescription("Choose name of project");
		
		summaryPage = new NewProjectSummaryPage();
		summaryPage.setTitle("New CubicTest project");
		summaryPage.setDescription("Ready to create project");
		
		addPage(namePage);
		addPage(summaryPage);
		
	}
	
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new WorkspaceModifyOperation(null) {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
					IProject project = createProject(monitor, summaryPage.getCreateTestOnFinish());
					createTestSuite(project);
				}
			});
			
		} catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		return true;
	}
	
	private IProject createProject(IProgressMonitor monitor, boolean launchNewTestWizard) {
		
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(namePage.getProjectName());
			
			IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
			
			if(!Platform.getLocation().equals(namePage.getLocationPath()))
	            description.setLocation(namePage.getLocationPath());
			
			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
			ICommand buildCommand = description.newCommand();
			buildCommand.setBuilderName(JavaCore.BUILDER_ID);
			description.setBuildSpec(new ICommand[] { buildCommand });
			project.create(description, monitor);
			project.open(monitor);

			IJavaProject javaProject = JavaCore.create(project);
			
			IFolder testFolder = project.getFolder(TESTS_FOLDER_NAME);
			testFolder.create(false, true, monitor);

			IFolder suiteFolder = project.getFolder(TEST_SUITES_FOLDER_NAME);
			suiteFolder.create(false, true, monitor);

			project.getFolder("src").create(false, true, monitor);
			project.getFolder("src/main").create(false, true, monitor);
			project.getFolder("src/test").create(false, true, monitor);

			IFolder javaSrcFolder = project.getFolder("src/main/java");
			javaSrcFolder.create(false, true, monitor);

			IFolder javaTestFolder = project.getFolder("src/test/java");
			javaTestFolder.create(false, true, monitor);

			IFolder customTestSuites = project.getFolder("src/test/java/customTestSuites");
			customTestSuites.create(false, true, monitor);

			IFolder binFolder = project.getFolder("bin");
			binFolder.create(false, true, monitor);
			
			IFolder libFolder = project.getFolder("lib");
			libFolder.create(false, true, monitor);
			
			WizardUtils.copyPom(project.getLocation().toFile());
			WizardUtils.copySampleCustomTestSuite(customTestSuites.getRawLocation().toFile());
			WizardUtils.copySettings(project.getLocation().toFile());
		
			//construct build path for the new project
			List<IClasspathEntry> classpathlist = new ArrayList<IClasspathEntry>();
			classpathlist.add(JavaCore.newSourceEntry(javaSrcFolder.getFullPath()));
			classpathlist.add(JavaCore.newSourceEntry(javaTestFolder.getFullPath()));
			classpathlist.add(JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER")));

			javaProject.setOutputLocation(binFolder.getFullPath(), monitor);
			
			javaProject.setRawClasspath(classpathlist.toArray(
					new IClasspathEntry[classpathlist.size()]), binFolder.getFullPath(), monitor);
			
			ResourceNavigator navigator = null;
			IViewPart viewPart = workbench.getActiveWorkbenchWindow().getActivePage().getViewReferences()[0].getView(false);
			
			if (viewPart instanceof ResourceNavigator) {
				navigator = (ResourceNavigator) viewPart;
			}

			if (launchNewTestWizard) {
				launchNewTestWizard(testFolder);
				if (navigator != null && testFolder.members().length > 0) {
					navigator.selectReveal(new StructuredSelection(testFolder.members()[0]));
				}
			}
			
			handlePluginBuildPathSupporters(javaProject, libFolder, classpathlist);
			
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			
			return project;
			
		} catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
			return null;
		}
	}
	private void handlePluginBuildPathSupporters(IJavaProject javaProject,
			IFolder libFolder, List<IClasspathEntry> classpathlist) {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(CustomStepEditor.CUBIC_TEST_CUSTOM_STEP_EXTENSION);
		IExtension[] extensions = extensionPoint.getExtensions();
		
		// For each extension ...
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			// For each member of the extension ...
			for (IConfigurationElement element : elements) {
				try {
					IBuildPathSupporter supporter = (IBuildPathSupporter)element.createExecutableExtension("buildpathSupporter");
					supporter.addClassPathContainers(javaProject, getShell());
					List<File> files = supporter.getFiles();
					for (File file : files) {
						FileUtils.copyFile(file, libFolder.getFile(file.getName()).getLocation().toFile());
						classpathlist.add(JavaCore.newLibraryEntry(libFolder.getFile(file.getName()).getFullPath(), null, null));
					}
				} catch (CoreException e) {
					Logger.info("Didn't find buildpath supporter for: " + element.getName() );
				} catch (Exception e) {
					Logger.error("Error when getting file from buildpath suppoerter", e);
				}
			}
		}
	}

	private void createTestSuite(IProject project) throws CoreException {
		Test emptySuite = WizardUtils.createEmptyTestWithTestSuiteStartPoint("Default test suite", "");
		IFile suiteFile = project.getFolder(NewCubicTestProjectWizard.TEST_SUITES_FOLDER_NAME).getFile("test suite.aat");
		TestPersistance.saveToFile(emptySuite, suiteFile);
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	public void launchNewTestWizard(IFolder testFolder) {
		// Create the wizard
		NewTestWizard wiz = new NewTestWizard();
		wiz.init(workbench, new StructuredSelection(testFolder));
		
		//Create the wizard dialog
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wiz);
		dialog.open();
	}

    @Override
	public boolean canFinish() {
    	if (getContainer().getCurrentPage() instanceof NewProjectSummaryPage) {
    		return true;
    	}
    	return false;
    }
}
