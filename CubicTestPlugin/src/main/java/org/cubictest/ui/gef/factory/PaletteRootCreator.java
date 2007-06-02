/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.factory;

import static org.cubictest.ui.gef.view.CubicTestImageRegistry.BUTTON_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.CHECKBOX_UNCHECKED_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.COMMON_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.CONNECTION_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.CONTEXT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.EXTENSION_POINT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.IMAGE_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.LINK_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.OPTION_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.PAGE_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.PASSWORD_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.RADIO_BUTTON_CHECKED_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.SELECT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.TEXT_AREA_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.TEXT_FIELD_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.TEXT_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.TITLE_IMAGE;
import static org.cubictest.ui.gef.view.CubicTestImageRegistry.USER_INTERACTION_IMAGE;

import java.util.ArrayList;

import org.cubictest.model.Common;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.Page;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.Transition;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
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

/**
 * Class to create the palette (toolbox) left in the test editor.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 * 
 * Creates the Palette in the grapical test editor.
 */
public class PaletteRootCreator extends PaletteRoot {

	/**
	 * @param project
	 * 
	 */
	public PaletteRootCreator(IProject project) {
		super();

		ArrayList<PaletteContainer> categories = new ArrayList<PaletteContainer>();

		PaletteGroup basics = new PaletteGroup("Basic controls");
		PaletteDrawer controls = new PaletteDrawer("Controls");
		PaletteDrawer pageElements = new PaletteDrawer("Page Elements", null);

		ToolEntry tool = new SelectionToolEntry();
		basics.add(tool);

		setDefaultEntry(tool);

		PaletteSeparator separator = new PaletteSeparator("palette.seperator");
		separator
				.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		controls.add(separator);

		// -- Creates the Controls --
		tool = new CombinedTemplateCreationEntry(
				"Page/State",
				"Create a new page/state, representing a stable state of the application between user interations",
				Page.class, new DataCreationFactory(Page.class),
				CubicTestImageRegistry.getDescriptor(PAGE_IMAGE),
				CubicTestImageRegistry.getDescriptor(PAGE_IMAGE));
		controls.add(tool);
		tool = new CombinedTemplateCreationEntry(
				"Common",
				"Create a new Common used for holding elements common to several pages/states",
				Common.class, new DataCreationFactory(Common.class),
				CubicTestImageRegistry.getDescriptor(COMMON_IMAGE),
				CubicTestImageRegistry.getDescriptor(COMMON_IMAGE));
		controls.add(tool);

		// tool = new CombinedTemplateCreationEntry("JavaScript Alert",
		// "Create a new JavaScript Alert", Image.class, new
		// DataCreationFactory(
		// Alert.class), CubicTestImageRegistry.getDescriptor(ALERT_IMAGE),
		// CubicTestImageRegistry.getDescriptor(ALERT_IMAGE));
		// controls.add(tool);
		//		
		// tool = new CombinedTemplateCreationEntry("JavaScript Confirm",
		// "Create a new JavaScript Confirm", Image.class, new
		// DataCreationFactory(
		// Confirm.class), CubicTestImageRegistry.getDescriptor(CONFIRM_IMAGE),
		// CubicTestImageRegistry.getDescriptor(CONFIRM_IMAGE));
		// controls.add(tool);
		//		
		// tool = new CombinedTemplateCreationEntry("JavaScript Prompt",
		// "Create a new JavaScript Prompt", Image.class, new
		// DataCreationFactory(
		// Prompt.class), CubicTestImageRegistry.getDescriptor(PROMPT_IMAGE),
		// CubicTestImageRegistry.getDescriptor(PROMPT_IMAGE));
		// controls.add(tool);

		tool = new CombinedTemplateCreationEntry(
				"Extension Point",
				"Add an extension point that other tests can start from or include",
				ExtensionPoint.class, new DataCreationFactory(
						ExtensionPoint.class), CubicTestImageRegistry
						.getDescriptor(EXTENSION_POINT_IMAGE),
				CubicTestImageRegistry.getDescriptor(EXTENSION_POINT_IMAGE));
		controls.add(tool);

		// tool = new CombinedTemplateCreationEntry("Custom Step",
		// "Create a new Custom Step that lets you write code to do operations
		// not available in the graphical test editor",
		// CustomTestStep.class, new
		// CustomTestStepCreationFactory(customTestStepLoader),
		// CubicTestImageRegistry.getDescriptor(CUSTOM_STEP_IMAGE), null);
		// controls.add(tool);

		// -- Creating User interactions --
		tool = new CombinedTemplateCreationEntry(
				"User Interaction",
				"Add a new User Interaction to a page (for changing the state of the page, e.g. filling out a form)",
				new DataCreationFactory(UserInteractionsTransition.class),
				CubicTestImageRegistry.getDescriptor(USER_INTERACTION_IMAGE),
				CubicTestImageRegistry.getDescriptor(USER_INTERACTION_IMAGE));
		controls.add(tool);

		// -- Creating Connections --
		tool = new ConnectionCreationToolEntry(
				"Connection",
				"Create a new Connection or User Interaction. Use \"drag and drop\" from source page to target page",
				new DataCreationFactory(Transition.class),
				CubicTestImageRegistry.getDescriptor(CONNECTION_IMAGE),
				CubicTestImageRegistry.getDescriptor(CONNECTION_IMAGE));
		tool.setToolClass(ConnectionDragCreationTool.class);
		tool.setToolProperty(AbstractTool.PROPERTY_UNLOAD_WHEN_FINISHED,
				Boolean.TRUE);
		controls.add(tool);

		// -- Creating Contexts --
		tool = new CombinedTemplateCreationEntry(
				"Context",
				"Create a new Context used for identyfying a part of the page. Other page elements can be put into the context",
				SimpleContext.class, new DataCreationFactory(
						SimpleContext.class), CubicTestImageRegistry
						.getDescriptor(CONTEXT_IMAGE), CubicTestImageRegistry
						.getDescriptor(CONTEXT_IMAGE));
		pageElements.add(tool);

		// tool = new CombinedTemplateCreationEntry("Row",
		// "Creates a new table Row",
		// Row.class, new DataCreationFactory(
		// Row.class), CubicTestImageRegistry.getDescriptor(ROW_IMAGE),
		// CubicTestImageRegistry.getDescriptor(ROW_IMAGE));
		// pageElements.add(tool);

		// tool = new CombinedTemplateCreationEntry("Frame",
		// "Creates a new Frame",
		// Frame.class, new DataCreationFactory(
		// Frame.class), null, null);
		// pageElements.add(tool);

		// -- Basic Page Elements --

		tool = new CombinedTemplateCreationEntry("Text",
				"Check for a text on the page", Text.class,
				new DataCreationFactory(Text.class), CubicTestImageRegistry
						.getDescriptor(TEXT_IMAGE), CubicTestImageRegistry
						.getDescriptor(TEXT_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Link",
				"Check for a link on the page", Link.class,
				new DataCreationFactory(Link.class), CubicTestImageRegistry
						.getDescriptor(LINK_IMAGE), CubicTestImageRegistry
						.getDescriptor(LINK_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Button",
				"Check for a button on the page.", Button.class,
				new DataCreationFactory(Button.class), CubicTestImageRegistry
						.getDescriptor(BUTTON_IMAGE), CubicTestImageRegistry
						.getDescriptor(BUTTON_IMAGE));
		pageElements.add(tool);

		// -- Creating Form Elements --
		tool = new CombinedTemplateCreationEntry("TextField",
				"Check for a text field on the page", TextField.class,
				new DataCreationFactory(TextField.class),
				CubicTestImageRegistry.getDescriptor(TEXT_FIELD_IMAGE),
				CubicTestImageRegistry.getDescriptor(TEXT_FIELD_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("CheckBox",
				"Check for a check box on the page", Checkbox.class,
				new DataCreationFactory(Checkbox.class), CubicTestImageRegistry
						.getDescriptor(CHECKBOX_UNCHECKED_IMAGE),
				CubicTestImageRegistry.getDescriptor(CHECKBOX_UNCHECKED_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("RadioButton",
				"Check for a radio button on the page", RadioButton.class,
				new DataCreationFactory(RadioButton.class),
				CubicTestImageRegistry
						.getDescriptor(RADIO_BUTTON_CHECKED_IMAGE),
				CubicTestImageRegistry
						.getDescriptor(RADIO_BUTTON_CHECKED_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("SelectList",
				"Check for a select list on the page", Select.class,
				new DataCreationFactory(Select.class), CubicTestImageRegistry
						.getDescriptor(SELECT_IMAGE), CubicTestImageRegistry
						.getDescriptor(SELECT_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Option",
				"Check for a option for the select list", Option.class,
				new DataCreationFactory(Option.class), CubicTestImageRegistry
						.getDescriptor(OPTION_IMAGE), CubicTestImageRegistry
						.getDescriptor(OPTION_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("TextArea",
				"Check for a text area on the page", TextArea.class,
				new DataCreationFactory(TextArea.class), CubicTestImageRegistry
						.getDescriptor(TEXT_AREA_IMAGE), CubicTestImageRegistry
						.getDescriptor(TEXT_AREA_IMAGE));
		pageElements.add(tool);
		tool = new CombinedTemplateCreationEntry("PasswordField",
				"Check for a password field on the page", Password.class,
				new DataCreationFactory(Password.class), CubicTestImageRegistry
						.getDescriptor(PASSWORD_IMAGE), CubicTestImageRegistry
						.getDescriptor(PASSWORD_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Image",
				"Check for an image on the page", Image.class,
				new DataCreationFactory(Image.class), CubicTestImageRegistry
						.getDescriptor(IMAGE_IMAGE), CubicTestImageRegistry
						.getDescriptor(IMAGE_IMAGE));
		pageElements.add(tool);

		tool = new CombinedTemplateCreationEntry("Title", "Check page title",
				Title.class, new DataCreationFactory(Title.class),
				CubicTestImageRegistry.getDescriptor(TITLE_IMAGE),
				CubicTestImageRegistry.getDescriptor(TITLE_IMAGE));
		pageElements.add(tool);

		// rebuildCustomTestStepsCategory();
		categories.add(basics);
		categories.add(controls);
		categories.add(pageElements);
		// categories.add(customTestSteps);
		addAll(categories);
	}
}
