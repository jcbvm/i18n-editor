package com.jvms.i18neditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jvms.i18neditor.Resource.ResourceType;
import com.jvms.i18neditor.event.ResourceEvent;
import com.jvms.i18neditor.event.ResourceListener;
import com.jvms.i18neditor.swing.JScrollablePanel;
import com.jvms.i18neditor.util.MessageBundle;
import com.jvms.i18neditor.util.Resources;
import com.jvms.i18neditor.util.TranslationKeys;
import com.jvms.i18neditor.util.SettingsBundle;

public class Editor extends JFrame {
	private static final long serialVersionUID = 1113029729495390082L;
	
	public static final String TITLE = "i18n Editor";
	public static final String VERSION = "0.1.0-beta.2";
	private static final int WINDOW_WIDTH = 1024;
	private static final int WINDOW_HEIGHT = 768;
	
	private List<Resource> resources;
	private Path resourcesDir;
	private boolean dirty;
	
	private EditorMenu editorMenu;
	private JSplitPane contentPanel;
	private JPanel translationsPanel;
	private TranslationTree translationTree;
	private TranslationField translationField;
	private JPanel resourcesPanel;
	private List<ResourceField> resourceFields;
	
	public Editor() {
		super();
		resources = Lists.newLinkedList();
		resourceFields = Lists.newLinkedList();
		setup();
	}
	
	public void importResources(String dir) {
		if (!closeCurrentSession()) return;
		if (resourcesDir != null) {
			reset();
		}
		resourcesDir = Paths.get(dir);
		try {
			Files.walk(Paths.get(dir),1).filter(path -> Resources.isResource(path)).forEach(path -> {
				try {
					Resource resource = Resources.read(path);
					setupResource(resource);
				} catch (Exception e) {
					e.printStackTrace();
					showError(MessageBundle.get("resources.open.error.single", path.toString()));
				}
			});
			
			List<String> recentDirs = SettingsBundle.getAsList("history");
			recentDirs.remove(dir);
			recentDirs.add(dir);
			if (recentDirs.size() > 5) {
				recentDirs.remove(0);
			}
			SettingsBundle.store("history", recentDirs);
			editorMenu.setRecentItems(Lists.reverse(recentDirs));
			
			Map<String,String> keys = Maps.newTreeMap();
			resources.forEach(resource -> keys.putAll(resource.getTranslations()));
			List<String> keyList = Lists.newArrayList(keys.keySet());
			translationTree.setModel(new TranslationTreeModel(MessageBundle.get("translations.model.name"), keyList));
			
			update();
		} catch (IOException e) {
			e.printStackTrace();
			showError(MessageBundle.get("resource.open.error.multiple"));
		}
	}
	
	public void saveResources() {
		boolean error = false;
		for (Resource resource : resources) {
			try {
				Resources.write(resource);
			} catch (Exception e) {
				error = true;
				e.printStackTrace();
				showError(MessageBundle.get("resources.write.error.single", resource.getPath().toString()));
			}
		}
		setDirty(error);
	}
	
	public void reloadResources() {
		importResources(resourcesDir.toString());
	}
	
	public void addTranslationKey(String key) {
		if (resources.isEmpty()) return;
		translationTree.addNodeByKey(key);
	}
	
	public void removeTranslationKey(String key) {
		if (resources.isEmpty()) return;
		resources.forEach(resource -> resource.removeTranslation(key));
		translationTree.removeNodeByKey(key);
	}
	
	public void renameTranslationKey(String key, String newName) {
		if (resources.isEmpty()) return;
		resources.forEach(resource -> resource.renameTranslation(key, newName));
		translationTree.renameNodeByKey(key, newName);
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		setTitle(getTitle().replaceFirst("\\*", ""));
		if (dirty) {
			setTitle("*" + getTitle());
		}
		editorMenu.setSaveable(dirty);
	}
	
	public void showError(String message) {
		JOptionPane.showMessageDialog(this, message, MessageBundle.get("dialogs.error.title"), JOptionPane.ERROR_MESSAGE);
	}
	
