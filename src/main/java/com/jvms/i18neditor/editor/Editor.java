package com.jvms.i18neditor.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jvms.i18neditor.FileStructure;
import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.ResourceType;
import com.jvms.i18neditor.io.ChecksumException;
import com.jvms.i18neditor.swing.JFileDrop;
import com.jvms.i18neditor.swing.JScrollablePanel;
import com.jvms.i18neditor.swing.util.Dialogs;
import com.jvms.i18neditor.util.Colors;
import com.jvms.i18neditor.util.ExtendedProperties;
import com.jvms.i18neditor.util.GithubRepoUtil;
import com.jvms.i18neditor.util.GithubRepoUtil.GithubRepoReleaseData;
import com.jvms.i18neditor.util.Images;
import com.jvms.i18neditor.util.Locales;
import com.jvms.i18neditor.util.MessageBundle;
import com.jvms.i18neditor.util.ResourceKeys;
import com.jvms.i18neditor.util.Resources;

/**
 * This class represents the main class of the editor.
 * 
 * @author Jacob van Mourik
 */
public class Editor extends JFrame {
	private final static long serialVersionUID = 1113029729495390082L;
	private final static Logger log = LoggerFactory.getLogger(Editor.class);
	
	public final static String TITLE = "i18n-editor";
	public final static String VERSION = "2.0.0-beta.1";
	public final static String GITHUB_USER = "jcbvm";
	public final static String GITHUB_PROJECT = "i18n-editor";
	public final static String PROJECT_FILE = ".i18n-editor-metadata";
	public final static String SETTINGS_FILE = ".i18n-editor";
	public final static String SETTINGS_DIR = System.getProperty("user.home");
	
	public final static Locale DEFAULT_LANGUAGE = Locale.ENGLISH;
	public final static List<Locale> SUPPORTED_LANGUAGES = Lists.newArrayList(
			new Locale("en"),
			new Locale("nl"),
			new Locale("pt", "BR"),
			new Locale("es", "ES"));
	
	private EditorProject project;
	private EditorSettings settings = new EditorSettings();
	private ExecutorService executor = Executors.newFixedThreadPool(2);
	private boolean dirty;
	
	private EditorMenuBar editorMenu;
	private JSplitPane contentPane;
	private JLabel introText;
	private JPanel translationsPanel;
	private JScrollPane resourcesScrollPane;
	private TranslationTree translationTree;
	private TranslationKeyField translationField;
	private JPanel resourcesPanel;
	private List<ResourceField> resourceFields = Lists.newArrayList();
	
	public void createProject(Path dir, ResourceType type) {
		try {
			Preconditions.checkArgument(Files.isDirectory(dir));
			
			if (!closeCurrentProject()) {
				return;
			}
			
			List<Resource> resourceList = Resources.get(dir, 
					settings.getResourceFileDefinition(), settings.getResourceFileStructure(), Optional.of(type));
			if (!resourceList.isEmpty()) {
				boolean importProject = Dialogs.showConfirmDialog(this, 
						MessageBundle.get("dialogs.project.new.conflict.title"),
						MessageBundle.get("dialogs.project.new.conflict.text"),
						JOptionPane.YES_NO_OPTION);
				if (importProject) {
					importProject(dir, false);
					return;
				}
			}
			
			clearUI();
			project = new EditorProject(dir);
			restoreProjectState(project);
			project.setResourceType(type);
			
			if (project.getResourceFileStructure() == FileStructure.Flat) {
				Resource resource = Resources.create(type, dir, 
						project.getResourceFileDefinition(), FileStructure.Flat, Optional.empty());
				setupResource(resource);
				project.addResource(resource);
			}
			translationTree.setModel(new TranslationTreeModel());
			
			updateHistory();
			updateUI();
			requestFocusInFirstResourceField();
			
			SwingUtilities.invokeLater(() -> showAddLocaleDialog());
		} catch (IOException e) {
			log.error("Error creating resource files", e);
			showError(MessageBundle.get("resources.create.error"));
		}
	}
	
