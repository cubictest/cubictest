/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.export.utils.exported;

import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.Moderator;
import org.cubictest.model.Test;
import org.eclipse.core.resources.IProject;

/**
 * Util class for test runners.
 * 
 * @author Christian Schwarz
 *
 */
public class RunnerUtils {
	
	public static boolean pass(String fromTest, String fromActualPage, Moderator moderator) {
		if (moderator.equals(Moderator.BEGIN))
			return fromActualPage.startsWith(fromTest);
		if (moderator.equals(Moderator.CONTAIN))
			return fromActualPage.indexOf(fromTest) >= 0;
		if (moderator.equals(Moderator.END))
			return fromActualPage.endsWith(fromTest);
		if (moderator.equals(Moderator.EQUAL))
			return fromActualPage.equals(fromTest);
		throw new IllegalArgumentException("Unknown Moderator");
	}
	
	public static IProject getProjectFromActiveEditorPage() {
		return ViewUtil.getProjectFromActivePage();
	}
	
	public static Test getTestFromActiveEditorPage() {
		return ViewUtil.getTestFromActivePage();
	}
}
