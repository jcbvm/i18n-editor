package com.jvms.i18neditor.editor;

import javax.swing.JPopupMenu;

import com.jvms.i18neditor.editor.menu.AddTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.DuplicateTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.RemoveTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.RenameTranslationMenuItem;
import com.jvms.i18neditor.editor.tree.TranslationTreeNode;

/**
 * This class represents a right click menu for a single node of the translation tree.
 * 
 * @author Jacob
 */
public class TranslationTreeNodeMenu extends JPopupMenu {
	private final static long serialVersionUID = -4407236120087907574L;
	
	public TranslationTreeNodeMenu(Editor editor, TranslationTreeNode node) {
		super();
		add(new AddTranslationMenuItem(editor, true));
		if (!node.isRoot()) {
			addSeparator();
			add(new RenameTranslationMenuItem(editor, true));
			add(new DuplicateTranslationMenuItem(editor, true));
			add(new RemoveTranslationMenuItem(editor, true));
		}
	}
}
