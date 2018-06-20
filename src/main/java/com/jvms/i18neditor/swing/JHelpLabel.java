package com.jvms.i18neditor.swing;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 * This class extends a default {@link javax.swing.JLabel} with a custom look and feel
 * for help messages.
 * 
 * @author Jacob van Mourik
 */
public class JHelpLabel extends JLabel {
	private final static long serialVersionUID = -6879887592161450052L;

	/**
	 * Constructs a {@link JHelpLabel}.
	 */
	public JHelpLabel(String text) {
		super(text);
		
		setFont(getFont().deriveFont(Font.PLAIN, getFont().getSize()-1));
		setForeground(UIManager.getColor("Label.disabledForeground"));
	}
}
