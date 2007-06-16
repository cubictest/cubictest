package org.cubictest.mojos.selenium;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
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
        throws MojoExecutionException
    {
        Collection files = FileUtils.listFiles(testDir, new String[] {"aat"}, true);
        Iterator iter = files.iterator();

        while (iter.hasNext()) {
        	File file = (File) iter.next();
        	getLog().info("Converting: " + file);
        	Test test = TestPersistance.loadFromFile(file, null);
        	getLog().info("Test loaded: " + test.getName());

    		RunnerSetup testRunner = null;
    		try {
    			testRunner = new RunnerSetup(test, null);
    			testRunner.run(null);
    		}
    		finally {
				((RunnerSetup) testRunner).stopSelenium();
    		}
        }
    }
 
}
