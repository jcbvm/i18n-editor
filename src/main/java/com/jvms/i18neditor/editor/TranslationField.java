package com.jvms.i18neditor.editor;

import java.awt.Insets;

import com.jvms.i18neditor.swing.JUndoableTextField;

/**
 * This class represents a text field for adding a new translation key.
 * 
 * @author Jacob
 */
public class TranslationField extends JUndoableTextField {
	private final static long serialVersionUID = -3951187528785224704L;
	
	public TranslationField() {
		super();
		setupUI();
	}
	
	public String getValue() {
		return getText().trim();
	}
	
	public void setValue(String value) {
		setText(value);
		undoManager.discardAllEdits();
	}
	
	private void setupUI() {
		setMargin(new Insets(4,4,4,4));
		setEditable(false);
	}
}
