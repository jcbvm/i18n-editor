package com.jvms.i18neditor.swing.text;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import com.google.common.base.Strings;
import com.jvms.i18neditor.swing.UndoAction;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * A popup menu implementation useful as a edit menu for a {@link JTextComponent}.
 * 
 * @author Jacob van Mourik
 */
public class JTextComponentMenu extends JPopupMenu {
	private final static long serialVersionUID = 5967213965940023534L;
	private JTextComponent parent;
	private UndoManager undoManager;
	private Action cutAction;
	private Action copyAction;
	private Action deleteAction;
	private Action undoAction;
	
	public JTextComponentMenu(JTextComponent parent, UndoManager undoManager) {
		super();
		
		this.parent = parent;
		this.undoManager = undoManager;
		int keyMask =Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		undoAction = new UndoAction(undoManager);
		undoAction.putValue(Action.NAME, MessageBundle.get("swing.action.undo"));
		undoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, keyMask));
		add(undoAction);
		
		addSeparator();
		
		cutAction = new DefaultEditorKit.CutAction();
		cutAction.putValue(Action.NAME, MessageBundle.get("swing.action.cut"));
		cutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, keyMask));
		add(cutAction);
		
		copyAction = new DefaultEditorKit.CopyAction();
		copyAction.putValue(Action.NAME, MessageBundle.get("swing.action.copy"));
		copyAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, keyMask));
		add(copyAction);
		
		Action pasteAction = new DefaultEditorKit.PasteAction();
		pasteAction.putValue(Action.NAME, MessageBundle.get("swing.action.paste"));
		pasteAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, keyMask));
		add(pasteAction);
		
		deleteAction = new DeleteAction(MessageBundle.get("swing.action.delete"));
		add(deleteAction);
		
		addSeparator();
		
		Action selectAllAction = new SelectAllAction(MessageBundle.get("swing.action.selectall"));
		selectAllAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, keyMask));
		add(selectAllAction);
	}
	
	@Override
	public void show(Component invoker, int x, int y) {
		super.show(invoker, x, y);
		boolean hasSelection = !Strings.isNullOrEmpty(parent.getSelectedText());
		undoAction.setEnabled(undoManager.canUndo());
		cutAction.setEnabled(hasSelection);
		copyAction.setEnabled(hasSelection);
		deleteAction.setEnabled(hasSelection);
	}
}
