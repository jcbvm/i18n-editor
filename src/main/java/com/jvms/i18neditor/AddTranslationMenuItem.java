package com.jvms.i18neditor;

import java.awt.Toolkit;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item for adding a new translation.
 * 
 * @author Jacob
 */
public class AddTranslationMenuItem extends JMenuItem {
	private final static long serialVersionUID = -2673278052970076105L;
	
	public AddTranslationMenuItem(Editor editor, boolean enabled) {
		super(MessageBundle.get("menu.edit.add.translation.title"));
		setAccelerator(KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addActionListener(e -> editor.showAddTranslationDialog());
        setEnabled(enabled);
	}
}