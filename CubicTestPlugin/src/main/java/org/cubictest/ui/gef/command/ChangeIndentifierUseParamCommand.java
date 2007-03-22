package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;

public class ChangeIndentifierUseParamCommand extends Command {

	private boolean newUseParam;
	private boolean oldUseParam;
	private Identifier identifier;
	private Test test;

	public void setNewUseParam(boolean newUseParam) {
		this.newUseParam = newUseParam;	
	}

	public void setOldUseParam(boolean oldUseParam) {
		this.oldUseParam = oldUseParam;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public void setTest(Test test) {
		this.test = test;
	}

	@Override
	public void execute() {
		super.execute();
		identifier.setUseParam(newUseParam);
		fixListeners(newUseParam);
	}
	
	@Override
	public void undo() {
		super.undo();
		identifier.setUseParam(oldUseParam);
		fixListeners(oldUseParam);
	}
	
	private void fixListeners(boolean addListener){
		int index = test.getParamList().getHeaders().indexOf(identifier.getParamKey());
		if(addListener){
			test.getParamList().getParameters().get((index < 0) ? 0 : index).addObserver(identifier);
		}else{
			test.getParamList().getParameters().get((index < 0) ? 0 : index).removeObserver(identifier);
		}
		test.updateObservers();
	}
}