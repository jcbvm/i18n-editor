package com.jvms.i18neditor;

import javax.swing.JPopupMenu;

/**
 * This class represents a right click menu for a single node of the translation tree.
 * 
 * @author Jacob
 */
public class TranslationTreeNodeMenu extends JPopupMenu {
	private static final long serialVersionUID = -4407236120087907574L;
	
	public TranslationTreeNodeMenu(Editor editor, TranslationTreeNode node) {
		super();
		add(new AddTranslationMenuItem(editor, true));
		if (!node.isRoot()) {
			addSeparator();
			add(new RenameTranslationMenuItem(editor, true));
			add(new RemoveTranslationMenuItem(editor, true));
		}
	}
}
