package com.jvms.i18neditor;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.jvms.i18neditor.Resource.ResourceType;
import com.jvms.i18neditor.util.MessageBundle;

public class EditorMenu extends JMenuBar {
	private static final long serialVersionUID = -101788804096708514L;
	
	private final Editor editor;
	private final TranslationTree tree;
	
	private JMenuItem saveMenuItem;
	private JMenuItem reloadMenuItem;
	private JMenuItem addTranslationMenuItem;
	private JMenuItem findTranslationMenuItem;
	private JMenuItem renameTranslationMenuItem;
	private JMenuItem removeTranslationMenuItem;
	private JMenu editMenu;
	private JMenu viewMenu;
	private JMenu openRecentMenuItem;
	
	public EditorMenu(Editor editor, TranslationTree tree) {
		super();
		this.editor = editor;
		this.tree = tree;
		setup();
	}
	
	public void setSaveable(boolean saveable) {
		saveMenuItem.setEnabled(saveable);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		reloadMenuItem.setEnabled(enabled);
		editMenu.setEnabled(enabled);
		viewMenu.setEnabled(enabled);
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
     			JMenuItem menuItem = new JMenuItem(n + ": " + items.get(i));
     			menuItem.addActionListener(e -> editor.importResources(menuItem.getText().replaceFirst("[0-9]+: ","")));
     			menuItem.setAccelerator(KeyStroke.getKeyStroke(n.toString().charAt(0), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
     			openRecentMenuItem.add(menuItem);
     		}
     	}
	}
	
	private void setup() {
     	JMenu fileMenu = new JMenu(MessageBundle.get("menu.file.title"));
     	fileMenu.setMnemonic(MessageBundle.getMnemonic("menu.file.vk"));
     	
        JMenuItem openMenuItem = new JMenuItem(MessageBundle.get("menu.file.open.title"), MessageBundle.getMnemonic("menu.file.open.vk"));
     	openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openMenuItem.addActionListener(e -> editor.showImportDialog());
        
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
        addJsonResourceMenuItem.addActionListener(e -> editor.showAddLocaleDialog(ResourceType.JSON));
        addJsonResourceMenuItem.setAccelerator(KeyStroke.getKeyStroke('J', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        JMenuItem addEs6ResourceMenuItem = new JMenuItem(MessageBundle.get("menu.edit.add.locale.es6.title"), MessageBundle.getMnemonic("menu.edit.add.locale.es6.vk"));
        addEs6ResourceMenuItem.addActionListener(e -> editor.showAddLocaleDialog(ResourceType.ES6));
        addEs6ResourceMenuItem.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
      	addLocaleMenuItem.add(addJsonResourceMenuItem);
        addLocaleMenuItem.add(addEs6ResourceMenuItem);
     	
        addTranslationMenuItem = new AddTranslationMenuItem(editor, false);
        findTranslationMenuItem = new FindTranslationMenuItem(editor, false);
        
        removeTranslationMenuItem = new RemoveTranslationMenuItem(editor, false);
        renameTranslationMenuItem = new RenameTranslationMenuItem(editor, false);
        
        editMenu.add(addLocaleMenuItem);
        editMenu.addSeparator();
        editMenu.add(addTranslationMenuItem);
        editMenu.add(findTranslationMenuItem);
        editMenu.addSeparator();
        editMenu.add(renameTranslationMenuItem);
        editMenu.add(removeTranslationMenuItem);
        
        viewMenu = new JMenu(MessageBundle.get("menu.view.title"));
        viewMenu.setMnemonic(MessageBundle.getMnemonic("menu.view.vk"));
        viewMenu.setEnabled(false);
        viewMenu.add(new ExpandTranslationsMenuItem(tree));
        viewMenu.add(new CollapseTranslationsMenuItem(tree));
        
     	JMenu helpMenu = new JMenu(MessageBundle.get("menu.help.title"));
     	helpMenu.setMnemonic(MessageBundle.getMnemonic("menu.help.vk"));
     	
     	JMenuItem aboutMenuItem = new JMenuItem(MessageBundle.get("menu.help.about.title", Editor.TITLE), MessageBundle.getMnemonic("menu.help.about.vk"));
     	aboutMenuItem.addActionListener(e -> editor.showAboutDialog());
     	
     	helpMenu.add(aboutMenuItem);
     	
     	add(fileMenu);
     	add(editMenu);
     	add(viewMenu);
     	add(helpMenu);
     	
     	tree.addTreeSelectionListener(new TranslationTreeNodeSelectionListener());
	}
	
	private class TranslationTreeNodeSelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TranslationTreeNode node = tree.getSelectedNode();
			renameTranslationMenuItem.setEnabled(node != null && !node.isRoot());
			removeTranslationMenuItem.setEnabled(node != null && !node.isRoot());
		}
	}
}
