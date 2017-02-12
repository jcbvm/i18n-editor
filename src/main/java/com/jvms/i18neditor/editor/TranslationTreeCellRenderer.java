package com.jvms.i18neditor.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.jvms.i18neditor.editor.TranslationTreeStatusIcon.StatusIconType;
import com.jvms.i18neditor.util.Images;

/**
 * This class represents a default cell renderer for the translation tree.
 * 
 * @author Jacob van Mourik
 */
public class TranslationTreeCellRenderer extends DefaultTreeCellRenderer {
	private final static long serialVersionUID = 3511394180407171920L;
	private final static ImageIcon ROOT_ICON = Images.loadFromClasspath("images/icon-folder.png");
	private final Color selectionBackground;
	
	public TranslationTreeCellRenderer() {
		super();
		Color bg = UIManager.getColor("Panel.background");
		selectionBackground = new Color(bg.getRed(), bg.getGreen(), bg.getBlue());
		setLeafIcon(null);
		setClosedIcon(null);
		setOpenIcon(null);
		for (MouseListener l : getMouseListeners()) {
			removeMouseListener(l);
		}
	}
	
	public Color getSelectionBackground() {
		return selectionBackground;
	}
	
	@Override 
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, 
			boolean leaf, int row, boolean hasFocus) {
		TranslationTreeNode node = (TranslationTreeNode) value;
		TranslationTreeModel model = (TranslationTreeModel) tree.getModel();
        JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        l.setOpaque(true);
        l.setForeground(tree.getForeground());
        l.setBackground(tree.getBackground());            	
        if (!node.isRoot() && (node.hasError() || model.hasErrorChildNode(node))) {
        	l.setIcon(new TranslationTreeStatusIcon(StatusIconType.Warning));
        }
        if (node.isRoot()) {
        	l.setIcon(ROOT_ICON);
        }
        if (selected) {
        	l.setBackground(selectionBackground);
        }
        return l;
    }
}
