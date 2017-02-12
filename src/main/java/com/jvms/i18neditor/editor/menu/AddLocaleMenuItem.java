package com.jvms.i18neditor.editor.menu;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.editor.Editor;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item for adding a new locale.
 * 
 * @author Jacob van Mourik
 */
public class AddLocaleMenuItem extends JMenuItem {
	private final static long serialVersionUID = -5108677891532028898L;

	public AddLocaleMenuItem(Editor editor, boolean enabled) {
		super(MessageBundle.get("menu.edit.add.locale.title"));
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addActionListener(e -> editor.showAddLocaleDialog());
        setEnabled(enabled);
	}
}