package com.jvms.i18neditor.editor;

import javax.swing.JPopupMenu;

import com.jvms.i18neditor.editor.menu.AddTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.DuplicateTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.RemoveTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.RenameTranslationMenuItem;

/**
 * This class represents a right click menu for a single node of the translation tree.
 * 
 * @author Jacob van Mourik
 */
public class TranslationTreeNodeMenu extends JPopupMenu {
	private final static long serialVersionUID = -8450484152294368841L;
	
	public TranslationTreeNodeMenu(Editor editor, TranslationTreeNode node) {
		super();
		add(new AddTranslationMenuItem(editor, node, true));
		if (!node.isRoot()) {
			addSeparator();
			add(new RenameTranslationMenuItem(editor, true));
			add(new DuplicateTranslationMenuItem(editor, true));
			add(new RemoveTranslationMenuItem(editor, true));
		}
	}
}
