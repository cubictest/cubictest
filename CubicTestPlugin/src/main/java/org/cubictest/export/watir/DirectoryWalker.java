/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.export.watir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.cubictest.common.converters.TreeTestWalker;
import org.cubictest.common.utils.TextUtil;
import org.cubictest.export.common.BaseDirectoryWalker;
import org.cubictest.export.watir.interfaces.IStepList;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.operation.IRunnableWithProgress;


/**
 * A directory walker-subclass for watir export.
 * 
 * @author chr_schwarz
 * 
 */
public class DirectoryWalker extends BaseDirectoryWalker implements IRunnableWithProgress {

	private TreeTestWalker<IStepList> testWalker;

	
	/**
	 * Default constructor.
	 * @param project The project containing the tests.
	 */
	public DirectoryWalker(IResource resource,
			TreeTestWalker<IStepList> testConverter,
			String outFileExtension) {
		
		super(resource, outFileExtension);
		
		this.testWalker = testConverter;
	}



	/*
	 * (non-Javadoc)
	 * @see org.cubictest.export.common.BaseDirectoryWalker#convertAatFile(org.eclipse.core.resources.IFile, org.eclipse.core.resources.IFolder, java.lang.String)
	 */
	protected void convertAatFile(IFile file, IFolder outFolder, String outFileExtension) throws IOException {
		String fileName = file.getName();
		int length = fileName.length() - 4;
		String testName = fileName.substring(0, length);
		String newFileName = testName + "." + outFileExtension;
		IFile newOutFile = outFolder.getFile(newFileName);

		Test test = TestPersistance.loadFromFile(file);
		
		String name = TextUtil.camel(test.getName());
		IStepList stepList = new StepList(name);
		
		testWalker.convertTest(test, stepList, null);

		//save the step list:
		File watirFile = newOutFile.getRawLocation().toFile();
    	FileWriter out = new FileWriter(watirFile);
        out.write(stepList.toString());
        out.close();
	}

}
