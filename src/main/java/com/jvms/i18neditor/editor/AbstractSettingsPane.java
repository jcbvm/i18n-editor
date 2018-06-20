package com.jvms.i18neditor.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * This class represents an abstract base class for all setting panes.
 * 
 * @author Jacob van Mourik
 */
public abstract class AbstractSettingsPane extends JPanel {
	private final static long serialVersionUID = -8953194193840198893L;
	private GridBagConstraints vGridBagConstraints;
	
	protected AbstractSettingsPane() {
		super();
		vGridBagConstraints = new GridBagConstraints();
		vGridBagConstraints.insets = new Insets(2,2,2,2);
		vGridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		vGridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		vGridBagConstraints.weightx = 1;
	}
	
	protected GridBagConstraints createVerticalGridBagConstraints(int weigthy) {
		vGridBagConstraints.gridy = (vGridBagConstraints.gridy + 1) % Integer.MAX_VALUE;
		vGridBagConstraints.weighty = weigthy;
		return vGridBagConstraints;
	}
	
	protected GridBagConstraints createVerticalGridBagConstraints() {
		return createVerticalGridBagConstraints(1);
	}
	
	protected JPanel createFieldset(String title) {
		JPanel fieldset = new JPanel(new GridBagLayout());
		fieldset.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, title), 
				BorderFactory.createEmptyBorder(5,5,5,5)));
		return fieldset;
	}
}
