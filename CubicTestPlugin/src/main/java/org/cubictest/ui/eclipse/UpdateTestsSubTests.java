package org.cubictest.ui.eclipse;

import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class UpdateTestsSubTests extends TraverseTestFilesWorkspaceJob{

	private final IPath movedFromPath;
	private IPath movedToPath;

	public UpdateTestsSubTests(IResource resource, IPath movedToPath) {
		super("Update Tests", resource);
		this.movedToPath = movedToPath;
		movedFromPath = resource.getFullPath();
		System.out.println("moved from: " + resource.getFullPath() + " to: " +
				movedToPath);
	}				

	@Override
	public boolean updateTest(Test test) throws CoreException {
		boolean updated = false;
		for(SubTest subtest : test.getSubTests()){
			if(subtest.getFilePath().equals(movedFromPath.toPortableString())){
				subtest.setFilePath(movedToPath.toPortableString());
				updated = true;
			}
		}
		return updated;
	}
}
