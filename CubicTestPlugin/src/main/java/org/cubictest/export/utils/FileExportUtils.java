/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.utils;

import java.io.File;

import org.cubictest.common.exception.CubicException;
import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Utils for export to files.
 * 
 * @author Christian Schwarz
 *
 */
public class FileExportUtils {

	/**
	 * Counts the number of files and subfolders.
	 * 
	 * @param file The file or folder to start counting from.
	 * @return The number of files and folders.
	 */
	public static int countFiles(File file) {
		if (file.isFile())
			return 1;

		int subs = 0;
		File[] sub = file.listFiles();
		for (int i = 0; i < sub.length; i++) {
			subs = subs + countFiles(sub[i]);
		}
		return 1;
	}
	
	
	
	public static IFolder prepareOutputFolder(IProgressMonitor monitor, IProject project, IResource resource) throws CoreException {
		IFolder destFolder = project.getFolder("generated");
		if (!destFolder.exists()) {
			destFolder.create(true, true, monitor);
		}
		IFolder updatedFolder = null;
		if (destFolder.getProjectRelativePath().segments().length < resource.getProjectRelativePath().segments().length) {
			updatedFolder = prepareDestionationDirectoryTree(monitor, project, destFolder, resource);
		}
		if (null != updatedFolder) {
			destFolder = updatedFolder;
		}
		return destFolder;
	}
	
	/**
	 * Private helper method that creates all required directories for the destionation.
	 * 
	 * @return the root directory to start generation from
	 */
	private static IFolder prepareDestionationDirectoryTree(IProgressMonitor monitor, IProject project, IFolder destFolder, IResource resource) {
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
						ErrorHandler.logAndShowErrorDialogAndRethrow(e, "Error creating directory");
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
	

}
