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
