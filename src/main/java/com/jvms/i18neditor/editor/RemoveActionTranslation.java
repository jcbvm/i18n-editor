package com.jvms.i18neditor.editor;

import java.util.Map;

import com.jvms.i18neditor.Resource;

/**
 * This class represents the action save in Undo/Redo when execute a
 * AddTranslationKey
 * 
 * @author Maximiliano Diaz
 */
public class RemoveActionTranslation extends ActionTranslation {
	private Map<Resource, String> map;

	public RemoveActionTranslation(String key) {
		super(key);
	}

	@Override
	public void execute(Editor editor) {
		editor.addTranslationKey(this.key);
		for (Map.Entry<Resource, String> entry : map.entrySet()) {
			entry.getKey().storeTranslation(key, entry.getValue());
		}
		editor.setNodeInFocus("");
		editor.setNodeInFocus(this.key);

	}

	@Override
	public void setTranslationMap(Map<Resource, String> map) {
		this.map = map;
	}

}
