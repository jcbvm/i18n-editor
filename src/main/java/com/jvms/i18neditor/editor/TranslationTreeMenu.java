package com.jvms.i18neditor.editor;

import javax.swing.JPopupMenu;

import com.jvms.i18neditor.editor.menu.AddTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.CollapseTranslationsMenuItem;
import com.jvms.i18neditor.editor.menu.ExpandTranslationsMenuItem;
import com.jvms.i18neditor.editor.menu.FindTranslationMenuItem;

/**
 * This class represents a right click menu for the translation tree.
 * 
 * @author Jacob van Mourik
 */
public class TranslationTreeMenu extends JPopupMenu {
	private final static long serialVersionUID = -8450484152294368841L;
	
	public TranslationTreeMenu(Editor editor, TranslationTree tree) {
		super();
		add(new AddTranslationMenuItem(editor, tree, true));
		add(new FindTranslationMenuItem(editor, true));
		addSeparator();
		add(new ExpandTranslationsMenuItem(tree));
		add(new CollapseTranslationsMenuItem(tree));
	}
}