	public void importProject(Path dir, boolean showEmptyProjectError) {
		try {
			Preconditions.checkArgument(Files.isDirectory(dir));
			
			if (!closeCurrentProject()) {
				return;
			}
			
			clearUI();
			project = new EditorProject(dir);
			restoreProjectState(project);
			
			Optional<ResourceType> type = Optional.ofNullable(project.getResourceType());
			List<Resource> resourceList = Resources.get(dir, 
					project.getResourceFileDefinition(), project.getResourceFileStructure(), type);
			Map<String,String> keys = Maps.newTreeMap();
			
			if (resourceList.isEmpty()) {
				project = null;
				if (showEmptyProjectError) {
					executor.execute(() -> showError(MessageBundle.get("resources.import.empty", dir)));
				}
			} else {
				project.setResourceType(type.orElseGet(() -> {
					ResourceType t = resourceList.get(0).getType();
					resourceList.removeIf(r -> r.getType() != t);
					return t;
				}));
				resourceList.forEach(resource -> {
					try {
						Resources.load(resource, settings.isPreserveCommnets(), settings.isUseSingleQuotes());
						setupResource(resource);
						project.addResource(resource);
					} catch (IOException e) {
						log.error("Error importing resource file " + resource.getPath(), e);
						showError(MessageBundle.get("resources.import.error.single", resource.getPath()));
					}
				});
				project.getResources().forEach(r -> keys.putAll(r.getTranslations()));
			}
			translationTree.setModel(new TranslationTreeModel(Lists.newArrayList(
					Collections2.filter(keys.keySet(), Predicates.not(Predicates.containsPattern("comment"))))));
			
			updateTreeNodeStatuses();
			updateHistory();
			updateUI();
			requestFocusInFirstResourceField();
		} catch (IOException e) {
			log.error("Error importing resource files", e);
			showError(MessageBundle.get("resources.import.error.multiple"));
		}
	}
	
	public boolean saveProject() {
		boolean error = false;
		if (project != null) {
			for (Resource resource : project.getResources()) {
				if (!saveResource(resource)) {
					error = true;
				}
			}
		}
		if (dirty) {
			setDirty(error);			
		}
		return !error;
	}
	
	public void reloadProject() {
		if (project != null) {
			importProject(project.getPath(), true);			
		}
	}
	
	public void removeSelectedTranslation() {
		TranslationTreeNode node = translationTree.getSelectionNode();
		if (node != null && !node.isRoot()) {
			TranslationTreeNode parent = (TranslationTreeNode) node.getParent();
			removeTranslation(node.getKey());
			translationTree.setSelectionNode(parent);
		}
	}
	
	public void renameSelectedTranslation() {
		TranslationTreeNode node = translationTree.getSelectionNode();
		if (node != null && !node.isRoot()) {
			showRenameTranslationDialog(node.getKey());
		}
	}
	
	public void copySelectedTranslationKey() {
		TranslationTreeNode node = translationTree.getSelectionNode();
		if (node != null && !node.isRoot()) {
			String key = node.getKey();
			StringSelection selection = new StringSelection(key);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
		}
	}
	
	public void duplicateSelectedTranslation() {
		TranslationTreeNode node = translationTree.getSelectionNode();
		if (node != null && !node.isRoot()) {
			showDuplicateTranslationDialog(node.getKey());
		}
	}
	
	public boolean addLocale(String localeString) {
		if (project == null) {
			return false;
		}
		Locale locale = Locales.parseLocale(localeString);
		if (locale == null) {
			showError(MessageBundle.get("dialogs.locale.add.error.invalid"));
			return false;
		}
		try {
			Resource resource = Resources.create(project.getResourceType(), project.getPath(), 
					project.getResourceFileDefinition(), project.getResourceFileStructure(), Optional.of(locale));
			addResource(resource);
			requestFocusInFirstResourceField();
			return true;
		} catch (IOException e) {
			log.error("Error creating new locale", e);
			showError(MessageBundle.get("dialogs.locale.add.error.create"));
			return false;
		}
	}
	
	public boolean addTranslation(String key) {
		if (!ResourceKeys.isValid(key)) {
			showError(MessageBundle.get("dialogs.translation.key.error"));
			return false;
		}
		TranslationTreeNode node = translationTree.getNodeByKey(key);
		if (node != null) {
			translationTree.setSelectionNode(node);
		} else if (!confirmNewTranslation(key)) {
			return false;
		}
		if (project != null) {
			project.getResources().forEach(resource -> resource.storeTranslation(key, ""));				
		}
		translationTree.addNodeByKey(key);			
		requestFocusInFirstResourceField();
		return true;
	}
	
	public void removeTranslation(String key) {
		if (project != null) {
			project.getResources().forEach(resource -> resource.removeTranslation(key));
		}
		translationTree.removeNodeByKey(key);
		requestFocusInFirstResourceField();
	}
	
	public boolean renameTranslation(String key, String newKey) {
		if (!ResourceKeys.isValid(newKey)) {
			showError(MessageBundle.get("dialogs.translation.key.error"));
			return false;
		}
		if (key.equals(newKey)) {
			return true;
		}
		if (!confirmNewTranslation(key, newKey)) {
			return false;
		}
		if (project != null) {
			project.getResources().forEach(resource -> resource.renameTranslation(key, newKey));
		}
		translationTree.renameNodeByKey(key, newKey);
		requestFocusInFirstResourceField();
		return true;
	}
	
