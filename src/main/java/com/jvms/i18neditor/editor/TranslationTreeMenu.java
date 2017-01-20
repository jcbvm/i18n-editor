package com.jvms.i18neditor.editor;

import javax.swing.JPopupMenu;

import com.jvms.i18neditor.editor.menu.AddTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.CollapseTranslationsMenuItem;
import com.jvms.i18neditor.editor.menu.ExpandTranslationsMenuItem;
import com.jvms.i18neditor.editor.menu.FindTranslationMenuItem;

/**
 * This class represents a right click menu for the translation tree.
 * 
 * @author Jacob
 */
public class TranslationTreeMenu extends JPopupMenu {
	private final static long serialVersionUID = -4407236120087907574L;
	
	public TranslationTreeMenu(Editor editor, TranslationTree tree) {
		super();
		add(new AddTranslationMenuItem(editor, true));
		add(new FindTranslationMenuItem(editor, true));
		addSeparator();
		add(new ExpandTranslationsMenuItem(tree));
		add(new CollapseTranslationsMenuItem(tree));
	}
}
