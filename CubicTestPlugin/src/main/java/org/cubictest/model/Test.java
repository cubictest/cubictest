/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.export.utils.TestWalkerUtils;
import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;


/**
 * A test of a web application.
 * 
 * @author SK Skytteren
 * @author Christian Schwarz
 */
public class Test extends PropertyAwareObject {
	
	private ConnectionPoint startPoint;
	private List<ExtensionPoint> extensionPoints = new ArrayList<ExtensionPoint>();
	private List<AbstractPage> pages = new ArrayList<AbstractPage>();
	private List<SubTest> subTests = new ArrayList<SubTest>();
	private List<Transition> transitions = new ArrayList<Transition>();
	private List<CustomTestStepHolder> customTestSteps = new ArrayList<CustomTestStepHolder>();
	private AllLanguages allLanguages = null;
	private ParameterList paramList = null; 
	private String name;
	private String description;
	private String id;
	protected String modelVersion = ModelInfo.getCurrentModelVersion();
	
	private transient IFile filePath;
	private transient IResourceMonitor resourceMonitor;
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
		String oldName = this.name;
		this.name = name;
		firePropertyChange(NAME, oldName, name);
	}
	@Override
	public void resetStatus() {
		getStartPoint().resetStatus();
		for (Transition t: transitions)
			t.resetStatus();
		for (AbstractPage p : pages)
			p.resetStatus();
		for (SubTest st : subTests)
			st.resetStatus();
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
		for(CustomTestStepHolder customTestStep :customTestSteps) {
			customTestStep.setProject(file.getProject());
		}
		if (startPoint instanceof ExtensionStartPoint) {
			ExtensionStartPoint esp = (ExtensionStartPoint) startPoint;
			esp.setProject(file.getProject());
			
		}
	}
	
	public String getFilePath() {
		return filePath.getLocation().toOSString();
	}
	
	public IFile getFile() {
		return filePath;
	}
	
	public IProject getProject() {
		return filePath.getProject();
	}
	
	public List<SubTest> getSubTests() {
		return subTests;
	}
	
	public void removeSubTest(SubTest subTest) {
		subTests.remove(subTest);
		firePropertyChange(CHILD,subTest,null);
	}
	
	public void addSubTest(SubTest test) {
		test.setResourceMonitor(resourceMonitor);
		subTests.add(test);
		firePropertyChange(CHILD, null, test);
	}
	
	public List<CustomTestStepHolder> getCustomTestSteps() {
		return customTestSteps;
	}
	
	public void removeCustomTestSteps(CustomTestStepHolder customTestStep) {
		customTestSteps.remove(customTestStep);
		firePropertyChange(CHILD,customTestStep,null);
	}
	public void addCustomTestStep(CustomTestStepHolder customTestStep) {
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
	public String getSetUpAndTearDownClassName() {
		return setUpAndTearDownClassName;
	}
	public void setSetUpAndTearDownClassName(String setUpAndTearDownClassName) {
		this.setUpAndTearDownClassName = setUpAndTearDownClassName;
		
	}
	
	public TransitionNode getFirstNodeAfterStartPoint() {
		List<Transition> trans = getStartPoint().getOutTransitions();
		if (trans.size() > 0) {
			return (TransitionNode) trans.get(0).getEnd();
		}
		return null;
		
	}
	
	
	/**
	 * Updates and return the overall test status (aggregates all page element statuses).
	 * If all elements pass, test passes, else it returns fail, warn or exception status. 
	 * @param hadException
	 * @return copy of the new status
	 */
	public TestPartStatus updateAndGetStatus(ConnectionPoint targetConnectionPoint) {
		TestPartStatus status = null;
		int passed = 0;
		int failed = 0;
		int exception = 0;
		int warn = 0;
		int unknown = 0;
		List<PropertyAwareObject> list = new ArrayList<PropertyAwareObject>();
		for (AbstractPage page : getPages()) {
			for (PageElement pe : page.getFlattenedElements()) {
				if (targetConnectionPoint != null && !TestWalkerUtils.isOnExtensionPointPath(page, targetConnectionPoint)) {
					continue;
				}
				list.add(pe);
			}
		}
		for (SubTest subTest : getSubTests()) {
			subTest.updateStatus(false, targetConnectionPoint);
			list.add(subTest);
		}
		if (getStartPoint() instanceof ExtensionStartPoint) {
			ExtensionStartPoint exStartPoint = (ExtensionStartPoint) getStartPoint();
			exStartPoint.updateStatus(false, exStartPoint);
			list.add(exStartPoint);
		}
		
		for (PropertyAwareObject object : list) {
			if (object.getStatus().equals(TestPartStatus.PASS))
				passed++;
			else if (object.getStatus().equals(TestPartStatus.FAIL))
				failed++;
			else if (object.getStatus().equals(TestPartStatus.EXCEPTION))
				exception++;
			else if (object.getStatus().equals(TestPartStatus.WARN))
				warn++;
			else if (object.getStatus().equals(TestPartStatus.UNKNOWN))
				unknown++;
		}
		
		if (passed == list.size())
			status = TestPartStatus.PASS;
		else if (failed == list.size())
			status = TestPartStatus.FAIL;
		else if (exception > 0)
			status = TestPartStatus.EXCEPTION;
		else if (unknown == list.size())
			status = TestPartStatus.UNKNOWN;
		else
			status = TestPartStatus.WARN;
		
		setStatus(status);
		return status;
	}
	
	@Override
	public String toString() {
		return "\"" + getName() + "\" <" + getFile().getName() + ">";
	}
}
