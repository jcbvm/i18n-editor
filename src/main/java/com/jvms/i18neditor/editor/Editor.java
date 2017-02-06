package com.jvms.i18neditor.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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
import javax.swing.border.CompoundBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jvms.i18neditor.LookAndFeel;
import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.ResourceType;
import com.jvms.i18neditor.swing.JFileDrop;
import com.jvms.i18neditor.swing.JScrollablePanel;
import com.jvms.i18neditor.swing.util.Dialogs;
import com.jvms.i18neditor.util.ExtendedProperties;
import com.jvms.i18neditor.util.GithubRepoUtil;
import com.jvms.i18neditor.util.GithubRepoUtil.GithubRepoReleaseData;
import com.jvms.i18neditor.util.MessageBundle;
import com.jvms.i18neditor.util.ResourceKeys;
import com.jvms.i18neditor.util.Resources;

/**
 * This class represents the main class of the editor.
 * 
 * @author Jacob
 */
public class Editor extends JFrame {
	private final static long serialVersionUID = 1113029729495390082L;
	private final static Logger log = LoggerFactory.getLogger(Editor.class);
	
	public final static String TITLE = "i18n-editor";
	public final static String VERSION = "1.0.0-beta.3";
	public final static String GITHUB_REPO = "jcbvm/i18n-editor";
	public final static String DEFAULT_RESOURCE_NAME = "translations";
	public final static String PROJECT_FILE = ".i18n-editor-metadata";
	public final static String SETTINGS_FILE = ".i18n-editor";
	public final static String SETTINGS_DIR = System.getProperty("user.home");
	
	private EditorProject project;
	private EditorSettings settings = new EditorSettings();
	private ExecutorService executor = Executors.newCachedThreadPool();
	private boolean dirty;
	
	private EditorMenuBar editorMenu;
	private JSplitPane contentPane;
	private JLabel introText;
	private JPanel translationsPanel;
	private JScrollPane resourcesScrollPane;
	private TranslationTree translationTree;
	private TranslationField translationField;
	private JPanel resourcesPanel;
	private List<ResourceField> resourceFields = Lists.newLinkedList();
	
	public Editor() {
		super();
		setupUI();
		setupFileDrop();
	}
	
