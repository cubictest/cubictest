/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.mojos.selenium;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.runner.TestRunner;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;

/**
 * Goal which runs the CubicTest tests in the directory specified by "testDir" in the pom.xml.
 * 
 * @author Christian Schwarz
 * 
 * @goal run-tests
 */
public class MavenSeleniumRunner extends AbstractMojo
{
    /**
     * Location of the tests.
     * @parameter
     * @required
     */
    private File testDir;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
    	String baseDir = System.getProperty("basedir", System.getProperty("user.dir"));
        CubicTestProjectSettings settings = new CubicTestProjectSettings(new File(baseDir));

        Collection files = FileUtils.listFiles(testDir, new String[] {"aat", "ats"}, true);
        Iterator iter = files.iterator();
        List<String> passedTests = new ArrayList<String>();
        List<String> failedTests = new ArrayList<String>();
        List<String> exceptionTests = new ArrayList<String>();

        boolean buildOk = true;
        
        while (iter.hasNext()) {
        	File file = (File) iter.next();
        	getLog().info("---------------------------");
        	getLog().info("Running test: " + file);
        	getLog().info("---------------------------");
        	Test test = TestPersistance.loadFromFile(file, null);
        	getLog().info("Test loaded: " + test.getName());

    		TestRunner testRunner = null;
    		try {
    			testRunner = new TestRunner(test, null, settings);
    			testRunner.setFailOnAssertionFailure(true);
    			//RUN IT!
    			testRunner.run(null);
    			passedTests.add(file.getName());
    		}
    		catch (ExporterException e) {
            	getLog().error("==========================================================");
    			getLog().error("Failure in test " + file.getName() + ": " + e.getMessage());
            	getLog().error("==========================================================");
    			failedTests.add(file.getName());
    			buildOk = false;
    			break;
    		}
    		catch (Throwable e) {
    			getLog().error(e);
    			exceptionTests.add(file.getName());
    			buildOk = false;
    			break;
			}
    		finally {
				((TestRunner) testRunner).stopSelenium();
    		}
        }
        
        getLog().info("Tests passed: " + passedTests.toString());
        getLog().info("Tests failed: " + failedTests.toString());
        getLog().info("Threw exception: " + exceptionTests.toString());

        if (!buildOk) {
        	throw new MojoFailureException("[CubicTest] There were test failures.");
        }
}
 
}
