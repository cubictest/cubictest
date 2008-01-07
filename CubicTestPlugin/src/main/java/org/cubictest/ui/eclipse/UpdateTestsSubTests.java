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
package org.cubictest.ui.eclipse;

import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;


/**
 * Updates subtests / extension start points in test files with new path after a file move.
 * 
 * @author SK Skytteren
 */
public class UpdateTestsSubTests extends TraverseTestFilesWorkspaceJob {

	private final IPath movedFromPath;
	private IFile movedToPath;

	public UpdateTestsSubTests(IResource resource, IFile movedToPath) {
		super("Update Tests", resource);
		this.movedToPath = movedToPath;
		movedFromPath = resource.getProjectRelativePath();
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
	 * Update path to sub test.
	 * @param updated
	 * @param subtest
	 * @return
	 */
	private void updatePath(SubTest subtest) {
		if(subtest.getFilePath().equals(movedFromPath.toPortableString())){
			subtest.setFilePath(movedToPath.getProjectRelativePath().toPortableString());
		}
	}
}
