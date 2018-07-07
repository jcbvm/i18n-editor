package com.jvms.i18neditor.editor;

/**
 * This class represents the action save in Undo/Redo
 * 
 * @author Maximiliano Diaz
 */
public abstract class ActionTranslation implements ActionTranslationInterface {
	protected String key;

	public ActionTranslation(String key) {
		this.key = key;
	}

	public String getkey() {
		return key;
	}

}
