package org.cubictest.ui.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.resources.UiText;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class CubicTestResourceChangeListener implements
		IResourceChangeListener, IResourceDeltaVisitor {
	private List<IResource> changed = new ArrayList<IResource>();

	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Resources changed");
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(this);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		for(IResource resource : changed){
			try {
				resource.touch(new NullProgressMonitor());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private IResource oldResource;

	public boolean visit(IResourceDelta delta) {
		IResource res = delta.getResource();
		System.out.println("Visit kind: " + delta.getKind());
		if(oldResource != null && res.getName().equals(oldResource.getName())){
			if(delta.getKind() == IResourceDelta.ADDED){
				setMovedResource(res,oldResource,res.getProject());
			}else if(delta.getKind() == IResourceDelta.REMOVED){
				setMovedResource(oldResource,res,oldResource.getProject());
			}
		}
		if(res.getType() == IResource.FILE)
			oldResource = res;
		return true; // visit the children
	}

	private void setMovedResource(IResource newResource, IResource oldResource, IContainer container) {
		try {
			for (IResource entry : container.members()) {
				if (entry.getType() == IResource.FOLDER) {
					setMovedResource(newResource, oldResource, (IContainer) entry);
				} else if(entry.getType() == IResource.FILE){
					// convert file if it is a .aat test
					String fileName = entry.getName();

					if (fileName.endsWith(".aat") && !entry.getFullPath().equals(newResource.getFullPath())) {
						try {
							Test test = TestPersistance.loadFromFile((IFile) entry);
							for(SubTest subtest : test.getSubTests()){
								if(subtest.getFilePath().equals(oldResource.getFullPath().toPortableString())){
									subtest.setFilePath(newResource.getFullPath().toPortableString());
									System.out.println("Setting new fullPath: " + newResource.getFullPath() + " from: " +
											oldResource.getFullPath());
									TestPersistance.saveToFile(test, (IFile) entry);

									changed.add(entry);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openWarning(new Shell(),UiText.APP_TITLE,
								"Could not open test \"" + fileName + "\" for getting its extension points");
						}
					}
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
