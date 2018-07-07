package com.jvms.i18neditor.editor;

import java.util.Map;

import com.jvms.i18neditor.Resource;

/**
 * This interface represents the methods importants that execute the actions
 * 
 * @author Maximiliano Diaz
 */
public interface ActionTranslationInterface {

	public void execute(Editor editor);

	public void setTranslationMap(Map<Resource, String> map);
}
