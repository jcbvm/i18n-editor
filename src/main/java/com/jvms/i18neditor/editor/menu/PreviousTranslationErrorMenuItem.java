package com.jvms.i18neditor.editor.menu;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.editor.TranslationTree;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item to navigate to the previous translation error in the translation tree.
 * 
 * @author Frédéric Courchesne
 */
public class PreviousTranslationErrorMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1605393223084076564L;

	public PreviousTranslationErrorMenuItem(TranslationTree tree) {
		super(MessageBundle.get("menu.view.previouserror.title"));
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		addActionListener(e -> tree.gotoPreviousError());
	}
}