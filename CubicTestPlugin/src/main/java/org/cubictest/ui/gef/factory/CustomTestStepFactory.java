package org.cubictest.ui.gef.factory;

import org.cubictest.model.customstep.CustomStepNode;

public class CustomTestStepFactory extends FileFactory {

	public Object getNewObject() {
		CustomStepNode customStepNode = new CustomStepNode(file.getProjectRelativePath().toPortableString(), file.getProject());
		return customStepNode;
	}

}
