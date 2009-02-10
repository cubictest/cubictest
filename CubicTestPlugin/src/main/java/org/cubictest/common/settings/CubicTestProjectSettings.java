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
package org.cubictest.common.settings;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.common.utils.ViewUtil;
import org.eclipse.core.resources.IProject;

/**
 * Provides access to properties for CubicTest projects.
 * Not very Eclipse'ish to have settings here, but has to be this way to support Maven.
 * 
 * @author Christian Schwarz
 * 
 */
public class CubicTestProjectSettings {

	/** The property file to load */
	static final String FILE_NAME = "test-project.properties";

	/** The test project's properties */
	Properties properties;
	
	File projectFolder;

	public CubicTestProjectSettings() {
	}
	

	/**
	 * Constructor that gets project from active Eclipse page.
	 */
	public static CubicTestProjectSettings getInstanceFromActivePage() {
		CubicTestProjectSettings settings = new CubicTestProjectSettings();
		IProject project = ViewUtil.getProjectFromActivePage();
		settings.projectFolder = project.getLocation().toFile();
		File propsFile = settings.getPropsFile(settings.projectFolder);
		settings.loadProperties(propsFile);
		return settings;
	}

	
	public CubicTestProjectSettings(IProject project) {
		projectFolder = project.getLocation().toFile();
		File propsFile = getPropsFile(projectFolder);
		loadProperties(propsFile);
	}

	public CubicTestProjectSettings(File projectFolder) {
		this.projectFolder = projectFolder;
		File propsFile = getPropsFile(projectFolder);
		loadProperties(propsFile);
	}



	/**
	 * Get boolean property from test-project.properties.
	 * 
	 * @param exporterId
	 * @param property
	 * @return
	 */
	public Boolean getBoolean(String prefix, String property, Boolean defaultValue) {
		Object prop = properties.get(prefix + "." + property);
		if (prop != null) {
			return ((String) prop).equalsIgnoreCase("true");
		}
		Logger.warn("Error getting boolean property " + prefix + "." + property + " from test-project.properties." +
				"Using default value: " + defaultValue);
		return defaultValue;
	}
	
	/**
	 * Get boolean property from test-project.properties.
	 * 
	 * @param exporterId
	 * @param property
	 * @return
	 */
	public Integer getInteger(String prefix, String property, Integer defaultValue) {
		Object prop = properties.get(prefix + "." + property);
		try {
			return Integer.parseInt((String) prop);
		}
		catch (Exception e) {
			Logger.warn("Error getting integer property " + prefix + "." + property + " from test-project.properties." +
					"Using default value: " + defaultValue);
			return defaultValue;
		}
	}

	
	/**
	 * Get string property from test-project.properties.
	 * 
	 * @param exporterId
	 * @param property
	 * @return
	 */
	public String getString(String prefix, String property, String defaultValue) {
		Object prop = properties.get(prefix + "." + property);
		if (prop != null) {
			return (String) prop;
		}
		Logger.warn("Error getting string property " + prefix + "." + property + " from test-project.properties. " +
				"Using default value: " + defaultValue);
		return defaultValue;
	}
	

	/**
	 * Looks up a resource named test-project.properties in the classpath.
	 * Caches the result.
	 */
	private void loadProperties(File propsFile) {
		InputStream in = null;
		
		try {
			in = FileUtils.openInputStream(propsFile);
			properties = new Properties();
			properties.load(in);
		} 
		catch (Exception e) {
			ErrorHandler.logAndRethrow("Error loading properties", e);
		} 
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e2) {
					Logger.warn("Error closing input stream from property file.");
				}
			}
		}
	}

	private File getPropsFile(File folder) {
		File[] files = folder.listFiles(getFilenameFilter());
		if (files == null || files.length == 0) {
			throw new CubicException("Did not find " + FILE_NAME);
		}
		return files[0];
	}

	private FilenameFilter getFilenameFilter() {
		return new FilenameFilter() {
			public boolean accept(File dir, String fileName) {
				if (fileName.equals(FILE_NAME)) {
					return true;
				}
				return false;
			}
		};
	}

	public File getProjectFolder() {
		return projectFolder;
	}
}
