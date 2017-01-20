package com.jvms.i18neditor.swing.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.google.common.base.Strings;
import com.jvms.i18neditor.swing.JHtmlPane;
import com.jvms.i18neditor.swing.JUndoableTextField;
import com.jvms.i18neditor.swing.event.RequestInitialFocusListener;
import com.jvms.i18neditor.swing.text.BlinkCaret;

/**
 * This class provides utility functions for dialogs using {@link JOptionPane}.
 * 
 * @author Jacob
 */
public final class Dialogs {
	
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
	
	public static void showHtmlDialog(Component parent, String title, String body) {
		Font font = parent.getFont();
		JHtmlPane pane = new JHtmlPane(parent, "<html><body style=\"font-family:" + font.getFamily() + ";font-size:" + font.getSize() + "pt;text-align:center;min-width:200px;\">" + body + "</body></html>");
		showMessageDialog(parent, title, pane);	
	}
	
	public static boolean showConfirmDialog(Component parent, String title, String message) {
		return showConfirmDialog(parent, title, message, JOptionPane.WARNING_MESSAGE);
	}
	
	public static boolean showConfirmDialog(Component parent, String title, String message, int type) {
		return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, type) == 0 ? true : false;
	}
	
	public static String showInputDialog(Component parent, String title, String label, int type) {
		return showInputDialog(parent, title, label, type, null, false);
	}
	
	public static String showInputDialog(Component parent, String title, String label, int type, String initialText, boolean selectAll) {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		if (!Strings.isNullOrEmpty(label)) {
			panel.add(new JLabel(label));
		}
		JUndoableTextField field =  new JUndoableTextField(initialText, 25);
		field.addAncestorListener(new RequestInitialFocusListener());
		field.setCaret(new BlinkCaret());
		if (initialText != null) {
			field.setCaretPosition(initialText.length());			
		}
		if (selectAll) {
			field.selectAll();
		}
		panel.add(field);
		int result = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION, type);
		return result == JOptionPane.OK_OPTION ? field.getText() : null;
	}
}
