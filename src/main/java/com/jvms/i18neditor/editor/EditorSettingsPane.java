package com.jvms.i18neditor.editor;

import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.jvms.i18neditor.swing.JUndoableTextField;
import com.jvms.i18neditor.util.MessageBundle;

public class EditorSettingsPane extends JPanel {
	private final static long serialVersionUID = 4488173853564278813L;
	private Editor editor;
	
	public EditorSettingsPane(Editor editor) {
		super();
		this.editor = editor;
		this.setupUI();
	}
	
	private void setupUI() {
		EditorSettings settings = editor.getSettings();
		
		GridLayout fieldsetLayout = new GridLayout(0, 1);
		fieldsetLayout.setVgap(5);
		
		// General settings
		JPanel fieldset1 = new JPanel(fieldsetLayout);
		fieldset1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, MessageBundle.get("settings.fieldset.general"), 
						TitledBorder.CENTER, TitledBorder.TOP), 
				BorderFactory.createEmptyBorder(10,10,10,10)));
		
		JPanel updatesPanel = new JPanel(new GridLayout(0, 1));
		JCheckBox checkForUpdatesBox = new JCheckBox(MessageBundle.get("settings.version.title"));
		checkForUpdatesBox.setSelected(settings.isCheckVersionOnStartup());
		checkForUpdatesBox.addChangeListener(e -> settings.setCheckVersionOnStartup(checkForUpdatesBox.isSelected()));
		updatesPanel.add(checkForUpdatesBox);
		fieldset1.add(updatesPanel);
		
		// New project settings
		JPanel fieldset2 = new JPanel(fieldsetLayout);
		fieldset2.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, MessageBundle.get("settings.fieldset.newprojects"), 
						TitledBorder.CENTER, TitledBorder.TOP), 
				BorderFactory.createEmptyBorder(10,10,10,10)));
		
		JPanel resourcePanel = new JPanel(new GridLayout(0, 1));
		JLabel resourceNameLabel = new JLabel(MessageBundle.get("settings.resourcename.title"));
		JUndoableTextField resourceNameField = new JUndoableTextField(settings.getResourceName(), 25);
		resourceNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = resourceNameField.getText().trim();
				settings.setResourceName(value.isEmpty() ? Editor.DEFAULT_RESOURCE_NAME : value);
			}
		});
		resourcePanel.add(resourceNameLabel);
		resourcePanel.add(resourceNameField);
		fieldset2.add(resourcePanel);
		
		JPanel minifyPanel = new JPanel(new GridLayout(0, 1));
		JCheckBox minifyBox = new JCheckBox(MessageBundle.get("settings.minify.title"));
		minifyBox.addChangeListener(e -> settings.setMinifyResources(minifyBox.isSelected()));
		minifyPanel.add(minifyBox);
		fieldset2.add(minifyPanel);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(fieldset1);
		add(Box.createVerticalStrut(10));
		add(fieldset2);
	}
}
