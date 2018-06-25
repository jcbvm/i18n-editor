package com.jvms.i18neditor.editor;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.jvms.i18neditor.ResourceType;
import com.jvms.i18neditor.editor.menu.AddLocaleMenuItem;
import com.jvms.i18neditor.editor.menu.AddTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.CollapseTranslationsMenuItem;
import com.jvms.i18neditor.editor.menu.CopyTranslationKeyToClipboardMenuItem;
import com.jvms.i18neditor.editor.menu.DuplicateTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.ExpandTranslationsMenuItem;
import com.jvms.i18neditor.editor.menu.FindTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.RemoveTranslationMenuItem;
import com.jvms.i18neditor.editor.menu.RenameTranslationMenuItem;
import com.jvms.i18neditor.swing.util.Dialogs;
import com.jvms.i18neditor.util.GithubRepoUtil;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents the top bar menu of the editor.
 * 
 * @author Jacob van Mourik
 */
public class EditorMenuBar extends JMenuBar {
	private final static long serialVersionUID = -101788804096708514L;
	
	private final Editor editor;
	private final TranslationTree tree;
	private JMenuItem saveMenuItem;
	private JMenuItem reloadMenuItem;
	private JMenuItem addTranslationMenuItem;
	private JMenuItem findTranslationMenuItem;
	private JMenuItem renameTranslationMenuItem;
	private JMenuItem copyTranslationKeyMenuItem;
	private JMenuItem duplicateTranslationMenuItem;
	private JMenuItem removeTranslationMenuItem;
	private JMenuItem openContainingFolderMenuItem;
	private JMenuItem projectSettingsMenuItem;
	private JMenuItem editorSettingsMenuItem;
	private JMenu openRecentMenuItem;
	private JMenu editMenu;
	private JMenu viewMenu;
	private JMenu settingsMenu;
	
	public EditorMenuBar(Editor editor, TranslationTree tree) {
		super();
		this.editor = editor;
		this.tree = tree;
		setupUI();
		setEnabled(false);
		setSaveable(false);
		setEditable(false);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		reloadMenuItem.setEnabled(enabled);
		openContainingFolderMenuItem.setEnabled(enabled);
		editMenu.setEnabled(enabled);
		viewMenu.setEnabled(enabled);
		settingsMenu.removeAll();
		if (enabled) {
			settingsMenu.add(projectSettingsMenuItem);
			settingsMenu.addSeparator();
			settingsMenu.add(editorSettingsMenuItem);
		} else {
			settingsMenu.add(editorSettingsMenuItem);
		}
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	public void setSaveable(boolean saveable) {
		saveMenuItem.setEnabled(saveable);
	}
	
	public void setEditable(boolean editable) {
		addTranslationMenuItem.setEnabled(editable);
		findTranslationMenuItem.setEnabled(editable);
	}
	
	public void setRecentItems(List<String> items) {
		openRecentMenuItem.removeAll();
     	if (items.isEmpty()) {
     		openRecentMenuItem.setEnabled(false);
     	} else {
     		openRecentMenuItem.setEnabled(true);
     		for (int i = 0; i < items.size(); i++) {
     			Integer n = i + 1;
     			JMenuItem menuItem = new JMenuItem(n + ": " + items.get(i), Character.forDigit(i, 10));
     			Path path = Paths.get(menuItem.getText().replaceFirst("[0-9]+: ",""));
     			menuItem.addActionListener(e -> editor.importProject(path, true));
     			openRecentMenuItem.add(menuItem);
     		}
     		JMenuItem clearMenuItem = new JMenuItem(MessageBundle.get("menu.file.recent.clear.title"));
     		clearMenuItem.addActionListener(e -> editor.clearHistory());
     		openRecentMenuItem.addSeparator();
     		openRecentMenuItem.add(clearMenuItem);
     	}
	}
	
	private void setupUI() {
		int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		// File menu
     	JMenu fileMenu = new JMenu(MessageBundle.get("menu.file.title"));
     	fileMenu.setMnemonic(MessageBundle.getMnemonic("menu.file.vk"));
        
     	JMenuItem createJsonMenuItem = new JMenuItem(MessageBundle.get("menu.file.project.new.json.title"));
     	createJsonMenuItem.addActionListener(e -> editor.showCreateProjectDialog(ResourceType.JSON));
     	createJsonMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, keyMask+KeyEvent.SHIFT_DOWN_MASK));
        
