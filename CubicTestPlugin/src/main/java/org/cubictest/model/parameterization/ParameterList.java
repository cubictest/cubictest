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

package org.cubictest.model.parameterization;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.SationObserver;
import org.cubictest.persistence.ParameterPersistance;


public class ParameterList extends PropertyAwareObject {

	private static final String INDEX = "index";
	private List<Parameter> parameters;
	private int parameterIndex;
	private String fileName;
	
	public ParameterList getNewUpdatedVersion() {
		ParameterList updated = ParameterPersistance.loadFromFile(this.fileName);
		updated.copyObserversFrom(this);
		updated.setParameterIndex(parameterIndex);
		return updated;
	}
	
	public boolean hasObservers() {
		int num = 0;
		for (Parameter param : parameters) {
			for (@SuppressWarnings("unused") SationObserver observer : param.getObservers()) {
				num++;
			}
		}
		return num > 0;
	}
	
	public boolean hasParameters() {
		return inputParameterSize() > 0;
	}

	public ParameterList() {
		parameters = new ArrayList<Parameter>();
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}
	
	public void removeObserverFromAllParams(SationObserver observer) {
		for (Parameter param : parameters) {
			param.removeObserver(observer);
		}
	}
	
	public int inputParameterSize(){
		if (parameters.size() == 0)
			return 0;
		return parameters.get(0).size();
	}

	public int size(){
		return parameters.size();
	}
	
	public void setParameterIndex(int parameterIndex){
		if (parameterIndex >= inputParameterSize())
			return;
		int oldParameterIndex = this.parameterIndex;
		this.parameterIndex = parameterIndex;
		updateObservers();
		firePropertyChange(INDEX, oldParameterIndex, parameterIndex);
	}
	
	public int getParameterIndex(){
		return parameterIndex;
	}
	
	public void increaseParameterIndex(){
		parameterIndex++;
		updateObservers();
	}

	public void updateObservers() {
		for(Parameter param : parameters)
			param.setParameterIndex(parameterIndex);
	}

	public List<ParamMapper> getInputLines() {
		List<ParamMapper> list = new ArrayList<ParamMapper>();
		for (int i = 0; i < inputParameterSize(); i++)
			list.add(getInputLine(i));
		return list;
	}

	private ParamMapper getInputLine(int input) {
		return new ParamMapper(this,input);
	}
	public ParamMapper getHeaders() {
		return new ParamMapper(this, -1);
	}

	public ParamMapper createInputRow() {
		ParamMapper mapper = new ParamMapper(this,this.inputParameterSize());
		for(Parameter param : parameters ){
			param.addParameterInput("");
		}
		return mapper;
	}

	public Parameter createParameter() {
		Parameter param = new Parameter();
		int size = inputParameterSize();
		for (int i = 0; i < size; i++)
			param.addParameterInput("");
		int i = 0;
		while(!validHeader(param.getHeader() + ++i))
			;
		param.setHeader(param.getHeader() + i);
		return param;
	}

	public boolean validHeader(String name) {
		for (Parameter param : parameters)
			if (param.getHeader().equals(name))
				return false;
		return true;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void resetStatus() {

	}

	public void copyObserversFrom(ParameterList oldParamList) {
		if(getParameters() == null || oldParamList == null) {
			return;
		}
		for(Parameter oldParam : oldParamList.getParameters()){
			for(Parameter param : getParameters()){
				if(param.getHeader().equals(oldParam.getHeader())){
					param.setObservers(oldParam.getObservers());
				}
			}
		}
		updateObservers();
	}

}