package com.jvms.i18neditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.google.common.collect.Lists;
import com.jvms.i18neditor.util.TranslationKeys;

/**
 * This class represents a tree view for the translation keys.
 * 
 * @author Jacob
 */
public class TranslationTree extends JTree {
	private static final long serialVersionUID = -2888673305196385241L;
	
	private final Editor editor;
	
	public TranslationTree(Editor editor) {
		super(new TranslationTreeModel());
		this.editor = editor;
		setup();
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
		// Collapse all but root node
		for (int i = getRowCount(); i > 0; i--) {
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
	
	public TranslationTreeNode addNodeByKey(String key) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode node = model.getNodeByKey(key);
		if (node == null) {
			TranslationTreeNode parent = (TranslationTreeNode) model.getClosestParentNodeByKey(key);
			String newKey = TranslationKeys.childKey(key, parent.getKey());
			String restKey = TranslationKeys.create(TranslationKeys.subParts(newKey, 1));
			String name = TranslationKeys.firstPart(newKey);
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
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode oldNode = model.getNodeByKey(key);
		TranslationTreeNode newNode = model.getNodeByKey(newKey);
		
		// Store expansion state of old tree
		List<TranslationTreeNode> expandedNodes = getExpandedNodes(oldNode);
		
		// Remove old and any existing new tree
		model.removeNodeFromParent(oldNode);
		if (newNode != null) {
			model.removeNodeFromParent(newNode);
		}
		
		// Create new node from new key and add it to parent of old node
		TranslationTreeNode parent = addNodeByKey(TranslationKeys.withoutLastPart(newKey));
		oldNode.setName(TranslationKeys.lastPart(newKey));
		model.insertNodeInto(oldNode, parent);
		
		// Restore expansion state
		expand(expandedNodes);
		
		// Restore selected node
		setSelectedNode(oldNode);
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
	
	private void setup() {
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        addMouseListener(new TranslationTreeMouseListener());
        setEditable(false);
        // Remove F2 keystroke binding
        getActionMap().getParent().remove("startEditing");
	}
	
	private class TranslationTreeMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!e.isPopupTrigger() || !isEditable()) return;
	    	TreePath path = getPathForLocation(e.getX(), e.getY());
	    	if (path == null) {
	    		setSelectionPath(null);
	    		TranslationTreeMenu menu = new TranslationTreeMenu(editor, TranslationTree.this);
	    		menu.show(e.getComponent(), e.getX(), e.getY());
	    	} else {
	    		setSelectionPath(path);
	    		TranslationTreeNode node = getSelectedNode();
	    		TranslationTreeNodeMenu menu = new TranslationTreeNodeMenu(editor, node);
	    		menu.show(e.getComponent(), e.getX(), e.getY());
	    	}
	    }
	}
}
