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
package org.cubictest.model.i18n;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

public class Language{
	private Properties properties;
	private String name = "";
	private transient IFile file;
	private String fileName = "";
	
	
	private IFile getFile(){
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
	}
	
	public Language(){
		properties = new Properties();
	}
	
	public Language(IFile inFile){
		this();
		if(inFile == null)
			this.file = getFile();
		else {
			this.file = inFile;
			this.fileName = file.getFullPath().toOSString();
		}
		
		updateLanguage();
	}

	public String get(String key) {
		if(key == null)
			key = keySet().iterator().next();
		if(key == null)
			return "";
		return getProperties().getProperty(key);
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for(Object o : getProperties().keySet()){
			result.add((String) o);
		}
		return result;
	}

	public String getFileName() {
		return fileName;
	}

	private Properties getProperties() {
		if (properties == null) {
			try {
				properties = new Properties();
				properties.load(getFile().getContents());
			} catch (IOException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
			} catch (CoreException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
			}
		}
		return properties;
	}

	public void updateLanguage() {
		try {
			properties.load(getFile().getContents());
		} catch (IOException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		} catch (CoreException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
}
