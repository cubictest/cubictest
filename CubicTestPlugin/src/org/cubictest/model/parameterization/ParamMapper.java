/*
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */package org.cubictest.model.parameterization;

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
