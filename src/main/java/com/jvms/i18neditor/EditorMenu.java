package com.jvms.i18neditor;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.jvms.i18neditor.Resource.ResourceType;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents the top bar menu of the editor.
 * 
 * @author Jacob
 */
public class EditorMenu extends JMenuBar {
	private final static long serialVersionUID = -101788804096708514L;
	private final Editor editor;
	private final TranslationTree tree;
	private JMenuItem saveMenuItem;
	private JMenuItem reloadMenuItem;
	private JMenuItem addTranslationMenuItem;
	private JMenuItem findTranslationMenuItem;
	private JMenuItem renameTranslationMenuItem;
	private JMenuItem duplicateTranslationMenuItem;
	private JMenuItem removeTranslationMenuItem;
	private JMenuItem openContainingFolderMenuItem;
	private JMenu editMenu;
	private JMenu viewMenu;
	private JMenu openRecentMenuItem;
	
	public EditorMenu(Editor editor, TranslationTree tree) {
		super();
		this.editor = editor;
		this.tree = tree;
		setupUI();
	}
	
	public void setSaveable(boolean saveable) {
		saveMenuItem.setEnabled(saveable);
		updateComponentTreeUI();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		reloadMenuItem.setEnabled(enabled);
		openContainingFolderMenuItem.setEnabled(enabled);
		editMenu.setEnabled(enabled);
		viewMenu.setEnabled(enabled);
		updateComponentTreeUI();
	}
	
	public void setEditable(boolean editable) {
		addTranslationMenuItem.setEnabled(editable);
		findTranslationMenuItem.setEnabled(editable);
		updateComponentTreeUI();
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
     			menuItem.addActionListener(e -> editor.importResources(path));
     			menuItem.setAccelerator(KeyStroke.getKeyStroke(n.toString().charAt(0), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
     			openRecentMenuItem.add(menuItem);
     		}
     	}
	}
	
