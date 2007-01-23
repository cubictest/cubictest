package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.PageElementAction;
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
	
	public void addPageElement(final PageElement element) {
		display.asyncExec(new Runnable() {
			public void run() {
				recorder.addPageElement(element);
			}
		});
	}

	public void addPageElementToCurrentPage(final PageElement element) {
		display.asyncExec(new Runnable() {
			public void run() {
				recorder.addPageElementToCurrentPage(element);
			}
		});
	}

	public void addUserInput(final PageElementAction action) {
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
}
