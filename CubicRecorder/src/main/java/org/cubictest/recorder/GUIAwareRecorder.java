/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - bug fixes and usability improvements
 *******************************************************************************/
package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.Transition;
import org.cubictest.model.UserInteraction;
import org.eclipse.swt.widgets.Display;

public class GUIAwareRecorder implements IRecorder {

	private final IRecorder recorder;
	private final Display display;

	
	public GUIAwareRecorder(IRecorder recorder, Display display) {
		this.recorder = recorder;
		this.display = display;
	}
	
	public void addPageElement(final PageElement element, final PageElement parent) {
		display.syncExec(new Runnable() {
			public void run() {
				recorder.addPageElement(element, parent);
			}
		});
	}

	public void addUserInput(final UserInteraction action, final PageElement parent) {
		display.syncExec(new Runnable() {
			public void run() {
				recorder.addUserInput(action, parent);
			}
		});
	}

	public void setCursor(final AbstractPage page) {
		display.syncExec(new Runnable() {
			public void run() {
				recorder.setCursor(page);
			}
		});
	}

	public void setStateTitle(final String title) {
		display.syncExec(new Runnable() {
			public void run() {
				recorder.setStateTitle(title);
			}
		});

	}

	public void setEnabled(boolean enabled) {
		recorder.setEnabled(enabled);
	}

	public boolean isEnabled() {
		return recorder.isEnabled();
	}

	public void addToTest(final Transition transition, final AbstractPage endPage) {
		display.syncExec(new Runnable() {
			public void run() {
				recorder.addToTest(transition, endPage);
			}
		});
	}
}
