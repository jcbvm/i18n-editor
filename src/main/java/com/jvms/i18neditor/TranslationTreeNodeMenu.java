package com.jvms.i18neditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

public class TranslationTreeNodeMenu extends JPopupMenu {
	private static final long serialVersionUID = -4407236120087907574L;
	
	private final Editor editor;
	private final TranslationTreeNode node;
	
	public TranslationTreeNodeMenu(Editor editor, TranslationTreeNode node) {
		super();
		this.editor = editor;
		this.node = node;
		setup();
	}
	
	private void setup() {
		JMenuItem addMenuItem = new JMenuItem("Add Translation...");
		addMenuItem.addActionListener(new AddMenuItemListener());
		add(addMenuItem);
		
		if (!node.isRoot()) {
			addSeparator();
			
			JMenuItem renameMenuItem = new JMenuItem("Rename...");
			renameMenuItem.setAccelerator(KeyStroke.getKeyStroke("F2"));
			renameMenuItem.addActionListener(new RenameMenuItemListener());
			add(renameMenuItem);
			
			JMenuItem deleteMenuItem = new JMenuItem("Delete");
			deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
			deleteMenuItem.addActionListener(new DeleteMenuItemListener());
			add(deleteMenuItem);
		}
	}
	
	private class AddMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.showAddTranslationDialog();
		}
	}
	
	private class RenameMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.showRenameDialog(node.getKey());
		}
	}
	
	private class DeleteMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.removeTranslationKey(node.getKey());
		}
	}
}
