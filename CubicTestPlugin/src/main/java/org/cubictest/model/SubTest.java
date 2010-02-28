/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.model;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.i18n.Language;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.eclipse.core.resources.IProject;

/**
 * Subtest node that contains another test.
 * 
 * @author ehalvorsen
 * @author chr_schwarz
 */
public class SubTest extends ConnectionPoint {
	
	private String filePath;
	private transient Test test;
	private transient IProject project;
	private transient IResourceMonitor resourceMonitor;
	private transient boolean dangling;
	private int parameterIndex = -1;
	private Language language;

	public SubTest(String filePath, IProject project) {
		super();
		this.filePath = filePath;
		this.project = project;
		if (project == null) {
			Logger.warn("Invoked SubTest constructor with null project");
		}
	}
	
	public boolean isDangling() {
		getTest(false).getName();
		return dangling;
	}
	
	/**
	 * Get whether this test has its own params configured, and target test has params enabled.
	 * @return
	 */
	public boolean hasOwnParams() {
		return getTest(false).hasParamsConfigured() && parameterIndex >= 0;
	}
	
	/**
	 * Get whether this test has its own language configured, and target test has i18n enabled.
	 * @return
	 */
	public boolean hasOwnLanguage() {
		return getTest(false).hasI18nConfigured() && language != null;
	}
	
	/**
	 * @return Test	the sub test that this object represents
	 */
	public Test getTest(boolean forceRefreshFile) {
		if(test == null || forceRefreshFile) {
			reloadTest();
		}
		return test;
	}

	public void reloadTest() {
		reloadTest(false);
	}
	
	public void reloadTest(boolean rethrowOnError) {
		try {
			if (project == null && test != null) {
				project = test.getProject();
			}
			test = TestPersistance.loadFromFile(project, getFilePath());
			test.setResourceMonitor(resourceMonitor);
			test.resetStatus();
			dangling = false;
		} catch (Exception e) {
			String message = "Failed to load test from file path " + getFilePath();
			Logger.error(message, e);
			dangling = true;
			test = new Test();
			test.setName(message);
			if (rethrowOnError) {
				throw new CubicException(message, e);
			}
		}
	}
	
	@Override
	public String getName() {
		Test test = getTest(false);
		if (StringUtils.isBlank(test.getName())) {
			return getFileName();
			
		}
		else {
			return test.getName() + " (" + getFileName() + ")";
		}
	}
	
	public String getFileName() {
		int pos1 = getFilePath().lastIndexOf("/");
		int pos2 = getFilePath().lastIndexOf("\\");
		return getFilePath().substring(Math.max(pos1, pos2) + 1);
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public boolean containsTest(Test test) {
		if(test.getFilePath().equals(getFilePath())) {
			return true;
		}
		
		for(SubTest subTest : getTest(false).getSubTests()) {
			if(subTest.getFilePath() == test.getFilePath() || subTest.containsTest(test)) {
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * We need to know the project to figure out what file to load (since all paths are project relative)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}
	
	
	public IProject getProject() {
		return project;
	}

	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);
		test = null;
	}

	public void setResourceMonitor(IResourceMonitor resourceMonitor) {
		this.resourceMonitor = resourceMonitor;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": Name = " + getName() + ", FilePath = " + getFilePath();
	}

	
	/**
	 * Tells the subtest to update its status (examine its page elements).
	 * @param hadException Flag indicating that subtest had exception.
	 * @param targetConnectionPoint must be known when examining subtest path to check statuses in page elements.
	 */
	public void updateStatus(boolean hadException, ConnectionPoint targetConnectionPoint) {
		if (hadException) {
			setStatus(TestPartStatus.EXCEPTION);
		}
		else {
			setStatus(getTest(false).updateAndGetStatus(targetConnectionPoint));
		}
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public void setParameterIndex(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

}