/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.export.htmlSkeleton;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.cubictest.export.common.BaseDirectoryWalker;
import org.cubictest.export.htmlSkeleton.delegates.TestConverter;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * A directory walker specifically designed for HTML skeleton.
 * Does not use a step list.
 * 
 * @author ehalvorsen
 * 
 */
public class DirectoryWalker extends BaseDirectoryWalker implements IRunnableWithProgress {
	
	private TestConverter testConverter;


	/**
	 * Default constructor, gets the TestConverter to use.
	 */
	public DirectoryWalker(IResource resource, String outFileExtension, TestConverter testConverter) {
		super(resource, outFileExtension);
		this.testConverter = testConverter;
	}


	/*
	 * (non-Javadoc)
	 * @see org.cubictest.export.common.BaseDirectoryWalker#convertAatFile(org.eclipse.core.resources.IFile, org.eclipse.core.resources.IFolder, java.lang.String)
	 */
	protected void convertAatFile(IFile file, IFolder outFolder, String outFileExtension) throws IOException, CoreException {
		Test test = TestPersistance.loadFromFile(file);
		IFolder destinationFolder = outFolder;
		if(outFolder.getName().indexOf(".aat") == -1) {
			destinationFolder = outFolder.getFolder(file.getName());
		}
		
		if(!destinationFolder.exists()) {
			destinationFolder.create(false, true, null);				
		}
		testConverter.convert(test, destinationFolder.getRawLocation().toFile(), null);
		
		try {
			//copy stylesheet:
			IFile destFile = destinationFolder.getFile("default.css");
			if (!destFile.exists()){
				URL cssUrl = this.getClass().getResource("default.css");
				File cssFile = FileUtils.toFile(FileLocator.toFileURL(cssUrl));
				File destination = destFile.getRawLocation().toFile();
				FileUtils.copyFile(cssFile, destination);
			}

			destFile = destinationFolder.getFile("cubic.js");
			if (!destFile.exists()){
				URL jsUrl = this.getClass().getResource("cubic.js");
				File jsFile = FileUtils.toFile(FileLocator.toFileURL(jsUrl));
				File destination = destFile.getRawLocation().toFile();
				FileUtils.copyFile(jsFile, destination);
			}
		}
		catch (Exception e) {
			System.out.println("Error copying stylesheet! Check that resources dir is in source path.");
		}
			
	}
}
