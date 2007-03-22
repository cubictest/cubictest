package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;

public class ChangeIdentifierParamKeyCommand extends Command {

	private Identifier identifier;
	private String newParamKey;
	private String oldParamKey;
	private Test test;

	public void setIndentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setNewParamKey(String newParamKey) {
		this.newParamKey = newParamKey;
	}

	public void setOldParamKey(String oldParamKey) {
		this.oldParamKey = oldParamKey;
	}
	
	@Override
	public void execute() {
		super.execute();
		removeObserver(oldParamKey);
		identifier.setParamKey(newParamKey);
		addObserver(newParamKey);
		test.updateObservers();
	}
	@Override
	public void undo() {
		super.undo();
		removeObserver(newParamKey);
		identifier.setParamKey(oldParamKey);
		addObserver(oldParamKey);
		test.updateObservers();
	}

	public void setTest(Test test) {
		this.test = test;
	}
	
	private void addObserver(String key){
		int index = getIndex(key);
		test.getParamList().getParameters().get((index < 0) ? 0 : index).addObserver(identifier);
	}
	private void removeObserver(String key){
		int index = getIndex(key);
		test.getParamList().getParameters().get((index < 0) ? 0 : index).removeObserver(identifier);
	}
	
	private int getIndex(String key){
		return test.getParamList().getHeaders().indexOf(key);
	}
}
