package com.jvms.i18neditor.editor;

import java.util.List;
import java.util.Locale;

import com.jvms.i18neditor.FileStructure;

/**
 * This class represents the editor settings.
 * 
 * @author Jacob van Mourik
 */
public class EditorSettings {
	public final static String DEFAULT_RESOURCE_FILE_DEFINITION = "translations{_LOCALE}";
	
	private int windowPositionX;
	private int windowPositionY;
	private int windowDeviderPosition;
	private int windowWidth;
	private int windowHeight;
	private boolean minifyResources;
	private boolean flattenJSON;
	private boolean preserveCommnets;
	private boolean useSingleQuotes; 
	private List<String> history;
	private List<String> lastExpandedNodes;
	private String lastSelectedNode;
	private boolean checkVersionOnStartup;
	private int defaultInputHeight;
	private boolean keyFieldEnabled;
	private boolean doubleClickTreeToggling;
	private String resourceFileDifinition;
	private Locale editorLanguage;
	private FileStructure resourceFileStructure;
	
	public int getWindowPositionX() {
		return windowPositionX;
	}
	
	public void setWindowPositionX(int windowPositionX) {
		this.windowPositionX = windowPositionX;
	}
	
	public int getWindowPositionY() {
		return windowPositionY;
	}
	
	public void setWindowPositionY(int windowPositionY) {
		this.windowPositionY = windowPositionY;
	}
	
	public int getWindowDeviderPosition() {
		return windowDeviderPosition;
	}
	
	public void setWindowDeviderPosition(int deviderPosition) {
		this.windowDeviderPosition = deviderPosition;
	}
	
	public int getWindowWidth() {
		return windowWidth;
	}
	
	public void setWindowWidth(int width) {
		this.windowWidth = width;
	}
	
	public int getWindowHeight() {
		return windowHeight;
	}
	
	public void setWindowHeight(int height) {
		this.windowHeight = height;
	}

	public List<String> getHistory() {
		return history;
	}

	public void setHistory(List<String> history) {
		this.history = history;
	}
	
	public List<String> getLastExpandedNodes() {
		return lastExpandedNodes;
	}

	public void setLastExpandedNodes(List<String> lastExpandedNodes) {
		this.lastExpandedNodes = lastExpandedNodes;
	}

	public String getLastSelectedNode() {
		return lastSelectedNode;
	}

	public void setLastSelectedNode(String lastSelectedNode) {
		this.lastSelectedNode = lastSelectedNode;
	}

	public String getResourceFileDefinition() {
		return resourceFileDifinition;
	}

	public void setResourceFileDefinition(String resourceFileDifinition) {
		this.resourceFileDifinition = resourceFileDifinition;
	}

	public boolean isMinifyResources() {
		return minifyResources;
	}

	public void setMinifyResources(boolean minifyResources) {
		this.minifyResources = minifyResources;
	}

	public boolean isCheckVersionOnStartup() {
		return checkVersionOnStartup;
	}

	public void setCheckVersionOnStartup(boolean checkVersionOnStartup) {
		this.checkVersionOnStartup = checkVersionOnStartup;
	}

	public int getDefaultInputHeight() {
		return defaultInputHeight;
	}

	public void setDefaultInputHeight(int rows) {
		this.defaultInputHeight = rows;
	}

	public boolean isKeyFieldEnabled() {
		return keyFieldEnabled;
	}

	public void setKeyFieldEnabled(boolean keyFieldEnabled) {
		this.keyFieldEnabled = keyFieldEnabled;
	}

	public boolean isDoubleClickTreeToggling() {
		return doubleClickTreeToggling;
	}

	public void setDoubleClickTreeToggling(boolean doubleClickTreeToggling) {
		this.doubleClickTreeToggling = doubleClickTreeToggling;
	}
	
	public boolean isFlattenJSON() {
		return flattenJSON;
	}

	public void setFlattenJSON(boolean flattenJSON) {
		this.flattenJSON = flattenJSON;
	}
	
	public boolean isPreserveCommnets() {
		return preserveCommnets;
	}

	public void setPreserveCommnets(boolean preservCommnets) {
		this.preserveCommnets = preservCommnets;
	}
	
	public boolean isUseSingleQuotes() {
		return useSingleQuotes;
	}

	public void setUseSingleQuotes(boolean useSingleQuotes) {
		this.useSingleQuotes = useSingleQuotes;
	}

	public Locale getEditorLanguage() {
		return editorLanguage;
	}

	public void setEditorLanguage(Locale editorLanguage) {
		this.editorLanguage = editorLanguage;
	}

	public FileStructure getResourceFileStructure() {
		return resourceFileStructure;
	}

	public void setResourceFileStructure(FileStructure resourceFileStructure) {
		this.resourceFileStructure = resourceFileStructure;
	}
}
