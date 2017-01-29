package com.jvms.i18neditor.swing;

import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.undo.UndoManager;

import com.jvms.i18neditor.LookAndFeel;

/**
 * This class extends a default {@link javax.swing.JTextArea} with a default {@link UndoManager}
 * and a custom look and feel.
 * 
 * @author Jacob
 */
public class JTextArea extends javax.swing.JTextArea implements JUndoableSupport {
	private final static long serialVersionUID = -5043046809426384893L;
	
	/**
	 * Constructs a {@link JTextArea}.
	 */
	public JTextArea() {
		super();
		
		Border border = BorderFactory.createLineBorder(LookAndFeel.TEXTFIELD_BORDER_COLOR);
		setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(6,8,6,8)));
		getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
		
		setAlignmentX(LEFT_ALIGNMENT);
		setLineWrap(true);
		setWrapStyleWord(true);
		
		// Add undo support
		getActionMap().put("undo", new UndoAction());
		getInputMap().put(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "undo");
		
		// Add redo support
		getActionMap().put("redo", new RedoAction());
		getInputMap().put(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "redo");
	}
}
