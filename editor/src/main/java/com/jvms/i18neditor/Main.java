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
			try {
				// Only use native look an feel when not running Linux
				// Linux might cause visual issues
				if (!SystemUtils.IS_OS_LINUX) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());				
				}
			} catch (Exception e) {
				//
			}
			new Editor().launch();
		});
	}
}
