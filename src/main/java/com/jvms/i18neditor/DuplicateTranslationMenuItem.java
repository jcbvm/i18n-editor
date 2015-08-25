package com.jvms.i18neditor;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item for duplicating a translation key.
 * 
 * @author Jacob
 */
public class DuplicateTranslationMenuItem extends JMenuItem {
	private static final long serialVersionUID = 5207946396515235714L;
	
	public DuplicateTranslationMenuItem(Editor editor, boolean enabled) {
        super(MessageBundle.get("menu.edit.duplicate.title"));
		setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
		addActionListener(e -> editor.duplicateSelectedTranslation());
		setEnabled(enabled);
	}
}