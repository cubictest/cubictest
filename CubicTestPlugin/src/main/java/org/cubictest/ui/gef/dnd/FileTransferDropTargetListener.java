package org.cubictest.ui.gef.dnd;

import org.cubictest.ui.gef.factory.FileFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;

public class FileTransferDropTargetListener extends
		AbstractTransferDropTargetListener {

	private FileFactory factory;

	public FileTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer, FileTransfer.getInstance());
		factory = new FileFactory();
	}
	
	@Override
	protected Request createTargetRequest() {
		CreateRequest request = new CreateRequest();
		request.setFactory(factory);
		return request;
	}

	@Override
	protected void updateTargetRequest() {
		((CreateRequest) getTargetRequest()).setLocation(getDropLocation());
	}

	@Override
	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOver();
	}
	
	@Override
	protected void handleDrop() {
		String filePath = ((String[]) getCurrentEvent().data)[0];
		if (filePath.endsWith(".aat") || filePath.endsWith(".custom")) { 
			IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(filePath));
			factory.setFile(iFile);
			super.handleDrop();
		}
	}
	
}
