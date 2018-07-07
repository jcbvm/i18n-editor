package com.jvms.i18neditor.editor;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JMenuItem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.editor.menu.updateStatusInterface;

/**
 * This class represents the Manager of Undo/Redo Action
 * 
 * @author Maximiliano Diaz
 */
public class UndoRedoManagerTranslation {

	private Stack<ActionTranslation> actionsUndo = new Stack<ActionTranslation>();
	private Stack<ActionTranslation> actionsRedo = new Stack<ActionTranslation>();
	private List<updateStatusInterface> optionMenus = Lists.newArrayList();
	private Editor editor;
	private StatusUndoRedoTransaction status = StatusUndoRedoTransaction.OTHER;

	public UndoRedoManagerTranslation(Editor editor, TranslationTree translationTree) {
		this.editor = editor;
	}

	public void rememberAction(ActionTranslation actionTranslation) {
		switch (status) {
		case UNDO:
			actionsRedo.push(actionTranslation);
			break;
		case OTHER:
			actionsUndo.push(actionTranslation);
			break;
		default:
			break;
		}
		Map<Resource, String> map = Maps.newHashMap();
		List<Resource> resources = editor.getProject().getResources();
		for (Resource resource : resources) {
			if (resource.getTranslation(actionTranslation.getkey()) != null) {
				map.put(resource, resource.getTranslation(actionTranslation.getkey()));
			}
		}
		actionTranslation.setTranslationMap(map);
		update();
	}

	public Boolean canUndo() {
		return !actionsUndo.isEmpty();
	}

	public void undo() {
		status = StatusUndoRedoTransaction.UNDO;
		if (canUndo()) {
			(actionsUndo.pop()).execute(editor);
		}
		status = StatusUndoRedoTransaction.OTHER;
		update();
	}

	public Boolean canRedo() {
		return !actionsRedo.isEmpty();
	}

	public void redo() {
		status = StatusUndoRedoTransaction.REDO;
		if (canRedo()) {
			(actionsRedo.pop()).execute(editor);
		}
		status = StatusUndoRedoTransaction.OTHER;
		update();
	}

	public void update() {
		for (updateStatusInterface optionMenu : optionMenus) {
			optionMenu.updateStatus(this.editor);
		}
	}

	public void addListener(JMenuItem translationKeyMenuItem) {
		optionMenus.add((updateStatusInterface) translationKeyMenuItem);
	}
}
