/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.factory;

import static org.cubictest.ui.gef.view.CubicTestImageRegistry.ALERT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.BUTTON_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.CHECKBOX_UNCHECKED_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.COMMON_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.CONFIRM_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.CONNECTION_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.CONTEXT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.CUSTOM_STEP_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.EXTENSION_POINT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.IMAGE_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.LINK_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.OPTION_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.PAGE_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.PASSWORD_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.PROMPT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.RADIO_BUTTON_CHECKED_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.ROW_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.SELECT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.TEXT_AREA_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.TEXT_FIELD_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.TEXT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.TITLE_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.USER_INTERACTION_IMAGE;

import java.util.ArrayList;

import org.cubictest.model.Common;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ICustomTestStep;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.Page;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.Transition;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.Frame;
import org.cubictest.model.context.Row;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
import org.cubictest.model.popup.Alert;
import org.cubictest.model.popup.Confirm;
import org.cubictest.model.popup.Prompt;
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
		PaletteDrawer pageElements = new PaletteDrawer("Page Elements", null);
		customTestSteps = new PaletteDrawer("Custom Test Steps", null);

		ToolEntry tool = new SelectionToolEntry();
		basics.add(tool);

		setDefaultEntry(tool);

		PaletteSeparator separator = new PaletteSeparator("palette.seperator"); 
		separator.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		controls.add(separator);		

		
		// -- Creates the Controls --
		tool = new CombinedTemplateCreationEntry(
				"Add Page/State",
				"Create a new page/state, representing a stable state of the application between user interations", Page.class,
				new DataCreationFactory(Page.class), CubicTestImageRegistry
						.getDescriptor(PAGE_IMAGE), null);
		controls.add(tool);
		tool = new CombinedTemplateCreationEntry("Add Common",
				"Create a new Common used for holding elements common to several pages/states", Common.class, new DataCreationFactory(
						Common.class), CubicTestImageRegistry
						.getDescriptor(COMMON_IMAGE), null);
		controls.add(tool);
		
//		tool = new CombinedTemplateCreationEntry("Add an JavaScript Alert",
//				"Create a new JavaScript Alert", Image.class, new DataCreationFactory(
//						Alert.class), CubicTestImageRegistry.getDescriptor(ALERT_IMAGE),
//				null);
//		controls.add(tool);
//		
//		tool = new CombinedTemplateCreationEntry("Add an JavaScript Confirm",
//				"Create a new JavaScript Confirm", Image.class, new DataCreationFactory(
//						Confirm.class), CubicTestImageRegistry.getDescriptor(CONFIRM_IMAGE),
//				null);
//		controls.add(tool);
//		
//		tool = new CombinedTemplateCreationEntry("Add an JavaScript Prompt",
//				"Create a new JavaScript Prompt", Image.class, new DataCreationFactory(
//						Prompt.class), CubicTestImageRegistry.getDescriptor(PROMPT_IMAGE),
//				null);
//		controls.add(tool);

		tool = new CombinedTemplateCreationEntry("Add an Extension Point",
				"Add an extension point that other tests can start from or include", ExtensionPoint.class,
				new DataCreationFactory(ExtensionPoint.class), CubicTestImageRegistry
						.getDescriptor(EXTENSION_POINT_IMAGE), null);
		controls.add(tool);

//		tool = new CombinedTemplateCreationEntry("Add a Custom Step",
//				"Create a new Custom Step that lets you write code to do operations not available in the graphical test editor",
//				CustomTestStep.class, new CustomTestStepCreationFactory(customTestStepLoader), 
//				CubicTestImageRegistry.getDescriptor(CUSTOM_STEP_IMAGE), null);
//		controls.add(tool);
			
		// -- Creating User interactions --
		tool = new CombinedTemplateCreationEntry(
				"Add User Interaction",
				"Add a new User Interaction to a page (for changing the state of the page, e.g. filling out a form)",
				new DataCreationFactory(UserInteractionsTransition.class), CubicTestImageRegistry.getDescriptor(USER_INTERACTION_IMAGE), null);
		controls.add(tool);

		// -- Creating Connections --
		tool = new ConnectionCreationToolEntry("Add Connection",
				"Create a new Connection or User Interaction. Use \"drag and drop\" from source page to target page", 
				new DataCreationFactory(Transition.class), CubicTestImageRegistry.getDescriptor(CONNECTION_IMAGE), null);
		tool.setToolClass(ConnectionDragCreationTool.class);
		tool.setToolProperty(AbstractTool.PROPERTY_UNLOAD_WHEN_FINISHED, Boolean.TRUE);
		controls.add(tool);


		// -- Creating Contexts --
		tool = new CombinedTemplateCreationEntry("Add Context",
				"Create a new Context used for identyfying a part of the page. Other page elements can be put into the context.",
				SimpleContext.class, new DataCreationFactory(
						SimpleContext.class), CubicTestImageRegistry.getDescriptor(CONTEXT_IMAGE), null);
		pageElements.add(tool);
		
