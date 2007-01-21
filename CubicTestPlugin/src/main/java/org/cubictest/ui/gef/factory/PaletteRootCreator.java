/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.factory;

import static org.cubictest.ui.gef.view.CubicTestImageRegistry.*;

import java.util.ArrayList;

import org.cubictest.custom.ICustomTestStep;
import org.cubictest.model.Common;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.Page;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.Transition;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserActions;
import org.cubictest.model.context.PageSection;
import org.cubictest.model.context.Row;
import org.cubictest.model.context.Table;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
import org.cubictest.pluginsupport.CustomElementLoader;
import org.cubictest.pluginsupport.interfaces.IClassChangeListener;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.gef.tools.ConnectionDragCreationTool;
import org.eclipse.swt.widgets.Display;

/**
 * Class to create the palette (toolbox) left in the test editor.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 * 
 * Creates the Palette in the grapical test editor.
 */
public class PaletteRootCreator extends PaletteRoot implements IClassChangeListener {


	private static CubicTestImageRegistry imageRegistry = new CubicTestImageRegistry();
	private PaletteDrawer customTestSteps;
	private CustomElementLoader customTestStepLoader;


	/**
	 * @param project
	 * 
	 */
	public PaletteRootCreator(IProject project, CustomElementLoader customTestStepLoader) {
		super();
		this.customTestStepLoader = customTestStepLoader;
		customTestStepLoader.addClassChangeListener(this);

		ArrayList<PaletteContainer> categories = new ArrayList<PaletteContainer>();

		PaletteGroup basics = new PaletteGroup("Basic controls");
		PaletteDrawer controls = new PaletteDrawer("Controls");
		PaletteDrawer connectionPoints = new
			PaletteDrawer("ConnectionPoints", null);
		PaletteDrawer connections = new PaletteDrawer("Connections", null);
		PaletteDrawer formElements = new PaletteDrawer("Input Elements", null);
		PaletteDrawer contexts = new PaletteDrawer("Contexts", null);
		customTestSteps = new PaletteDrawer("Custom Test Steps", null);

		ToolEntry tool = new SelectionToolEntry();
		basics.add(tool);

		setDefaultEntry(tool);

		PaletteSeparator separator = new PaletteSeparator("palette.seperator"); 
		separator.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		basics.add(separator);

		// -- Create the ConnctionPoints
		
		tool = new CombinedTemplateCreationEntry( "Add StartPoint", 
				"Create a new StartPoint that start from an url", UrlStartPoint.class, 
				new DataCreationFactory(UrlStartPoint.class), null, null);
		connectionPoints.add(tool); 
		
		tool = new CombinedTemplateCreationEntry(
				"Add StartExtentionPoint", 
				"Create a new startpoint from an existing ExtentionPoint", 
				ExtensionStartPoint.class, 
				new DataCreationFactory(ExtensionStartPoint.class), 
					null, null);
		connectionPoints.add(tool); 
		

		
		// -- Creates the Controls --
		tool = new CombinedTemplateCreationEntry(
				"Add Page/State",
				"Create a new page/state. Weather it is a state or a "
						+ "page depends on how you look at it. Very basically it is a \"placeholder\" "
						+ " for a series of assertions.", Page.class,
				new DataCreationFactory(Page.class), imageRegistry
						.getDescriptor(PAGE_IMAGE), null);
		controls.add(tool);
		tool = new CombinedTemplateCreationEntry("Add Common",
				"Create a new Common", Common.class, new DataCreationFactory(
						Common.class), imageRegistry
						.getDescriptor(COMMON_IMAGE), null);
		controls.add(tool);
		tool = new CombinedTemplateCreationEntry("Add a Title",
				"Check for Title of the document", Title.class,
				new DataCreationFactory(Title.class), imageRegistry
						.getDescriptor(TITLE_IMAGE), null);
		controls.add(tool);
		tool = new CombinedTemplateCreationEntry("Add a Text",
				"Check for a Text", Text.class, new DataCreationFactory(
						Text.class), imageRegistry.getDescriptor(TEXT_IMAGE),
				null);
		controls.add(tool);
		tool = new CombinedTemplateCreationEntry("Add a Link",
				"Check for a Link", Link.class, new DataCreationFactory(
						Link.class), imageRegistry.getDescriptor(LINK_IMAGE),
				null);
		controls.add(tool);
		tool = new CombinedTemplateCreationEntry("Add an Image",
				"Check for an Image", Image.class, new DataCreationFactory(
						Image.class), imageRegistry.getDescriptor(IMAGE_IMAGE),
				null);
		controls.add(tool);
		tool = new CombinedTemplateCreationEntry("Add an Extension Point",
				"Facilitate extension of this test", ExtensionPoint.class,
				new DataCreationFactory(ExtensionPoint.class), imageRegistry
						.getDescriptor(EXTENSION_POINT_IMAGE), null);
		controls.add(tool);

		tool = new CombinedTemplateCreationEntry("Add a Custom Step",
				"Create a new custom step",
				CustomTestStep.class, new CustomTestStepCreationFactory(customTestStepLoader), null, null);
		controls.add(tool);
			
		// -- Creating Connections --
		tool = new ConnectionCreationToolEntry("Add Connection",
				"Create a new Connection", new DataCreationFactory(
						Transition.class), null, null);
		tool.setToolClass(ConnectionDragCreationTool.class);
		tool.setToolProperty(AbstractTool.PROPERTY_UNLOAD_WHEN_FINISHED, Boolean.TRUE);
		connections.add(tool);

		// -- Creating User interactions --
		tool = new ConnectionCreationToolEntry(
				"Add User Interaction",
				"Create user interaction which is used to change the state of the page.",
				new DataCreationFactory(UserActions.class), null, null);
		tool.setToolProperty(AbstractTool.PROPERTY_UNLOAD_WHEN_FINISHED,
				Boolean.TRUE);
		tool.setToolClass(ConnectionDragCreationTool.class);
		connections.add(tool);

		// -- Creating Contexts --
		tool = new CombinedTemplateCreationEntry("Add Page Section",
				"Creates a new Page Section (e.g. a <div> element), identifying logical a part of the page.",
				PageSection.class, new DataCreationFactory(
						PageSection.class), null, null);
		contexts.add(tool);
		tool = new CombinedTemplateCreationEntry("Add Table",
				"Creates a new Table",
				Table.class, new DataCreationFactory(
						Table.class), null, null);
		contexts.add(tool);
		tool = new CombinedTemplateCreationEntry("Add Row",
				"Creates a new Row",
				Row.class, new DataCreationFactory(
						Row.class), null, null);
		contexts.add(tool);

		
		// -- Creating Form Elements --
		tool = new CombinedTemplateCreationEntry("Add TextField",
				"Create a new TextField", TextField.class,
				new DataCreationFactory(TextField.class), imageRegistry
						.getDescriptor(TEXT_FIELD_IMAGE), null);
		formElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add Button",
				"Create a new Button. This is any kind of button",
				Button.class, new DataCreationFactory(Button.class),
				imageRegistry.getDescriptor(BUTTON_IMAGE), null);
		formElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add CheckBox",
				"Create a new CheckBox", Checkbox.class,
				new DataCreationFactory(Checkbox.class), imageRegistry
						.getDescriptor(CHECKBOX_UNCHECKED_IMAGE), null);
		formElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add RadioButton",
				"Create a new RadioButton for a RadioButtonGroup.",
				RadioButton.class, new DataCreationFactory(RadioButton.class),
				imageRegistry.getDescriptor(RADIO_BUTTON_CHECKED_IMAGE), null);
		formElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add SelectList",
				"Create a new SelectList", Select.class,
				new DataCreationFactory(Select.class), imageRegistry
						.getDescriptor(SELECT_IMAGE), null);
		formElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add Option",
				"Create a new Option for the SelectList", Option.class,
				new DataCreationFactory(Option.class), imageRegistry
						.getDescriptor(OPTION_IMAGE), null);
		formElements.add(tool);
		
		tool = new CombinedTemplateCreationEntry("Add TextArea",
				"Create a new TextArea", TextArea.class,
				new DataCreationFactory(TextArea.class), imageRegistry
						.getDescriptor(TEXT_AREA_IMAGE), null);
		formElements.add(tool);
		tool = new CombinedTemplateCreationEntry("Add PasswordField",
				"Create a new PasswordField", Password.class,
				new DataCreationFactory(Password.class), imageRegistry
						.getDescriptor(PASSWORD_IMAGE), null);
		formElements.add(tool);
		

		rebuildCustomTestStepsCategory();
		
		categories.add(basics);
		categories.add(controls);
		categories.add(connections);
		categories.add(contexts);
		categories.add(formElements);
		categories.add(customTestSteps);
		addAll(categories);
	}

	private void rebuildCustomTestStepsCategory() {
		customTestSteps.setChildren(new ArrayList());

		for (String customClass : customTestStepLoader.getClasses()) {
			ICustomTestStep instance;
			try {
				instance = customTestStepLoader.newInstance(customClass);
				ToolEntry tool = new CombinedTemplateCreationEntry(
					instance.getName(),
					instance.getDescription(),
					instance.getClass(),
					new CustomTestStepCreationFactory(customTestStepLoader, customClass),
					null,
					null
				);
				customTestSteps.add(tool);
			} catch (ClassNotFoundException e) {}
		}
	}
	
	public void classChanged() {
		System.out.println("Rebuilding custom Test Steps");
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				rebuildCustomTestStepsCategory();
			}
		});
		
	}
}
