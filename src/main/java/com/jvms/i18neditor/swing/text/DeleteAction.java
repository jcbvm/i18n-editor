package com.jvms.i18neditor.swing.text;

import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 * An action implementation useful for deleting text.
 * 
 * @author Jacob van Mourik
 */
public class DeleteAction extends TextAction {
	private final static long serialVersionUID = -7933405670677160997L;
	
	public DeleteAction(String name) {
		super(name);
	}
    
    @Override
	public void actionPerformed(ActionEvent e) {
        JTextComponent component = getFocusedComponent();
        component.replaceSelection("");
    }
}
