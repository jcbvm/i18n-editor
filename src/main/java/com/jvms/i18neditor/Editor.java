package com.jvms.i18neditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.apache.commons.lang3.SystemUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jvms.i18neditor.Resource.ResourceType;
import com.jvms.i18neditor.swing.JFileDrop;
import com.jvms.i18neditor.swing.JScrollablePanel;
import com.jvms.i18neditor.util.ExtendedProperties;
import com.jvms.i18neditor.util.GithubRepoUtils;
import com.jvms.i18neditor.util.GithubRepoUtils.GithubReleaseData;
import com.jvms.i18neditor.util.MessageBundle;
import com.jvms.i18neditor.util.Resources;
import com.jvms.i18neditor.util.TranslationKeys;

/**
 * This class represents the main class of the editor.
 * 
 * @author Jacob
 */
public class Editor extends JFrame {
	private final static long serialVersionUID = 1113029729495390082L;
	
	public final static Path SETTINGS_PATH = Paths.get(System.getProperty("user.home"), ".i18n-editor");
	public final static String TITLE = "i18n Editor";
	public final static String VERSION = "0.7.0";
	public final static String COPYRIGHT_YEAR = "2016";
	public final static String GITHUB_REPO = "jcbvm/ember-i18n-editor";
	public final static int DEFAULT_WIDTH = 1024;
	public final static int DEFAULT_HEIGHT = 768;
	
	private List<Resource> resources = Lists.newLinkedList();
	private Path resourcesDir;
	private boolean dirty;
	private boolean minifyOutput;
	
	private EditorMenu editorMenu;
	private JSplitPane contentPane;
	private JPanel translationsPanel;
	private JScrollPane resourcesScrollPane;
	private TranslationTree translationTree;
	private TranslationField translationField;
	private JPanel resourcesPanel;
	private List<ResourceField> resourceFields = Lists.newLinkedList();
	private ExtendedProperties settings = new ExtendedProperties();
	
	public Editor() {
		super();
		setupUI();
		setupFileDrop();
	}
	
