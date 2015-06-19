package com.jvms.i18neditor;

import java.awt.EventQueue;

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
    	String dir = SettingsBundle.get("resourcesDir");
    	if (dir == null) {
    		editor.showImportDialog();
    	} else {
    		editor.importResources(dir);
    	}
	}
}