	public boolean duplicateTranslation(String key, String newKey) {
		if (!ResourceKeys.isValid(newKey)) {
			showError(MessageBundle.get("dialogs.translation.key.error"));
			return false;
		}
		if (key.equals(newKey)) {
			return true;
		}
		if (!confirmNewTranslation(key, newKey)) {
			return false;
		}
		if (project != null) {
			project.getResources().forEach(resource -> resource.duplicateTranslation(key, newKey));
		}
		translationTree.duplicateNodeByKey(key, newKey);
		requestFocusInFirstResourceField();
		return true;
	}
	
	public void addResource(Resource resource) {
		setupResource(resource);
		updateUI();
		if (project != null) {
			project.addResource(resource);
			updateTreeNodeStatuses();
		}
	}
	
	public EditorProject getProject() {
		return project;
	}
	
	public EditorSettings getSettings() {
		return settings;
	}
	
	public Locale getCurrentLocale() {
		Locale locale = settings.getEditorLanguage();
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		updateTitle();
		editorMenu.setSaveable(dirty);
	}
	
	public void clearHistory() {
		settings.setHistory(Lists.newArrayList());
		editorMenu.setRecentItems(Lists.newArrayList());
	}
	
	public void showCreateProjectDialog(ResourceType type) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(MessageBundle.get("dialogs.project.new.title"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			createProject(Paths.get(fc.getSelectedFile().getPath()), type);
		} else {
			updateHistory();
			updateUI();
		}
	}
	
	public void showImportProjectDialog() {
		String path = null;
		if (project != null) {
			path = project.getPath().toString();
		}
		JFileChooser fc = new JFileChooser(path);
		fc.setDialogTitle(MessageBundle.get("dialogs.project.import.title"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			importProject(Paths.get(fc.getSelectedFile().getPath()), true);
		}
	}
	
	public void showAddLocaleDialog() {
		String localeString = "";
		while (localeString != null) {
			localeString = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.locale.add.title"),
					MessageBundle.get("dialogs.locale.add.text"),
					null, JOptionPane.QUESTION_MESSAGE);
			if (localeString != null) {
				boolean result = addLocale(localeString.trim());
				if (result) {
					break;
				}
			}
		}
	}
	
