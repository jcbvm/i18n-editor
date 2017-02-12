package com.jvms.i18neditor.swing.text;

import java.awt.event.ActionEvent;

import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 * An action implementation useful for selecting all text.
 * 
 * @author Jacob van Mourik
 */
public class SelectAllAction extends TextAction {
	private final static long serialVersionUID = -4913270947629733919L;
	
	public SelectAllAction(String name) {
		super(name);
	}
    
    @Override
	public void actionPerformed(ActionEvent e) {
        JTextComponent component = getFocusedComponent();
        component.selectAll();
    }
}
