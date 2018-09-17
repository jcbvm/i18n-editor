package com.jvms.i18neditor.editor;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jvms.i18neditor.ResourceType;
import com.jvms.i18neditor.swing.JHelpLabel;
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
		EditorProject project = editor.getProject();
		
		// General settings
		JPanel fieldset1 = createFieldset(MessageBundle.get("settings.fieldset.general"));
		
		ComboBoxFileStructure currentFileStructureItem = null;
		for (ComboBoxFileStructure item : fileStructureComboBoxItems) {
			if (item.getStructure().equals(project.getResourceFileStructure())) {
				currentFileStructureItem = item;
				break;
			}
		}
		JPanel fileStructurePanel = new JPanel(new GridLayout(0, 1));
		JLabel fileStructureLabel = new JLabel(MessageBundle.get("settings.filestructure.title"));
		JComboBox fileStructureField = new JComboBox(fileStructureComboBoxItems.toArray());
		fileStructureField.setSelectedItem(currentFileStructureItem);
		fileStructureField.addActionListener(e -> {
			project.setResourceFileStructure(((ComboBoxFileStructure)fileStructureField.getSelectedItem()).getStructure());
		});
		fileStructurePanel.add(fileStructureLabel);
		fileStructurePanel.add(fileStructureField);
		fieldset1.add(fileStructurePanel, createVerticalGridBagConstraints());
		
		JPanel resourceDefinitionPanel = new JPanel(new GridLayout(0, 1));
		JLabel resourceDefinitionLabel = new JLabel(MessageBundle.get("settings.resourcedef.title"));
		JHelpLabel resourceDefinitionHelpLabel = new JHelpLabel(MessageBundle.get("settings.resourcedef.help"));
		JTextField resourceDefinitionField = new JTextField(project.getResourceFileDefinition());
		resourceDefinitionField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = resourceDefinitionField.getText().trim();
				project.setResourceFileDefinition(value.isEmpty() ? EditorSettings.DEFAULT_RESOURCE_FILE_DEFINITION : value);
			}
		});
		resourceDefinitionPanel.add(resourceDefinitionLabel);
		resourceDefinitionPanel.add(resourceDefinitionField);
		fieldset1.add(resourceDefinitionPanel, createVerticalGridBagConstraints());
		fieldset1.add(resourceDefinitionHelpLabel, createVerticalGridBagConstraints());
		
		ResourceType type = project.getResourceType();
		if (type == ResourceType.JSON || type == ResourceType.ES6) {
			JCheckBox minifyBox = new JCheckBox(MessageBundle.get("settings.minify.title"));
			minifyBox.setSelected(project.isMinifyResources());
			minifyBox.addChangeListener(e -> project.setMinifyResources(minifyBox.isSelected()));
			fieldset1.add(minifyBox, createVerticalGridBagConstraints());
			
			JCheckBox flattenJSONBox = new JCheckBox(MessageBundle.get("settings.flattenjson.title"));
			flattenJSONBox.setSelected(project.isFlattenJSON());
			flattenJSONBox.addChangeListener(e -> project.setFlattenJSON(flattenJSONBox.isSelected()));
			fieldset1.add(flattenJSONBox, createVerticalGridBagConstraints());
			
			if (type == ResourceType.ES6) {
				JCheckBox commentsJSONBox = new JCheckBox(MessageBundle.get("settings.comments.title"));
				commentsJSONBox.setSelected(project.isPreserveComments());
				commentsJSONBox.addChangeListener(e -> project.setPreservCommnets(commentsJSONBox.isSelected()));
				fieldset1.add(commentsJSONBox, createVerticalGridBagConstraints());
				
				JCheckBox singleQuotesJSONBox = new JCheckBox(MessageBundle.get("settings.singlequote.title"));
				singleQuotesJSONBox.setSelected(project.isUseSingleQuotes());
				singleQuotesJSONBox.addChangeListener(e -> project.setUseSingleQuotes(singleQuotesJSONBox.isSelected()));
				fieldset1.add(singleQuotesJSONBox, createVerticalGridBagConstraints());
			}
		}
		
		setLayout(new GridBagLayout());
		add(fieldset1, createVerticalGridBagConstraints());
	}
}
