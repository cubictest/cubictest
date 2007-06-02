package org.cubictest.model.customstep.data;

import java.util.ArrayList;
import java.util.List;

public final class CustomTestStepData {

	private String displayText = "";
	private String path = "";
	private List<ICustomTestStepDataListener> listeners = new ArrayList<ICustomTestStepDataListener>();

	public String getDisplayText() {
		return displayText;
	}

	public String getPath() {
		return path;
	}

	public void setDisplayText(String text) {
		this.displayText = text;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void addChangeListener(ICustomTestStepDataListener listener) {
		listeners.add(listener);
	}
}
