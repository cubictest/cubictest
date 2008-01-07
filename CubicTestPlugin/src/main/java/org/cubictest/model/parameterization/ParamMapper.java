/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/package org.cubictest.model.parameterization;

import java.util.ArrayList;
import java.util.List;

public class ParamMapper {

	private ParameterList parameterList;
	private int row;
	
	public ParamMapper(ParameterList parameterList, int row){
		this.parameterList = parameterList;
		this.row = row; 
	}
	
	public String get(int columnIndex) {
		return getList().get(columnIndex);
	}

	private List<String> getList(){
		List<String> list = new ArrayList<String>();
		if (row == -1){
			for(Parameter param : parameterList.getParameters())
				list.add(param.getHeader());
			return list;
		}
		for(Parameter param : parameterList.getParameters())
			list.add(param.getParameterInput(row));
		return list;
	}
	
	public String[] toArray() {
		return getList().toArray(new String[]{});
	}

	public int indexOf(String property) {
		return getList().indexOf(property);
	}

	public void set(String text, int index) {
		if (row == -1)
			parameterList.getParameters().get(index).setHeader(text);
		else
			parameterList.getParameters().get(index).setParameterInput(text, row);
	}

}
