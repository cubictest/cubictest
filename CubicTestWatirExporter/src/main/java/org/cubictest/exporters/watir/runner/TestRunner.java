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
import org.cubictest.export.runner.BaseTestRunner;
import org.cubictest.exporters.watir.converters.ContextConverter;
import org.cubictest.exporters.watir.converters.CustomTestStepConverter;
import org.cubictest.exporters.watir.converters.PageElementConverter;
import org.cubictest.exporters.watir.converters.TransitionConverter;
import org.cubictest.exporters.watir.converters.UrlStartPointConverter;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.Test;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * The runner that starts the Watir servers and test system and starts traversal
 * of the test model.
 * 
 * @author Christian Schwarz
 */
public class TestRunner extends BaseTestRunner {

	public static final String RUNNER_TEMP_FILENAME = "cubictest_watir_runner_temp.rb";
	private Display display;
	private CubicTestProjectSettings settings;
	protected boolean processDone;
	WatirHolder watirHolder;

	
	public TestRunner(Test test, Display display, CubicTestProjectSettings settings) {
		super(display, settings, test);
		this.settings = settings;
		this.display = display;
	}


	public void run(IProgressMonitor monitor) {

		try {
			watirHolder = new WatirHolder(true, display, settings);
			WatirMonitor watirMonitor = new WatirMonitor(watirHolder);

			TreeTestWalker<WatirHolder> testWalker = new TreeTestWalker<WatirHolder>(
					UrlStartPointConverter.class, PageElementConverter.class,
					ContextConverter.class, TransitionConverter.class,
					CustomTestStepConverter.class);

			if (monitor != null) {
				monitor.beginTask("Traversing the test model...",
						IProgressMonitor.UNKNOWN);
			}

			testWalker.convertTest(test, null, watirHolder);

			//write exported watir script to temp file:
			File generatedFolder = new File(settings.getProjectFolder().getAbsolutePath() + File.separator + "generated");
			generatedFolder.mkdir();
			File tempFile = new File(generatedFolder.getAbsolutePath() + File.separator + RUNNER_TEMP_FILENAME);
			FileWriter out = new FileWriter(tempFile);
			out.write(watirHolder.toResultString());
			out.close();

			//start Watir!
			ProcessBuilder builder = new ProcessBuilder(new String[]{"ruby", tempFile.getAbsolutePath()});
			builder.redirectErrorStream(true);
			Process process = builder.start();

			CancelHandler cancelHandler = new CancelHandler(process, monitor, this);
			cancelHandler.start();
			
			//monitor process output:
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while (!monitor.isCanceled() && (line = input.readLine()) != null) {
				System.out.println(line);
				watirMonitor.handle(line);
			}
			processDone = true;
			input.close();

			if (monitor != null) {
				monitor.done();
			}
			watirMonitor.verify();

		} catch (TestFailedException e) {
			throw e;
		} catch (Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
	}
	
	

	
	/**
	 * Show the results of the test in the GUI.
	 * @return
	 */
	public String getResultMessage() {
		return watirHolder.getResults();
	}

	/** Tell that the Watir process has shut down */
	public void setProcessDone(boolean b) {
		this.processDone = b;
	}
	
	/** Get whether the Watir process has shut down */
	public boolean isProcessDone() {
		return processDone;
	}

}
