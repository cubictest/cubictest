package org.cubictest.ui.gef.dnd;

import org.cubictest.ui.gef.factory.CustomTestStepFactory;
import org.cubictest.ui.gef.factory.FileFactory;
import org.cubictest.ui.gef.factory.SubTestFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;

public class CustomStepFileTransferDropTargetListener extends
	FileTransferDropTargetListener {

	public CustomStepFileTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer);
		factory = new CustomTestStepFactory();
	}

	private CustomTestStepFactory factory;

	@Override
	protected FileFactory getFactory() {
		return factory;
	}

	@Override
	protected String getFileExt() {
		return ".custom";
	}


}
