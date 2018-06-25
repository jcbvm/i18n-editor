package com.jvms.i18neditor.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.google.common.collect.Lists;
import com.jvms.i18neditor.FileStructure;
import com.jvms.i18neditor.util.MessageBundle;

/**
 * This class represents an abstract base class for all setting panes.
 * 
 * @author Jacob van Mourik
 */
public abstract class AbstractSettingsPane extends JPanel {
	private final static long serialVersionUID = -8953194193840198893L;
	private GridBagConstraints vGridBagConstraints;
	
	protected final List<ComboBoxFileStructure> fileStructureComboBoxItems = Lists.newArrayList(FileStructure.values()).stream()
			.map(val -> new ComboBoxFileStructure(val, MessageBundle.get("settings.filestructure." + val.name().toLowerCase())))
			.sorted()
			.collect(Collectors.toList());
	
	protected final List<ComboBoxLocale> localeComboBoxItems = Editor.SUPPORTED_LANGUAGES.stream()
			.map(val -> new ComboBoxLocale(val))
			.sorted()
			.collect(Collectors.toList());
	
	protected AbstractSettingsPane() {
		super();
		vGridBagConstraints = new GridBagConstraints();
		vGridBagConstraints.insets = new Insets(4,4,4,4);
		vGridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		vGridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		vGridBagConstraints.weightx = 1;
	}
	
	protected GridBagConstraints createVerticalGridBagConstraints() {
		vGridBagConstraints.gridy = (vGridBagConstraints.gridy + 1) % Integer.MAX_VALUE;
		return vGridBagConstraints;
	}
	
	protected JPanel createFieldset(String title) {
		JPanel fieldset = new JPanel(new GridBagLayout());
		fieldset.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(null, title), 
				BorderFactory.createEmptyBorder(5,5,5,5)));
		return fieldset;
	}
	
	protected class ComboBoxFileStructure implements Comparable<ComboBoxFileStructure> {
		private FileStructure structure;
		private String label;
		
		public ComboBoxFileStructure(FileStructure structure, String label) {
			this.structure = structure;
			this.label = label;
		}
		
		public FileStructure getStructure() {
			return structure;
		}
		
		public String toString() {
			return label;
		}

		@Override
		public int compareTo(ComboBoxFileStructure o) {
			return toString().compareTo(o.toString());
		}
	}
	
	protected class ComboBoxLocale implements Comparable<ComboBoxLocale> {
		private Locale locale;
		
		public ComboBoxLocale(Locale locale) {
			this.locale = locale;
		}
		
		public Locale getLocale() {
			return locale;
		}
		
		public String toString() {
			return locale.getDisplayName();
		}

		@Override
		public int compareTo(ComboBoxLocale o) {
			return toString().compareTo(o.toString());
		}
	}
}
