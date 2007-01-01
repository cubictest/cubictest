package org.cubictest.ui.utils;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.views.navigator.ResourceNavigator;

public class ResourceNavigatorGetter {

	/**
	 * Returns the resource navigator of the active perspective. If 
	 * there isn't any resource navigator part <code>null</code> is returned.
	 */
	public static ResourceNavigator getFromActivePerspective() {
		IWorkbenchPage activePage= JavaPlugin.getActivePage();
		if (activePage == null)
			return null;
		IViewPart view= activePage.findView("org.eclipse.ui.views.ResourceNavigator");
		if (view instanceof ResourceNavigator)
			return (ResourceNavigator)view;
		return null;	
	}
}
