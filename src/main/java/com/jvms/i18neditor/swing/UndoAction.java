package com.jvms.i18neditor.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.UndoManager;

/**
 * An action implementation useful for undoing an edit.
 * 
 * @author Jacob van Mourik
 */
public class UndoAction extends AbstractAction {
	private final static long serialVersionUID = -3051499148079684354L;
	private final UndoManager undoManager;
	
	public UndoAction(UndoManager undoManager) {
		super();
		this.undoManager = undoManager;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (undoManager.canUndo()) {
			undoManager.undo();
		}
	}
}
