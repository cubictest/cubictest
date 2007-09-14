/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.export.exceptions.TestFailedException;
import org.cubictest.exporters.watir.converters.ContextConverter;
import org.cubictest.exporters.watir.converters.CustomTestStepConverter;
import org.cubictest.exporters.watir.converters.PageElementConverter;
import org.cubictest.exporters.watir.converters.TransitionConverter;
import org.cubictest.exporters.watir.converters.UrlStartPointConverter;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.model.Test;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

/**
 * The runner that starts the Watir servers and test system and starts traversal
 * of the test model.
 * 
 * @author Christian Schwarz
 */
public class RunnerSetup implements IRunnableWithProgress {

	Test test;
	WatirMonitor watirMonitor;
	private Display display;
	private CubicTestProjectSettings settings;
	private boolean processDone;

	public RunnerSetup(Test test, Display display, CubicTestProjectSettings settings) {
		this.settings = settings;
		this.test = test;
		this.display = display;
	}


	public void run(IProgressMonitor monitor) {

		try {
			StepList stepList = new StepList(true, display);
			watirMonitor = new WatirMonitor(stepList, display);

			TreeTestWalker<StepList> testWalker = new TreeTestWalker<StepList>(
					UrlStartPointConverter.class, PageElementConverter.class,
					ContextConverter.class, TransitionConverter.class,
					CustomTestStepConverter.class);

			if (monitor != null) {
				monitor.beginTask("Traversing the test model...",
						IProgressMonitor.UNKNOWN);
			}

			testWalker.convertTest(test, null, stepList);

			//write exported watir script to temp file:
			File generatedFolder = new File(settings.getProjectFolder().getAbsolutePath() + File.separator + "generated");
			generatedFolder.mkdir();
			File tempFile = new File(generatedFolder.getAbsolutePath() + File.separator + "cubictest_watir_runner_temp.rb");
			FileWriter out = new FileWriter(tempFile);
			out.write(stepList.toResultString());
			out.close();

			//start Watir!
			ProcessBuilder builder = new ProcessBuilder(new String[]{"ruby", tempFile.getAbsolutePath()});
			builder.redirectErrorStream(true);
			Process process = builder.start();
			ProcessTerminationDetector terminationDetector = new ProcessTerminationDetector(process, this);
			terminationDetector.start();
			
			//monitor process output:
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while (!processDone && (line = input.readLine()) != null) {
				System.out.println(line);
				watirMonitor.handle(line);
			}
			input.close();

			if (monitor != null) {
				monitor.done();
			}

		} catch (TestFailedException e) {
			throw e;
		} catch (Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
	}
	
	
	/** Class that monitors the Proces object and detects when it shuts down */
	public class ProcessTerminationDetector extends Thread {
		Process process;
		RunnerSetup runner;
		
		public ProcessTerminationDetector(Process process, RunnerSetup runner) {
			this.process = process;
			this.runner = runner;
		}
		
		public void run() {
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				runner.setProcessDone(true);
			}
			runner.setProcessDone(true);
		}
	}
	
	
	/**
	 * Show the results of the test in the GUI.
	 * @return
	 */
	public String getResultInfo() {
		return watirMonitor.getResultInfo();
	}

	/** Tell that the Watir process has shut down */
	public void setProcessDone(boolean b) {
		this.processDone = b;
	}

}
