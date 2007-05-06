/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.factory;

import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.eclipse.core.resources.IFile;
import org.eclipse.gef.requests.CreationFactory;

public class SubTestFactory implements CreationFactory {

	private IFile file;

	public SubTestFactory() {}
	
	public Object getNewObject() {
		SubTest subTest = new SubTest(file.getProjectRelativePath().toPortableString(), file.getProject());
		return subTest;
	}

	public Object getObjectType() {
		return Page.class;
	}

	public void setFile(IFile file) {
		this.file = file;
	}

}
