package com.jvms.i18neditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class TranslationTreeMenu extends JPopupMenu {
	private static final long serialVersionUID = -4407236120087907574L;
	
	private final Editor editor;
	
	public TranslationTreeMenu(Editor editor) {
		super();
		this.editor = editor;
		setup();
	}
	
	private void setup() {
		JMenuItem addMenuItem = new JMenuItem("Add Translation...");
		addMenuItem.addActionListener(new AddMenuItemListener());
		add(addMenuItem);
	}
	
	private class AddMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.showAddTranslationDialog();
		}
	}
}
