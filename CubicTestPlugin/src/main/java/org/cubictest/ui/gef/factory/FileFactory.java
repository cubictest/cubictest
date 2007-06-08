package org.cubictest.ui.gef.factory;

import org.cubictest.model.Page;
import org.eclipse.core.resources.IFile;
import org.eclipse.gef.requests.CreationFactory;

public abstract class FileFactory implements CreationFactory {

	protected IFile file;

	public Object getObjectType() {
		return Page.class;
	}

	public void setFile(IFile file) {
		this.file = file;
	}
	
}
