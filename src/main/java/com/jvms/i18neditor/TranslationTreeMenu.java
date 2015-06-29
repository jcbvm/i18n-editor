package com.jvms.i18neditor;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.util.MessageBundle;

public class TranslationTreeMenu extends JPopupMenu {
	private static final long serialVersionUID = -4407236120087907574L;
	
	private final Editor editor;
	private final TranslationTree tree;
	
	public TranslationTreeMenu(Editor editor, TranslationTree tree) {
		super();
		this.editor = editor;
		this.tree = tree;
		setup();
	}
	
	private void setup() {
		JMenuItem addMenuItem = new JMenuItem(MessageBundle.get("menu.translation.add.title"));
		addMenuItem.setAccelerator(KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		addMenuItem.addActionListener(new AddMenuItemListener());
		
     	JMenuItem expandAll = new JMenuItem(MessageBundle.get("menu.tree.expand.title"));
     	expandAll.addActionListener(new ExpandAllMenuItemListener());
     	
     	JMenuItem collapseAll = new JMenuItem(MessageBundle.get("menu.tree.collapse.title"));
     	collapseAll.addActionListener(new CollapseAllMenuItemListener());
     	
		add(addMenuItem);
		addSeparator();
		add(expandAll);
		add(collapseAll);
	}
	
	private class AddMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.showAddTranslationDialog();
		}
	}
	
	private class CollapseAllMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			tree.collapseAll();
		}
	}
	
	private class ExpandAllMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			tree.expandAll();
		}
	}
}
