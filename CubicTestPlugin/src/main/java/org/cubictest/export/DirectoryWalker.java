/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export;

import java.io.File;
import java.io.FileWriter;

import org.cubictest.common.exception.ResourceNotCubicTestFileException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.holders.IResultHolder;
import org.cubictest.export.utils.FileExportUtils;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.ui.utils.ModelUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Directory walker for CubicTest export.
 * 
 * @author ovstetun
 * @author chr_schwarz
 */
public class DirectoryWalker<T extends IResultHolder> implements IRunnableWithProgress  {
	private IResource resource;
	private String outFileExtension;
	private TreeTestWalker<T> testWalker;
	private final Class<? extends T> resultHolderClass;

	
	
	public DirectoryWalker(IResource resource, TreeTestWalker<T> treeTestWalker, String outFileExtension, Class<? extends T> resultHolder) {
		this.resource = resource;
		this.testWalker = treeTestWalker;
		this.resultHolderClass = resultHolder;
		this.outFileExtension = outFileExtension;
	}

	
	
	/**
	 * Starts the export of the selected resource. May be directory tree.
	 * 
	 * @param monitor The monitor to report progress to.
	 */
	public void run(IProgressMonitor monitor) {
		try {
			int totalUnits = FileExportUtils.countFiles(resource.getRawLocation().toFile());
			if (monitor != null) {
				monitor.beginTask("Traversing the test model...", totalUnits);
			}

			IProject project = resource.getProject();
			IFolder destFolder = FileExportUtils.prepareOutputFolder(monitor, project, resource);
			
			if(resource.getRawLocation().toFile().isFile()) {
				if (ModelUtil.isTestFile(resource.getName())) {
					convertCubicTestFile((IFile)resource, destFolder, monitor, true);
				}
				else {
					throw new ResourceNotCubicTestFileException();
				}
			} 
			else {
				traverseFolder(project.getFolder(resource.getProjectRelativePath()), destFolder, monitor);
			}
			
			destFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
			
			if (monitor != null) {
				monitor.done();
			}
		}
		catch (Exception e) {
			ErrorHandler.rethrow(e);
		}
	}




	/**
	 * Traverses a directory. 
	 * @throws Exception 
	 */
	public void traverseFolder(IFolder selectedFolder, IFolder destFolder, IProgressMonitor monitor) throws Exception {
		for (IResource entry : selectedFolder.members()) {
			if (entry.getType() == IResource.FOLDER) {
				IFolder newOutFolder = destFolder.getFolder(entry.getName());
				if (!newOutFolder.exists()) {
					newOutFolder.create(false, true, monitor);
				}
				if (monitor != null) {
					monitor.worked(1);
				}
				//recursive call:
				traverseFolder((IFolder)entry, newOutFolder, monitor);
			} 
			else {
				// convert file if it is a CubicTest test
				String fileName = entry.getName();

				if (ModelUtil.isTestFile(fileName)) {
					convertCubicTestFile((IFile)entry, destFolder, monitor, false);
				}
				if (monitor != null) {
					monitor.worked(1);
				}
			}
		}
	}
	


	/**
	 * Exports a CubicTest file.
	 */
	protected void convertCubicTestFile(IFile testFile, IFolder outFolder, IProgressMonitor monitor, boolean isSelected) throws Exception {
		//load Test and start the conversion:
		Test test = TestPersistance.loadFromFile(testFile);
		
		if (!ExportUtils.testIsOkForExport(test)) {
			if (isSelected) {
				ExportUtils.throwTestNotOkForExportException(test);
				return;
			}
			else {
				//just skip file
				return;
			}
		}
	
		T resultHolder = resultHolderClass.newInstance();
		testWalker.convertTest(test, resultHolder);

		//write result to file:
		String testFileName = testFile.getName();
		int length = testFileName.length() - 4;
		String outFileName = testFileName.substring(0, length) + "." + outFileExtension;
		if (!outFolder.exists()) {
			outFolder.create(false, true, monitor);
		}
		File outFile = outFolder.getFile(outFileName).getRawLocation().toFile();
    	FileWriter out = new FileWriter(outFile);
        out.write(resultHolder.toResultString());
        out.close();
	}

	
}
