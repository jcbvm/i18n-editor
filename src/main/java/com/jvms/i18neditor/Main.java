package com.jvms.i18neditor;

import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;

import com.google.common.collect.Sets;
import com.jvms.i18neditor.editor.Editor;

/**
 * 
 * @author Jacob
 */
public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// For MAC OS enable global menu
			if (SystemUtils.IS_OS_MAC) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			}
			// For non Linux use native look an feel
			if (!SystemUtils.IS_OS_LINUX) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					//
				}
			}
			// For windows use menu font for entire UI
			if (SystemUtils.IS_OS_WINDOWS) {
				Font menuFont = UIManager.getFont("Menu.font");
				setUIFont(menuFont);				
			}
			// Launch the editor
			new Editor().launch();
		});
	}

	private static void setUIFont(Font font) {
		UIDefaults defaults = UIManager.getDefaults();
		Sets.newHashSet(
			"List.font",
			"TableHeader.font",
			"Panel.font",
			"TextArea.font",
			"ToggleButton.font",
			"ComboBox.font",
			"ScrollPane.font",
			"Spinner.font",
			"Slider.font",
			"EditorPane.font",
			"OptionPane.font",
			"ToolBar.font",
			"Tree.font",
			"TitledBorder.font",
			"Table.font",
			"Label.font",
			"TextField.font",
			"TextPane.font",
			"CheckBox.font",
			"ProgressBar.font",
			"FormattedTextField.font",
			"ColorChooser.font",
			"PasswordField.font",
			"Viewport.font",
			"TabbedPane.font",
			"RadioButton.font",
			"ToolTip.font",
			"Button.font"
		).forEach(key -> defaults.put(key, font));
	}
}
