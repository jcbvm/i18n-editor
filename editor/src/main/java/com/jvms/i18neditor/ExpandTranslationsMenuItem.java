package com.jvms.i18neditor;

import javax.swing.JMenuItem;

import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item for expanding all keys in of the translation tree.
 * 
 * @author Jacob
 */
public class ExpandTranslationsMenuItem extends JMenuItem {
	private final static long serialVersionUID = 7316102121075733726L;

	public ExpandTranslationsMenuItem(TranslationTree tree) {
        super(MessageBundle.get("menu.view.expand.title"));
     	addActionListener(e -> tree.expandAll());
	}
}