	public void createProject(Path dir, ResourceType type) {
		try {
			Preconditions.checkArgument(Files.isDirectory(dir));
			
			if (!closeCurrentProject()) {
				return;
			}
			
			List<Resource> resourceList = Resources.get(dir, settings.getResourceName(), Optional.empty());
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
			
			if (type == ResourceType.Properties) {
				Resource resource = Resources.create(dir, type, Optional.empty(), project.getResourceName());
				setupResource(resource);
				project.addResource(resource);
			}
			translationTree.setModel(new TranslationTreeModel(Lists.newLinkedList()));
			
			updateHistory();
			updateUI();
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
			List<Resource> resourceList = Resources.get(dir, project.getResourceName(), type);
			List<String> keyList = Lists.newLinkedList();
			
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
						Resources.load(resource);
						setupResource(resource);
						project.addResource(resource);
					} catch (IOException e) {
						log.error("Error importing resource file " + resource.getPath(), e);
						showError(MessageBundle.get("resources.import.error.single", resource.getPath().toString()));
					}
				});
				Map<String,String> keys = Maps.newTreeMap();
				project.getResources().forEach(resource -> keys.putAll(resource.getTranslations()));
				keyList.addAll(keys.keySet());
			}
			translationTree.setModel(new TranslationTreeModel(keyList));
			
			updateHistory();
			updateUI();
		} catch (IOException e) {
			log.error("Error importing resource files", e);
			showError(MessageBundle.get("resources.import.error.multiple"));
		}
	}
	
	public boolean saveProject() {
		boolean error = false;
		if (project != null) {
			for (Resource resource : project.getResources()) {
				try {
					Resources.write(resource, !project.isMinifyResources());
				} catch (IOException e) {
					error = true;
					log.error("Error saving resource file " + resource.getPath(), e);
					showError(MessageBundle.get("resources.write.error.single", resource.getPath().toString()));
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
		TranslationTreeNode node = translationTree.getNodeByKey(key);
		if (node != null) {
			translationTree.setSelectedNode(node);
		} else {
			translationTree.addNodeByKey(key);			
			if (project != null) {
				project.getResources().forEach(resource -> resource.storeTranslation(key, ""));				
			}
		}
	}
	
	public void removeTranslationKey(String key) {
		translationTree.removeNodeByKey(key);
		if (project != null) {
			project.getResources().forEach(resource -> resource.removeTranslation(key));
		}
	}
	
	public void renameTranslationKey(String key, String newKey) {
		translationTree.renameNodeByKey(key, newKey);
		if (project != null) {
			project.getResources().forEach(resource -> resource.renameTranslation(key, newKey));
		}
	}
	
	public void duplicateTranslationKey(String key, String newKey) {
		translationTree.duplicateNodeByKey(key, newKey);
		if (project != null) {
			project.getResources().forEach(resource -> resource.duplicateTranslation(key, newKey));
		}
	}
	
	public EditorProject getProject() {
		return project;
	}
	
	public EditorSettings getSettings() {
		return settings;
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
		Path path = project.getPath();
		ResourceType type = project.getResourceType();
		while (localeString != null && localeString.isEmpty()) {
			localeString = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.locale.add.title", type),
					MessageBundle.get("dialogs.locale.add.text"),
					JOptionPane.QUESTION_MESSAGE);
			if (localeString != null) {
				localeString = localeString.trim();
				if (localeString.isEmpty()) {
					showError(MessageBundle.get("dialogs.locale.add.error.invalid"));
				} else {
					try {
						Locale locale = LocaleUtils.toLocale(localeString);
						Resource resource = Resources.create(path, type, Optional.of(locale), project.getResourceName());
						setupResource(resource);
						project.addResource(resource);
						updateUI();
					} catch (IOException e) {
						log.error("Error creating new locale", e);
						showError(MessageBundle.get("dialogs.locale.add.error.create"));
					}
				}
			}
		}
	}
	
	public void showRenameTranslationDialog(String key) {
		String newKey = "";
		while (newKey != null && newKey.isEmpty()) {
			newKey = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.translation.rename.title"),
					MessageBundle.get("dialogs.translation.rename.text"),
					JOptionPane.QUESTION_MESSAGE, key, true);
			if (newKey != null) {
				if (!ResourceKeys.isValid(newKey)) {
					showError(MessageBundle.get("dialogs.translation.rename.error"));
				} else {
					TranslationTreeNode newNode = translationTree.getNodeByKey(newKey);
					TranslationTreeNode oldNode = translationTree.getNodeByKey(key);
					if (newNode != null) {
						boolean isReplace = newNode.isLeaf() || oldNode.isLeaf();
						boolean confirm = Dialogs.showConfirmDialog(this, 
								MessageBundle.get("dialogs.translation.conflict.title"), 
								MessageBundle.get("dialogs.translation.conflict.text." + (isReplace ? "replace" : "merge")),
								JOptionPane.WARNING_MESSAGE);
						if (confirm) {
							renameTranslationKey(key, newKey);
						}
					} else {
						renameTranslationKey(key, newKey);
					}
				}
			}
		}
	}
	
	public void showDuplicateTranslationDialog(String key) {
		String newKey = "";
		while (newKey != null && newKey.isEmpty()) {
			newKey = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.translation.duplicate.title"),
					MessageBundle.get("dialogs.translation.duplicate.text"),
					JOptionPane.QUESTION_MESSAGE, key, true);
			if (newKey != null) {
				newKey = newKey.trim();
				if (!ResourceKeys.isValid(newKey)) {
					showError(MessageBundle.get("dialogs.translation.duplicate.error"));
				} else {
					TranslationTreeNode newNode = translationTree.getNodeByKey(newKey);
					TranslationTreeNode oldNode = translationTree.getNodeByKey(key);
					if (newNode != null) {
						boolean isReplace = newNode.isLeaf() || oldNode.isLeaf();
						boolean confirm = Dialogs.showConfirmDialog(this, 
								MessageBundle.get("dialogs.translation.conflict.title"), 
								MessageBundle.get("dialogs.translation.conflict.text." + (isReplace ? "replace" : "merge")),
								JOptionPane.WARNING_MESSAGE);
						if (confirm) {
							duplicateTranslationKey(key, newKey);
						}
					} else {
						duplicateTranslationKey(key, newKey);
					}
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
		while (newKey != null && newKey.isEmpty()) {
			newKey = Dialogs.showInputDialog(this,
					MessageBundle.get("dialogs.translation.add.title"),
					MessageBundle.get("dialogs.translation.add.text"),
					JOptionPane.QUESTION_MESSAGE, key, false);
			if (newKey != null) {
				newKey = newKey.trim();
				if (!ResourceKeys.isValid(newKey)) {
					showError(MessageBundle.get("dialogs.translation.add.error"));
				} else {
					addTranslationKey(newKey);
				}
			}
		}
	}
	
	public void showFindTranslationDialog() {
		String key = Dialogs.showInputDialog(this,
				MessageBundle.get("dialogs.translation.find.title"),
				MessageBundle.get("dialogs.translation.find.text"),
				JOptionPane.QUESTION_MESSAGE);
		if (key != null) {
			TranslationTreeNode node = translationTree.getNodeByKey(key.trim());
			if (node == null) {
				Dialogs.showWarningDialog(this, 
						MessageBundle.get("dialogs.translation.find.title"), 
						MessageBundle.get("dialogs.translation.find.error"));
			} else {
				translationTree.setSelectedNode(node);
			}
		}
	}
	
	public void showAboutDialog() {
		Dialogs.showHtmlDialog(this, MessageBundle.get("dialogs.about.title", TITLE), 
				"<span style=\"font-size:1.5em;\"><strong>" + TITLE + "</strong></span><br>" + 
				VERSION + "<br><br>" +
				"Copyright (c) 2015 - 2017<br>" +
				"Jacob van Mourik<br>" + 
				"MIT Licensed");
	}
	
	public void showVersionDialog(boolean newVersionOnly) {
		executor.execute(() -> {
			GithubRepoReleaseData data;
			String content;
			try {
				data = GithubRepoUtil.getLatestRelease(GITHUB_REPO).get(30, TimeUnit.SECONDS);
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
				translationTree.setSelectedNode(selectedNode);
			}
		}
		
		if (settings.isCheckVersionOnStartup()) {
			showVersionDialog(true);			
		}
	}
	
	private void clearUI() {
		translationField.clear();
		translationTree.clear();
		resourceFields.clear();
		updateUI();
	}
	
	private void setupUI() {
		setTitle(TITLE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new EditorWindowListener());
		
		setIconImages(Lists.newArrayList("512","256","128","64","48","32","24","20","16").stream()
				.map(size -> getClasspathImage("images/icon-" + size + ".png"))
				.collect(Collectors.toList()));
		
        translationTree = new TranslationTree();
        translationTree.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
        translationTree.addTreeSelectionListener(new TranslationTreeNodeSelectionListener());
        translationTree.addMouseListener(new TranslationTreeMouseListener());
        
		translationField = new TranslationField();
		translationField.addKeyListener(new TranslationFieldKeyListener());
		translationField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1,0,0,1,LookAndFeel.TEXTFIELD_BORDER_COLOR),
				((CompoundBorder)translationField.getBorder()).getInsideBorder()));
		
		JScrollPane translationsScrollPane = new JScrollPane(translationTree);
		translationsScrollPane.getViewport().setOpaque(false);
		translationsScrollPane.setOpaque(false);
		translationsScrollPane.setBorder(
				BorderFactory.createMatteBorder(0,0,0,1,LookAndFeel.TEXTFIELD_BORDER_COLOR));
		
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
		introText.setIcon(new ImageIcon(getClasspathImage("images/icon-intro.png")));
		
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
	
	private void setupResource(Resource resource) {
		resource.addListener(e -> setDirty(true));
		ResourceField field = new ResourceField(resource);
		field.addKeyListener(new ResourceFieldKeyListener());
		resourceFields.add(field);
	}
	
	private void updateUI() {
		TranslationTreeNode selectedNode = translationTree.getSelectedNode();
		
		resourcesPanel.removeAll();
		resourceFields.stream().sorted().forEach(field -> {
			Locale locale = field.getResource().getLocale();
			String label = locale != null ? locale.getDisplayName() : MessageBundle.get("resources.locale.default");
			field.setEditable(selectedNode != null && selectedNode.isEditable());
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
			translationTree.setEditable(!resources.isEmpty());
			translationField.setEditable(!resources.isEmpty());
		} else {
			container.add(introText);
			container.remove(contentPane);
			editorMenu.setEnabled(false);
			editorMenu.setEditable(false);
			translationTree.setEditable(false);
			translationField.setEditable(false);
		}
		
		updateTitle();
		validate();
		repaint();
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
	
	private Image getClasspathImage(String path) {
		return new ImageIcon(getClass().getClassLoader().getResource(path)).getImage();
	}
	
	private void storeProjectState() {
		ExtendedProperties props = new ExtendedProperties();
		props.setProperty("minify_resources", project.isMinifyResources());
		props.setProperty("resource_name", project.getResourceName());
		props.setProperty("resource_type", project.getResourceType().toString());
		props.store(Paths.get(project.getPath().toString(), PROJECT_FILE));
	}
	
	private void restoreProjectState(EditorProject project) {
		ExtendedProperties props = new ExtendedProperties();
		Path path = Paths.get(project.getPath().toString(), PROJECT_FILE);
		if (Files.exists(path)) {
			props.load(Paths.get(project.getPath().toString(), PROJECT_FILE));
			project.setMinifyResources(props.getBooleanProperty("minify_resources", settings.isMinifyResources()));
			project.setResourceName(props.getProperty("resource_name", settings.getResourceName()));
			project.setResourceType(props.getEnumProperty("resource_type", ResourceType.class));
		} else {
			project.setResourceName(settings.getResourceName());
			project.setMinifyResources(settings.isMinifyResources());
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
		props.setProperty("resource_name", settings.getResourceName());
		props.setProperty("check_version", settings.isCheckVersionOnStartup());
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
			TranslationTreeNode selectedNode = translationTree.getSelectedNode();
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
		settings.setHistory(props.getListProperty("history"));
		settings.setLastExpandedNodes(props.getListProperty("last_expanded"));
		settings.setLastSelectedNode(props.getProperty("last_selected"));
		settings.setMinifyResources(props.getBooleanProperty("minify_resources", false));
		settings.setResourceName(props.getProperty("resource_name", DEFAULT_RESOURCE_NAME));
		settings.setCheckVersionOnStartup(props.getBooleanProperty("check_version", true));
	}
	
	private class TranslationTreeMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			showPopupMenu(e);
	    }
		
		@Override
		public void mousePressed(MouseEvent e) {
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
	    		TranslationTreeNode node = translationTree.getSelectedNode();
	    		TranslationTreeNodeMenu menu = new TranslationTreeNodeMenu(Editor.this, node);
	    		menu.show(e.getComponent(), e.getX(), e.getY());
	    	}
		}
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
				translationField.setValue(key);
				resourceFields.forEach(f -> {
					f.setValue(key);
					f.setEditable(node.isEditable());
				});
				
				// Restore scroll position
				SwingUtilities.invokeLater(() -> resourcesScrollPane.getVerticalScrollBar().setValue(scrollValue));
			}
		}
	}
	
	private class TranslationFieldKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				TranslationField field = (TranslationField) e.getSource();
				String key = field.getValue();
				if (ResourceKeys.isValid(key)) {
					addTranslationKey(key);						
				}
			}
		}
	}
	
	private class ResourcesPaneMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			showPopupMenu(e);
	    }
		
		@Override
		public void mousePressed(MouseEvent e) {
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
			ResourceField field = (ResourceField) e.getSource();
			String key = translationTree.getSelectedNode().getKey();
			String value = field.getValue();
			field.getResource().storeTranslation(key, value);
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
