package com.jvms.i18neditor.editor;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.InputMap;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.google.common.collect.Lists;
import com.jvms.i18neditor.util.ResourceKeys;

/**
 * This class represents a tree view for translation keys.
 * 
 * @author Jacob van Mourik
 */
public class TranslationTree extends JTree {
	private final static long serialVersionUID = -2888673305196385241L;
	
	public TranslationTree() {
		super(new TranslationTreeModel());
		setupUI();
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
	
	public void updateNodes(Set<String> errorKeys) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		Enumeration<TranslationTreeNode> e = model.getEnumeration();
		while (e.hasMoreElements()) {
	    	TranslationTreeNode n = e.nextElement();
	    	n.setError(errorKeys.contains(n.getKey()));
	        model.nodeChanged(n);
	    }
	}
	
	public void updateNode(String key, boolean error) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode node = model.getNodeByKey(key);
		node.setError(error);
		model.nodeWithParentsChanged(node);
	}
	
	public TranslationTreeNode addNodeByKey(String key) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode node = model.getNodeByKey(key);
		if (node == null) {
			TranslationTreeNode parent = (TranslationTreeNode) model.getClosestParentNodeByKey(key);
			String newKey = ResourceKeys.childKey(key, parent.getKey());
			String firstPart = ResourceKeys.firstPart(newKey);
			String lastPart = ResourceKeys.withoutFirstPart(newKey);
			model.insertNodeInto(new TranslationTreeNode(firstPart, 
					lastPart.isEmpty() ? Lists.newArrayList() : Lists.newArrayList(lastPart)), parent);
			node = model.getNodeByKey(key);
		}
		setSelectionNode(node);
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
	
	public TranslationTreeNode getSelectionNode() {
		return (TranslationTreeNode) getLastSelectedPathComponent();
	}
	
	@Override
	public void setSelectionPath(TreePath path) {
		super.setSelectionPath(path);
		scrollPathToVisible(path);
	}
	
	@Override
	public void setSelectionRow(int row) {
		TreePath path = getPathForRow(row);
		setSelectionPath(path);
	}
	
	public void setSelectionNode(TranslationTreeNode node) {
		TreePath path = new TreePath(node.getPath());
		setSelectionPath(path);
	}
	
	public void clear() {
		setModel(new TranslationTreeModel());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		TranslationTreeCellRenderer renderer = (TranslationTreeCellRenderer) getCellRenderer();
		Color c1 = renderer.getSelectionBackground();
		
		FontMetrics metrics = g.getFontMetrics(getFont());
		setRowHeight(metrics.getHeight() + 8);
		
		g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        for (int i : getSelectionRows()) {
        	Rectangle r = getRowBounds(i);
        	g.setColor(c1);
            g.fillRect(0, r.y, getWidth(), r.height);
        }
        
        super.paintComponent(g);
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
		addMouseListener(new TranslationTreeMouseListener());
		setEditable(false);
		setOpaque(false);
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
		
		setSelectionNode(node);
	}
	
	private class TranslationTreeMouseListener extends MouseAdapter {
		private boolean isPopupTrigger;
		
		@Override
		public void mousePressed(MouseEvent e) {
			isPopupTrigger = e.isPopupTrigger();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!isPopupTrigger && !e.isPopupTrigger() && e.getClickCount() == getToggleClickCount()) {
				int row = getRowForLocation(e.getX(), e.getY());
				if (isCollapsed(row)) {
					expandRow(row);
				} else {
					collapseRow(row);
				}
			}
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
}
