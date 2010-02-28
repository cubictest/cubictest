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
package org.cubictest.model.customstep.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.model.customstep.data.CustomTestStepDataEvent.EventType;

public final class CustomTestStepData {

	private String displayText = "";
	private String path = "";
	private Map<String, Object> exporterUserSettings;
	
	private transient List<ICustomTestStepDataListener> listeners = new ArrayList<ICustomTestStepDataListener>();

	public Object getExporterUserSetting(String key) {
		if (exporterUserSettings == null) {
			exporterUserSettings = new HashMap<String, Object>();
		}
		return exporterUserSettings.get(key);
	}
	
	public void setExporterUserSetting(String key, Object value) {
		if (exporterUserSettings == null) {
			exporterUserSettings = new HashMap<String, Object>();
		}
		exporterUserSettings.put(key, value);
	}
	
	public String getDisplayText() {
		return displayText;
	}

	public String getPath() {
		return path;
	}

	public void setDisplayText(String text) {
		String oldText = this.displayText;
		this.displayText = text;
		fireEvent(CustomTestStepDataEvent.EventType.DISPLAY_TEXT,oldText,text);
	}

	private void fireEvent(EventType type, String oldText, String text) {
		CustomTestStepDataEvent event = new CustomTestStepDataEvent(this,type,oldText,text);
		for(ICustomTestStepDataListener listener: listeners){
			listener.handleEvent(event);
		}
	}

	public void setPath(String path) {
		String oldPath = this.path;
		this.path = path;
		fireEvent(CustomTestStepDataEvent.EventType.PATH,oldPath,path);
	}

	public void addChangeListener(ICustomTestStepDataListener listener) {
		
		getListeners().add(listener);
	}

	private List<ICustomTestStepDataListener> getListeners() {
		if(listeners == null)
			listeners = new ArrayList<ICustomTestStepDataListener>();
		return listeners;
	}
}
