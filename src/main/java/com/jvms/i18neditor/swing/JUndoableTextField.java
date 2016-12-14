package com.jvms.i18neditor.swing;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

/**
 * This class extends a default {@link JTextField} with a default {@link UndoManager}.
 * 
 * @author Jacob
 */
public class JUndoableTextField extends JTextField {
	private final static long serialVersionUID = 5296894112638304738L;
	protected final UndoManager undoManager = new UndoManager();
	
	/**
	 * Constructs a {@link JUndoableTextField}.
	 */
	public JUndoableTextField() {
		this(null);
	}
	
	/**
	 * Constructs a {@link JUndoableTextField} with an initial text.
	 */
	public JUndoableTextField(String text) {
		super(text);
		getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
		
		// Add undo support
		getActionMap().put("undo", new UndoAction());
		getInputMap().put(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "undo");
		
		// Add redo support
		getActionMap().put("redo", new RedoAction());
		getInputMap().put(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "redo");
	}
	
	@SuppressWarnings("serial")
	private class UndoAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (undoManager.canUndo()) {
				undoManager.undo();
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class RedoAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		}
	}
}
