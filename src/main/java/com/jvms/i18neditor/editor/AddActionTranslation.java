package com.jvms.i18neditor.editor;

import java.util.Map;

import com.jvms.i18neditor.Resource;

/**
 * This class represents the action save in Undo/Redo when RemoveTranslationKey
 * 
 * @author Maximiliano Diaz
 */
public class AddActionTranslation extends ActionTranslation {

	public AddActionTranslation(String key) {
		super(key);
	}

	@Override
	public void execute(Editor editor) {
		editor.removeTranslationKey(key);
		editor.setNodeInFocus("");

	}

	@Override
	public void setTranslationMap(Map<Resource, String> map) {
		// TODO Auto-generated method stub

	}
}
