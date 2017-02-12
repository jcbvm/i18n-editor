package com.jvms.i18neditor.editor;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jvms.i18neditor.ResourceType;
import com.jvms.i18neditor.swing.JTextField;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents the project settings pane.
 * 
 * @author Jacob van Mourik
 */
public class EditorProjectSettingsPane extends AbstractSettingsPane {
	private final static long serialVersionUID = 5665963334924596315L;
	private Editor editor;
	
	public EditorProjectSettingsPane(Editor editor) {
		super();
		this.editor = editor;
		this.setupUI();
	}
	
	private void setupUI() {
		EditorSettings settings = editor.getSettings();
		EditorProject project = editor.getProject();
		
		// General settings
		JPanel fieldset1 = createFieldset(MessageBundle.get("settings.fieldset.general"));
		
		if (project.getResourceType() != ResourceType.Properties) {
			JCheckBox minifyBox = new JCheckBox(MessageBundle.get("settings.minify.title"));
			minifyBox.setSelected(project.isMinifyResources());
			minifyBox.addChangeListener(e -> project.setMinifyResources(minifyBox.isSelected()));
			fieldset1.add(minifyBox, createVerticalGridBagConstraints());
		}
		
		JPanel resourcePanel = new JPanel(new GridLayout(0, 1));
		JLabel resourceNameLabel = new JLabel(MessageBundle.get("settings.resourcename.title"));
		JTextField resourceNameField = new JTextField(project.getResourceName());
		resourceNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = resourceNameField.getText().trim();
				project.setResourceName(value.isEmpty() ? settings.getResourceName() : value);
			}
		});
		resourcePanel.add(resourceNameLabel);
		resourcePanel.add(resourceNameField);
		fieldset1.add(resourcePanel, createVerticalGridBagConstraints());		
		
		setLayout(new GridBagLayout());
		add(fieldset1, createVerticalGridBagConstraints());
	}
}