	public void showRenameTranslationDialog(String key) {
		String newKey = "";
		while (newKey != null) {
			newKey = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.translation.rename.title"),
					MessageBundle.get("dialogs.translation.rename.text"),
					MessageBundle.get("dialogs.translation.add.help"),
					JOptionPane.QUESTION_MESSAGE, key, new TranslationKeyCaret());
			if (newKey != null) {
				boolean result = renameTranslation(key, newKey.trim());
				if (result) {
					break;
				}
			}
		}
	}
	
	public void showDuplicateTranslationDialog(String key) {
		String newKey = "";
		while (newKey != null) {
			newKey = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.translation.duplicate.title"),
					MessageBundle.get("dialogs.translation.duplicate.text"),
					MessageBundle.get("dialogs.translation.add.help"),
					JOptionPane.QUESTION_MESSAGE, key, new TranslationKeyCaret());
			if (newKey != null) {
				boolean result = addTranslation(newKey.trim());
				if (result) {
					break;
				}
			}
		}
	}
	
	public void showAddTranslationDialog(TranslationTreeNode node) {
		String key = "";
		String newKey = "";
		if (node != null && !node.isRoot()) {
			key = node.getKey() + ".";
		}
		while (newKey != null) {
			newKey = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.translation.add.title"),
					MessageBundle.get("dialogs.translation.add.text"),
					MessageBundle.get("dialogs.translation.add.help"),
					JOptionPane.QUESTION_MESSAGE, key, new TranslationKeyCaret());
			if (newKey != null) {
				boolean result = addTranslation(newKey.trim());
				if (result) {
					break;
				}
			}
		}
	}
	
	public void showFindTranslationDialog() {
		String key = "";
		while (key != null) {
			key = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.translation.find.title"),
					MessageBundle.get("dialogs.translation.find.text"),
					null, JOptionPane.QUESTION_MESSAGE, key, new TranslationKeyCaret());
			if (key != null) {
				TranslationTreeNode node = translationTree.getNodeByKey(key.trim());
				if (node == null) {
					Dialogs.showWarningDialog(this, 
							MessageBundle.get("dialogs.translation.find.title"), 
							MessageBundle.get("dialogs.translation.find.error"));
				} else {
					translationTree.setSelectionNode(node);
					break;
				}
			}
		}
	}
	
	public void showAboutDialog() {
		Dialogs.showHtmlDialog(this, MessageBundle.get("dialogs.about.title", TITLE), 
				"<img src=\"" + Images.getClasspathURL("images/icon-48.png") + "\"><br>" +
				"<span style=\"font-size:1.3em;\"><strong>" + TITLE + "</strong></span><br>" + 
				VERSION + "<br><br>" +
				"Copyright (c) 2015 - 2018<br>" +
				"Jacob van Mourik<br>" + 
				"MIT Licensed");
	}
	
	public void showVersionDialog(boolean newVersionOnly) {
		executor.execute(() -> {
			GithubRepoReleaseData data;
			String content;
			try {
				data = GithubRepoUtil.getLatestRelease(Editor.GITHUB_USER, Editor.GITHUB_PROJECT).get(30, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				data = null;
			}
			if (data != null && VERSION.compareToIgnoreCase(data.getTagName()) < 0) {
				content = MessageBundle.get("dialogs.version.new") + " " +
						"<strong>" + data.getTagName() + "</strong><br>" + 
						"<a href=\"" + data.getHtmlUrl() + "\">" + MessageBundle.get("dialogs.version.link") + "</a>";
			} else if (!newVersionOnly) {
				content = MessageBundle.get("dialogs.version.uptodate");
			} else {
				return;
			}
			Dialogs.showHtmlDialog(this, MessageBundle.get("dialogs.version.title"), content);		
		});
	}
	
	public boolean closeCurrentProject() {
		boolean result = true;
		if (dirty) {
			int confirm = JOptionPane.showConfirmDialog(this, 
					MessageBundle.get("dialogs.save.text"), 
					MessageBundle.get("dialogs.save.title"), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				result = saveProject();
			} else {
				result = confirm != JOptionPane.CANCEL_OPTION;
			}
		}
		if (result && project != null) {
			storeProjectState();
		}
		if (result && dirty) {
			setDirty(false);
		}
		return result;
	}
	
	public void openProjectDirectory() {
		if (project == null) return;
		try {
			Desktop.getDesktop().open(project.getPath().toFile());
		} catch (IOException ex) {
			log.error("Unable to open project directory " + project.getPath(), ex);
		}
	}
	
	public void launch() {
		restoreEditorState();
		
		if (settings.getEditorLanguage() != null) {
			Locale.setDefault(settings.getEditorLanguage());
		} else {
			Locale.setDefault(DEFAULT_LANGUAGE);
		}
		
		MessageBundle.loadResources();
		
		setupUI();
		setupFileDrop();
		setupGlobalKeyEventDispatcher();
		
		setPreferredSize(new Dimension(settings.getWindowWidth(), settings.getWindowHeight()));
		setLocation(settings.getWindowPositionX(), settings.getWindowPositionY());
		contentPane.setDividerLocation(settings.getWindowDeviderPosition());
		
    	pack();
    	setVisible(true);
    	
		List<String> dirs = settings.getHistory();
    	if (!dirs.isEmpty()) {
    		String lastDir = dirs.get(dirs.size()-1);
    		Path path = Paths.get(lastDir);
    		if (Files.exists(path)) {
    			importProject(path, false);
    		}
    	}
    	
    	if (project == null) {
    		updateHistory();
    	}
		if (project != null && project.hasResources()) {
			// Restore last expanded nodes
			List<String> expandedKeys = settings.getLastExpandedNodes();
			List<TranslationTreeNode> expandedNodes = expandedKeys.stream()
					.map(translationTree::getNodeByKey)
					.filter(n -> n != null)
					.collect(Collectors.toList());
			translationTree.expand(expandedNodes);
			// Restore last selected node
			String selectedKey = settings.getLastSelectedNode();
			TranslationTreeNode selectedNode = translationTree.getNodeByKey(selectedKey);
			if (selectedNode != null) {
				translationTree.setSelectionNode(selectedNode);
			}
		}
		
		if (settings.isCheckVersionOnStartup()) {
			showVersionDialog(true);			
		}
	}
	
	public void updateUI() {
		TranslationTreeNode selectedNode = translationTree.getSelectionNode();
		
		resourcesPanel.removeAll();
		resourceFields = resourceFields.stream().sorted().collect(Collectors.toList());
		resourceFields.forEach(field -> {
			Locale locale = field.getResource().getLocale();
			String label = locale != null ? locale.getDisplayName() : MessageBundle.get("resources.locale.default");
			field.setEnabled(selectedNode != null && selectedNode.isEditable() && 
					(selectedNode.isLeaf() || field.getResource().supportsParentValues()));
			field.setRows(settings.getDefaultInputHeight());
			resourcesPanel.add(Box.createVerticalStrut(5));
			resourcesPanel.add(new JLabel(label));
			resourcesPanel.add(Box.createVerticalStrut(5));
			resourcesPanel.add(field);
			resourcesPanel.add(Box.createVerticalStrut(10));
		});
		
		Container container = getContentPane();
		if (project != null) {
			container.add(contentPane);
			container.remove(introText);
			List<Resource> resources = project.getResources();
			editorMenu.setEnabled(true);
			editorMenu.setEditable(!resources.isEmpty());
			translationField.setEditable(!resources.isEmpty());
		} else {
			container.add(introText);
			container.remove(contentPane);
			editorMenu.setEnabled(false);
			editorMenu.setEditable(false);
			translationField.setEditable(false);
		}
		
		translationField.setVisible(settings.isKeyFieldEnabled());
		translationTree.setToggleClickCount(settings.isDoubleClickTreeToggling() ? 2 : 1);
		
		updateTitle();
		validate();
		repaint();
	}
	
	private boolean confirmNewTranslation(String oldKey, String newKey) {
		TranslationTreeNode newNode = translationTree.getNodeByKey(newKey);
		TranslationTreeNode oldNode = translationTree.getNodeByKey(oldKey);
		if (newNode != null) {
			boolean isReplace = newNode.isLeaf() || oldNode.isLeaf();
			boolean confirm = Dialogs.showConfirmDialog(this, 
					MessageBundle.get("dialogs.translation.conflict.title"), 
					MessageBundle.get("dialogs.translation.conflict.text." + (isReplace?"replace":"merge")),
					JOptionPane.WARNING_MESSAGE);
			if (!confirm) {
				return false;
			}
		}
		return confirmNewTranslation(newKey);
	}
	
	private boolean confirmNewTranslation(String key) {
		if (project == null || project.supportsResourceParentValues()) {
			return true;
		}
		// Check if there is an existing leaf node in the key path with one or more values
		key = ResourceKeys.withoutLastPart(key);
		while (!Strings.isNullOrEmpty(key)) {
			TranslationTreeNode node = translationTree.getNodeByKey(key);
			if (node != null && !node.isRoot() && node.isLeaf()) {
				boolean hasValue = project.getResources().stream().anyMatch(r -> {
					return !Strings.isNullOrEmpty(r.getTranslation(node.getKey()));
				});
				if (hasValue) {
					return Dialogs.showConfirmDialog(this, 
							MessageBundle.get("dialogs.translation.overwrite.title"), 
							MessageBundle.get("dialogs.translation.overwrite.text", node.getKey()),
							JOptionPane.WARNING_MESSAGE);
				}
			}
			key = ResourceKeys.withoutLastPart(key);
		}
		return true;
	}
	
	private void requestFocusInFirstResourceField() {
		resourceFields.stream().findFirst().ifPresent(f -> {
			f.requestFocusInWindow();
		});
	}
	
	private void clearUI() {
		translationField.clear();
		translationTree.clear();
		resourceFields.clear();
		updateUI();
	}
	
	private void setupUI() {
		Color borderColor = Colors.scale(UIManager.getColor("Panel.background"), .8f);
		
		setTitle(TITLE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new EditorWindowListener());
		
		setIconImages(Lists.newArrayList("512","256","128","64","48","32","24","20","16").stream()
				.map(size -> Images.loadFromClasspath("images/icon-" + size + ".png").getImage())
				.collect(Collectors.toList()));
		
        translationTree = new TranslationTree();
        translationTree.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
        translationTree.addTreeSelectionListener(new TranslationTreeNodeSelectionListener());
        translationTree.addMouseListener(new TranslationTreeMouseListener());
        
		translationField = new TranslationKeyField();
		translationField.addKeyListener(new TranslationFieldKeyListener());
		translationField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1,0,0,1,borderColor),
				((CompoundBorder)translationField.getBorder()).getInsideBorder()));
		
		JScrollPane translationsScrollPane = new JScrollPane(translationTree);
		translationsScrollPane.getViewport().setOpaque(false);
		translationsScrollPane.setOpaque(false);
		translationsScrollPane.setBorder(BorderFactory.createMatteBorder(0,0,0,1,borderColor));
		
		translationsPanel = new JPanel(new BorderLayout());
		translationsPanel.add(translationsScrollPane);
		translationsPanel.add(translationField, BorderLayout.SOUTH);
		
        resourcesPanel = new JScrollablePanel(true, false);
        resourcesPanel.setLayout(new BoxLayout(resourcesPanel, BoxLayout.Y_AXIS));
        resourcesPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        resourcesPanel.setOpaque(false);
        resourcesPanel.addMouseListener(new ResourcesPaneMouseListener());
        
        resourcesScrollPane = new JScrollPane(resourcesPanel);
        resourcesScrollPane.getViewport().setOpaque(false);
        resourcesScrollPane.setOpaque(false);
        resourcesScrollPane.setBorder(null);
        resourcesScrollPane.addMouseListener(new ResourcesPaneMouseListener());
		
		contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, translationsPanel, resourcesScrollPane);
		contentPane.setBorder(null);
		contentPane.setDividerSize(10);
		
		// Style the split pane divider if possible
		SplitPaneUI splitPaneUI = contentPane.getUI();
	    if (splitPaneUI instanceof BasicSplitPaneUI) {
	        BasicSplitPaneDivider divider = ((BasicSplitPaneUI)splitPaneUI).getDivider();
	        divider.setBorder(null);
			resourcesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,20));
	    }
	    
		introText = new JLabel("<html><body style=\"text-align:center; padding:30px;\">" + 
				MessageBundle.get("core.intro.text") + "</body></html>");
		introText.setOpaque(true);
		introText.setFont(introText.getFont().deriveFont(28f));
		introText.setHorizontalTextPosition(JLabel.CENTER);
		introText.setVerticalTextPosition(JLabel.BOTTOM);
		introText.setHorizontalAlignment(JLabel.CENTER);
		introText.setVerticalAlignment(JLabel.CENTER);
		introText.setForeground(getBackground().darker());
		introText.setIcon(Images.loadFromClasspath("images/icon-intro.png"));
		
		Container container = getContentPane();
		container.add(introText);
		
		editorMenu = new EditorMenuBar(this, translationTree);
		setJMenuBar(editorMenu);
	}
	
	private void setupFileDrop() {
		new JFileDrop(getContentPane(), null, new JFileDrop.Listener() {
			@Override
			public void filesDropped(java.io.File[] files) {
				try {
					Path path = Paths.get(files[0].getCanonicalPath());
					importProject(path, true);
                } catch (IOException e) {
                	log.error("Error importing resources via file drop", e);
                	showError(MessageBundle.get("resources.open.error.multiple"));
                }
            }
        });
	}
	
	private void setupGlobalKeyEventDispatcher() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getID() != KeyEvent.KEY_PRESSED || !e.isAltDown() ||
				(SystemUtils.IS_OS_MAC && !e.isMetaDown()) ||
				(!SystemUtils.IS_OS_MAC && !e.isShiftDown())) {
				return false;
			}
			TreePath selected = translationTree.getSelectionPath();
			if (selected == null) {
				return false;
			}
			boolean result = false;
			int row = translationTree.getRowForPath(selected);
			switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				if (!translationTree.isExpanded(row)) {
					translationTree.expandRow(row);						
				}
				result = true;
				break;
			case KeyEvent.VK_LEFT:
				if (translationTree.isCollapsed(row)) {
					translationTree.setSelectionPath(selected.getParentPath());
				} else {
					translationTree.collapseRow(row);						
				}
				result = true;
				break;
			case KeyEvent.VK_UP:
				TreePath prev = translationTree.getPathForRow(Math.max(0, row-1));
				if (prev != null) {
					translationTree.setSelectionPath(prev);
				}
				result = true;
				break;
			case KeyEvent.VK_DOWN:
				TreePath next = translationTree.getPathForRow(row+1);
				if (next != null) {
					translationTree.setSelectionPath(next);
				}
				result = true;
				break;
			}
			if (result && !resourceFields.isEmpty()) {
				Component comp = getFocusOwner();
				if (comp != null && (comp instanceof ResourceField || comp.equals(this))) {
					TranslationTreeNode current = translationTree.getSelectionNode();
					if (!current.isLeaf() || current.isRoot()) {
						requestFocusInWindow();
					} else if (comp.equals(this)) {
						requestFocusInFirstResourceField();
					}
				}
			}
			return result;
		});
	}
	
	private void setupResource(Resource resource) {
		resource.addListener(e -> setDirty(true));
		ResourceField field = new ResourceField(resource);
		field.addKeyListener(new ResourceFieldKeyListener());
		resourceFields.add(field);
	}
	
	private void updateHistory() {
		List<String> recentDirs = settings.getHistory();
		if (project != null) {
			String path = project.getPath().toString();
			recentDirs.remove(path);
			recentDirs.add(path);
			if (recentDirs.size() > 10) {
				recentDirs.remove(0);
			}
			settings.setHistory(recentDirs);			
		}
		editorMenu.setRecentItems(Lists.reverse(recentDirs));
	}
	
	private void updateTitle() {
		String dirtyPart = dirty ? "*" : "";
		String projectPart = "";
		if (project != null) {
			projectPart = project.getPath().toString() + " [" + project.getResourceType() + "] - ";
		}
		setTitle(dirtyPart + projectPart + TITLE);
	}
	
	private void showError(String message) {
		Dialogs.showErrorDialog(this, MessageBundle.get("dialogs.error.title"), message);
	}
	
	private void updateTreeNodeStatuses() {
		if (project == null) return;
		Set<String> keys = project.getResources().stream()
				.flatMap(r -> r.getTranslations().keySet().stream())
				.filter(key -> project.getResources().stream().anyMatch(r -> !r.hasTranslation(key)))
				.collect(Collectors.toSet());
		translationTree.updateNodes(keys);
	}
	
	private void updateTreeNodeStatus(String key) {
		if (project == null) return;
		boolean hasError = project.getResources().stream().anyMatch(r -> !r.hasTranslation(key));
		translationTree.updateNode(key, hasError);
	}
	
	private boolean saveResource(Resource resource) {
		if (project != null) {
			try {
				Resources.write(resource, !project.isMinifyResources(), project.isFlattenJSON(), project.isPreserveComments(), project.isUseSingleQuotes());
			} catch (ChecksumException e) {
				boolean confirm = Dialogs.showConfirmDialog(this, 
						MessageBundle.get("dialogs.save.checksum.title"), 
						MessageBundle.get("dialogs.save.checksum.text", resource.getPath()),
						JOptionPane.WARNING_MESSAGE);
				if (confirm) {
					resource.setChecksum(null);
					saveResource(resource);
				} else {
					return false;
				}
			} catch (IOException e) {
				log.error("Error saving resource file " + resource.getPath(), e);
				showError(MessageBundle.get("resources.write.error.single", resource.getPath().toString()));
				return false;
			}
		}
		return true;
	}
	
	private void storeProjectState() {
		ExtendedProperties props = new ExtendedProperties();
		props.setProperty("minify_resources", project.isMinifyResources());
		props.setProperty("flatten_json", project.isFlattenJSON());
		props.setProperty("preserve_comments", project.isPreserveComments());
		props.setProperty("use_single_quotes", project.isUseSingleQuotes());
		props.setProperty("resource_type", project.getResourceType().toString());
		props.setProperty("resource_definition", project.getResourceFileDefinition());
		props.setProperty("resource_structure", project.getResourceFileStructure());
		props.store(Paths.get(project.getPath().toString(), PROJECT_FILE));
	}
	
	private void restoreProjectState(EditorProject project) {
		ExtendedProperties props = new ExtendedProperties();
		Path path = Paths.get(project.getPath().toString(), PROJECT_FILE);
		if (Files.exists(path)) {
			props.load(Paths.get(project.getPath().toString(), PROJECT_FILE));
			project.setMinifyResources(props.getBooleanProperty("minify_resources", settings.isMinifyResources()));
			project.setFlattenJSON(props.getBooleanProperty("flatten_json", settings.isFlattenJSON()));
			project.setPreservCommnets(props.getBooleanProperty("preserve_comments", settings.isPreserveCommnets()));
			project.setUseSingleQuotes(props.getBooleanProperty("use_single_quotes", settings.isUseSingleQuotes()));
			project.setResourceType(props.getEnumProperty("resource_type", ResourceType.class));
			String resourceName = props.getProperty("resource_name"); // for backwards compatibility
			if (Strings.isNullOrEmpty(resourceName)) {
				project.setResourceFileDefinition(props.getProperty("resource_definition", settings.getResourceFileDefinition()));
				project.setResourceFileStructure(props.getEnumProperty("resource_structure", FileStructure.class, settings.getResourceFileStructure()));
			} else {
				// for backwards compatibility
				project.setResourceFileDefinition(resourceName);
				project.setResourceFileStructure(project.getResourceType() == ResourceType.Properties ? FileStructure.Flat : FileStructure.Nested);
			}
		} else {
			project.setMinifyResources(settings.isMinifyResources());
			project.setFlattenJSON(settings.isFlattenJSON());
			project.setResourceFileDefinition(settings.getResourceFileDefinition());
			project.setResourceFileStructure(settings.getResourceFileStructure());
		}
	}
	
	private void storeEditorState() {
		ExtendedProperties props = new ExtendedProperties();
		props.setProperty("window_width", getWidth());
		props.setProperty("window_height", getHeight());
		props.setProperty("window_pos_x", getX());
		props.setProperty("window_pos_y", getY());
		props.setProperty("window_div_pos", contentPane.getDividerLocation());
		props.setProperty("minify_resources", settings.isMinifyResources());
		props.setProperty("flatten_json", settings.isFlattenJSON());
		props.setProperty("preserve_comments", project.isPreserveComments());
		props.setProperty("use_single_quotes", project.isUseSingleQuotes());
		props.setProperty("resource_definition", settings.getResourceFileDefinition());
		props.setProperty("resource_structure", settings.getResourceFileStructure());
		props.setProperty("check_version", settings.isCheckVersionOnStartup());
		props.setProperty("default_input_height", settings.getDefaultInputHeight());
		props.setProperty("key_field_enabled", settings.isKeyFieldEnabled());
		props.setProperty("double_click_tree_toggling", settings.isDoubleClickTreeToggling());
		if (settings.getEditorLanguage() != null) {
			props.setProperty("editor_language", settings.getEditorLanguage());
		}
		if (!settings.getHistory().isEmpty()) {
			props.setProperty("history", settings.getHistory());
		}
		if (project != null) {
			// Store keys of expanded nodes
			List<String> expandedNodeKeys = translationTree.getExpandedNodes().stream()
					.map(TranslationTreeNode::getKey)
					.collect(Collectors.toList());
			props.setProperty("last_expanded", expandedNodeKeys);
			// Store key of selected node
			TranslationTreeNode selectedNode = translationTree.getSelectionNode();
			props.setProperty("last_selected", selectedNode == null ? "" : selectedNode.getKey());
		}
		props.store(Paths.get(SETTINGS_DIR, SETTINGS_FILE));
	}
	
	private void restoreEditorState() {
		ExtendedProperties props = new ExtendedProperties();
		props.load(Paths.get(SETTINGS_DIR, SETTINGS_FILE));
		settings.setWindowWidth(props.getIntegerProperty("window_width", 1024));
		settings.setWindowHeight(props.getIntegerProperty("window_height", 768));
		settings.setWindowPositionX(props.getIntegerProperty("window_pos_x", 0));
		settings.setWindowPositionY(props.getIntegerProperty("window_pos_y", 0));
		settings.setWindowDeviderPosition(props.getIntegerProperty("window_div_pos", 250));
		settings.setCheckVersionOnStartup(props.getBooleanProperty("check_version", true));
		settings.setDefaultInputHeight(props.getIntegerProperty("default_input_height", 5));
		settings.setKeyFieldEnabled(props.getBooleanProperty("key_field_enabled", true));
		settings.setDoubleClickTreeToggling(props.getBooleanProperty("double_click_tree_toggling", false));
		settings.setMinifyResources(props.getBooleanProperty("minify_resources", false));
		settings.setFlattenJSON(props.getBooleanProperty("flatten_json", false));
		settings.setPreserveCommnets(props.getBooleanProperty("preserve_comments", false));
		settings.setUseSingleQuotes(props.getBooleanProperty("use_single_quotes", false));
		settings.setHistory(props.getListProperty("history"));
		settings.setLastExpandedNodes(props.getListProperty("last_expanded"));
		settings.setLastSelectedNode(props.getProperty("last_selected"));
		settings.setResourceFileDefinition(props.getProperty("resource_definition", EditorSettings.DEFAULT_RESOURCE_FILE_DEFINITION));
		settings.setResourceFileStructure(props.getEnumProperty("resource_structure", FileStructure.class, FileStructure.Flat));
		settings.setEditorLanguage(props.getLocaleProperty("editor_language"));
	}
	
	private class TranslationTreeMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			showPopupMenu(e);
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			showPopupMenu(e);
	    }
		
		private void showPopupMenu(MouseEvent e) {
			if (!e.isPopupTrigger() || project == null || !project.hasResources()) {
				return;
			}
			TreePath path = translationTree.getPathForLocation(e.getX(), e.getY());
	    	if (path == null) {
	    		TranslationTreeMenu menu = new TranslationTreeMenu(Editor.this, translationTree);
	    		menu.show(e.getComponent(), e.getX(), e.getY());
	    	} else {
	    		translationTree.setSelectionPath(path);
	    		TranslationTreeNode node = translationTree.getSelectionNode();
	    		TranslationTreeNodeMenu menu = new TranslationTreeNodeMenu(Editor.this, node);
	    		menu.show(e.getComponent(), e.getX(), e.getY());
	    	}
	    }
	}
	
	private class TranslationTreeNodeSelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TranslationTreeNode node = translationTree.getSelectionNode();
			
			if (node != null) {
				// Store scroll position
				int scrollValue = resourcesScrollPane.getVerticalScrollBar().getValue();
				
				// Update UI values
				String key = node.getKey();
				translationField.setValue(key);
				resourceFields.forEach(f -> {
					f.setValue(key);
					f.setEnabled(node.isEditable() && (node.isLeaf() || f.getResource().supportsParentValues()));
				});
				
				// Restore scroll position and foc
				SwingUtilities.invokeLater(() -> resourcesScrollPane.getVerticalScrollBar().setValue(scrollValue));
			}
		}
	}
	
	private class TranslationFieldKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				TranslationKeyField field = (TranslationKeyField) e.getSource();
				String key = field.getValue();
				addTranslation(key);						
			}
		}
	}
	
	private class ResourcesPaneMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			showPopupMenu(e);
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			showPopupMenu(e);
	    }
		
		private void showPopupMenu(MouseEvent e) {
			if (!e.isPopupTrigger() || project == null) {
				return;
			}
			ResourcesPaneMenu menu = new ResourcesPaneMenu(Editor.this);
    		menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	}
	
	private class ResourceFieldKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			TranslationTreeNode node = translationTree.getSelectionNode();
			ResourceField field = (ResourceField) e.getSource();
			String key = node.getKey();
			String value = field.getValue();
			field.getResource().storeTranslation(key, value);
			updateTreeNodeStatus(key);
		}
	}
	
	private class EditorWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			if (closeCurrentProject()) {
				storeEditorState();
				System.exit(0);
			}
  		}
	}
}