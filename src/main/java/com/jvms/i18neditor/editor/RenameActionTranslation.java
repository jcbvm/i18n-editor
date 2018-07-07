package com.jvms.i18neditor.editor;

import java.util.Map;

import com.jvms.i18neditor.Resource;

/**
 * This class represents the action save in Undo/Redo when execute a
 * RenameTranslationKey
 * 
 * @author Maximiliano Diaz
 */
public class RenameActionTranslation extends ActionTranslation {
	private String newKey;

	public RenameActionTranslation(String key, String newKey) {
		super(key);
		this.newKey = newKey;
	}

	@Override
	public void execute(Editor editor) {
		editor.renameTranslationKey(this.newKey, this.key);
	}

	@Override
	public void setTranslationMap(Map<Resource, String> map) {
		// TODO Auto-generated method stub

	}

}
