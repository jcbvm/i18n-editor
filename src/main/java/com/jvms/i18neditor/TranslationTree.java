package com.jvms.i18neditor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.google.common.collect.Lists;
import com.jvms.i18neditor.util.TranslationKeys;

public class TranslationTree extends JTree {
	private static final long serialVersionUID = -2888673305196385241L;
	
	private final Editor editor;
	private boolean editable;
	
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
	public boolean isEditable() {
		return editable;
	}
	
	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	@SuppressWarnings("unchecked")
	public void addNodeByKey(String key) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode node = model.getNodeByKey(key);
		if (node == null) {
			TranslationTreeNode parent = (TranslationTreeNode) model.getClosestParentNodeByKey(key);
			
			String newKey = TranslationKeys.childKey(key, parent.getKey());
			String restKey = TranslationKeys.create(TranslationKeys.subParts(newKey, 1));
			String name = TranslationKeys.firstPart(newKey);
			List<String> keys = restKey.isEmpty() ? Lists.newArrayList() : Lists.newArrayList(restKey);
			
			node = new TranslationTreeNode(name, keys);
			int index = getNodeIndex(node, Collections.list(parent.children()));
			model.insertNodeInto(node, parent, index);
			setSelectedNode((TranslationTreeNode) node.getFirstLeaf());
		} else {
			setSelectedNode(node);
		}
	}
	
	public void removeNodeByKey(String key) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode node = model.getNodeByKey(key);
		if (node != null) {
			model.removeNodeFromParent(node);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void renameNodeByKey(String key, String newName) {
		TranslationTreeModel model = (TranslationTreeModel) getModel();
		TranslationTreeNode oldNode = model.getNodeByKey(key);
		TranslationTreeNode parent = (TranslationTreeNode) model.getClosestParentNodeByKey(key);
		Collections.list(parent.children()).forEach(c -> {
			TranslationTreeNode n = (TranslationTreeNode) c;
			if (n.getName().equals(newName)) {
				model.removeNodeFromParent(n);
			}
		});
		model.removeNodeFromParent(oldNode);
		oldNode.setName(newName);
		int index = getNodeIndex(oldNode, Collections.list(parent.children()));
		model.insertNodeInto(oldNode, parent, index);
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
	
	private int getNodeIndex(TranslationTreeNode node, List<TranslationTreeNode> nodes) {
		int result = 0;
		for (TranslationTreeNode n : nodes) {
			if (n.toString().compareTo(node.toString()) < 0) {
				result++;
			}
		}
		return result;
	}
	
	private void setup() {
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        addKeyListener(new TranslationTreeKeyListener());
        addMouseListener(new TranslationTreeMouseListener());
        setEditable(false);
	}
	
	private class TranslationTreeKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			TranslationTreeNode node = getSelectedNode();
			if (node != null && !node.isRoot()) {
				String key = node.getKey();
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					editor.removeTranslationKey(key);
				} else if (e.getKeyCode() == KeyEvent.VK_F2) {
					editor.showRenameTranslationDialog(key);
				}
			}
		}
	}
	
	private class TranslationTreeMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!e.isPopupTrigger() || !isEditable()) return;
	    	TreePath path = getPathForLocation(e.getX(), e.getY());
	    	if (path == null) {
	    		setSelectionPath(null);
	    		TranslationTreeMenu menu = new TranslationTreeMenu(editor);
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