	public void importResources(Path dir) {
		if (!closeCurrentSession()) {
			return;
		}
		if (Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)) {
			reset();
			resourcesDir = dir;
		} else {
			showError(MessageBundle.get("resources.open.error.multiple"));
			return;
		}
		try {
			Files.walk(resourcesDir, 1).filter(path -> Resources.isResource(path)).forEach(path -> {
				try {
					Resource resource = Resources.read(path);
					setupResource(resource);
				} catch (Exception e) {
					e.printStackTrace();
					showError(MessageBundle.get("resources.open.error.single", path.toString()));
				}
			});
			
			List<String> recentDirs = settings.getListProperty("history");
			recentDirs.remove(resourcesDir);
			recentDirs.add(resourcesDir.toString());
			if (recentDirs.size() > 5) {
				recentDirs.remove(0);
			}
			settings.setProperty("history", recentDirs);
			editorMenu.setRecentItems(Lists.reverse(recentDirs));
			
			Map<String,String> keys = Maps.newTreeMap();
			resources.forEach(resource -> keys.putAll(resource.getTranslations()));
			List<String> keyList = Lists.newArrayList(keys.keySet());
			translationTree.setModel(new TranslationTreeModel(keyList));
			
			updateUI();
		} catch (IOException e) {
			e.printStackTrace();
			showError(MessageBundle.get("resources.open.error.multiple"));
		}
	}
	
	public void saveResources() {
		boolean error = false;
		for (Resource resource : resources) {
			try {
				Resources.write(resource, !minifyOutput);
			} catch (Exception e) {
				error = true;
				e.printStackTrace();
				showError(MessageBundle.get("resources.write.error.single", resource.getPath().toString()));
			}
		}
		setDirty(error);
	}
	
	public void reloadResources() {
		importResources(resourcesDir);
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
	
	public void duplicateSelectedTranslation() {
		TranslationTreeNode node = translationTree.getSelectedNode();
		if (node != null && !node.isRoot()) {
			showDuplicateTranslationDialog(node.getKey());
		}
	}
	
	public void addTranslationKey(String key) {
		if (resources.isEmpty()) return;
		resources.forEach(resource -> resource.storeTranslation(key, ""));
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
	
	public void duplicateTranslationKey(String key, String newKey) {
		if (resources.isEmpty()) return;
		resources.forEach(resource -> resource.duplicateTranslation(key, newKey));
		translationTree.duplicateNodeByKey(key, newKey);
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public Path getResourcesPath() {
		return resourcesDir;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		updateTitle();
		editorMenu.setSaveable(dirty);
	}
	
	public boolean isMinifyOutput() {
		return minifyOutput;
	}
	
	public void setMinifyOutput(boolean minifyOutput) {
		this.minifyOutput = minifyOutput;
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
	
	public void showMessage(String title, Component component) {
		showMessageDialog(title, component, JOptionPane.PLAIN_MESSAGE);
	}
	
	public void showMessageDialog(String title, String message, int type) {
		JOptionPane.showMessageDialog(this, message, title, type);
	}
	
	public void showMessageDialog(String title, Component component, int type) {
		JOptionPane.showMessageDialog(this, component, title, type);
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
			importResources(Paths.get(fc.getSelectedFile().getPath()));
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
						updateUI();
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
	
	public void showDuplicateTranslationDialog(String key) {
		String newKey = "";
		while (newKey != null && newKey.isEmpty()) {
			newKey = (String) JOptionPane.showInputDialog(this, 
					MessageBundle.get("dialogs.translation.duplicate.text"), 
					MessageBundle.get("dialogs.translation.duplicate.title"), 
					JOptionPane.QUESTION_MESSAGE, null, null, key);
			if (newKey != null) {
				newKey = newKey.trim();
				if (!TranslationKeys.isValid(newKey)) {
					showError(MessageBundle.get("dialogs.translation.duplicate.error"));
				} else {
					duplicateTranslationKey(key, newKey);
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
				"<html><body style=\"text-align:center;width:200px;\">" +
					"<span style=\"font-weight:bold;font-size:1.2em;\">" + TITLE + "</span><br>" +
					"v" + VERSION + "<br><br>" +
					"(c) Copyright " + COPYRIGHT_YEAR + "<br>" +
					"Jacob van Mourik<br>" +
					"MIT Licensed<br><br>" +
				"</body></html>");
	}
	
	public void showVersionDialog() {
		GithubReleaseData data = GithubRepoUtils.getLatestRelease(GITHUB_REPO);
		String content = "";
		if (data != null && !VERSION.equals(data.getTagName())) {
			content = MessageBundle.get("dialogs.version.new", data.getTagName()) + "<br>" + 
					"<a href=\"" + data.getHtmlUrl() + "\">" + MessageBundle.get("dialogs.version.link") + "</a>";
		} else {
			content = MessageBundle.get("dialogs.version.uptodate");
		}
		Font font = getFont();
	    JEditorPane pane = new JEditorPane("text/html", "<html><body style=\"font-family:" + font.getFamily() + ";font-size:" + font.getSize() + "pt;text-align:center;width:200px;\">" + content + "</body></html>");
	    pane.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
            	try {
	                Desktop.getDesktop().browse(e.getURL().toURI());
	            } catch (Exception e1) {
	                //
	            }
            }
	    });
	    pane.setBackground(getBackground());
	    pane.setEditable(false);
		showMessage(MessageBundle.get("dialogs.version.title"), pane);
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
		updateUI();
	}
	
	public void updateUI() {
		TranslationTreeNode selectedNode = translationTree.getSelectedNode();
		
		resourcesPanel.removeAll();
		resourceFields.stream().sorted().forEach(field -> {
			field.setEditable(selectedNode != null && selectedNode.isEditable());
			resourcesPanel.add(Box.createVerticalStrut(5));
			resourcesPanel.add(new JLabel(field.getResource().getLocale().getDisplayName()));
			resourcesPanel.add(Box.createVerticalStrut(5));
			resourcesPanel.add(field);
			resourcesPanel.add(Box.createVerticalStrut(5));
		});
		if (!resourceFields.isEmpty()) {
			resourcesPanel.remove(0);
			resourcesPanel.remove(resourcesPanel.getComponentCount()-1);
		}
		
		editorMenu.setEnabled(resourcesDir != null);
		editorMenu.setEditable(!resources.isEmpty());
		translationTree.setEditable(!resources.isEmpty());
		translationField.setEditable(!resources.isEmpty());
		
		updateTitle();
		validate();
		repaint();
	}
	
	public void launch() {
		settings.load(SETTINGS_PATH);
		
		// Restore editor settings
		minifyOutput = settings.getBooleanProperty("minify_output");
    	
		// Restore window bounds
		setPreferredSize(new Dimension(settings.getIntegerProperty("window_width", 1024), settings.getIntegerProperty("window_height", 768)));
		setLocation(settings.getIntegerProperty("window_pos_x", 0), settings.getIntegerProperty("window_pos_y", 0));
		contentPane.setDividerLocation(settings.getIntegerProperty("divider_pos", 250));
		
    	pack();
    	setVisible(true);
    	
    	if (!loadResourcesFromHistory()) {
    		showImportDialog();
    	} else {
    		// Restore last expanded nodes
			List<String> expandedKeys = settings.getListProperty("last_expanded");
			List<TranslationTreeNode> expandedNodes = expandedKeys.stream()
					.map(k -> translationTree.getNodeByKey(k))
					.filter(n -> n != null)
					.collect(Collectors.toList());
			translationTree.expand(expandedNodes);
			
			// Restore last selected node
			String selectedKey = settings.getProperty("last_selected");
			TranslationTreeNode selectedNode = translationTree.getNodeByKey(selectedKey);
			if (selectedNode != null) {
				translationTree.setSelectedNode(selectedNode);
			}
    	}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				// Only use native look an feel when not running Linux, Linux might cause visual issues
				if (!SystemUtils.IS_OS_LINUX) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());				
				}
			} catch (Exception e) {
				//
			}
			new Editor().launch();
		});
	}
	
	private boolean loadResourcesFromHistory() {
		List<String> dirs = settings.getListProperty("history");
    	if (!dirs.isEmpty()) {
    		String lastDir = dirs.get(dirs.size()-1);
    		Path path = Paths.get(lastDir);
    		if (Files.exists(path)) {
    			importResources(path);
    			return true;
    		}
    	}
    	return false;
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
	
	private void setupUI() {
		setTitle(TITLE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new EditorWindowListener());
		
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
		translationField = new TranslationField();
		translationField.addKeyListener(new TranslationFieldKeyListener());
		translationsPanel.add(new JScrollPane(translationTree));
		translationsPanel.add(translationField, BorderLayout.SOUTH);
		
        resourcesPanel = new JScrollablePanel(true, false);
        resourcesPanel.setLayout(new BoxLayout(resourcesPanel, BoxLayout.Y_AXIS));
        resourcesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        resourcesScrollPane = new JScrollPane(resourcesPanel);
        resourcesScrollPane.getViewport().setOpaque(false);
        
		contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, translationsPanel, resourcesScrollPane);
     	editorMenu = new EditorMenu(this, translationTree);
     	
		Container container = getContentPane();
		container.add(editorMenu, BorderLayout.NORTH);
		container.add(contentPane);
	}
	
	private void setupFileDrop() {
		new JFileDrop(this, new JFileDrop.Listener() {
			@Override
			public void filesDropped(java.io.File[] files) {
				try {
					Path path = Paths.get(files[0].getCanonicalPath());
					importResources(path);
                } catch (IOException e ) {
                	e.printStackTrace();
                	showError(MessageBundle.get("resources.open.error.multiple"));
                }
            }
        });
	}
	
	private Image getResourceImage(String path) {
		return new ImageIcon(getClass().getClassLoader().getResource(path)).getImage();
	}
	
	private void storeEditorState() {
		// Store editor settings
		settings.setProperty("minify_output", minifyOutput);
		
		// Store window bounds
		settings.setProperty("window_width", getWidth());
		settings.setProperty("window_height", getHeight());
		settings.setProperty("window_pos_x", getX());
		settings.setProperty("window_pos_y", getY());
		settings.setProperty("divider_pos", contentPane.getDividerLocation());
		
		if (!resources.isEmpty()) {
			// Store keys of expanded nodes
			List<String> expandedNodeKeys = translationTree.getExpandedNodes().stream()
					.map(n -> n.getKey())
					.collect(Collectors.toList());
			settings.setProperty("last_expanded", expandedNodeKeys);
			
			// Store key of selected node
			TranslationTreeNode selectedNode = translationTree.getSelectedNode();
			settings.setProperty("last_selected", selectedNode == null ? "" : selectedNode.getKey());
		}
		
		settings.store(SETTINGS_PATH);
	}
	
	private class TranslationTreeNodeSelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TranslationTreeNode node = translationTree.getSelectedNode();
			
			if (node != null) {
				// Store scroll position
				int scrollValue = resourcesScrollPane.getVerticalScrollBar().getValue();
				
				// Update UI values
				String key = node.getKey();
				translationField.setText(key);
				resourceFields.forEach(f -> {
					f.updateValue(key);
					f.setEditable(node.isEditable());
				});
				
				// Restore scroll position
				SwingUtilities.invokeLater(() -> resourcesScrollPane.getVerticalScrollBar().setValue(scrollValue));
			}
		}
	}
	
	private class ResourceFieldKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			ResourceField field = (ResourceField) e.getSource();
			String key = translationTree.getSelectedNode().getKey();
			String value = field.getValue();
			field.getResource().storeTranslation(key, value);
		}
	}
	
	private class TranslationFieldKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				TranslationField field = (TranslationField) e.getSource();
				String key = field.getValue();
				if (TranslationKeys.isValid(key)) {
					TranslationTreeNode node = translationTree.getNodeByKey(key);
					if (node == null) {
						addTranslationKey(key);						
					} else {
						translationTree.setSelectedNode(node);
					}
				}
			}
		}
	}
	
	private class EditorWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			if (closeCurrentSession()) {
				storeEditorState();
				System.exit(0);
			}
  		}
	}
}
