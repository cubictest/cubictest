/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.directEdit;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.CellEditorActionHandler;

/**
 * @author SK Skytteren
 * @author chr_schwarz
 *
 * Manages the direct edit. Sets zoom and hooks actions (copy, paste etc.)
 */
public class CubicTestDirectEditManager extends DirectEditManager {

	private String label;
	private IActionBars actionBars;
	private CellEditorActionHandler actionHandler;
	private IAction copy, cut, paste, undo, redo, find, selectAll, delete;
	private double cachedZoom = -1.0;
	private Font scaledFont;
	private ZoomListener zoomListener = new ZoomListener() {
		public void zoomChanged(double newZoom) {
			updateScaledFont(newZoom);
		}
	};
	
	/**
	 * Constructor for the <code>CubicTestDirectEditManager</code>.
	 * @param source see <code>DirectEditManager(GraphicalEditPart source, Class editorType,
			CellEditorLocator locator)</code>
	 * @param editorType<code>DirectEditManager(GraphicalEditPart source, Class editorType,
			CellEditorLocator locator)</code>
	 * @param locator<code>DirectEditManager(GraphicalEditPart source, Class editorType,
			CellEditorLocator locator)</code>
	 * @param label the label to edit
	 */
	public CubicTestDirectEditManager(GraphicalEditPart source, Class editorType,
			CellEditorLocator locator, String label) {
		super(source, editorType, locator);
		this.label = label;
	}

	/**
	 * Unregisters actions (copy, paste etc) and zoom listeners.
	 * @see org.eclipse.gef.tools.DirectEditManager#bringDown()
	 */
	protected void bringDown() {
		ZoomManager zoomMgr = (ZoomManager)getEditPart().getViewer()
				.getProperty(ZoomManager.class.toString());
		if (zoomMgr != null)
			zoomMgr.removeZoomListener(zoomListener);

		if (actionHandler != null) {
			actionHandler.dispose();
			actionHandler = null;
		}
		if (actionBars != null) {
			restoreSavedActions(actionBars);
			actionBars.updateActionBars();
			actionBars = null;
		}
		
		super.bringDown();
		// dispose any scaled fonts that might have been created
		disposeScaledFont();
	}

	private void disposeScaledFont() {
		if (scaledFont != null) {
			scaledFont.dispose();
			scaledFont = null;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.tools.DirectEditManager#initCellEditor()
	 */
	protected void initCellEditor(){
		Text text = (Text) getCellEditor().getControl();

		getCellEditor().setValue(label);

		IFigure figure = ((GraphicalEditPart) getEditPart()).getFigure();
		Font figureFont = figure.getFont();
		FontData data = figureFont.getFontData()[0];
		Dimension fontSize = new Dimension(0, data.getHeight());

		data.setHeight(fontSize.height);
		figureFont = new Font(null, data);

		text.setFont(figureFont);
		text.selectAll();

		// update font
		ZoomManager zoomMgr = (ZoomManager)getEditPart().getViewer()
				.getProperty(ZoomManager.class.toString());
		if (zoomMgr != null) {
			// this will force the font to be set
			cachedZoom = -1.0;
			updateScaledFont(zoomMgr.getZoom());
			zoomMgr.addZoomListener(zoomListener);
		} else
			getCellEditor().getControl().setFont(getEditPart().getFigure().getFont());

		// Hook the cell editor's copy/paste actions to the actionBars so that they can
		// be invoked via keyboard shortcuts.
		actionBars = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorSite().getActionBars();
		saveCurrentActions(actionBars);
		actionHandler = new CellEditorActionHandler(actionBars);
		actionHandler.addCellEditor(getCellEditor());
		actionBars.updateActionBars();
	}

	
	/**
	 * Sets the text <code>String</code> to edit.
	 * @param text
	 */
	public void setText(String text) {
		this.label = text;
	}

	private void restoreSavedActions(IActionBars actionBars){
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), paste);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), delete);
		actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAll);
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cut);
		actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), find);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undo);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redo);
	}

	private void saveCurrentActions(IActionBars actionBars) {
		copy = actionBars.getGlobalActionHandler(ActionFactory.COPY.getId());
		paste = actionBars.getGlobalActionHandler(ActionFactory.PASTE.getId());
		delete = actionBars.getGlobalActionHandler(ActionFactory.DELETE.getId());
		selectAll = actionBars.getGlobalActionHandler(ActionFactory.SELECT_ALL.getId());
		cut = actionBars.getGlobalActionHandler(ActionFactory.CUT.getId());
		find = actionBars.getGlobalActionHandler(ActionFactory.FIND.getId());
		undo = actionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
		redo = actionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
	}

	private void updateScaledFont(double zoom) {
		if (cachedZoom == zoom)
			return;
		
		Text text = (Text)getCellEditor().getControl();
		Font font = getEditPart().getFigure().getFont();
		
		disposeScaledFont();
		cachedZoom = zoom;
		if (zoom == 1.0)
			text.setFont(font);
		else {
			FontData fd = font.getFontData()[0];
			fd.setHeight((int)(fd.getHeight() * zoom));
			text.setFont(scaledFont = new Font(null, fd));
		}
	}
	
	@Override
	protected void commit() {
		super.commit();
		getEditPart().setSelected(EditPart.SELECTED_NONE);
	}
}
