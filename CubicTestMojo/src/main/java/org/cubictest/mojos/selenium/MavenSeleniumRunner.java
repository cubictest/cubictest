/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.mojos.selenium;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestFailure;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cubictest.export.exceptions.TestFailedException;
import org.cubictest.exporters.selenium.runner.RunnerSetup;
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
    	
        Collection files = FileUtils.listFiles(testDir, new String[] {"aat"}, true);
        Iterator iter = files.iterator();
        List<String> passedTests = new ArrayList<String>();
        List<String> failedTests = new ArrayList<String>();
        List<String> exceptionTests = new ArrayList<String>();
        
        boolean buildOk = true;
        
        while (iter.hasNext()) {
        	File file = (File) iter.next();
        	getLog().info("Converting: " + file);
        	Test test = TestPersistance.loadFromFile(file, null);
        	getLog().info("Test loaded: " + test.getName());

    		RunnerSetup testRunner = null;
    		try {
    			testRunner = new RunnerSetup(test, null);
    			testRunner.setFailOnAssertionFailure(true);
    			testRunner.run(null);
    			passedTests.add(file.getName());
    		}
    		catch (TestFailedException tfe) {
            	getLog().error("==========================================================");
    			getLog().error("Failure in test " + file.getName() + ": " + tfe.getMessage());
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
				((RunnerSetup) testRunner).stopSelenium();
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
