package com.jvms.i18neditor;

import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import com.jvms.i18neditor.util.TranslationKeys;

/**
 * This class represents a text field for adding a new translation key.
 * 
 * @author Jacob
 */
public class TranslationField extends JTextField {
	private static final long serialVersionUID = -3951187528785224704L;
	
	private final Editor editor;
	
	public TranslationField(Editor editor) {
		super();
		this.editor = editor;
		setup();
	}
	
	private void setup() {
		setMargin(new Insets(4,4,4,4));
		addKeyListener(new TranslationFieldKeyListener());
		setEditable(false);
	}
	
	private class TranslationFieldKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String key = getText().trim();
				if (TranslationKeys.isValid(key)) {
					editor.addTranslationKey(key);
				}
			}
		}
	}
}
