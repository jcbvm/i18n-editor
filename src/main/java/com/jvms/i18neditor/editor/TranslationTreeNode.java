package com.jvms.i18neditor.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.jvms.i18neditor.util.ResourceKeys;

/**
 * This class represents a single node of the translation tree.
 * 
 * @author Jacob van Mourik
 */
public class TranslationTreeNode extends DefaultMutableTreeNode {
	private final static long serialVersionUID = -7372403592538358822L;
	private String name;
	private boolean error;
	
	public TranslationTreeNode(String name, List<String> keys) {
		super();
		this.name = name;
		ResourceKeys.uniqueRootKeys(keys).forEach(rootKey -> {
			List<String> subKeys = ResourceKeys.extractChildKeys(keys, rootKey);
			add(new TranslationTreeNode(rootKey, subKeys));
		});
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setError(boolean error) {
		this.error = error;
	}
	
	public boolean hasError() {
		return isEditable() && error;
	}
	
	public boolean isEditable() {
		return !isRoot();
	}
	
	public String getKey() {
		List<TreeNode> path = Arrays.asList(getPath());
		List<String> parts = path.stream()
				.filter(p -> !((TranslationTreeNode)p).isRoot())
				.map(p -> p.toString())
				.collect(Collectors.toList());
		return ResourceKeys.create(parts);
	}
	
	@SuppressWarnings("unchecked")
	public List<TranslationTreeNode> getChildren() {
		List<TranslationTreeNode> result = new ArrayList<TranslationTreeNode>();
		List<TreeNode> children = Collections.list(children());
		for (TreeNode child : children) {
			result.add((TranslationTreeNode)child);
		}
		return result;
	}
	
	public TranslationTreeNode getChild(String name) {
		Optional<TranslationTreeNode> child = getChildren().stream()
				.filter(i -> i.getName().equals(name))
				.findFirst();
		return child.isPresent() ? child.get() : null;
	}
	
	public TranslationTreeNode cloneWithChildren() {
		return cloneWithChildren(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	private TranslationTreeNode cloneWithChildren(TranslationTreeNode parent) {
		TranslationTreeNode newParent = (TranslationTreeNode) parent.clone();
		for (TranslationTreeNode n : parent.getChildren()) {
			newParent.add(cloneWithChildren(n));
		}
		return newParent;
	}
}