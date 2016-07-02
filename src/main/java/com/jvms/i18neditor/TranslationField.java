package com.jvms.i18neditor;

import java.awt.Insets;

import javax.swing.JTextField;

/**
 * This class represents a text field for adding a new translation key.
 * 
 * @author Jacob
 */
public class TranslationField extends JTextField {
	private final static long serialVersionUID = -3951187528785224704L;
	
	public TranslationField() {
		super();
		setupUI();
	}
	
	public String getValue() {
		return getText().trim();
	}
	
	private void setupUI() {
		setMargin(new Insets(4,4,4,4));
		setEditable(false);
	}
}
