package org.cubictest.ui.gef.command;

import org.cubictest.model.Test;
import org.cubictest.model.parameterization.Parameter;
import org.cubictest.model.parameterization.ParameterList;
import org.eclipse.gef.commands.Command;

public class ChangeParameterListCommand extends Command {

	private Test test;
	private ParameterList newParameterList;
	private ParameterList oldParameterList;

	public void setTest(Test test) {
		this.test = test;
	}

	public void setNewParamList(ParameterList newParameterList) {
		this.newParameterList = newParameterList;
	}

	public void setOldParamList(ParameterList oldParameterList) {
		this.oldParameterList = oldParameterList;
	}
	
	@Override
	public boolean canExecute() {
		if (newParameterList.equals(oldParameterList)) {
			return false;
		}
		return true;
	}

	@Override
	public void execute() {
		super.execute();
		updateObservers(newParameterList,oldParameterList);
		test.setParamList(newParameterList);
		test.updateObservers();
	}

	@Override
	public void undo() {
		super.undo();
		updateObservers(oldParameterList, newParameterList);
		test.setParamList(oldParameterList);
		test.updateObservers();
	}
	
	private void updateObservers(ParameterList newParamList, ParameterList oldParamList) {
		if(oldParamList == null || newParamList == null)
			return;
		for(Parameter oldParam : oldParamList.getParameters()){
			for(Parameter newParam : newParamList.getParameters()){
				if(newParam.getHeader().equals(oldParam.getHeader())){
					newParam.setObservers( oldParam.getObservers());
				}
			}
			
		}
		
	}
}
