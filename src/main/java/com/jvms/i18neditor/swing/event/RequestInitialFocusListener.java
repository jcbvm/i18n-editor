package com.jvms.i18neditor.swing.event;

import javax.swing.*;
import javax.swing.event.*;

/**
 * This class implements a {@link AncestorListener} to request initial focus on a component.
 * 
 * <p>When the component is added to an active ancestor the component will request focus immediately.<br>
 * When the component is added to a non active ancestor, the focus request will be made once the ancestor is active.</p>
 * 
 * @author Jacob van Mourik
 */
public class RequestInitialFocusListener implements AncestorListener {
	
	@Override
	public void ancestorAdded(AncestorEvent e) {
		JComponent component = e.getComponent();
		component.requestFocusInWindow();
		component.removeAncestorListener(this);			
	}
	
	@Override
	public void ancestorMoved(AncestorEvent e) {
		//
	}
	
	@Override
	public void ancestorRemoved(AncestorEvent e) {
		//
	}
}