	private void setupUI() {
		// File menu
     	JMenu fileMenu = new JMenu(MessageBundle.get("menu.file.title"));
     	fileMenu.setMnemonic(MessageBundle.getMnemonic("menu.file.vk"));
     	
        JMenuItem openMenuItem = new JMenuItem(MessageBundle.get("menu.file.open.title"), MessageBundle.getMnemonic("menu.file.open.vk"));
     	openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openMenuItem.addActionListener(e -> editor.showImportDialog());
        
        openContainingFolderMenuItem = new JMenuItem(MessageBundle.get("menu.file.folder.title"));
        openContainingFolderMenuItem.addActionListener(e -> {
        	try {
    			Desktop.getDesktop().open(editor.getResourcesPath().toFile());
    		} catch (IOException ex) {
    			ex.printStackTrace();
    		}
        });
        
        openRecentMenuItem = new JMenu(MessageBundle.get("menu.file.recent.title"));
        openRecentMenuItem.setMnemonic(MessageBundle.getMnemonic("menu.file.recent.vk"));
        
        saveMenuItem = new JMenuItem(MessageBundle.get("menu.file.save.title"), MessageBundle.getMnemonic("menu.file.save.vk"));
        saveMenuItem.setEnabled(false);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveMenuItem.addActionListener(e -> editor.saveResources());
        
        reloadMenuItem = new JMenuItem(MessageBundle.get("menu.file.reload.title"), MessageBundle.getMnemonic("menu.file.reload.vk"));
        reloadMenuItem.setEnabled(false);
        reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        reloadMenuItem.addActionListener(e -> editor.reloadResources());
        
        JMenuItem exitMenuItem = new JMenuItem(MessageBundle.get("menu.file.exit.title"), MessageBundle.getMnemonic("menu.file.exit.vk"));
        exitMenuItem.addActionListener(e -> editor.dispatchEvent(new WindowEvent(editor, WindowEvent.WINDOW_CLOSING)));
        
        fileMenu.add(openMenuItem);
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
     	editMenu.setEnabled(false);
     	
      	JMenuItem addJsonResourceMenuItem = new JMenuItem(MessageBundle.get("menu.edit.add.locale.json.title"));
        addJsonResourceMenuItem.addActionListener(e -> editor.showAddLocaleDialog(ResourceType.JSON));
        addJsonResourceMenuItem.setAccelerator(KeyStroke.getKeyStroke('J', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        JMenuItem addEs6ResourceMenuItem = new JMenuItem(MessageBundle.get("menu.edit.add.locale.es6.title"));
        addEs6ResourceMenuItem.addActionListener(e -> editor.showAddLocaleDialog(ResourceType.ES6));
        addEs6ResourceMenuItem.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        JMenu addLocaleMenuItem = new JMenu(MessageBundle.get("menu.edit.add.locale.title"));
      	addLocaleMenuItem.add(addJsonResourceMenuItem);
        addLocaleMenuItem.add(addEs6ResourceMenuItem);
     	
        addTranslationMenuItem = new AddTranslationMenuItem(editor, false);
        findTranslationMenuItem = new FindTranslationMenuItem(editor, false);
        removeTranslationMenuItem = new RemoveTranslationMenuItem(editor, false);
        duplicateTranslationMenuItem = new DuplicateTranslationMenuItem(editor, true);
        renameTranslationMenuItem = new RenameTranslationMenuItem(editor, false);
        
        editMenu.add(addLocaleMenuItem);
        editMenu.addSeparator();
        editMenu.add(addTranslationMenuItem);
        editMenu.add(findTranslationMenuItem);
        editMenu.addSeparator();
        editMenu.add(renameTranslationMenuItem);
        editMenu.add(duplicateTranslationMenuItem);
        editMenu.add(removeTranslationMenuItem);
        
        // View menu
        viewMenu = new JMenu(MessageBundle.get("menu.view.title"));
        viewMenu.setMnemonic(MessageBundle.getMnemonic("menu.view.vk"));
        viewMenu.setEnabled(false);
        viewMenu.add(new ExpandTranslationsMenuItem(tree));
        viewMenu.add(new CollapseTranslationsMenuItem(tree));
        
        // Settings menu
        JMenu settingsMenu = new JMenu(MessageBundle.get("menu.settings.title"));
        settingsMenu.setMnemonic(MessageBundle.getMnemonic("menu.settings.vk"));
        
        JCheckBoxMenuItem minifyMenuItem = new JCheckBoxMenuItem(MessageBundle.get("menu.settings.minify.title"), editor.isMinifyOutput());
        minifyMenuItem.addActionListener(e -> editor.setMinifyOutput(minifyMenuItem.isSelected()));
        
        settingsMenu.add(minifyMenuItem);
        
        // Help menu
     	JMenu helpMenu = new JMenu(MessageBundle.get("menu.help.title"));
     	helpMenu.setMnemonic(MessageBundle.getMnemonic("menu.help.vk"));
     	
     	JMenuItem versionMenuItem = new JMenuItem(MessageBundle.get("menu.help.version.title"));
     	versionMenuItem.addActionListener(e -> editor.checkForNewVersion(true));
     	
     	JMenuItem aboutMenuItem = new JMenuItem(MessageBundle.get("menu.help.about.title", Editor.TITLE));
     	aboutMenuItem.addActionListener(e -> editor.showAboutDialog());
     	
     	helpMenu.add(versionMenuItem);
     	helpMenu.add(aboutMenuItem);
     	
     	add(fileMenu);
     	add(editMenu);
     	add(viewMenu);
     	add(settingsMenu);
     	add(helpMenu);
     	
     	tree.addTreeSelectionListener(e -> {
     		TranslationTreeNode node = tree.getSelectedNode();
     		boolean enabled = node != null && !node.isRoot();
			renameTranslationMenuItem.setEnabled(enabled);
			duplicateTranslationMenuItem.setEnabled(enabled);
			removeTranslationMenuItem.setEnabled(enabled);
			updateComponentTreeUI();
     	});
	}
	
	/**
	 * This method is needed for MacOS to (force) update the global menu bar
	 * To be used when menu items change, like getting enabled/disabled
	 */
	private void updateComponentTreeUI() {
		SwingUtilities.updateComponentTreeUI(this);
	}
}
