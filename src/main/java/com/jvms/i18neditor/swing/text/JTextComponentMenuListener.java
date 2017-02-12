package com.jvms.i18neditor.swing.text;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

/**
 * Mouse listener for showing a {@link JTextComponentMenu}.
 * 
 * @author Jacob van Mourik
 */
public class JTextComponentMenuListener extends MouseAdapter {
	private final JTextComponent parent;
	private final JTextComponentMenu menu;
	
	public JTextComponentMenuListener(JTextComponent parent, UndoManager undoManager) {
		super();
		this.parent = parent;
		this.menu = new JTextComponentMenu(parent, undoManager);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		showPopupMenu(e);
    }
	
	@Override
	public void mousePressed(MouseEvent e) {
		showPopupMenu(e);
    }
	
	private void showPopupMenu(MouseEvent e) {
		if (!e.isPopupTrigger() || !parent.isEditable()) {
			return;
		}
		parent.requestFocusInWindow();
		menu.show(parent, e.getX(), e.getY());
	}
}
