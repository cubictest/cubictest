/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.editors;


import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.resources.ResourceMonitor;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.cubictest.ui.gef.actions.AddExtensionPointAction;
import org.cubictest.ui.gef.actions.AddPageElementAction;
import org.cubictest.ui.gef.actions.AddUserInteractionTransitionAction;
import org.cubictest.ui.gef.actions.AutoLayoutAction;
import org.cubictest.ui.gef.actions.CopyAction;
import org.cubictest.ui.gef.actions.CutAction;
import org.cubictest.ui.gef.actions.PasteAction;
import org.cubictest.ui.gef.actions.PopulateCommonAction;
import org.cubictest.ui.gef.actions.PresentAction;
import org.cubictest.ui.gef.actions.ResetTestAction;
import org.cubictest.ui.gef.actions.TestContextMenuProvider;
import org.cubictest.ui.gef.actions.UpdateTestStartPointAction;
import org.cubictest.ui.gef.dnd.DataEditDropTargetListner;
import org.cubictest.ui.gef.dnd.FileTransferDropTargetListener;
import org.cubictest.ui.gef.factory.PaletteRootCreator;
import org.cubictest.ui.gef.factory.TestEditPartFactory;
import org.cubictest.ui.gef.interfaces.IDisposeSubject;
import org.cubictest.ui.gef.interfaces.exported.IDisposeListener;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.view.AddElementContextMenuList;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SaveAction;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.actions.StackAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.gef.ui.views.palette.PaletteViewerPage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * The graphical editor for editing the tests.
 * 
 * @author SK Skytteren
 * @author chr_schwarz 
 */
