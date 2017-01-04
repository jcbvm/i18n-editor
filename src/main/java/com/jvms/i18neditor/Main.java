package com.jvms.i18neditor;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;

public class Main {

	public static void main(String[] args) {
		if (SystemUtils.IS_OS_MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		// Only use native look an feel when not running Linux, Linux might cause visual issues
		if (!SystemUtils.IS_OS_LINUX) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());				
			} catch (Exception e) {
				//
			}
		}
		SwingUtilities.invokeLater(() -> {
			new Editor().launch();
		});
	}
}
