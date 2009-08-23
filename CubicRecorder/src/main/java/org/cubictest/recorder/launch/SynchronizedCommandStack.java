package org.cubictest.recorder.launch;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Display;

public class SynchronizedCommandStack  {

	private Display display;
	private CommandStack commandStack;
	
	
	@SuppressWarnings("unused")
	private SynchronizedCommandStack() {
	}

	public SynchronizedCommandStack(Display display, CommandStack commandStack) {
		this.display = display;
		this.commandStack = commandStack;
	}

	
	public void execute(final Command command) {
		display.syncExec(new Runnable() {
			public void run() {
				commandStack.execute(command);
			}
		});
	}
	
	public void execute(Runnable runnable) {
		display.syncExec(runnable);
	}
	
}