public class GraphicalTestEditor extends EditorPart implements IAdaptable, 
		ITabbedPropertySheetPageContributor, IDisposeSubject, ITestEditor {
	
	private static final String PERSPECTIVE_ALLWAYS_NO = "org.cubictest.perspective.allwaysNo";

	private static final String PERSPECTIVE_ALLWAYS_YES = "org.cubictest.perspective.allwaysYes";

	private GraphicalViewer graphicalViewer;
	
	private EditDomain editDomain;
	
	private boolean isDirty;
	
	private List<IDisposeListener> disposeListeners = new ArrayList<IDisposeListener>();
	
	private CommandStackListener commandStackListener = new CommandStackListener(){
		public void commandStackChanged(EventObject event){
			
			updateActions(stackActionIDs);
			setDirty(getCommandStack().isDirty());
		}
	};
	private ISelectionListener selectionListener = new ISelectionListener(){
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			updateActions(editPartActionIDs);
		}
	};
	
	private PaletteRoot paletteRoot;
	
	private ActionRegistry actionRegistry;
	
	private List<String> editPartActionIDs = new ArrayList<String>();
	private List<String> stackActionIDs = new ArrayList<String>();
	private List<String> editorActionIDs = new ArrayList<String>();

	private TestOverviewOutlinePage testOverviewOutlinePage;

	private IPropertySheetPage undoablePropertySheetPage;

	private IResourceMonitor resourceMonitor;

	private FlyoutPaletteComposite splitter;

	private PaletteViewerProvider provider;

	private CustomPalettePage page;
	
	/**
	 * Constructor.
	 */
	public GraphicalTestEditor() {
		super();
	}
	
	public ActionRegistry getActionRegistry(){
		if ( actionRegistry == null ){
			actionRegistry = new ActionRegistry();
		}
		return actionRegistry;
	}
	
	public CommandStack getCommandStack() {
		return getEditDomain().getCommandStack();
	}
	
	protected CommandStackListener getCommandStackListener(){
		return commandStackListener;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent){
		splitter = new FlyoutPaletteComposite(parent, SWT.NONE, getSite().getPage(),
				getPaletteViewerProvider(), getPalettePreferences());
		createGraphicalViewer(splitter);
		splitter.setGraphicalControl(getGraphicalControl());
		if (page != null) {
			splitter.setExternalViewer(page.getPaletteViewer());
			page = null;
		}
		registerContextMenus();
	}
	
	/**
	 * @return	the graphical viewer's control
	 */
	protected Control getGraphicalControl() {
		return getGraphicalViewer().getControl();
	}
	
	/**
	 * @return a newly-created {@link CustomPalettePage}
	 */
	protected CustomPalettePage createPalettePage() {
		return new CustomPalettePage(getPaletteViewerProvider());
	}
	
	
	/**
	 * Returns the palette viewer provider that is used to create palettes for the view and
	 * the flyout.  Creates one if it doesn't already exist.
	 * 
	 * @return	the PaletteViewerProvider that can be used to create PaletteViewers for
	 * 			this editor
	 * @see	#createPaletteViewerProvider()
	 */
	protected final PaletteViewerProvider getPaletteViewerProvider() {
		if (provider == null)
			provider = createPaletteViewerProvider();
		return provider;
	}
	
	/**
	 * By default, this method returns a FlyoutPreferences object that stores the flyout
	 * settings in the GEF plugin.  Sub-classes may override.
	 * @return	the FlyoutPreferences object used to save the flyout palette's preferences 
	 */
	protected FlyoutPreferences getPalettePreferences() {
		FlyoutPreferences prefs = FlyoutPaletteComposite
				.createFlyoutPreferences(CubicTestPlugin.getDefault().getPluginPreferences());
		if(prefs.getDockLocation() != PositionConstants.EAST)
			prefs.setDockLocation(PositionConstants.WEST);
		if(prefs.getPaletteState() != FlyoutPaletteComposite.STATE_COLLAPSED)
			prefs.setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
		return prefs;
	}
	
	/**
	 * Creates a PaletteViewerProvider that will be used to create palettes for the view
	 * and the flyout.
	 * 
	 * @return	the palette provider
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()){
			@Override
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}
	
	protected GraphicalViewer createGraphicalViewer(Composite parent){
		
		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		viewer.createControl(parent);
		graphicalViewer = viewer;
		
		viewer.getControl().setBackground(parent.getBackground());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		
		GraphicalViewerKeyHandler graphicalViewerKeyHandler = new GraphicalViewerKeyHandler(viewer);
		KeyHandler parentKeyHandler = graphicalViewerKeyHandler.setParent(getCommonKeyHandler());
		viewer.setKeyHandler(parentKeyHandler);
		
		getEditDomain().addViewer(viewer);
		
		getSite().setSelectionProvider(viewer);
		
		viewer.addDropTargetListener(new DataEditDropTargetListner(viewer));
		viewer.addDropTargetListener(new FileTransferDropTargetListener(viewer));
		viewer.setEditPartFactory(getEditPartFactory());
		viewer.setContents(getContent());
		
		return viewer;
	}

	private void registerContextMenus() {
		ContextMenuProvider provider = new TestContextMenuProvider(graphicalViewer, getActionRegistry());
		graphicalViewer.setContextMenu(provider);
		getSite().registerContextMenu("cubicTestPlugin.editor.contextmenu", provider, graphicalViewer);
	}
	
	public GraphicalViewer getGraphicalViewer(){
		return graphicalViewer;
	}
	
	private Test getContent() {
		Test test = TestPersistance.loadFromFile(((IFileEditorInput)getEditorInput()).getFile());
		test.setResourceMonitor(resourceMonitor);
		test.resetStatus();
		return test;
	}
	
	public IResourceMonitor getResourceMonitor() {
		if(resourceMonitor == null) {
			resourceMonitor = new ResourceMonitor(getProject());
		}
		return resourceMonitor;
	}
	/**
	 * @return
	 */
	private EditPartFactory getEditPartFactory() {
		return new TestEditPartFactory();
	}
	
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	@Override
	public void dispose() {
		for(IDisposeListener listener : disposeListeners) {
			listener.disposed();
		}
		
		getCommandStack().removeCommandStackListener(getCommandStackListener());
		
		getSite()
			.getWorkbenchWindow()
			.getSelectionService()
			.removeSelectionListener(
				getSelectionListener());
		
		getActionRegistry().dispose();
		
		getResourceMonitor().dispose();
		
		super.dispose();
	}
	
	public Test getTest(){
		//get test from viewer:
		EditPart part = graphicalViewer.getContents();
		return (Test)part.getModel();
	}
	
	/**
	 * Saves the multi-page editor's document.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		TestPersistance.saveToFile((Test)graphicalViewer.getContents().getModel(),((IFileEditorInput)getEditorInput()).getFile());
		getCommandStack().markSaveLocation();

		try{
			((IFileEditorInput)getEditorInput()).getFile().refreshLocal(1, monitor);
		} catch (CoreException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e, "Error saving file.");
		}
	}
	
	/**
	 * Unsupported and throws an <code>UnsupportedOperationException</code>
	 */
	@Override
	public void doSaveAs() {
		//getCommandStack().markSaveLocation();
		throw new UnsupportedOperationException();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if(!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Input must be a valid file.");
		}
		
		setSite(site);
		setInput(editorInput);
		
		setPartName(editorInput.getName());
		
		getCommandStack().addCommandStackListener(getCommandStackListener());
		getCommandStack().setUndoLimit(99);
		
		
		getSite()
			.getWorkbenchWindow()
			.getSelectionService()
			.addSelectionListener(
				getSelectionListener());
		
		createActions();
		
		checkPerspective();
	}
	
	private void createActions() {
		addStackAction(new UndoAction(this));
		addStackAction(new RedoAction(this));
		
		addEditPartAction(new CutAction((IWorkbenchPart)this));
		addEditPartAction(new CopyAction((IWorkbenchPart)this));
		addEditPartAction(new PasteAction((IWorkbenchPart)this));
		addEditPartAction(new DeleteAction((IWorkbenchPart) this));
		addEditPartAction(new DirectEditAction((IWorkbenchPart) this));
		addEditPartAction(new PresentAction((IWorkbenchPart) this));
		addEditPartAction(new PopulateCommonAction((IWorkbenchPart) this));
		addEditPartAction(new AddUserInteractionTransitionAction((IWorkbenchPart) this));
		addEditPartAction(new AddExtensionPointAction((IWorkbenchPart) this));
		addEditPartAction(new UpdateTestStartPointAction((IWorkbenchPart) this));
		
		for (Class<? extends PageElement> elementClass : AddElementContextMenuList.getList()) {
			addEditPartAction(new AddPageElementAction((IWorkbenchPart) this, elementClass));
		}

		addEditorAction(new SaveAction(this));
		addEditorAction(new ResetTestAction(this));
		addEditorAction(new AutoLayoutAction(this));

		addAction(new PrintAction(this));
		addAction(new SelectAllAction(this));
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public EditDomain getEditDomain(){
		if (editDomain == null){
			editDomain = new DefaultEditDomain(this);
			editDomain.setPaletteRoot(getPaletteRoot());
		}
		return editDomain;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return isDirty;
	}
	/**
	 * @param isDirty The isDirty to set.
	 */
	protected void setDirty(boolean dirty) {
		if(isDirty != dirty){
			isDirty = dirty;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}
	
	private void checkPerspective(){
		IWorkbench workbench = CubicTestPlugin.getDefault().getWorkbench();
		if(workbench != null){
			IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
			if(activePage == null)
				return;
			IPerspectiveDescriptor currentPerspective = activePage.getPerspective();
			IDialogSettings dialogSettings = CubicTestPlugin.getDefault().getDialogSettings();
			String ctPerspectiveId = "org.cubictest.CubicTestPerspective";
			try {
				if(dialogSettings.getBoolean(PERSPECTIVE_ALLWAYS_YES)){
					workbench.showPerspective(ctPerspectiveId, workbench.getActiveWorkbenchWindow());
				}else if(dialogSettings.getBoolean(PERSPECTIVE_ALLWAYS_NO)){
					return;
				}
				else if(!ctPerspectiveId.equals(currentPerspective.getId())){
					MessageDialog dialog = new MessageDialog(new Shell(), "Change Perspective", 
							null, "Change Perspective to CubicTest(Recomended)?", 0, 
								new String[]{"Yes", "Allways Yes", "No", "Allways No"}, 0);
					int dialogResult = dialog.open();
					switch (dialogResult) {
					case 1:
						dialogSettings.put(PERSPECTIVE_ALLWAYS_YES, true);
					case 0:
						workbench.showPerspective(ctPerspectiveId, workbench.getActiveWorkbenchWindow());
						break;
					case 3:
						dialogSettings.put(PERSPECTIVE_ALLWAYS_NO, true);
					default:
						break;
					}
				}
			} catch (WorkbenchException e) {
				ErrorHandler.logAndShowErrorDialog(e);
			}
		}
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter){
		if (adapter == GraphicalViewer.class || adapter == EditPartViewer.class)
			return getGraphicalViewer();
		if (adapter == PalettePage.class) {
			if (splitter == null) {
				page = createPalettePage();
				return page;
			}
			return createPalettePage();
		}
		if (adapter == CommandStack.class)
			return getCommandStack();
		if (adapter == EditDomain.class) 
			return getEditDomain();
		if (adapter == ActionRegistry.class)
			return getActionRegistry();
		if (adapter == IPropertySheetPage.class)
			return getPropertySheetPage();
		if (adapter == IContentOutlinePage.class)
			return getTestOverviewOutlinePage();
		if (adapter == ZoomManager.class)
			return ((ScalableFreeformRootEditPart)getGraphicalViewer()
			.getRootEditPart()).getZoomManager();
		return super.getAdapter(adapter);
	}
	
	protected PaletteRoot getPaletteRoot() {
		if (paletteRoot == null){
			paletteRoot = new PaletteRootCreator(getProject());
		}
		return paletteRoot;
	}

	public IProject getProject() {
		return ((IFileEditorInput)getEditorInput()).getFile().getProject();
	}
	
	/**
	 * Adds a <code>SelectionAction</code>.
	 * @param action
	 */
	protected void addEditPartAction(SelectionAction action){
		getActionRegistry().registerAction(action);
		editPartActionIDs.add(action.getId());
	}
	/**
	 * Adds a <code>StackAction</code>.
	 * @param action
	 */
	protected void addStackAction(StackAction action){
		getActionRegistry().registerAction(action);
		stackActionIDs.add(action.getId());
	}
	/**
	 * Adds an <code>EditorPartAction</code>.
	 * @param action
	 */
	protected void addEditorAction(EditorPartAction action){
		getActionRegistry().registerAction(action);
		editorActionIDs.add(action.getId());
	}
	
	/**
	 * Used to add an action.
	 * @param action
	 */
	protected void addAction(IAction action){
		getActionRegistry().registerAction(action);
	}
	
	private void updateActions(List actionIds){
		for(int i = 0; i < actionIds.size(); i++){
			IAction action = getActionRegistry().getAction(actionIds.get(i));
			if (action != null && action instanceof UpdateAction){
				((UpdateAction) action).update();
			}
		}
	}
	
	protected ISelectionListener getSelectionListener(){
		return selectionListener;
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#firePropertyChange(int)
	 */
	@Override
	protected void firePropertyChange(int propertyId){
		super.firePropertyChange(propertyId);
		updateActions(editorActionIDs);
	}
	
	protected KeyHandler getCommonKeyHandler(){
		KeyHandler sharedKeyHandler = new KeyHandler();
		sharedKeyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0), getActionRegistry().getAction(
				ActionFactory.DELETE.getId()));
		sharedKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0), getActionRegistry().getAction(
				GEFActionConstants.DIRECT_EDIT));
		return sharedKeyHandler;
	}
	
	/*
	 * Returns the overview for the outline view.
	 * 
	 * @return the overview
	 */
	protected TestOverviewOutlinePage getTestOverviewOutlinePage(){
		if (null == testOverviewOutlinePage && null != getGraphicalViewer()){
			RootEditPart rootEditPart = getGraphicalViewer().getRootEditPart();
			if (rootEditPart instanceof ScalableFreeformRootEditPart){
				testOverviewOutlinePage = new TestOverviewOutlinePage((ScalableFreeformRootEditPart) rootEditPart);
			}
		}
		return testOverviewOutlinePage;
	}
	/*
	 * Returns the undoable <code>PropertySheetPage</code> for this editor.
	 * 
	 * @return the undoable <code>PropertySheetPage</code>
	 */
	protected IPropertySheetPage getPropertySheetPage(){
		if (null == undoablePropertySheetPage){	
			undoablePropertySheetPage = new TabbedPropertySheetPage(this);
		}
		return undoablePropertySheetPage;
	}

	public String getContributorId() {
		return getSite().getId();
	}

	public void addDisposeListener(IDisposeListener listener) {
		disposeListeners.add(listener);
	}

	public void removeDisposeListener(IDisposeListener listener) {
		disposeListeners.remove(listener);
	}
	
	/**
	 * A custom PalettePage that helps GraphicalEditorWithFlyoutPalette keep the two
	 * PaletteViewers (one displayed in the editor and the other displayed in the PaletteView)
	 * in sync when switching from one to the other (i.e., it helps maintain state across the
	 * two viewers).
	 * 
	 * @author Pratik Shah
	 * @since 3.0
	 */
	protected class CustomPalettePage extends PaletteViewerPage {
		/**
		 * Constructor
		 * @param provider	the provider used to create a PaletteViewer
		 */
		public CustomPalettePage(PaletteViewerProvider provider) {
			super(provider);
		}
		/**
		 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createControl(Composite parent) {
			super.createControl(parent);
			if (splitter != null)
				splitter.setExternalViewer(viewer);
		}
		/**
		 * @see org.eclipse.ui.part.IPage#dispose()
		 */
		@Override
		public void dispose() {
			if (splitter != null)
				splitter.setExternalViewer(null);
			super.dispose();
		}
		/**
		 * @return	the PaletteViewer created and displayed by this page
		 */
		public PaletteViewer getPaletteViewer() {
			return viewer;
		}
	}

}