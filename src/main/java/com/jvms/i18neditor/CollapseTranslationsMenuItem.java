package com.jvms.i18neditor;

import javax.swing.JMenuItem;

import com.jvms.i18neditor.util.MessageBundle;

class CollapseTranslationsMenuItem extends JMenuItem {
	private static final long serialVersionUID = 7885728865417192564L;

	public CollapseTranslationsMenuItem(TranslationTree tree) {
        super(MessageBundle.get("menu.view.collapse.title"));
     	addActionListener(e -> tree.collapseAll());
	}
}