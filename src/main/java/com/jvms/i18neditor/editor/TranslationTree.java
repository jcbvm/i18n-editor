package com.jvms.i18neditor.editor;

import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.google.common.collect.Lists;
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
	
	private void setupUI() {
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		renderer.setBorderSelectionColor(renderer.getBackgroundSelectionColor());
		renderer.setBorder(BorderFactory.createCompoundBorder(getBorder(),
        		BorderFactory.createEmptyBorder(2,2,2,2)));
		
        addTreeWillExpandListener(new TranslationTreeExpandListener());
        setEditable(false);
        
        // Remove F2 keystroke binding
        getActionMap().getParent().remove("startEditing");
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
	
	private class TranslationTreeExpandListener implements TreeWillExpandListener {
		@Override
		public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {}
		
		@Override
    	public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
			// Prevent root key from being collapsed
    		if (e.getPath().getPathCount() == 1) {
    			throw new ExpandVetoException(e);        			
    		}
    	}
	}
}
