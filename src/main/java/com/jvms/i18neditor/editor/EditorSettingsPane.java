package com.jvms.i18neditor.editor;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.apache.commons.lang3.LocaleUtils;

import com.jvms.i18neditor.swing.JHelpLabel;
import com.jvms.i18neditor.swing.JTextField;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents the editor settings pane.
 * 
 * @author Jacob van Mourik
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
		
		JCheckBox versionBox = new JCheckBox(MessageBundle.get("settings.checkversion.title"));
		versionBox.setSelected(settings.isCheckVersionOnStartup());
		versionBox.addChangeListener(e -> settings.setCheckVersionOnStartup(versionBox.isSelected()));
		fieldset1.add(versionBox, createVerticalGridBagConstraints());
		
		ComboBoxLocale currentLocaleItem = null;
		for (Locale locale : LocaleUtils.localeLookupList(editor.getCurrentLocale(), Locale.ENGLISH)) {
			for (ComboBoxLocale item : localeComboBoxItems) {
				if (item.getLocale().equals(locale)) {
					currentLocaleItem = item;
					break;
				}
			}
			if (currentLocaleItem != null) {
				break;
			}
		}
		JPanel languageListPanel = new JPanel(new GridLayout(0, 1));
		JLabel languageListLabel = new JLabel(MessageBundle.get("settings.language.title"));
		JComboBox languageListField = new JComboBox(localeComboBoxItems.toArray());
		languageListField.setSelectedItem(currentLocaleItem);
		languageListField.addActionListener(e -> {
			settings.setEditorLanguage(((ComboBoxLocale)languageListField.getSelectedItem()).getLocale());
		});
		languageListPanel.add(languageListLabel);
		languageListPanel.add(languageListField);
		fieldset1.add(languageListPanel, createVerticalGridBagConstraints());
		
		// New project settings
		JPanel fieldset2 = createFieldset(MessageBundle.get("settings.fieldset.newprojects"));
		
		ComboBoxFileStructure currentFileStructureItem = null;
		for (ComboBoxFileStructure item : fileStructureComboBoxItems) {
			if (item.getStructure().equals(settings.getResourceFileStructure())) {
				currentFileStructureItem = item;
				break;
			}
		}
		JPanel fileStructurePanel = new JPanel(new GridLayout(0, 1));
		JLabel fileStructureLabel = new JLabel(MessageBundle.get("settings.filestructure.title"));
		JComboBox fileStructureField = new JComboBox(fileStructureComboBoxItems.toArray());
		fileStructureField.setSelectedItem(currentFileStructureItem);
		fileStructureField.addActionListener(e -> {
			settings.setResourceFileStructure(((ComboBoxFileStructure)fileStructureField.getSelectedItem()).getStructure());
		});
		fileStructurePanel.add(fileStructureLabel);
		fileStructurePanel.add(fileStructureField);
		fieldset2.add(fileStructurePanel, createVerticalGridBagConstraints());
		
		JPanel resourceDefinitionPanel = new JPanel(new GridLayout(0, 1));
		JLabel resourceDefinitionLabel = new JLabel(MessageBundle.get("settings.resourcedef.title"));
		JHelpLabel resourceDefinitionHelpLabel = new JHelpLabel(MessageBundle.get("settings.resourcedef.help"));
		JTextField resourceDefinitionField = new JTextField(settings.getResourceFileDefinition());
		resourceDefinitionField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = resourceDefinitionField.getText().trim();
				settings.setResourceFileDefinition(value.isEmpty() ? EditorSettings.DEFAULT_RESOURCE_FILE_DEFINITION : value);
			}
		});
		resourceDefinitionPanel.add(resourceDefinitionLabel);
		resourceDefinitionPanel.add(resourceDefinitionField);
		fieldset2.add(resourceDefinitionPanel, createVerticalGridBagConstraints());
		fieldset2.add(resourceDefinitionHelpLabel, createVerticalGridBagConstraints());
		
		JCheckBox minifyBox = new JCheckBox(MessageBundle.get("settings.minify.title") + " " + 
				MessageBundle.get("settings.resource.jsones6"));
		minifyBox.setSelected(settings.isMinifyResources());
		minifyBox.addChangeListener(e -> settings.setMinifyResources(minifyBox.isSelected()));		
		fieldset2.add(minifyBox, createVerticalGridBagConstraints());
		
		JCheckBox flattenJSONBox = new JCheckBox(MessageBundle.get("settings.flattenjson.title") + " " + 
				MessageBundle.get("settings.resource.jsones6"));
		flattenJSONBox.setSelected(settings.isFlattenJSON());
		flattenJSONBox.addChangeListener(e -> settings.setFlattenJSON(flattenJSONBox.isSelected()));
		fieldset2.add(flattenJSONBox, createVerticalGridBagConstraints());
		
		JCheckBox commentsJSONBox = new JCheckBox(MessageBundle.get("settings.comments.title") + " " + 
				MessageBundle.get("settings.resource.jsones6"));
		commentsJSONBox.setSelected(settings.isPreserveCommnets());
		commentsJSONBox.addChangeListener(e -> settings.setPreserveCommnets(commentsJSONBox.isSelected()));
		fieldset2.add(commentsJSONBox, createVerticalGridBagConstraints());
		
		JCheckBox singleQuotesJSONBox = new JCheckBox(MessageBundle.get("settings.singlequote.title") + " " + 
				MessageBundle.get("settings.resource.jsones6"));
		singleQuotesJSONBox.setSelected(settings.isUseSingleQuotes());
		singleQuotesJSONBox.addChangeListener(e -> settings.setUseSingleQuotes(singleQuotesJSONBox.isSelected()));
		fieldset2.add(singleQuotesJSONBox, createVerticalGridBagConstraints());
		
		
		// Editing settings
		JPanel fieldset3 = createFieldset(MessageBundle.get("settings.fieldset.editing"));
		
		JCheckBox keyFieldBox = new JCheckBox(MessageBundle.get("settings.keyfield.title"));
		keyFieldBox.setSelected(settings.isKeyFieldEnabled());
		keyFieldBox.addChangeListener(e -> {
			settings.setKeyFieldEnabled(keyFieldBox.isSelected());
			editor.updateUI();
		});
		fieldset3.add(keyFieldBox, createVerticalGridBagConstraints());
		
		JCheckBox keyNodeClickBox = new JCheckBox(MessageBundle.get("settings.treetogglemode.title"));
		keyNodeClickBox.setSelected(settings.isDoubleClickTreeToggling());
		keyNodeClickBox.addChangeListener(e -> {
			settings.setDoubleClickTreeToggling(keyNodeClickBox.isSelected());
			editor.updateUI();
		});
		fieldset3.add(keyNodeClickBox, createVerticalGridBagConstraints());
		
		JPanel resourceHeightPanel = new JPanel(new GridLayout(0, 1));
		JLabel resourceHeightLabel = new JLabel(MessageBundle.get("settings.inputheight.title"));
		JSlider resourceHeightSlider = new JSlider(JSlider.HORIZONTAL, 1, 15, settings.getDefaultInputHeight());
		resourceHeightSlider.addChangeListener(e -> {
			settings.setDefaultInputHeight(resourceHeightSlider.getValue());
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
