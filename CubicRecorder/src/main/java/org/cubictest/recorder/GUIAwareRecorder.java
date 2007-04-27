/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.eclipse.swt.widgets.Display;

public class GUIAwareRecorder implements IRecorder {

	private final IRecorder recorder;
	private final Display display;
	
	/**
	 * Uses the current thread's Display
	 * @param recorder
	 */
	public GUIAwareRecorder(IRecorder recorder) {
		this.recorder = recorder;
		this.display = Display.getCurrent();
	}
	
	public GUIAwareRecorder(IRecorder recorder, Display display) {
		this.recorder = recorder;
		this.display = display;
	}
	
	public void addPageElement(final PageElement element, final PageElement parent) {
		display.asyncExec(new Runnable() {
			public void run() {
				recorder.addPageElement(element, parent);
			}
		});
	}

	public void addUserInput(final UserInteraction action) {
		display.asyncExec(new Runnable() {
			public void run() {
				recorder.addUserInput(action);
			}
		});
	}

	public void setCursor(final AbstractPage page) {
		display.asyncExec(new Runnable() {
			public void run() {
				recorder.setCursor(page);
			}
		});
	}

	public void setStateTitle(final String title) {
		display.asyncExec(new Runnable() {
			public void run() {
				recorder.setStateTitle(title);
			}
		});

	}
}
