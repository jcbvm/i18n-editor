package com.jvms.i18neditor.editor.menu;

import javax.swing.JMenuItem;

import com.jvms.i18neditor.editor.Editor;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item for copying a translations key to the
 * system clipboard.
 * 
 * @author Fabian Terstegen
 *
 */
public class UndoTranslationKeyMenuItem extends JMenuItem {
	private static final long serialVersionUID = 6032182493888769724L;

	public UndoTranslationKeyMenuItem(Editor editor, boolean enabled) {
		super(MessageBundle.get("menu.edit.undo.title"));
		// setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		// Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		addActionListener(e -> editor.UndoTranslationKey());
		setEnabled(enabled);
	}

}
