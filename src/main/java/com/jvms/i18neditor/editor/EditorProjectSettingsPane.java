package com.jvms.i18neditor.editor;

import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.jvms.i18neditor.ResourceType;
import com.jvms.i18neditor.swing.JUndoableTextField;
import com.jvms.i18neditor.util.MessageBundle;

public class EditorProjectSettingsPane extends JPanel {
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
		
		GridLayout fieldsetLayout = new GridLayout(0, 1);
		fieldsetLayout.setVgap(5);
		
		// General settings
		JPanel fieldset1 = new JPanel(fieldsetLayout);
		fieldset1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, MessageBundle.get("settings.fieldset.general"), 
						TitledBorder.CENTER, TitledBorder.TOP), 
				BorderFactory.createEmptyBorder(10,10,10,10)));
		
		JPanel resourcePanel = new JPanel(new GridLayout(0, 1));
		JLabel resourceNameLabel = new JLabel(MessageBundle.get("settings.resourcename.title"));
		JUndoableTextField resourceNameField = new JUndoableTextField(project.getResourceName(), 25);
		resourceNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = resourceNameField.getText().trim();
				project.setResourceName(value.isEmpty() ? settings.getResourceName() : value);
			}
		});
		resourcePanel.add(resourceNameLabel);
		resourcePanel.add(resourceNameField);
		fieldset1.add(resourcePanel);
		
		if (project.getResourceType() != ResourceType.Properties) {
			JPanel minifyPanel = new JPanel(new GridLayout(0, 1));
			JCheckBox minifyBox = new JCheckBox(MessageBundle.get("settings.minify.title"));
			minifyBox.addChangeListener(e -> project.setMinifyResources(minifyBox.isSelected()));
			minifyPanel.add(minifyBox);
			fieldset1.add(minifyPanel);
		}
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(fieldset1);
	}
}