//		tool = new CombinedTemplateCreationEntry("Add Row",
//				"Creates a new table Row",
//				Row.class, new DataCreationFactory(
//						Row.class), CubicTestImageRegistry.getDescriptor(ROW_IMAGE), null);
//		pageElements.add(tool);

//		tool = new CombinedTemplateCreationEntry("Add Frame",
//				"Creates a new Frame",
//				Frame.class, new DataCreationFactory(
//						Frame.class), null, null);
//		pageElements.add(tool);

		
		
		// -- Basic Page Elements --
		
		tool = new CombinedTemplateCreationEntry("Add a Title",
				"Check for title of the document", Title.class,
				new DataCreationFactory(Title.class), CubicTestImageRegistry
						.getDescriptor(TITLE_IMAGE), null);
		pageElements.add(tool);
		
		tool = new CombinedTemplateCreationEntry("Add a Text",
				"Check for a text on the page", Text.class, new DataCreationFactory(
						Text.class), CubicTestImageRegistry.getDescriptor(TEXT_IMAGE),
				null);
		pageElements.add(tool);
		
		tool = new CombinedTemplateCreationEntry("Add a Link",
				"Check for a link on the page", Link.class, new DataCreationFactory(
						Link.class), CubicTestImageRegistry.getDescriptor(LINK_IMAGE),
				null);
		pageElements.add(tool);
		
		tool = new CombinedTemplateCreationEntry("Add an Image",
				"Check for an image on the page", Image.class, new DataCreationFactory(
						Image.class), CubicTestImageRegistry.getDescriptor(IMAGE_IMAGE),
				null);
		pageElements.add(tool);
		
		
		// -- Creating Form Elements --
		tool = new CombinedTemplateCreationEntry("Add TextField",
				"Check for a text field on the page", TextField.class,
				new DataCreationFactory(TextField.class), CubicTestImageRegistry
						.getDescriptor(TEXT_FIELD_IMAGE), null);
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add Button",
				"Check for a button on the page. This is any kind of button",
				Button.class, new DataCreationFactory(Button.class),
				CubicTestImageRegistry.getDescriptor(BUTTON_IMAGE), null);
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add CheckBox",
				"Check for a check box on the page", Checkbox.class,
				new DataCreationFactory(Checkbox.class), CubicTestImageRegistry
						.getDescriptor(CHECKBOX_UNCHECKED_IMAGE), null);
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add RadioButton",
				"Check for a radio button on the page",
				RadioButton.class, new DataCreationFactory(RadioButton.class),
				CubicTestImageRegistry.getDescriptor(RADIO_BUTTON_CHECKED_IMAGE), null);
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add SelectList",
				"Check for a select list on the page", Select.class,
				new DataCreationFactory(Select.class), CubicTestImageRegistry
						.getDescriptor(SELECT_IMAGE), null);
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Add Option",
				"Check for a option for the select list", Option.class,
				new DataCreationFactory(Option.class), CubicTestImageRegistry
						.getDescriptor(OPTION_IMAGE), null);
		pageElements.add(tool);
		
		tool = new CombinedTemplateCreationEntry("Add TextArea",
				"Check for a text area on the page", TextArea.class,
				new DataCreationFactory(TextArea.class), CubicTestImageRegistry
						.getDescriptor(TEXT_AREA_IMAGE), null);
		pageElements.add(tool);
		tool = new CombinedTemplateCreationEntry("Add PasswordField",
				"Check for a password field on the page", Password.class,
				new DataCreationFactory(Password.class), CubicTestImageRegistry
						.getDescriptor(PASSWORD_IMAGE), null);
		pageElements.add(tool);
		

//		rebuildCustomTestStepsCategory();
		categories.add(basics);
		categories.add(controls);
		categories.add(pageElements);
//		categories.add(customTestSteps);
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
