package com.jvms.i18neditor.editor;

import com.jvms.i18neditor.swing.JTextField;

/**
 * This class represents a text field for displaying, finding and adding a translation key.
 * 
 * @author Jacob van Mourik
 */
public class TranslationKeyField extends JTextField {
	private final static long serialVersionUID = -3951187528785224704L;
	
	public TranslationKeyField() {
		super();
		setupUI();
	}
	
	public void clear() {
		setValue(null);
	}
	
	public String getValue() {
		return getText().trim();
	}
	
	public void setValue(String value) {
		setText(value);
		undoManager.discardAllEdits();
	}
	
	private void setupUI() {
		setEditable(false);
		setCaret(new TranslationKeyCaret());
	}
}
