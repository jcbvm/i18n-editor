package com.jvms.i18neditor;

import javax.swing.JMenuItem;

import com.jvms.i18neditor.util.MessageBundle;

class ExpandTranslationsMenuItem extends JMenuItem {
	private static final long serialVersionUID = 7316102121075733726L;

	public ExpandTranslationsMenuItem(TranslationTree tree) {
        super(MessageBundle.get("menu.view.expand.title"));
     	addActionListener(e -> tree.expandAll());
	}
}