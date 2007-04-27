/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.eclipse;

import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;


/**
 * Updates subtests / extension start points in .aat files with new path after a file move.
 * 
 * @author SK Skytteren
 */
public class UpdateTestsSubTests extends TraverseTestFilesWorkspaceJob{

	private final IPath movedFromPath;
	private IPath movedToPath;

	public UpdateTestsSubTests(IResource resource, IPath movedToPath) {
		super("Update Tests", resource);
		this.movedToPath = movedToPath;
		movedFromPath = resource.getFullPath();
	}				

	@Override
	public boolean updateTest(Test test) throws CoreException {
		boolean updated = false;
		for(SubTest subtest : test.getSubTests()){
			updatePath(subtest);
			updated = true;
		}
		if (test.getStartPoint() instanceof ExtensionStartPoint) {
			SubTest subtest = (SubTest) test.getStartPoint();
			updatePath(subtest);
			updated = true;
		}
		return updated;
	}

	
	/**
	 * Private utility method.
	 * @param updated
	 * @param subtest
	 * @return
	 */
	private void updatePath(SubTest subtest) {
		if(subtest.getFilePath().equals(movedFromPath.toPortableString())){
			subtest.setFilePath(movedToPath.toPortableString());
		}
	}
}
