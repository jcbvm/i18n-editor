package com.jvms.i18neditor.editor;

import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Locale;

import javax.swing.JComponent;

import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.swing.JTextArea;

/**
 * This class represents a text area to edit the value of a translation.
 * 
 * @author Jacob van Mourik
 */
public class ResourceField extends JTextArea implements Comparable<ResourceField> {
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
		Locale a = getResource().getLocale();
		Locale b = o.getResource().getLocale();
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}
		return a.getDisplayName().compareTo(b.getDisplayName());
	}
	
	private void setupUI() {
		// Add focus traversal support
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
	    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
	    addFocusListener(new ResourceFieldFocusListener());
	}
	
	private class ResourceFieldFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			JComponent parent = (JComponent)getParent();
			Rectangle bounds = new Rectangle(getBounds());
			bounds.y -= 35; // add fixed space at the top
			bounds.height += 70; // add fixed space at the bottom
			parent.scrollRectToVisible(bounds);
		}
	}
}
