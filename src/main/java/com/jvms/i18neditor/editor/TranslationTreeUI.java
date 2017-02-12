package com.jvms.i18neditor.editor;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import com.jvms.i18neditor.editor.TranslationTreeToggleIcon.ToggleIconType;

/**
 * This class represents a default UI for the translation tree.
 * 
 * @author Jacob van Mourik
 */
public class TranslationTreeUI extends BasicTreeUI {
	private TranslationTreeToggleIcon expandedIcon = new TranslationTreeToggleIcon(ToggleIconType.Expanded);
	private TranslationTreeToggleIcon collapsedIcon = new TranslationTreeToggleIcon(ToggleIconType.Collapsed);
	
	@Override
	protected void toggleExpandState(TreePath path) {
		// do nothing
	}
	
	@Override
    protected void paintVerticalLine(Graphics g, JComponent c, int y, int left, int right) {}
	
	@Override
    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {}
	
    @Override
    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {}
    
    @Override
    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, 
    		TreePath path, int row, boolean expanded, boolean hasBeenExpanded, boolean leaf) {}
    
	@Override
    public Rectangle getPathBounds(JTree tree, TreePath path) {
        if (tree != null && treeState != null) {
            return getPathBounds(path, tree.getInsets(), new Rectangle());
        }
        return null;
    }
	
	@Override
    public Icon getCollapsedIcon() {
        return collapsedIcon;
    }
	
    @Override
    public Icon getExpandedIcon() {
        return expandedIcon;
    }
	
    private Rectangle getPathBounds(TreePath path, Insets insets, Rectangle bounds) {
        bounds = treeState.getBounds(path, bounds);
        if (bounds != null) {
        	bounds.x = 0;
            bounds.y += insets.top;
            bounds.width = tree.getWidth();
        }
        return bounds;
    }
}
