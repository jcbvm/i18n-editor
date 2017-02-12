package com.jvms.i18neditor.editor;

import javax.swing.JPopupMenu;

import com.jvms.i18neditor.editor.menu.AddLocaleMenuItem;

/**
 * This class represents a right click menu for the resource pane.
 * 
 * @author Jacob van Mourik
 */
public class ResourcesPaneMenu extends JPopupMenu {
	private final static long serialVersionUID = 2259323824622576156L;

	public ResourcesPaneMenu(Editor editor) {
		super();
		add(new AddLocaleMenuItem(editor, true));
	}
}
