/*
 * Created on 02.apr.2007
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.interfaces.exported;

import org.cubictest.model.Test;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.commands.CommandStack;

/**
 * Interface for test editors.
 * 
 * @author Christian Schwarz
 */
public interface ITestEditor {

	public static final int INITIAL_PAGE_POS_Y = 100;
	public static final int INITIAL_PAGE_POS_X = 170;
	public static final int INITIAL_PAGE_WIDTH = 150;
	public static final int INITIAL_PAGE_HEIGHT = 95;
	public static final int NEW_PATH_OFFSET = 250;

	public Test getTest();
	
	public void addDisposeListener(IDisposeListener listener);
	
	public CommandStack getCommandStack();
	
	public IProject getProject();
	
}
