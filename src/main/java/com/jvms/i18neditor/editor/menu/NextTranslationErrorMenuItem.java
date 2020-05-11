package com.jvms.i18neditor.editor.menu;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.editor.TranslationTree;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents a menu item to navigate to the next translation error in the translation tree.
 * 
 * @author Frédéric Courchesne
 */
public class NextTranslationErrorMenuItem extends JMenuItem {
	//private final static long serialVersionUID = 7316102121075733726L;

	public NextTranslationErrorMenuItem(TranslationTree tree) {
        super(MessageBundle.get("menu.view.nexterror.title"));
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
     	addActionListener(e -> tree.gotoNextError());
	}
}