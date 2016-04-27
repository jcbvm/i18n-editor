package com.jvms.i18neditor;

import java.awt.Toolkit;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item for duplicating a translation key.
 * 
 * @author Jacob
 */
public class DuplicateTranslationMenuItem extends JMenuItem {
	private final static long serialVersionUID = 5207946396515235714L;
	
	public DuplicateTranslationMenuItem(Editor editor, boolean enabled) {
        super(MessageBundle.get("menu.edit.duplicate.title"));
		setAccelerator(KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		addActionListener(e -> editor.duplicateSelectedTranslation());
		setEnabled(enabled);
	}
}