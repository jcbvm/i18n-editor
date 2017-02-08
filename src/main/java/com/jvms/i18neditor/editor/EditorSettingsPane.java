package com.jvms.i18neditor.editor;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.jvms.i18neditor.swing.JTextField;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents the editor settings pane.
 * 
 * @author Jacob
 */
public class EditorSettingsPane extends AbstractSettingsPane {
	private final static long serialVersionUID = 4488173853564278813L;
	private Editor editor;
	
	public EditorSettingsPane(Editor editor) {
		super();
		this.editor = editor;
		this.setupUI();
	}
	
	private void setupUI() {
		EditorSettings settings = editor.getSettings();
		
		// General settings
		JPanel fieldset1 = createFieldset(MessageBundle.get("settings.fieldset.general"));
		
		JCheckBox versionBox = new JCheckBox(MessageBundle.get("settings.version.title"));
		versionBox.setSelected(settings.isCheckVersionOnStartup());
		versionBox.addChangeListener(e -> settings.setCheckVersionOnStartup(versionBox.isSelected()));
		fieldset1.add(versionBox, createVerticalGridBagConstraints());
		
		// New project settings
		JPanel fieldset2 = createFieldset(MessageBundle.get("settings.fieldset.newprojects"));
		
		JPanel resourceNamePanel = new JPanel(new GridLayout(0, 1));
		JLabel resourceNameLabel = new JLabel(MessageBundle.get("settings.resourcename.title"));
		JTextField resourceNameField = new JTextField(settings.getResourceName());
		resourceNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = resourceNameField.getText().trim();
				settings.setResourceName(value.isEmpty() ? Editor.DEFAULT_RESOURCE_NAME : value);
			}
		});
		resourceNamePanel.add(resourceNameLabel);
		resourceNamePanel.add(resourceNameField);
		fieldset2.add(resourceNamePanel, createVerticalGridBagConstraints());
		
		JCheckBox minifyBox = new JCheckBox(MessageBundle.get("settings.minify.title"));
		minifyBox.setSelected(settings.isMinifyResources());
		minifyBox.addChangeListener(e -> settings.setMinifyResources(minifyBox.isSelected()));		
		fieldset2.add(minifyBox, createVerticalGridBagConstraints());
		
		// Editing settings
		JPanel fieldset3 = createFieldset(MessageBundle.get("settings.fieldset.editing"));
		
		JCheckBox keyFieldBox = new JCheckBox(MessageBundle.get("settings.keyfield.title"));
		keyFieldBox.setSelected(settings.isShowKeyField());
		keyFieldBox.addChangeListener(e -> {
			settings.setShowKeyField(keyFieldBox.isSelected());
			editor.updateUI();
		});
		fieldset3.add(keyFieldBox, createVerticalGridBagConstraints());
		
		JPanel resourceHeightPanel = new JPanel(new GridLayout(0, 1));
		JLabel resourceHeightLabel = new JLabel(MessageBundle.get("settings.inputheight.title"));
		JSlider resourceHeightSlider = new JSlider(JSlider.HORIZONTAL, 1, 15, settings.getInputHeight());
		resourceHeightSlider.addChangeListener(e -> {
			settings.setInputHeight(resourceHeightSlider.getValue());
			editor.updateUI();
		});
		resourceHeightPanel.add(resourceHeightLabel);
		resourceHeightPanel.add(resourceHeightSlider);
		fieldset3.add(resourceHeightPanel, createVerticalGridBagConstraints());
		
		setLayout(new GridBagLayout());
		add(fieldset1, createVerticalGridBagConstraints());
		add(fieldset2, createVerticalGridBagConstraints());
		add(fieldset3, createVerticalGridBagConstraints());
	}
}
