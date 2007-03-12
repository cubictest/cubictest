/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.exporters.htmlPrototype;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.cubictest.export.DirectoryWalker;
import org.cubictest.export.IResultHolder;
import org.cubictest.exporters.htmlPrototype.delegates.HtmlPageCreator;
import org.cubictest.exporters.htmlPrototype.delegates.TestConverter;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A directory walker specifically designed for HTML skeleton.
 * Does not use a step list.
 * 
 * @author ehalvorsen
 * 
 */
public class HtmlExportDirectoryWalker<T extends IResultHolder> extends DirectoryWalker<T>  {
	
	/**
	 * This exporter uses its own test converter, not the common TreeTestWalker
	 */
	private TestConverter testConverter;
	
	public HtmlExportDirectoryWalker(IResource resource, TestConverter testConverter, String outFileExtension) {
		super(resource, null, outFileExtension, null);
		this.testConverter = testConverter;
	}

	
	@Override
	protected void convertCubicTestFile(IFile file, IFolder outFolder, IProgressMonitor monitor) throws Exception{
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
