package de.linnk.fatclient.application;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.linnk.fatclient.application.v02.Settings;
import de.mxro.swing.dialogwizard.CustomSwingDialog;

public class SettingsDialog extends CustomSwingDialog<Settings> {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JRadioButton jRadioEnabled = null;

	private JRadioButton jRadioDisabled = null;

	private JPanel jPanelDescription = null;

	private JLabel jLabelDescription = null;

	private JButton jButtonApply = null;

	private JButton jButtonAbort = null;

	private JLabel jLabelSpellCheck = null;

	private JComboBox jComboBoxLanguage = null;

	private JLabel jLabelLanguage = null;

	/**
	 * @param owner
	 */
	public SettingsDialog(Frame owner) {
		super(owner);
		this.initialize();
	}

	
	
	@Override
	public void updateView() {
		this.getJRadioEnabled().setSelected (this.getObject().isSpellCheckEnabled());
		this.getJRadioDisabled().setSelected(!this.getObject().isSpellCheckEnabled());
		this.getJComboBoxLanguage().setSelectedItem(this.getObject().getSelectedLanguage());
	}



	@Override
	public void updateObject() {
		this.getObject().setSpellCheckEnabled(this.getJRadioEnabled().isSelected());
		this.getObject().setSelectedLanguage((String) this.jComboBoxLanguage.getSelectedItem());
	}



	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(337, 200);
		this.setContentPane(this.getJContentPane());
		this.setTitle("Preferences");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (this.jContentPane == null) {
			jLabelLanguage = new JLabel();
			jLabelLanguage.setBounds(new Rectangle(30, 99, 90, 16));
			jLabelLanguage.setText("Language");
			this.jLabelSpellCheck = new JLabel();
			this.jLabelSpellCheck.setText("SpellCheck");
			jLabelSpellCheck.setBounds(new Rectangle(11, 43, 106, 16));
			
			this.jLabelDescription = new JLabel();
			this.jLabelDescription.setAlignmentY(Component.TOP_ALIGNMENT);
			this.jLabelDescription.setText("Linnk Settings");
			this.jLabelDescription.setFont(LinnkFatClient.largeFont);
			jLabelDescription.setBounds(new Rectangle(10, 8, 136, 23));
			this.jLabelDescription.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jContentPane = new JPanel();
			this.jContentPane.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jContentPane.setLayout(null);
			this.jContentPane.add(this.getJPanelDescription(), null);
			this.jContentPane.add(this.jLabelDescription, null);
			this.jContentPane.add(this.getJButtonApply(), null);
			this.jContentPane.add(this.getJButtonAbort(), null);
			
			this.jContentPane.add(this.getJRadioEnabled(), null);
			this.jContentPane.add(this.getJRadioDisabled(), null);
			jContentPane.add(jLabelSpellCheck, null);
			jContentPane.add(jLabelDescription, null);
			jContentPane.add(getJComboBoxLanguage(), null);
			jContentPane.add(jLabelLanguage, null);
			this.jContentPane.add(this.jLabelSpellCheck, null);
			
			ButtonGroup group = new ButtonGroup();
		    group.add(this.getJRadioEnabled());
		    group.add(this.getJRadioDisabled());
		}
		return this.jContentPane;
	}

	

	

	

	/**
	 * This method initializes jRadioEnabled	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioEnabled() {
		if (this.jRadioEnabled == null) {
			this.jRadioEnabled = new JRadioButton();
			this.jRadioEnabled.setText("Enabled");
			this.jRadioEnabled.setBounds(new Rectangle(58, 63, 123, 16));
		}
		return this.jRadioEnabled;
	}

	/**
	 * This method initializes jRadioDisabled	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioDisabled() {
		if (this.jRadioDisabled == null) {
			this.jRadioDisabled = new JRadioButton();
			this.jRadioDisabled.setText("Disabled");
			this.jRadioDisabled.setBounds(new Rectangle(199, 64, 130, 16));
		}
		return this.jRadioDisabled;
	}

	/**
	 * This method initializes jPanelDescription	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelDescription() {
		if (this.jPanelDescription == null) {
			this.jPanelDescription = new JPanel();
			this.jPanelDescription.setLayout(new BoxLayout(this.getJPanelDescription(), BoxLayout.Y_AXIS));
			this.jPanelDescription.setBounds(new Rectangle(137, 0, 0, 0));
			this.jPanelDescription.add(this.jLabelDescription, new GridBagConstraints());
		}
		return this.jPanelDescription;
	}

	/**
	 * This method initializes jButtonApply	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonApply() {
		if (this.jButtonApply == null) {
			this.jButtonApply = new JButton();
			this.jButtonApply.setText("Apply");
			this.jButtonApply.setBounds(new Rectangle(208, 133, 119, 29));
			this.jButtonApply.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SettingsDialog.this.updateObject();
					SettingsDialog.this.setVisible(false);
				}
			});
		}
		return this.jButtonApply;
	}

	/**
	 * This method initializes jButtonAbort	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAbort() {
		if (this.jButtonAbort == null) {
			this.jButtonAbort = new JButton();
			this.jButtonAbort.setText("Cancel");
			this.jButtonAbort.setBounds(new Rectangle(83, 133, 122, 29));
			this.jButtonAbort.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SettingsDialog.this.setVisible(false);
				}
			});
		}
		return this.jButtonAbort;
	}



	/**
	 * This method initializes jComboBoxLanguage	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxLanguage() {
		if (jComboBoxLanguage == null) {
			String[] comboStrings = { "Automatic", "English", "Deutsch" };
			jComboBoxLanguage = new JComboBox(comboStrings);
			jComboBoxLanguage.setBounds(new Rectangle(131, 92, 196, 27));
			jComboBoxLanguage.setSelectedIndex(1);
			
		}
		return jComboBoxLanguage;
	}

	

}  //  @jve:decl-index=0:visual-constraint="102,37"
