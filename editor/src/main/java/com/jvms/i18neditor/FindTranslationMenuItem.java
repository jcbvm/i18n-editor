package com.jvms.i18neditor;

import java.awt.Toolkit;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item for searching a translation key.
 * 
 * @author Jacob
 */
public class FindTranslationMenuItem extends JMenuItem {
	private final static long serialVersionUID = 5207946396515235714L;

	public FindTranslationMenuItem(Editor editor, boolean enabled) {
		super(MessageBundle.get("menu.edit.find.translation.title"), MessageBundle.getMnemonic("menu.edit.find.translation.vk"));
		setAccelerator(KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addActionListener(e -> editor.showFindTranslationDialog());
        setEnabled(enabled);
	}
}