package com.jvms.i18neditor;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

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
	private JMenu translationsMenu;
	private JMenu addMenu;
	
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
	
	public void setAddingEnabled(boolean enabled) {
		addMenu.setEnabled(enabled);
	}
	
	public void setTranslationsMenuEnabled(boolean enabled) {
		translationsMenu.setEnabled(enabled);
	}
	
	private void setup() {
		int menuShotcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
     	JMenu fileMenu = new JMenu(MessageBundle.get("menu.file.title"));
     	fileMenu.setMnemonic(MessageBundle.getMnemonic("menu.file.vk"));
     	
        JMenuItem openMenuItem = new JMenuItem(MessageBundle.get("menu.file.open.title"), MessageBundle.getMnemonic("menu.file.open.vk"));
     	openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', menuShotcutKeyMask));
        openMenuItem.addActionListener(new OpenMenuItemListener());
        
        addMenu = new JMenu(MessageBundle.get("menu.file.resource.title"));
     	addMenu.setMnemonic(MessageBundle.getMnemonic("menu.file.resource.vk"));
     	addMenu.setEnabled(false);
     	
        JMenuItem addJsonResourceMenuItem = new JMenuItem(MessageBundle.get("menu.file.resource.json.title"), MessageBundle.getMnemonic("menu.file.resource.json.vk"));
        addJsonResourceMenuItem.addActionListener(new AddResourceMenuItemListener(ResourceType.JSON));
        JMenuItem addEs6ResourceMenuItem = new JMenuItem(MessageBundle.get("menu.file.resource.es6.title"), MessageBundle.getMnemonic("menu.file.resource.es6.vk"));
        addEs6ResourceMenuItem.addActionListener(new AddResourceMenuItemListener(ResourceType.ES6));
        
        addMenu.add(addJsonResourceMenuItem);
        addMenu.add(addEs6ResourceMenuItem);
        
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
        fileMenu.add(addMenu);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(reloadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
     	translationsMenu = new JMenu(MessageBundle.get("menu.translations.title"));
     	translationsMenu.setMnemonic(MessageBundle.getMnemonic("menu.translations.vk"));
     	translationsMenu.setEnabled(false);
     	
        JMenuItem addTranslationMenuItem = new JMenuItem(MessageBundle.get("menu.translations.add.title"), MessageBundle.getMnemonic("menu.translations.add.vk"));
        addTranslationMenuItem.setAccelerator(KeyStroke.getKeyStroke('T', menuShotcutKeyMask));
        addTranslationMenuItem.addActionListener(new AddTranslationMenuItemListener());
        
        translationsMenu.add(addTranslationMenuItem);
     	
     	JMenu helpMenu = new JMenu(MessageBundle.get("menu.help.title"));
     	helpMenu.setMnemonic(MessageBundle.getMnemonic("menu.help.vk"));
     	
     	JMenuItem aboutMenuItem = new JMenuItem(MessageBundle.get("menu.help.about.title", Editor.TITLE), MessageBundle.getMnemonic("menu.help.about.vk"));
     	aboutMenuItem.addActionListener(new AboutMenuItemListener());
     	helpMenu.add(aboutMenuItem);
     	
     	add(fileMenu);
     	add(translationsMenu);
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
			editor.showAddResourceDialog(type);
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
	
	private class SaveMenuItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			editor.saveResources();
		}
	}
}
