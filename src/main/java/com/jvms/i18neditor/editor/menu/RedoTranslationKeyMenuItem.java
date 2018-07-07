package com.jvms.i18neditor.editor.menu;

import javax.swing.JMenuItem;

import com.jvms.i18neditor.editor.Editor;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents the Redo in Menu
 * 
 * @author Maximiliano Diaz
 */
public class RedoTranslationKeyMenuItem extends JMenuItem implements updateStatusInterface {
	private static final long serialVersionUID = 6032182493888769724L;

	public RedoTranslationKeyMenuItem(Editor editor, boolean enabled) {
		super(MessageBundle.get("menu.edit.redo.title"));
		addActionListener(e -> editor.RedoTranslationKey());
		setEnabled(enabled);
	}

	public void updateStatus(Editor editor) {
		setEnabled(editor.canRedoTranslation());
	}
}
