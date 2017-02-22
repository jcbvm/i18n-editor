package com.jvms.i18neditor.editor;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import com.google.common.collect.Lists;
import com.jvms.i18neditor.util.MessageBundle;
import com.jvms.i18neditor.util.ResourceKeys;

/**
 * This class represents a model for the translation tree.
 * 
 * @author Jacob van Mourik
 */
public class TranslationTreeModel extends DefaultTreeModel {
	private final static long serialVersionUID = 3261808274177599488L;
	
	public TranslationTreeModel() {
		super(new TranslationTreeNode(MessageBundle.get("tree.root.name"), Lists.newArrayList()));
	}
	
	public TranslationTreeModel(List<String> keys) {
		super(new TranslationTreeNode(MessageBundle.get("tree.root.name"), keys));
	}
	
	@SuppressWarnings("unchecked")
	public Enumeration<TranslationTreeNode> getEnumeration() {
		return ((TranslationTreeNode)getRoot()).depthFirstEnumeration();
	}
	
	public TranslationTreeNode getNodeByKey(String key) {
		Enumeration<TranslationTreeNode> e = getEnumeration();
	    while (e.hasMoreElements()) {
	    	TranslationTreeNode n = e.nextElement();
	        if (n.getKey().equals(key)) {
	            return n;
	        }
	    }
	    return null;
	}
	
	public boolean hasErrorChildNode(TranslationTreeNode node) {
		Enumeration<TranslationTreeNode> e = getEnumeration();
    	while (e.hasMoreElements()) {
    		TranslationTreeNode n = e.nextElement();
    		if (n.hasError()) {
    			return true;
    		}
    	}
    	return false;
	}
	
	public TranslationTreeNode getClosestParentNodeByKey(String key) {
		TranslationTreeNode node = null;
		int count = ResourceKeys.size(key);
		while (node == null && count > 0) {
			key = ResourceKeys.withoutLastPart(key);
			node = getNodeByKey(key);
			count--;
		}
		if (node != null) {
			return node;
		} else {
			return (TranslationTreeNode) getRoot();
		}
	}
	
	public void insertNodeInto(TranslationTreeNode newChild, TranslationTreeNode parent) {
		insertNodeInto(newChild, parent, getNewChildIndex(newChild, parent));
	}
	
	public void insertDescendantsInto(TranslationTreeNode source, TranslationTreeNode target) {
		source.getChildren().forEach(child -> {
			TranslationTreeNode existing = target.getChild(child.getName());
			if (existing != null) {
				if (existing.isLeaf()) {
					removeNodeFromParent(existing);
					insertNodeInto(child, target);
				} else {
					insertDescendantsInto(child, existing);					
				}
			} else {
				insertNodeInto(child, target);
			}
		});
	}
	
	public void nodeWithParentsChanged(TranslationTreeNode node) {
		while (node != null) {
			nodeChanged(node);
			node = (TranslationTreeNode) node.getParent();
		}
	}
	
	private int getNewChildIndex(TranslationTreeNode newChild, TranslationTreeNode parent) {
		int result = 0;
		for (TranslationTreeNode n : parent.getChildren()) {
			if (n.getName().compareTo(newChild.getName()) < 0) {
				result++;
			}
		}
		return result;
	}
}
