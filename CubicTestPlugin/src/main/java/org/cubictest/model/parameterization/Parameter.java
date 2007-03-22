/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model.parameterization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cubictest.model.SationObserver;

public class Parameter {

	private String header;
	private List<String> inputs;
	private List<SationObserver> observers;
	
	public Parameter(){
		inputs = new ArrayList<String>();
		observers = new ArrayList<SationObserver>();
		header = "param";
	}

	public void setHeader(String header){
		this.header = header;
	}
	
	public String getHeader(){
		return header;
	}
	
	public List<String> getParameterInputs() {
		return inputs;
	}

	public void setParameterInputs(List<String> inputs) {
		this.inputs = inputs;
	}
	
	public void addParameterInput(String input) {
		inputs.add(input);
	}
	
	public String getParameterInput(int index){
		return inputs.get(index);
	}
	
	public void setParameterIndex(int parameterIndex) {
		if(parameterIndex < size()){
			for(SationObserver observer: observers){
				String paramValue = inputs.get(parameterIndex);
				if(observer.useI18n()){
					String[] values = paramValue.split(";");
					paramValue = observer.getValue();
					if(paramValue == null)
						paramValue = "";
					for(int i = 0; i < values.length; i++)
						paramValue = StringUtils.replace(paramValue,"{" + i + "}", values[i]);
				}
				observer.setValue(paramValue);
			}
		}
	}

	public void setParameterInput(String text, int index) {
		inputs.set(index, text);
	}

	public int size() {
		return inputs.size();
	}
	
	public void addObserver(SationObserver observer){
		observers.add(observer);
	}
	public void removeObserver(SationObserver observer){
		observers.remove(observer);
	}

	public List<SationObserver> getObservers() {
		List<SationObserver> clone = new ArrayList<SationObserver>();
		for(SationObserver ob : observers){
			clone.add(ob);
		}
		return clone;
	}

	public void setObservers(List<SationObserver> observers) {
		this.observers = observers; 
	}

}
