/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.pluginsupport.CustomElementLoader;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.eclipse.core.resources.IFile;


/**
 * A test of a web application. Can start from an extension point in another test
 * and include other subtests.
 * 
 * @author SK Skytteren
 */
public class Test extends PropertyAwareObject {
	
	private ConnectionPoint startPoint;
	private List<ExtensionPoint> extensionPoints = new ArrayList<ExtensionPoint>();
	private List<AbstractPage> pages = new ArrayList<AbstractPage>();
	private List<SubTest> subTests = new ArrayList<SubTest>();
	private List<Transition> transitions = new ArrayList<Transition>();
	private List<CustomTestStep> customTestSteps = new ArrayList<CustomTestStep>();
	private AllLanguages allLanguages = null;
	private ParameterList paramList = null; 
	private String name;
	private String description;
	private String id;
	private String modelVersion = ModelInfo.getCurrentModelVersion();
	
	private transient IFile filePath;
	private transient IResourceMonitor resourceMonitor;
	private transient CustomElementLoader customTestStepLoader;
	private String setUpAndTearDownClassName;

	
	/**
	 * @return Returns the startPoint.
	 */
	public ConnectionPoint getStartPoint() {
		//Just re-setting model version, as XStream overwrites it from file after object initialization.
		this.modelVersion = ModelInfo.getCurrentModelVersion();
		return startPoint;
	}
	/**
	 * @param startPoint The startPoint to set.
	 */
	public void setStartPoint(ConnectionPoint startPoint) {
		ConnectionPoint oldStartPoint = this.startPoint;
		this.startPoint = startPoint;
		firePropertyChange(CHILD,oldStartPoint,startPoint);
	}
	public void addPage(AbstractPage p) {
		//idempotent:
		if (!getPages().contains(p)) {
			getPages().add(p);
			firePropertyChange(CHILD,null,p);
		}
	}
	
	public List<AbstractPage> getPages() {
		if(pages == null) {
			pages = new ArrayList<AbstractPage>();
		}
		return pages;
	}
	
	public void setPages(List<AbstractPage> pages) {
		this.pages = pages;
	}
	
	/**
	 * @return Returns the extensionPoints.
	 */
	public List<ExtensionPoint> getExtensionPoints() {
		if(extensionPoints == null) {
			extensionPoints = new ArrayList<ExtensionPoint>();
		}
		return extensionPoints;
	}
	
	/**
	 * 
	 * @return List	All extension points, including subtests
	 */
	public List<ExtensionPoint> getAllExtensionPoints() {
		List<ExtensionPoint> result = new ArrayList<ExtensionPoint>();
		for (ExtensionPoint point : extensionPoints) {
			if (point.getName() != null) {
				result.add(point);
			}
		}
		return result;
	}
	
	/**
	 * @param extensionPoints The extensionPoints to set.
	 */
	public void setExtensionPoints(List<ExtensionPoint> extensionPoints) {
		this.extensionPoints = extensionPoints;
		firePropertyChange(CHILD,null,extensionPoints);
	}
	
	public void addExtensionPoint(ExtensionPoint point) {
		getExtensionPoints().add(point);
		firePropertyChange(CHILD,null,point);
	}

	/**
	 * @param extensionPoint
	 */
	public void removeExtensionPoint(ExtensionPoint extensionPoint) {
		getExtensionPoints().remove(extensionPoint);
		firePropertyChange(CHILD,extensionPoint,null);
	}
	
	/**
	 * @return Returns the transitions.
	 */
	public List<Transition> getTransitions() {
		if(transitions == null) {
			transitions = new ArrayList<Transition>();
		}
		return transitions;
	}
	/**
	 * @param transitions The transitions to set.
	 */
	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public void addTransition(Transition transition) {
		transition.connect();
		transitions.add(transition);
		firePropertyChange(CHILD,transition,null);
	}
	
	/**
	 * @param page
	 */
	public void removePage(AbstractPage page) {
		pages.remove(page);
		firePropertyChange(CHILD,page,null);
	}
	/**
	 * @param transition
	 */
	public void removeTransition(Transition transition) {
		transition.disconnect();
		transitions.remove(transition);		
		firePropertyChange(CHILD,transition,null);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Override
	public void resetStatus() {
		for (Transition t: transitions)
			t.resetStatus();
		for (AbstractPage p : pages)
			p.resetStatus();
	}
	public AllLanguages getAllLanguages() {
		if(allLanguages == null){
			allLanguages = new AllLanguages();
		}
		return allLanguages;
	}
	public void setAllLanuages(AllLanguages allLanguages) {
		AllLanguages old = this.allLanguages;
		this.allLanguages = allLanguages;
		firePropertyChange(PropertyAwareObject.PARAM,old,allLanguages);
	}
	public ParameterList getParamList() {
		return paramList;
	}
	public void setParamList(ParameterList paramList) {
		ParameterList old = this.paramList;
		this.paramList = paramList;
		firePropertyChange(PropertyAwareObject.PARAM,old,paramList);
	}
	
	public void setFile(IFile file) {
		this.filePath = file;
		for(SubTest subTest : subTests) {
			subTest.setProject(file.getProject());
		}
		if (startPoint instanceof ExtensionStartPoint) {
			ExtensionStartPoint esp = (ExtensionStartPoint) startPoint;
			esp.setProject(file.getProject());
			
		}
	}
	
	public String getFilePath() {
		return filePath.getLocation().toOSString();
	}
	
	public List<SubTest> getSubTests() {
		return subTests;
	}
	
	public void removeSubTest(SubTest subTest) {
		subTests.remove(subTest);
		firePropertyChange(CHILD,subTest,null);
	}
	
	public void addSubTest(SubTest test) {
		test.setCustomTestStepLoader(customTestStepLoader);
		test.setResourceMonitor(resourceMonitor);
		subTests.add(test);
		firePropertyChange(CHILD, null, test);
	}
	
	public List<CustomTestStep> getCustomTestSteps() {
		return customTestSteps;
	}
	
	public void removeCustomTestSteps(CustomTestStep customTestStep) {
		customTestSteps.remove(customTestStep);
		firePropertyChange(CHILD,customTestStep,null);
	}
	
	public void addCustomTestStep(CustomTestStep customTestStep) {
		customTestStep.setCustomTestStepLoader(customTestStepLoader);
		customTestSteps.add(customTestStep);
		firePropertyChange(CHILD, null, customTestStep);
	}
	public void updateObservers() {
		if(allLanguages != null){
			allLanguages.updateObservers();
		}
		if(paramList != null){
			paramList.updateObservers();
		}
	}
	public void setResourceMonitor(IResourceMonitor resourceMonitor) {
		this.resourceMonitor = resourceMonitor;
		for(SubTest subTest : subTests) {
			subTest.setResourceMonitor(resourceMonitor);
		}
	}
	public void setCustomTestStepLoader(CustomElementLoader customTestStepLoader) {
		this.customTestStepLoader = customTestStepLoader;
		for(SubTest subTest : subTests) {
			subTest.setCustomTestStepLoader(customTestStepLoader);
		}		
		for(CustomTestStep customTestStep : customTestSteps) {
			customTestStep.setCustomTestStepLoader(customTestStepLoader);
		}
	}
	public String getSetUpAndTearDownClassName() {
		return setUpAndTearDownClassName;
	}
	public void setSetUpAndTearDownClassName(String setUpAndTearDownClassName) {
		this.setUpAndTearDownClassName = setUpAndTearDownClassName;
		
	}
}
