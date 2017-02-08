package com.jvms.i18neditor.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.google.common.collect.Lists;
import com.jvms.i18neditor.LookAndFeel;
import com.jvms.i18neditor.util.ResourceKeys;

/**
 * This class represents a tree view for translation keys.
 * 
 * @author Jacob
 */
public class TranslationTree extends JTree {
	private final static long serialVersionUID = -2888673305196385241L;
	
	public TranslationTree() {
		super(new TranslationTreeModel());
		setupUI();
	}
	
	@Override
	public void setModel(TreeModel model) {
		super.setModel(model);
		Object root = model.getRoot();
		if (root != null) {
			setSelectedNode((TranslationTreeNode) root);
		}
	}
	
	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public void collapseAll() {
		for (int i = getRowCount(); i >= 0; i--) {
		    collapseRow(i);
		}
	}
	
	public void expandAll() {
		for (int i = 0; i < getRowCount(); i++) {
	        expandRow(i);
		}
	}
	
	public void expand(List<TranslationTreeNode> nodes) {
		nodes.forEach(n -> expandPath(new TreePath(n.getPath())));
	}
	
	public void collapse(List<TranslationTreeNode> nodes) {
		nodes.forEach(n -> collapsePath(new TreePath(n.getPath())));
	}
	
	public TranslationTreeNode addNodeByKey(String key) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode node = model.getNodeByKey(key);
		if (node == null) {
			TranslationTreeNode parent = (TranslationTreeNode) model.getClosestParentNodeByKey(key);
			String newKey = ResourceKeys.childKey(key, parent.getKey());
			String restKey = ResourceKeys.create(ResourceKeys.subParts(newKey, 1));
			String name = ResourceKeys.firstPart(newKey);
			List<String> keys = restKey.isEmpty() ? Lists.newArrayList() : Lists.newArrayList(restKey);
			node = new TranslationTreeNode(name, keys);
			model.insertNodeInto(node, parent);
			setSelectedNode((TranslationTreeNode) node.getFirstLeaf());
		} else {
			setSelectedNode(node);
		}
		return node;
	}
	
	public void removeNodeByKey(String key) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode node = model.getNodeByKey(key);
		if (node != null) {
			model.removeNodeFromParent(node);
		}
	}
	
	public TranslationTreeNode getNodeByKey(String key) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		return model.getNodeByKey(key);
	}
	
	public List<TranslationTreeNode> getExpandedNodes() {
		TranslationTreeNode node = (TranslationTreeNode) getModel().getRoot();
		return getExpandedNodes(node);
	}
	
	public List<TranslationTreeNode> getExpandedNodes(TranslationTreeNode node) {
		List<TranslationTreeNode> expandedNodes = Lists.newLinkedList();
		Enumeration<TreePath> expandedChilds = getExpandedDescendants(new TreePath(node.getPath()));
		if (expandedChilds != null) {
			while (expandedChilds.hasMoreElements()) {
				TreePath path = expandedChilds.nextElement();
				TranslationTreeNode expandedNode = (TranslationTreeNode) path.getLastPathComponent();
				if (!expandedNode.isRoot()) { // do not return the root node
					expandedNodes.add(expandedNode);
				}
			}
		}
		return expandedNodes;
	}
	
	public void renameNodeByKey(String key, String newKey) {
		duplicateNodeByKey(key, newKey, false);
	}
	
	public void duplicateNodeByKey(String key, String newKey) {
		duplicateNodeByKey(key, newKey, true);
	}
	
	public TranslationTreeNode getSelectedNode() {
		return (TranslationTreeNode) getLastSelectedPathComponent();
	}
	
	public void setSelectedNode(TranslationTreeNode node) {
		TreePath path = new TreePath(node.getPath());
		setSelectionPath(path);
		scrollPathToVisible(path);
	}
	
	public void clear() {
		setModel(new TranslationTreeModel());
	}
	
	private void duplicateNodeByKey(String key, String newKey, boolean keepOld) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode node = model.getNodeByKey(key);
		TranslationTreeNode newNode = model.getNodeByKey(newKey);
		List<TranslationTreeNode> expandedNodes = null;
		
		if (keepOld) {
			node = node.cloneWithChildren();
		} else {
			expandedNodes = getExpandedNodes(node);
			model.removeNodeFromParent(node);
		}
		
		if (node.isLeaf() && newNode != null) {
			model.removeNodeFromParent(newNode);
			newNode = null;
		}
		if (newNode != null) {
			model.insertDescendantsInto(node, newNode);
			node = newNode;
		} else {
			TranslationTreeNode parent = addNodeByKey(ResourceKeys.withoutLastPart(newKey));
			node.setName(ResourceKeys.lastPart(newKey));
			model.insertNodeInto(node, parent);			
		}
		
		if (expandedNodes != null) {
			expand(expandedNodes);
		}
		
		setSelectedNode(node);
	}
	
	private void setupUI() {
		UIManager.put("Tree.repaintWholeRow", Boolean.TRUE);
		
		// Remove all key strokes
		InputMap inputMap = getInputMap().getParent();
		for (KeyStroke k : getRegisteredKeyStrokes()) {
			inputMap.remove(k);
		}
		
        setUI(new TranslationTreeUI());
		setCellRenderer(new TranslationTreeCellRenderer());
		addTreeWillExpandListener(new TranslationTreeExpandListener());
		setToggleClickCount(1);
		setEditable(false);
		setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Rectangle r;
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(LookAndFeel.PRIMARY_COLOR);
        for (int i : getSelectionRows()) {
            r = getRowBounds(i);
            g.fillRect(0, r.y, getWidth(), r.height);
        }
        super.paintComponent(g);        
    }
	
	private class TranslationTreeCellRenderer extends DefaultTreeCellRenderer {
		private final static long serialVersionUID = 3511394180407171920L;
		
		public TranslationTreeCellRenderer() {
			super();
			setLeafIcon(null);
			setClosedIcon(null);
			setOpenIcon(null);
			setRowHeight(Math.max(getRowHeight() + 6, 22));
		}
		
		@Override 
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, 
				boolean leaf, int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (selected) {
            	l.setForeground(Color.WHITE);
            }
           	l.setBackground(selected ? LookAndFeel.PRIMARY_COLOR : tree.getBackground());            	
            l.setOpaque(true);
            return l;
        }
    }
	
	private class TranslationTreeUI extends BasicTreeUI {
		private TranslationTreeToggleIcon expandedIcon = new TranslationTreeToggleIcon(TranslationTreeToggleIcon.Type.Expanded);
		private TranslationTreeToggleIcon collapsedIcon = new TranslationTreeToggleIcon(TranslationTreeToggleIcon.Type.Collapsed);
		
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
	
	private class TranslationTreeExpandListener implements TreeWillExpandListener {
		@Override
		public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {}
		
		@Override
    	public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
			// Prevent root key from being collapsed
    		if (e.getPath().getPathCount() == 1) {
    			throw new ExpandVetoException(e);        			
    		}
    	}
	}
	
	private static class TranslationTreeToggleIcon implements Icon {
	    private final static int SIZE = 10;
	    private final Type type;
	    
	    public enum Type {
	    	Collapsed, Expanded
	    }
	    
	    public TranslationTreeToggleIcon(Type type) {
	        this.type = type;
	    }
	    
	    @Override
	    public void paintIcon(Component c, Graphics g, int x, int y) {
	    	g.setColor(UIManager.getColor("Tree.background"));
	    	g.fillRect(x, y, SIZE, SIZE);
	    	g.setColor(UIManager.getColor("Tree.hash").darker());
	    	g.drawRect(x, y, SIZE, SIZE);
	    	g.setColor(UIManager.getColor("Tree.foreground"));
	    	g.drawLine(x + 2, y + SIZE/2, x + SIZE - 2, y + SIZE/2);
	        if (type == Type.Collapsed) {
	        	g.drawLine(x + SIZE/2, y + 2, x + SIZE/2, y + SIZE - 2);
	    	}
	    }
	    
	    @Override
	    public int getIconWidth() {
	        return SIZE;
	    }
	    
	    @Override
	    public int getIconHeight() {
	        return SIZE;
	    }
	}
}
