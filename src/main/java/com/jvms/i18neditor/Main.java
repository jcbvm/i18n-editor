package com.jvms.i18neditor;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.UIManager;

import com.jvms.i18neditor.util.SettingsBundle;

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
		Editor editor = new Editor();
		editor.setVisible(true);
		List<String> dirs = SettingsBundle.getAsList("history");
    	if (dirs.isEmpty()) {
    		editor.showImportDialog();
    	} else {
    		String lastDir = dirs.get(dirs.size()-1);
    		editor.importResources(lastDir);
    	}
	}
}
