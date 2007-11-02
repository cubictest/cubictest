/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.exporters.htmlPrototype;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.DirectoryWalker;
import org.cubictest.export.holders.IResultHolder;
import org.cubictest.export.utils.exported.ExportUtils;
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
	protected void convertCubicTestFile(IFile file, IFolder outFolder, IProgressMonitor monitor, boolean isSelected) throws Exception{
		Test test = TestPersistance.loadFromFile(file);
		
		if (!ExportUtils.testIsOkForExport(test)) {
			if (isSelected) {
				//selected file must be OK for export. Show error
				ExportUtils.throwTestNotOkForExportException(test);
				return;
			}
			else {
				//File is part of multiple files export. Just skip file
				return;
			}
		}

		IFolder destinationFolder = outFolder;
		if(!ExportUtils.isTestFile(outFolder.getName())) {
			destinationFolder = outFolder.getFolder(file.getName());
		}
		
		if(!destinationFolder.exists()) {
			destinationFolder.create(false, true, null);				
		}
		
		
		testConverter.convert(test, destinationFolder.getRawLocation().toFile(), null);
		
		try {
			//copy stylesheet and javascript:
			copy(destinationFolder, "default.css");
			copy(destinationFolder, "cubic.js");
		}
		catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error copying stylesheet or javascript file! The prototype will have limited functionality.", e);
		}
			
	}


	private void copy(IFolder destinationFolder, String fileName) throws IOException {
		IFile destFile = destinationFolder.getFile(fileName);
		if (!destFile.exists()){
			File destination = destFile.getRawLocation().toFile();
			InputStream in = this.getClass().getResourceAsStream(fileName);
			OutputStream out = FileUtils.openOutputStream(destination);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(out);
		}
	}
}
