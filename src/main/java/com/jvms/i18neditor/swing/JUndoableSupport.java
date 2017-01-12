package com.jvms.i18neditor.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.undo.UndoManager;

/**
 * Interface to support undo and redo actions.
 * 
 * @author jacob
 */
public interface JUndoableSupport {
	final UndoManager undoManager = new UndoManager();
	
	@SuppressWarnings("serial")
	class UndoAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (undoManager.canUndo()) {
				undoManager.undo();
			}
		}
	}
	
	@SuppressWarnings("serial")
	class RedoAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		}
	}
}
