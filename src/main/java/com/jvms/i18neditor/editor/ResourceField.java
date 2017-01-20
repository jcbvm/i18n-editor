package com.jvms.i18neditor.editor;

import java.awt.Color;
import java.awt.KeyboardFocusManager;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.swing.JUndoableTextArea;

/**
 * This class represents a text area to edit the value of a translation.
 * 
 * @author Jacob
 */
public class ResourceField extends JUndoableTextArea implements Comparable<ResourceField> {
	private final static long serialVersionUID = 2034814490878477055L;
	private final Resource resource;
	
	public ResourceField(Resource resource) {
		super();
		this.resource = resource;
		setupUI();
	}
	
	public String getValue() {
		return getText().trim();
	}
	
	public void setValue(String key) {
		setText(resource.getTranslation(key));
		undoManager.discardAllEdits();
	}
	
	public Resource getResource() {
		return resource;
	}
	
	@Override
	public int compareTo(ResourceField o) {
		String a = getResource().getLocale().getDisplayName();
		String b = o.getResource().getLocale().getDisplayName();
		return a.compareTo(b);
	}
	
	private void setupUI() {
		Border border = BorderFactory.createLineBorder(Color.GRAY);
		setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(4,4,4,4)));
		setAlignmentX(LEFT_ALIGNMENT);
		setLineWrap(true);
		setWrapStyleWord(true);
		setRows(10);
		
		// Add focus traversal support
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
	    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
	}
}
