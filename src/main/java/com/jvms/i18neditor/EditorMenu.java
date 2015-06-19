package com.jvms.i18neditor;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.Resource.ResourceType;
import com.jvms.i18neditor.util.MessageBundle;

public class EditorMenu extends JMenuBar {
	private static final long serialVersionUID = -101788804096708514L;
	
	private final Editor editor;
	
	private JMenuItem saveMenuItem;
	private JMenuItem reloadMenuItem;
	private JMenuItem addTranslationMenuItem;
	private JMenu editMenu;
	private JMenu openRecentMenuItem;
	
	public EditorMenu(Editor editor) {
		super();
		this.editor = editor;
		setup();
	}
	
	public void setSaveEnabled(boolean enabled) {
		saveMenuItem.setEnabled(enabled);
	}
	
	public void setReloadEnabled(boolean enabled) {
		reloadMenuItem.setEnabled(enabled);
	}
	
	public void setEditEnabled(boolean enabled) {
		editMenu.setEnabled(enabled);
	}
	
	public void setAddTranslationEnabled(boolean enabled) {
		addTranslationMenuItem.setEnabled(enabled);
	}
	
	public void setRecentItems(List<String> items) {
		openRecentMenuItem.removeAll();
     	if (items.isEmpty()) {
     		openRecentMenuItem.setEnabled(false);
     	} else {
     		openRecentMenuItem.setEnabled(true);
     		for (String item : items) {
     			JMenuItem menuItem = new JMenuItem(item);
     			menuItem.addActionListener(new OpenRecentMenuItemListener());
     			openRecentMenuItem.add(menuItem);
     		}
     	}
	}
	
	private void setup() {
		int menuShotcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
     	JMenu fileMenu = new JMenu(MessageBundle.get("menu.file.title"));
     	fileMenu.setMnemonic(MessageBundle.getMnemonic("menu.file.vk"));
     	
        JMenuItem openMenuItem = new JMenuItem(MessageBundle.get("menu.file.open.title"), MessageBundle.getMnemonic("menu.file.open.vk"));
     	openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', menuShotcutKeyMask));
        openMenuItem.addActionListener(new OpenMenuItemListener());
        
        openRecentMenuItem = new JMenu(MessageBundle.get("menu.file.recent.title"));
        openRecentMenuItem.setMnemonic(MessageBundle.getMnemonic("menu.file.recent.vk"));
        
        saveMenuItem = new JMenuItem(MessageBundle.get("menu.file.save.title"), MessageBundle.getMnemonic("menu.file.save.vk"));
        saveMenuItem.setEnabled(false);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', menuShotcutKeyMask));
        saveMenuItem.addActionListener(new SaveMenuItemListener());
        
        reloadMenuItem = new JMenuItem(MessageBundle.get("menu.file.reload.title"), MessageBundle.getMnemonic("menu.file.reload.vk"));
        reloadMenuItem.setEnabled(false);
        reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        reloadMenuItem.addActionListener(new ReloadMenuItemListener());
        
        JMenuItem exitMenuItem = new JMenuItem(MessageBundle.get("menu.file.exit.title"), MessageBundle.getMnemonic("menu.file.exit.vk"));
        exitMenuItem.addActionListener(new ExitMenuItemListener());
        
        fileMenu.add(openMenuItem);
        fileMenu.add(openRecentMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(reloadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
     	editMenu = new JMenu(MessageBundle.get("menu.edit.title"));
     	editMenu.setMnemonic(MessageBundle.getMnemonic("menu.edit.vk"));
     	editMenu.setEnabled(false);
     	
     	JMenu addLocaleMenuItem = new JMenu(MessageBundle.get("menu.edit.add.locale.title"));
      	addLocaleMenuItem.setMnemonic(MessageBundle.getMnemonic("menu.edit.add.locale.vk"));
      	
      	JMenuItem addJsonResourceMenuItem = new JMenuItem(MessageBundle.get("menu.edit.add.locale.json.title"), MessageBundle.getMnemonic("menu.edit.add.locale.json.vk"));
        addJsonResourceMenuItem.addActionListener(new AddResourceMenuItemListener(ResourceType.JSON));
        JMenuItem addEs6ResourceMenuItem = new JMenuItem(MessageBundle.get("menu.edit.add.locale.es6.title"), MessageBundle.getMnemonic("menu.edit.add.locale.es6.vk"));
        addEs6ResourceMenuItem.addActionListener(new AddResourceMenuItemListener(ResourceType.ES6));
         
      	addLocaleMenuItem.add(addJsonResourceMenuItem);
        addLocaleMenuItem.add(addEs6ResourceMenuItem);
     	
        addTranslationMenuItem = new JMenuItem(MessageBundle.get("menu.edit.add.translation.title"), MessageBundle.getMnemonic("menu.edit.add.translation.vk"));
        addTranslationMenuItem.setAccelerator(KeyStroke.getKeyStroke('T', menuShotcutKeyMask));
        addTranslationMenuItem.addActionListener(new AddTranslationMenuItemListener());
        addTranslationMenuItem.setEnabled(false);
        
        editMenu.add(addLocaleMenuItem);
        editMenu.add(addTranslationMenuItem);
     	
     	JMenu helpMenu = new JMenu(MessageBundle.get("menu.help.title"));
     	helpMenu.setMnemonic(MessageBundle.getMnemonic("menu.help.vk"));
     	
     	JMenuItem aboutMenuItem = new JMenuItem(MessageBundle.get("menu.help.about.title", Editor.TITLE), MessageBundle.getMnemonic("menu.help.about.vk"));
     	aboutMenuItem.addActionListener(new AboutMenuItemListener());
     	helpMenu.add(aboutMenuItem);
     	
     	add(fileMenu);
     	add(editMenu);
     	add(helpMenu);
	}
	
	private class ReloadMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.reloadResources();
		}
	}
	
	private class AddResourceMenuItemListener implements ActionListener {
		private final ResourceType type;
		
		public AddResourceMenuItemListener(ResourceType type) {
			this.type = type;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.showAddLocaleDialog(type);
		}
	}
	
	private class AddTranslationMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.showAddTranslationDialog();
		}
	}
	
	private class AboutMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String content = 
					"<html>" +
						"<body style=\"text-align:center;width:200px;\"><br>" +
							"<span style=\"font-weight:bold;font-size:1.2em;\">" + Editor.TITLE + "</span><br>" +
							"v" + Editor.VERSION + "<br><br>" +
							"(c) Copyright 2015<br>" +
							"Jacob van Mourik<br>" +
							"MIT Licensed<br><br>" +
						"</body>" +
					"</html>";
			JOptionPane.showMessageDialog(editor, content, MessageBundle.get("dialogs.about.title", Editor.TITLE), JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	private class ExitMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.dispatchEvent(new WindowEvent(editor, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	private class OpenMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.showImportDialog();
		}
	}
	
	private class OpenRecentMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem target = (JMenuItem) e.getSource();
			editor.importResources(target.getText());
		}
	}
	
	private class SaveMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.saveResources();
		}
	}
}
