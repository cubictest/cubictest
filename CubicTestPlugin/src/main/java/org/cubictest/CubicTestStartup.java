package org.cubictest;

import org.cubictest.ui.eclipse.CubicTestResourceChangeListener;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.ui.IStartup;

public class CubicTestStartup implements IStartup {

	private CubicTestResourceChangeListener listener;
	private static final String SKIP_INTRO = "CUbicTestFirstTime";

	public CubicTestStartup() {
		listener = new CubicTestResourceChangeListener();
	}

	public void earlyStartup() {
		System.out.println("EarlyStartup by CubicTest");
		Preferences prefs = CubicTestPlugin.getDefault().getPluginPreferences();
		if(!prefs.getBoolean(SKIP_INTRO)){
			//IWorkbenchPreferenceConstants.
			prefs.setValue(SKIP_INTRO, true);
		}
		CubicTestPlugin.getDefault().savePluginPreferences();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
			 listener, IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE);
	}

}
