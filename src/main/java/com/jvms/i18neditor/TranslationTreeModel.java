package com.jvms.i18neditor;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import com.jvms.i18neditor.util.TranslationKeys;

public class TranslationTreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 3261808274177599488L;
	
	public TranslationTreeModel() {
		super(null);
	}
	
	public TranslationTreeModel(String rootName, List<String> keys) {
		super(new TranslationTreeNode(rootName, keys));
	}
	
	@SuppressWarnings("unchecked")
	public TranslationTreeNode getNodeByKey(String key) {
		TranslationTreeNode node = (TranslationTreeNode) getRoot();
		Enumeration<TranslationTreeNode> e = node.depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	    	TranslationTreeNode n = e.nextElement();
	        if (n.getKey().equalsIgnoreCase(key)) {
	            return n;
	        }
	    }
	    return null;
	}
	
	public TranslationTreeNode getClosestParentNodeByKey(String key) {
		TranslationTreeNode node = null;
		int count = TranslationKeys.size(key);
		while (node == null && count > 0) {
			key = TranslationKeys.withoutLastPart(key);
			node = getNodeByKey(key);
			count--;
		}
		if (node != null) {
			return node;
		} else {
			return (TranslationTreeNode) getRoot();
		}
	}
	
	public void insertNodeInto(TranslationTreeNode node, TranslationTreeNode parent) {
		insertNodeInto(node, parent, getChildIndex(node, parent));
	}
	
	private int getChildIndex(TranslationTreeNode node, TranslationTreeNode parent) {
		int result = 0;
		for (TranslationTreeNode n : parent.getChildren()) {
			if (n.toString().compareTo(node.toString()) < 0) {
				result++;
			}
		}
		return result;
	}
}
