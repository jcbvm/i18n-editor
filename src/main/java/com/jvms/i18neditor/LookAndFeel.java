package com.jvms.i18neditor;

import java.awt.Color;

import javax.swing.UIManager;

/**
 * This class represents custom look and feel constants.
 * 
 * @author Jacob
 */
public class LookAndFeel {
	public final static Color BORDER_COLOR = Color.LIGHT_GRAY;
	public final static Color TREE_SELECTION_BACKGROUND = UIManager.getColor("Panel.background");
	public final static Color TREE_SELECTION_FOREGROUND = UIManager.getColor("Tree.foreground");
	public final static Color TREE_WARNING_STATUS_COLOR = new Color(220,160,0);
}
