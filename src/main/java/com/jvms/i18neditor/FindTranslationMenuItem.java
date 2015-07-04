package com.jvms.i18neditor;

import java.awt.Toolkit;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.util.MessageBundle;

class FindTranslationMenuItem extends JMenuItem {
	private static final long serialVersionUID = 5207946396515235714L;

	public FindTranslationMenuItem(Editor editor, boolean enabled) {
		super(MessageBundle.get("menu.edit.find.translation.title"), MessageBundle.getMnemonic("menu.edit.find.translation.vk"));
		setAccelerator(KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addActionListener(e -> editor.showFindTranslationDialog());
        setEnabled(enabled);
	}
}