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
package org.cubictest.common.utils;

import java.io.File;

import org.cubictest.export.exceptions.ExporterException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

public class FileUtil {

	public static File getFileFromWorkspaceRoot(String fileName){
		try {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName)).getLocation().toFile();
		}
		catch (Exception e) {
			File result = new File("../" + fileName); //language uses Project name in path. Remove it.
	    	
	    	if (!result.exists()) {
	    		throw new ExporterException("File not found: " + result.getAbsolutePath());
	    	}
	    	return result;
		}
	}
}
