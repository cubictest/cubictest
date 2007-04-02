package org.cubictest.model;

import java.util.List;

public interface ICustomTestStep {
	
	/**
	 * The text to display in the editor
	 */
	public String getDisplayText();
	
	/**
	 * The description 
	 * @return description
	 */
	public String getDescription();
	
	/**
	 * The text to display in the tool palette
	 * @return name
	 */
	public String getName();
	
	/**
	 * Returns a list of configuration properties
	 * @return argument names
	 */
	public List<String> getArgumentNames();
		
//	/**
//	 * Execute the test this element represents
//	 */
//	public void execute(Map<String,String> arguments, IElementContext context, Document document);
}