	public void showImportDialog() {
		String path = null;
		if (resourcesDir != null) {
			path = resourcesDir.toString();
		}
		JFileChooser fc = new JFileChooser(path);
		fc.setDialogTitle(MessageBundle.get("dialogs.import.title"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			importResources(fc.getSelectedFile().getPath());
		}
	}
	
	public void showAddLocaleDialog(ResourceType type) {
		String locale = "";
		while (locale != null && locale.isEmpty()) {
			locale = (String) JOptionPane.showInputDialog(this, 
					MessageBundle.get("dialogs.locale.add.text"), 
					MessageBundle.get("dialogs.locale.add.title", type.toString()), 
					JOptionPane.QUESTION_MESSAGE);
			if (locale != null) {
				locale = locale.trim();
				Path path = Paths.get(resourcesDir.toString() + "/" + locale);
				if (locale.isEmpty() || Files.isDirectory(path)) {
					showError(MessageBundle.get("dialogs.locale.add.error.invalid"));
				} else {
					try {
						Resource resource = Resources.create(type, path);
						setupResource(resource);
						update();
					} catch (IOException e) {
						e.printStackTrace();
						showError(MessageBundle.get("dialogs.locale.add.error.create"));
					}
				}
			}
		}
	}
	
	public void showRenameTranslationDialog(String key) {
		String name = TranslationKeys.lastPart(key);
		String newName = "";
		while (newName != null && newName.isEmpty()) {
			newName = (String) JOptionPane.showInputDialog(this, 
					MessageBundle.get("dialogs.translation.rename.text"), 
					MessageBundle.get("dialogs.translation.rename.title"), 
					JOptionPane.QUESTION_MESSAGE, null, null, name);
			if (newName != null) {
				newName = newName.trim();
				if (newName.isEmpty() || newName.contains(".")) {
					showError(MessageBundle.get("dialogs.translation.rename.error"));
				} else {
					renameTranslationKey(key, newName);
				}
			}
		}
	}
	
	public void showAddTranslationDialog() {
		String key = "";
		String newKey = "";
		TranslationTreeNode node = translationTree.getSelectedNode();
		if (node != null && !node.isRoot()) {
			key = node.getKey();
		}
		while (newKey != null && newKey.isEmpty()) {
			newKey = (String) JOptionPane.showInputDialog(this, 
					MessageBundle.get("dialogs.translation.add.text"), 
					MessageBundle.get("dialogs.translation.add.title"), 
					JOptionPane.QUESTION_MESSAGE, null, null, key);
			if (newKey != null) {
				newKey = newKey.trim();
				if (newKey.isEmpty()) {
					showError(MessageBundle.get("dialogs.translation.add.error"));
				} else {
					addTranslationKey(newKey);
				}
			}
		}
	}
	
	public boolean closeCurrentSession() {
		if (isDirty()) {
			int result = JOptionPane.showConfirmDialog(this, 
					MessageBundle.get("dialogs.save.text"), 
					MessageBundle.get("dialogs.save.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				saveResources();
			}
			return result != JOptionPane.CANCEL_OPTION;
		}
		return true;
	}
	
	public void reset() {
		translationTree.clear();
		resources.clear();
		resourceFields.clear();
		setDirty(false);
		update();
	}
	
	public void update() {
		setTitle(getTitle().split("-")[0].trim() + (resourcesDir == null ? "" : " - " + resourcesDir.toString()));
		
		TranslationTreeNode selectedNode = translationTree.getSelectedNode();
		
		resourcesPanel.removeAll();
		resourceFields.sort((f1, f2) -> f1.compareTo(f2));
		resourceFields.forEach(f -> {
			f.setEditable(selectedNode != null && selectedNode.isEditable());
			resourcesPanel.add(new JLabel(f.getResource().getLocale().getDisplayName()));
			resourcesPanel.add(Box.createVerticalStrut(5));
			resourcesPanel.add(f);
			resourcesPanel.add(Box.createVerticalStrut(5));
		});
		if (!resourceFields.isEmpty()) {
			resourcesPanel.remove(resourcesPanel.getComponentCount()-1);
		}
		
		contentPanel.setVisible(resourcesDir != null);
		editorMenu.setEnabled(resourcesDir != null);
		editorMenu.setEditable(!resources.isEmpty());
		translationTree.setEditable(!resources.isEmpty());
		translationField.setEditable(!resources.isEmpty());
		
		validate();
		repaint();
	}
	
	private void setupResource(Resource resource) {
		resource.addListener(new ResourceChangeListener());
		ResourceField field = new ResourceField(resource);
		field.addKeyListener(new ResourceFieldKeyListener());
		resources.add(resource);
		resourceFields.add(field);
	}
	
	private void setup() {
		editorMenu = new EditorMenu(this);
		editorMenu.setRecentItems(SettingsBundle.getAsList("recentResourcesDirs"));
		
		translationsPanel = new JPanel(new BorderLayout());
        translationTree = new TranslationTree(this);
        translationTree.addTreeSelectionListener(new TranslationTreeNodeSelectionListener());
		translationField = new TranslationField(this);
		translationsPanel.add(new JScrollPane(translationTree));
		translationsPanel.add(translationField, BorderLayout.SOUTH);
		
        resourcesPanel = new JScrollablePanel(true, false);
        resourcesPanel.setLayout(new BoxLayout(resourcesPanel, BoxLayout.Y_AXIS));
        resourcesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
		contentPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, translationsPanel, new JScrollPane(resourcesPanel));
     	contentPanel.setDividerLocation(WINDOW_WIDTH/4);
     	contentPanel.setVisible(false);
		
		Container contentPane = getContentPane();
		contentPane.add(editorMenu, BorderLayout.NORTH);
		contentPane.add(contentPanel);
		
		setTitle(TITLE);
		setIconImages(Lists.newArrayList(
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-512.png")).getImage(),
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-256.png")).getImage(),
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-128.png")).getImage(),
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-64.png")).getImage(),
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-48.png")).getImage(),
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-32.png")).getImage(),
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-24.png")).getImage(),
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-20.png")).getImage(),
			new ImageIcon(getClass().getClassLoader().getResource("images/icon-16.png")).getImage()
		));
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		addWindowListener(new EditorWindowListener());
	}
	
	private class TranslationTreeNodeSelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TranslationTreeNode node = (TranslationTreeNode) e.getPath().getLastPathComponent();
			
			// Store scroll position
			JScrollPane scrollPane = (JScrollPane) resourcesPanel.getParent().getParent();
			int scrollValue = scrollPane.getVerticalScrollBar().getValue();
			
			// Update UI values
			String key = node.getKey();
			translationField.setText(key);
			resourceFields.forEach(f -> {
				f.updateValue(key);
				f.setEditable(node.isEditable());
			});
			
			// Restore scroll position
			SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollValue));
		}
	}
	
	private class ResourceChangeListener implements ResourceListener {
		@Override
		public void resourceChanged(ResourceEvent e) {
			setDirty(true);
		}
	}
	
	private class ResourceFieldKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			ResourceField field = (ResourceField) e.getSource();
			String key = translationTree.getSelectedNode().getKey();
			String value = field.getText().trim();
			field.getResource().storeTranslation(key, value);
		}
	}
	
	private class EditorWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			if (closeCurrentSession()) {
				System.exit(0);
			}
  		}
	}
}
