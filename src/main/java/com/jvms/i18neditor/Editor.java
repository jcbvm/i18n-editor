package com.jvms.i18neditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
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
import com.jvms.i18neditor.swing.JScrollablePanel;
import com.jvms.i18neditor.util.ExtendedProperties;
import com.jvms.i18neditor.util.MessageBundle;
import com.jvms.i18neditor.util.Resources;
import com.jvms.i18neditor.util.TranslationKeys;

/**
 * This class represents the main class of the editor.
 * 
 * @author Jacob
 */
public class Editor extends JFrame {
	private static final long serialVersionUID = 1113029729495390082L;
	
	public static final Path SETTINGS_PATH = Paths.get(System.getProperty("user.home"), ".i18n-editor");
	public static final String TITLE = "i18n Editor";
	public static final String VERSION = "0.2.0";
	public static final int DEFAULT_WIDTH = 1024;
	public static final int DEFAULT_HEIGHT = 768;
	
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
	private ExtendedProperties settings;
	
	public Editor(ExtendedProperties settings) {
		super();
		this.settings = settings;
		resources = Lists.newLinkedList();
		resourceFields = Lists.newLinkedList();
		setup();
	}
	
	public void importResources(String dir) {
		if (!closeCurrentSession()) {
			return;
		}
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
			
			List<String> recentDirs = settings.getListProperty("history");
			recentDirs.remove(dir);
			recentDirs.add(dir);
			if (recentDirs.size() > 5) {
				recentDirs.remove(0);
			}
			settings.setProperty("history", recentDirs);
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
	
	public void removeSelectedTranslation() {
		TranslationTreeNode node = translationTree.getSelectedNode();
		if (node != null && !node.isRoot()) {
			TranslationTreeNode parent = (TranslationTreeNode) node.getParent();
			removeTranslationKey(node.getKey());
			translationTree.setSelectedNode(parent);
		}
	}
	
	public void renameSelectedTranslation() {
		TranslationTreeNode node = translationTree.getSelectedNode();
		if (node != null && !node.isRoot()) {
			showRenameTranslationDialog(node.getKey());
		}
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
	
	public void renameTranslationKey(String key, String newKey) {
		if (resources.isEmpty()) return;
		resources.forEach(resource -> resource.renameTranslation(key, newKey));
		translationTree.renameNodeByKey(key, newKey);
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		updateTitle();
		editorMenu.setSaveable(dirty);
	}
	
	public void showError(String message) {
		showMessageDialog(MessageBundle.get("dialogs.error.title"), message, JOptionPane.ERROR_MESSAGE);
	}
	
	public void showWarning(String title, String message) {
		showMessageDialog(title, message, JOptionPane.WARNING_MESSAGE);
	}
	
	public void showMessage(String title, String message) {
		showMessageDialog(title, message, JOptionPane.PLAIN_MESSAGE);
	}
	
	public void showMessageDialog(String title, String message, int type) {
		JOptionPane.showMessageDialog(this, message, title, type);
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
				Path path = Paths.get(resourcesDir.toString(), locale);
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
		String newKey = "";
		while (newKey != null && newKey.isEmpty()) {
			newKey = (String) JOptionPane.showInputDialog(this, 
					MessageBundle.get("dialogs.translation.rename.text"), 
					MessageBundle.get("dialogs.translation.rename.title"), 
					JOptionPane.QUESTION_MESSAGE, null, null, key);
			if (newKey != null) {
				newKey = newKey.trim();
				if (!TranslationKeys.isValid(newKey)) {
					showError(MessageBundle.get("dialogs.translation.rename.error"));
				} else {
					renameTranslationKey(key, newKey);
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
				if (!TranslationKeys.isValid(newKey)) {
					showError(MessageBundle.get("dialogs.translation.add.error"));
				} else {
					addTranslationKey(newKey);
				}
			}
		}
	}
	
	public void showFindTranslationDialog() {
		String key = (String) JOptionPane.showInputDialog(this, 
				MessageBundle.get("dialogs.translation.find.text"), 
				MessageBundle.get("dialogs.translation.find.title"), 
				JOptionPane.QUESTION_MESSAGE);
		if (key != null) {
			TranslationTreeNode node = translationTree.getNodeByKey(key.trim());
			if (node == null) {
				showWarning(MessageBundle.get("dialogs.translation.find.title"), 
						MessageBundle.get("dialogs.translation.find.error"));
			} else {
				translationTree.setSelectedNode(node);
			}
		}
	}
	
	public void showAboutDialog() {
		showMessage(MessageBundle.get("dialogs.about.title", TITLE), 
				"<html>" +
						"<body style=\"text-align:center;width:200px;\"><br>" +
							"<span style=\"font-weight:bold;font-size:1.2em;\">" + TITLE + "</span><br>" +
							"v" + VERSION + "<br><br>" +
							"(c) Copyright 2015<br>" +
							"Jacob van Mourik<br>" +
							"MIT Licensed<br><br>" +
						"</body>" +
				"</html>");
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
		
		updateTitle();
		validate();
		repaint();
	}
	
	private void updateTitle() {
		String dirtyPart = dirty ? "*" : "";
		String filePart = resourcesDir == null ? "" : resourcesDir.toString() + " - ";
		setTitle(dirtyPart + filePart + TITLE);
	}
	
	private void setupResource(Resource resource) {
		resource.addListener(e -> setDirty(true));
		ResourceField field = new ResourceField(resource);
		field.addKeyListener(new ResourceFieldKeyListener());
		resources.add(resource);
		resourceFields.add(field);
	}
	
	private void setup() {
		setTitle(TITLE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		setPreferredSize(new Dimension(
				settings.getIntegerProperty("window_width", 1024), 
				settings.getIntegerProperty("window_height", 768)));
		
		setIconImages(Lists.newArrayList(
				getResourceImage("images/icon-512.png"),
				getResourceImage("images/icon-256.png"),
				getResourceImage("images/icon-128.png"),
				getResourceImage("images/icon-64.png"),
				getResourceImage("images/icon-48.png"),
				getResourceImage("images/icon-32.png"),
				getResourceImage("images/icon-24.png"),
				getResourceImage("images/icon-20.png"),
				getResourceImage("images/icon-16.png")));
		
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
     	contentPanel.setDividerLocation(settings.getIntegerProperty("divider_pos", 250));
     	contentPanel.setVisible(false);
		
     	editorMenu = new EditorMenu(this, translationTree);
     	
		Container contentPane = getContentPane();
		contentPane.add(editorMenu, BorderLayout.NORTH);
		contentPane.add(contentPanel);
		
		pack();
		
		if (settings.containsKey("window_pos_x") && settings.containsKey("window_pos_y")) {
			setLocation(settings.getIntegerProperty("window_pos_x"), 
						settings.getIntegerProperty("window_pos_y"));
		} else {
			setLocationRelativeTo(null);
		}
		
		addWindowListener(new EditorWindowListener());
	}
	
	private Image getResourceImage(String path) {
		return new ImageIcon(getClass().getClassLoader().getResource(path)).getImage();
	}
	
	private void storeWindowState() {
		settings.setProperty("window_width", getWidth());
		settings.setProperty("window_height", getHeight());
		settings.setProperty("window_pos_x", getX());
		settings.setProperty("window_pos_y", getY());
		settings.setProperty("divider_pos", contentPanel.getDividerLocation());
		settings.store(SETTINGS_PATH);
	}
	
	private class TranslationTreeNodeSelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TranslationTreeNode node = translationTree.getSelectedNode();
			
			if (node != null) {
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
				storeWindowState();
				System.exit(0);
			}
  		}
	}
}
