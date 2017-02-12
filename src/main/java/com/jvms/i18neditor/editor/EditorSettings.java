package com.jvms.i18neditor.editor;

import java.util.List;

/**
 * This class represents the editor settings.
 * 
 * @author Jacob van Mourik
 */
public class EditorSettings {
	private int windowPositionX;
	private int windowPositionY;
	private int windowDeviderPosition;
	private int windowWidth;
	private int windowHeight;
	private String resourceName;
	private boolean minifyResources;
	private List<String> history;
	private List<String> lastExpandedNodes;
	private String lastSelectedNode;
	private boolean checkVersionOnStartup;
	private int defaultInputHeight;
	private boolean keyFieldEnabled;
	private boolean doubleClickTreeToggling;
	
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

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
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
}
