package org.cubictest.ui.gef.factory;

import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.eclipse.core.resources.IFile;
import org.eclipse.gef.requests.CreationFactory;

public final class FileFactory implements CreationFactory {

	protected IFile file;

	public Object getObjectType() {
		return Page.class;
	}

	public void setFile(IFile file) {
		this.file = file;
	}

	public Object getNewObject() {
		if("custom".equals(file.getFileExtension()))
			return new CustomTestStepHolder(file.getProjectRelativePath().
					toPortableString(), file.getProject());
		return new SubTest(file.getProjectRelativePath().
				toPortableString(), file.getProject());
	}
	
}
