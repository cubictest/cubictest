/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.settings;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.Logger;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

/**
 * Properties for CubicTest projects.
 * 
 * @author Christian Schwarz
 * 
 */
public class CubicTestProjectSettings {

	/** The property file to load */
	private static final String FILE_NAME = "test-project.properties";

	/** Cached properties */
	private static Properties properties;

	/** Timestamp of the property file that is cached */
	private static long cachedLastModified = 0;

	/** Lock to hold while reading properties */
	private static Object lock = new Object();

	/**
	 * Get boolean property from test-project.properties.
	 * 
	 * @param exporterId
	 * @param property
	 * @return
	 */
	public static Boolean getBooleanProperty(String exporterId, String property) {
		if (properties == null) {
			loadProperties();
		}
		Object prop = properties.get(exporterId + "." + property);
		if (prop != null) {
			return ((String) prop).equalsIgnoreCase("true");
		}
		return null;
	}

	/**
	 * Looks up a resource named test-project.properties in the classpath.
	 * Caches the result.
	 */
	private static void loadProperties() {
		InputStream in = null;
		long lastModified = 0;

		try {
			IProject project = ViewUtil.getProjectFromActivePage();
			File propsFile = project.getFile(FILE_NAME).getLocation().toFile();
			lastModified = propsFile.lastModified();

			if (properties != null && lastModified == cachedLastModified) {
				// use cached properties
				return;
			}

			in = FileUtils.openInputStream(propsFile);
			if (in != null) {
				//load the properties
				synchronized (lock) {
					properties = new Properties();
					properties.load(in);
					cachedLastModified = lastModified;
				}
			}
		} catch (Exception e) {
			Logger.error("Error loading properties: " + e.toString());
			properties = null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e2) {
					Logger.warn("Error closing input stream from property file.");
				}
		}
	}


}
