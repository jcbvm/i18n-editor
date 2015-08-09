package com.jvms.i18neditor;

import java.awt.EventQueue;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.UIManager;

import com.jvms.i18neditor.util.ExtendedProperties;

/**
 * The main entry class of the program.
 * 
 * @author Jacob
 */
public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			setupLookAndFeel();
			setupEditor();
		});
	}
	
	private static void setupLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			//
		}
	}
	
	private static void setupEditor() {
		ExtendedProperties settings = new ExtendedProperties();
		settings.load(Editor.SETTINGS_PATH);
		Editor editor = new Editor(settings);
		editor.setVisible(true);
		
		// Try to load previously loaded resources
		List<String> dirs = settings.getListProperty("history");
    	if (!dirs.isEmpty()) {
    		String lastDir = dirs.get(dirs.size()-1);
    		if (Files.exists(Paths.get(lastDir))) {
    			editor.importResources(lastDir);
    			return;
    		}
    	}
    	
    	editor.showImportDialog();
	}
}
