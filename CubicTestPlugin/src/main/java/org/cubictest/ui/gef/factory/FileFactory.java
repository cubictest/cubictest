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
