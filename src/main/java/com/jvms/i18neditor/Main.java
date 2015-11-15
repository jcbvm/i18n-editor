package com.jvms.i18neditor;

import java.awt.EventQueue;

import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;

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
			if (!SystemUtils.IS_OS_LINUX) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());				
			}
		} catch (Exception e) {
			//
		}
	}
	
	private static void setupEditor() {
		Editor editor = new Editor();
		editor.launch();
	}
}
