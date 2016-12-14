package com.jvms.i18neditor.util;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.google.common.base.Strings;
import com.jvms.i18neditor.swing.JUndoableTextField;
import com.jvms.i18neditor.swing.RequestInitialFocusListener;

/**
 * This class provides utility functions for dialogs using {@link JOptionPane}.
 * 
 * @author Jacob
 */
public class Dialogs {
	
	public static void showErrorDialog(Component parent, String title, String message) {
		showMessageDialog(parent, title, message, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showWarningDialog(Component parent, String title, String message) {
		showMessageDialog(parent, title, message, JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showMessageDialog(Component parent, String title, String message) {
		showMessageDialog(parent, title, message, JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void showMessageDialog(Component parent, String title, Component component) {
		showMessageDialog(parent, title, component, JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void showMessageDialog(Component parent, String title, String message, int type) {
		JOptionPane.showMessageDialog(parent, message, title, type);
	}
	
	public static void showMessageDialog(Component parent, String title, Component component, int type) {
		JOptionPane.showMessageDialog(parent, component, title, type);
	}
	
	public static boolean showConfirmDialog(Component parent, String title, String message) {
		return showConfirmDialog(parent, title, message, JOptionPane.WARNING_MESSAGE);
	}
	
	public static boolean showConfirmDialog(Component parent, String title, String message, int type) {
		return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, type) == 0 ? true : false;
	}
	
	public static String showInputDialog(Component parent, String title, String label, String initialText, int type) {
		JPanel panel = new JPanel(new BorderLayout(0, 4));
		if (!Strings.isNullOrEmpty(label)) {
			panel.add(new JLabel(label), BorderLayout.NORTH);
		}
		JUndoableTextField field =  new JUndoableTextField(initialText);
		field.addAncestorListener(new RequestInitialFocusListener());
		panel.add(field);
		int result = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION, type);
		return result == JOptionPane.OK_OPTION ? field.getText() : null;
	}
}
