package com.jvms.i18neditor.swing;

import java.awt.Toolkit;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

/**
 * This class extends a default {@link JTextArea} with a default {@link UndoManager}.
 * 
 * @author Jacob
 */
public class JUndoableTextArea extends JTextArea implements JUndoableSupport {
	private final static long serialVersionUID = -5043046809426384893L;
	
	/**
	 * Constructs a {@link JUndoableTextArea}.
	 */
	public JUndoableTextArea() {
		super();
		getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
		
		// Add undo support
		getActionMap().put("undo", new UndoAction());
		getInputMap().put(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "undo");
		
		// Add redo support
		getActionMap().put("redo", new RedoAction());
		getInputMap().put(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "redo");
	}
}
