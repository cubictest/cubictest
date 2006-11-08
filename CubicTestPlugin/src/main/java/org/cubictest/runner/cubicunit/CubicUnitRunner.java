/*
 * Created on 04.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.runner.cubicunit;

import org.cubictest.common.converters.PageWalker;
import org.cubictest.common.converters.TreeTestWalker;
import org.cubictest.model.Test;
import org.cubictest.runner.cubicunit.delegates.UrlStartPointConverter;
import org.cubictest.runner.cubicunit.delegates.ContextConverter;
import org.cubictest.runner.cubicunit.delegates.ElementConverter;
import org.cubictest.runner.cubicunit.delegates.Holder;
import org.cubictest.runner.cubicunit.delegates.TransitionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class CubicUnitRunner implements IRunnableWithProgress, ILaunchConfigurationDelegate{

	private Test test;
	
	public CubicUnitRunner(Test test) {
		this.test = test;
	}
	
	public void run(IProgressMonitor monitor) {
		//	Set up dependency hierarchy:
		Holder holder = new Holder();
		
		PageWalker<Holder> pw = new PageWalker<Holder>(ElementConverter.class, ContextConverter.class);
		TreeTestWalker<Holder> testWalker = new TreeTestWalker<Holder>(UrlStartPointConverter.class, pw, 
				TransitionConverter.class,CustomTestStepConverter.class);
		
		monitor.beginTask("Traversing the test model...", IProgressMonitor.UNKNOWN);
		
		testWalker.convertTest(test, holder, null);
		
		monitor.done();
	}

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

}
