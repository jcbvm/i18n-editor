package com.jvms.i18neditor;

import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.jvms.i18neditor.editor.Editor;

/**
 * 
 * @author Jacob van Mourik
 */
public class Main {
	private final static Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// Enable global menu on MAC OS
			if (SystemUtils.IS_OS_MAC) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			}
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				// For windows use menu font for entire UI
				if (SystemUtils.IS_OS_WINDOWS) {
					setUIFont(UIManager.getFont("Menu.font"));				
				}
			} catch (Exception e) {
				log.warn("Unable to use native look and feel");
			}
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