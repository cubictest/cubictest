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
package org.cubictest.ui.utils;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * Gets a reference to the resource navigator.
 * 
 * @author Christian Schwarz
 */
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
