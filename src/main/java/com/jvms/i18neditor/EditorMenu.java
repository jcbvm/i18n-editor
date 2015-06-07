package com.jvms.i18neditor;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.jvms.i18neditor.Resource.ResourceType;

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
		
     	JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem openMenuItem = new JMenuItem("Open Resources...", KeyEvent.VK_O);
     	openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', menuShotcutKeyMask));
        openMenuItem.addActionListener(new OpenMenuItemListener());
        
        addMenu = new JMenu("Add Resource");
     	addMenu.setMnemonic(KeyEvent.VK_A);
     	addMenu.setEnabled(false);
        JMenuItem addJsonResourceMenuItem = new JMenuItem("JSON Format...", KeyEvent.VK_J);
        addJsonResourceMenuItem.addActionListener(new AddResourceMenuItemListener(ResourceType.JSON));
        JMenuItem addEs6ResourceMenuItem = new JMenuItem("ES6 Format...", KeyEvent.VK_E);
        addEs6ResourceMenuItem.addActionListener(new AddResourceMenuItemListener(ResourceType.ES6));
        addMenu.add(addJsonResourceMenuItem);
        addMenu.add(addEs6ResourceMenuItem);
        
        saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.setEnabled(false);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', menuShotcutKeyMask));
        saveMenuItem.addActionListener(new SaveMenuItemListener());
        
        reloadMenuItem = new JMenuItem("Reload", KeyEvent.VK_F5);
        reloadMenuItem.setEnabled(false);
        reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        reloadMenuItem.addActionListener(new ReloadMenuItemListener());
        
        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(new ExitMenuItemListener());
        
        fileMenu.add(openMenuItem);
        fileMenu.add(addMenu);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(reloadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
     	translationsMenu = new JMenu("Translations");
     	translationsMenu.setEnabled(false);
     	translationsMenu.setMnemonic(KeyEvent.VK_R);
     	
        JMenuItem addTranslationMenuItem = new JMenuItem("Add Translation...", KeyEvent.VK_T);
        addTranslationMenuItem.setAccelerator(KeyStroke.getKeyStroke('T', menuShotcutKeyMask));
        addTranslationMenuItem.addActionListener(new AddTranslationMenuItemListener());
        
        translationsMenu.add(addTranslationMenuItem);
     	
     	JMenu helpMenu = new JMenu("Help");
     	helpMenu.setMnemonic(KeyEvent.VK_H);
     	
     	JMenuItem aboutMenuItem = new JMenuItem("About " + Editor.NAME, KeyEvent.VK_A);
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
			JOptionPane.showMessageDialog(editor, "<html><body style=\"text-align:center;width:200px;\"><strong>" + Editor.NAME + "</strong><br><br>(c) Copyright 2015<br>Jacob van Mourik<br>MIT Licensed<br><br></body></html>", "About", JOptionPane.PLAIN_MESSAGE);
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
