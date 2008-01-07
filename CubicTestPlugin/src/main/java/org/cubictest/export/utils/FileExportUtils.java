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
package org.cubictest.export.utils;

import java.io.File;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.ModelUtil;
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
			IFolder folder = project.getFolder(destFolder.getProjectRelativePath() + addedSegments.toString());
			if (!folder.exists()) {
				if (!ModelUtil.isTestFile(folder.getName())) {
					try {
						folder.create(true, true, monitor);
					} 
					catch (CoreException e) {
						ErrorHandler.logAndShowErrorDialogAndRethrow("Error creating directory", e);
					}
					updatedFolder = folder;
				}
			} else {
				updatedFolder = folder;
			}
			i++;
		}
		return updatedFolder;
	}
	

}
