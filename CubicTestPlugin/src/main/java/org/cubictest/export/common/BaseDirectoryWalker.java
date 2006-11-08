/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.common;

import java.io.File;
import java.io.IOException;

import org.cubictest.common.exception.CubicException;
import org.cubictest.common.exception.ResourceNotCubicFileException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Base directory walker.
 * 
 * @author ovstetun
 * @author chr_schwarz
 */
public abstract class BaseDirectoryWalker implements IRunnableWithProgress  {
	private IResource resource;
	private String outFileExtension;


	public BaseDirectoryWalker(IResource resource, String outFileExtension) {
		this.resource = resource;
		this.outFileExtension = outFileExtension;		
	}
	
	/**
	 * Starts the conversion of the selected resource.
	 * 
	 * @param monitor The monitor to report progress to.
	 * @throws IOException 
	 * @throws CoreException 
	 */
	public void run(IProgressMonitor monitor) {
		try {
			IProject project = resource.getProject();
			
			IFolder destFolder = project.getFolder("generated");
			if (!destFolder.exists()) {
				destFolder.create(true, true, monitor);
			}
			int totalUnits = countFiles(resource.getRawLocation().toFile());
			if (monitor != null) {
				monitor.beginTask("Traversing the test model...", totalUnits);
			}
			IFolder updatedFolder = null;
			if (destFolder.getProjectRelativePath().segments().length < resource.getProjectRelativePath().segments().length) {
				updatedFolder = prepareDestionationDirectoryTree(monitor, project, destFolder);
			}
			if (null != updatedFolder) {
				destFolder = updatedFolder;
			}
			if(resource.getRawLocation().toFile().isFile()) {
				convertFile((IFile)resource, destFolder, monitor);				
			} else {
				traverseFolder(project.getFolder(resource.getProjectRelativePath()), destFolder, monitor);
			}
			destFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
			
			if (monitor != null) {
				monitor.done();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new CubicException("Error with file processing: " + e.toString(), e);
		} catch (CoreException e) {
			e.printStackTrace();
			throw new CubicException("Core error: " + e.toString(), e);
		}
	}
	
	/**
	 * Creates all required directories for the destionation.
	 * @param monitor current monito
	 * @param project current project
	 * @param destFolder destination folder
	 * @return the root directory to start generation from
	 */
	private IFolder prepareDestionationDirectoryTree(IProgressMonitor monitor, IProject project, IFolder destFolder) {
		IFolder updatedFolder = null;
		int i = destFolder.getProjectRelativePath().segments().length;
		StringBuffer addedSegments = new StringBuffer();
		while (i < resource.getProjectRelativePath().segmentCount()) {
			addedSegments.append("/" + resource.getProjectRelativePath().segment(i));
			IFolder ff = project.getFolder(destFolder.getProjectRelativePath() + addedSegments.toString());
			if (!ff.exists()) {
				if (-1 == ff.getName().indexOf("aat")) {
					try {
						ff.create(true, true, monitor);
					} 
					catch (CoreException e) {
						e.printStackTrace();
						throw new CubicException("Error creating directory: " + e.toString());
					}
					updatedFolder = ff;
				}
			} else {
				updatedFolder = ff;
			}
			i++;
		}
		return updatedFolder;
	}
	

	/**
	 * Traverses a directory (and not file -- see own method for traversing file). 
	 * 
	 * @param selectedFolder
	 * @param destFolder
	 * @param monitor
	 * @throws CoreException
	 * @throws IOException
	 */
	public void traverseFolder(IFolder selectedFolder, IFolder destFolder, IProgressMonitor monitor) throws CoreException, IOException {
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
			} else {
				// convert file if it is a .aat test
				String fileName = entry.getName();

				if (fileName.endsWith(".aat")) {
					convertAatFile((IFile)entry, destFolder, outFileExtension);
				}
				if (monitor != null) {
					monitor.worked(1);
				}
			}
		}
	}
	
	
	/**
	 * Starts the conversion of a file.
	 * @throws CoreException 
	 * @throws IOException 
	 */
	public void convertFile(IFile selectedFile, IFolder destFolder, IProgressMonitor monitor) throws CoreException, IOException {
		if (!destFolder.exists()) {
			destFolder.create(false, true, monitor);
		}
		
		if (selectedFile.getName().endsWith(("aat"))) {
			convertAatFile(selectedFile, destFolder, outFileExtension);
		}
		else {
			//Do not process file.
			throw new ResourceNotCubicFileException();
		}
	}

	/**
	 * Counts the number of files and subfolders.
	 * 
	 * @param file The file or folder to start counting from.
	 * @return The number of files and folders.
	 */
	private int countFiles(File file) {
		if (file.isFile())
			return 1;

		int subs = 0;
		File[] sub = file.listFiles();
		for (int i = 0; i < sub.length; i++) {
			subs = subs + countFiles(sub[i]);
		}
		return 1;
	}

	/**
	 * Initializes the export of an .aat file.
	 * 
	 * @param file
	 * @param outFolder
	 * @param outFileExtension
	 * @throws IOException
	 * @throws CoreException
	 */
	protected abstract void convertAatFile(IFile file, IFolder outFolder, String outFileExtension) throws IOException, CoreException;

}
