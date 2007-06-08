/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.dnd;

import org.cubictest.ui.gef.factory.FileFactory;
import org.cubictest.ui.gef.factory.SubTestFactory;
import org.eclipse.gef.EditPartViewer;

public class SubTestFileTransferDropTargetListener extends
		FileTransferDropTargetListener {

	private SubTestFactory factory;

	public SubTestFileTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer);
		factory = new SubTestFactory();
	}

	@Override
	protected FileFactory getFactory() {
		return factory;
	}
	@Override
	protected String getFileExt() {
		return ".aat";
	}
}
