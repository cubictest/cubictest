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
package org.cubictest.model.i18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.FileUtil;
import org.eclipse.core.resources.IFile;

public class Language{
	
	private transient Properties properties;
	private String name = "";
	private String fileName = "";

	
	public boolean isEmpty() {
		return getProperties().isEmpty();
	}
	
	public Language(){
		properties = new Properties();
	}
	
	public Language(IFile inFile){
		this();
		this.fileName = inFile.getFullPath().toOSString();
		updateLanguage();
	}

	public String get(String key) {
		if(key == null) {
			if (keySet().isEmpty()) {
				return "[No keys defined in property file]";
			} else {
				key = keySet().iterator().next();
			}
		}
		if(key == null)
			return "";
		
		String value = getProperties().getProperty(key);
		if (StringUtils.isBlank(value))
			return "[Missing language value]";
		else
			return value;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public Set<String> keySet() {
		List<String> list = new ArrayList<String>();
		for(Object o : getProperties().keySet()){
			list.add((String) o);
		}
		Collections.sort(list);
		return new LinkedHashSet<String>(list);
	}

	public String getFileName() {
		return fileName;
	}

	private Properties getProperties() {
		if (properties == null) {
			try {
				properties = new Properties();
				properties.load(FileUtils.openInputStream(FileUtil.getFileFromWorkspaceRoot(fileName)));
			} catch (IOException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
			}
		}
		return properties;
	}

	public boolean updateLanguage() {
		boolean success = false;
		try {
			properties = new Properties();
			properties.load(FileUtils.openInputStream(FileUtil.getFileFromWorkspaceRoot(fileName)));
			success = true;
		} catch (IOException e) {
			ErrorHandler.logAndShowErrorDialog(e);
		}
		return success;
	}
	
	@Override
	public String toString() {
		return name + " - " + fileName;
	}
	
	public boolean relaxedEqual(Language other) {
		if (other == null) {
			return false;
		}
		return name.equals(other.getName()) && fileName.equals(other.getFileName());
	}
}