        JMenuItem createEs6MenuItem = new JMenuItem(MessageBundle.get("menu.file.project.new.es6.title"));
        createEs6MenuItem.addActionListener(e -> editor.showCreateProjectDialog(ResourceType.ES6));
        createEs6MenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, keyMask+KeyEvent.SHIFT_DOWN_MASK));
        
        JMenuItem createPropertiesMenuItem = new JMenuItem(MessageBundle.get("menu.file.project.new.properties.title"));
        createPropertiesMenuItem.addActionListener(e -> editor.showCreateProjectDialog(ResourceType.Properties));
        createPropertiesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, keyMask+KeyEvent.SHIFT_DOWN_MASK));
        
        JMenu createMenuItem = new JMenu(MessageBundle.get("menu.file.project.new.title"));
        createMenuItem.setMnemonic(MessageBundle.getMnemonic("menu.file.project.new.vk"));
        createMenuItem.add(createJsonMenuItem);
        createMenuItem.add(createEs6MenuItem);
        createMenuItem.add(createPropertiesMenuItem);
     	
        JMenuItem importMenuItem = new JMenuItem(MessageBundle.get("menu.file.project.import.title"));
        importMenuItem.setMnemonic(MessageBundle.getMnemonic("menu.file.project.import.vk"));
        importMenuItem.addActionListener(e -> editor.showImportProjectDialog());
        
        openContainingFolderMenuItem = new JMenuItem(MessageBundle.get("menu.file.folder.title"));
        openContainingFolderMenuItem.addActionListener(e -> editor.openProjectDirectory());
        
        openRecentMenuItem = new JMenu(MessageBundle.get("menu.file.recent.title"));
        openRecentMenuItem.setMnemonic(MessageBundle.getMnemonic("menu.file.recent.vk"));
        
        saveMenuItem = new JMenuItem(MessageBundle.get("menu.file.save.title"), MessageBundle.getMnemonic("menu.file.save.vk"));
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, keyMask));
        saveMenuItem.addActionListener(e -> editor.saveProject());
        
        reloadMenuItem = new JMenuItem(MessageBundle.get("menu.file.reload.title"), MessageBundle.getMnemonic("menu.file.reload.vk"));
        reloadMenuItem.addActionListener(e -> editor.reloadProject());
        
        JMenuItem exitMenuItem = new JMenuItem(MessageBundle.get("menu.file.exit.title"), MessageBundle.getMnemonic("menu.file.exit.vk"));
        exitMenuItem.addActionListener(e -> editor.dispatchEvent(new WindowEvent(editor, WindowEvent.WINDOW_CLOSING)));
        
        fileMenu.add(createMenuItem);
        fileMenu.add(importMenuItem);
        if (Desktop.isDesktopSupported()) {
	        fileMenu.add(openContainingFolderMenuItem);
		}
        fileMenu.add(openRecentMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(reloadMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Edit menu
     	editMenu = new JMenu(MessageBundle.get("menu.edit.title"));
     	editMenu.setMnemonic(MessageBundle.getMnemonic("menu.edit.vk"));
     	
        addTranslationMenuItem = new AddTranslationMenuItem(editor, tree, false);
        findTranslationMenuItem = new FindTranslationMenuItem(editor, false);
        removeTranslationMenuItem = new RemoveTranslationMenuItem(editor, false);
        duplicateTranslationMenuItem = new DuplicateTranslationMenuItem(editor, true);
        renameTranslationMenuItem = new RenameTranslationMenuItem(editor, false);
        copyTranslationKeyMenuItem = new CopyTranslationKeyToClipboardMenuItem(editor, false);
        
        editMenu.add(new AddLocaleMenuItem(editor, true));
        editMenu.addSeparator();
        editMenu.add(addTranslationMenuItem);
        editMenu.add(findTranslationMenuItem);
        editMenu.addSeparator();
        editMenu.add(renameTranslationMenuItem);
        editMenu.add(duplicateTranslationMenuItem);
        editMenu.add(removeTranslationMenuItem);
        editMenu.add(copyTranslationKeyMenuItem);
        
        // View menu
        viewMenu = new JMenu(MessageBundle.get("menu.view.title"));
        viewMenu.setMnemonic(MessageBundle.getMnemonic("menu.view.vk"));
        viewMenu.add(new ExpandTranslationsMenuItem(tree));
        viewMenu.add(new CollapseTranslationsMenuItem(tree));
        
        // Settings menu
        settingsMenu = new JMenu(MessageBundle.get("menu.settings.title"));
        settingsMenu.setMnemonic(MessageBundle.getMnemonic("menu.settings.vk"));
        
        editorSettingsMenuItem = new JMenuItem(MessageBundle.get("menu.settings.preferences.editor.title"));
        editorSettingsMenuItem.addActionListener(e -> {
        	Dialogs.showComponentDialog(editor, 
        			MessageBundle.get("dialogs.preferences.editor.title"), 
        			new EditorSettingsPane(editor));
        });
        
        projectSettingsMenuItem = new JMenuItem(MessageBundle.get("menu.settings.preferences.project.title"));
        projectSettingsMenuItem.addActionListener(e -> {
        	Dialogs.showComponentDialog(editor, 
        			MessageBundle.get("dialogs.preferences.project.title"), 
        			new EditorProjectSettingsPane(editor));
        });
        
        settingsMenu.add(editorSettingsMenuItem);
        
        // Help menu
     	JMenu helpMenu = new JMenu(MessageBundle.get("menu.help.title"));
     	helpMenu.setMnemonic(MessageBundle.getMnemonic("menu.help.vk"));
     	
     	JMenuItem versionMenuItem = new JMenuItem(MessageBundle.get("menu.help.version.title"));
     	versionMenuItem.addActionListener(e -> editor.showVersionDialog(false));
     	
     	JMenuItem homeMenuItem = new JMenuItem(MessageBundle.get("menu.help.home.title", Editor.TITLE));
     	homeMenuItem.addActionListener(e -> {
     		try {
				Desktop.getDesktop().browse(URI.create(GithubRepoUtil.getURL(Editor.GITHUB_USER, Editor.GITHUB_PROJECT)));
			} catch (IOException e1) {
				//
			}
     	});
     	
     	JMenuItem aboutMenuItem = new JMenuItem(MessageBundle.get("menu.help.about.title", Editor.TITLE));
     	aboutMenuItem.addActionListener(e -> editor.showAboutDialog());
     	
     	helpMenu.add(versionMenuItem);
     	helpMenu.addSeparator();
     	helpMenu.add(homeMenuItem);
     	helpMenu.add(aboutMenuItem);
     	
     	add(fileMenu);
     	add(editMenu);
     	add(viewMenu);
     	add(settingsMenu);
     	add(helpMenu);
     	
     	tree.addTreeSelectionListener(e -> {
     		TranslationTreeNode node = tree.getSelectionNode();
     		boolean enabled = node != null && !node.isRoot();
			renameTranslationMenuItem.setEnabled(enabled);
			copyTranslationKeyMenuItem.setEnabled(enabled);
			duplicateTranslationMenuItem.setEnabled(enabled);
			removeTranslationMenuItem.setEnabled(enabled);
     	});
	}
}
