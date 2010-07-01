package de.linnk.extpack.v03;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.linnk.basispack.v05.NodeDocument;
import de.linnk.documentfilter.IsUnchanged;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.streaming.Publisher;
import de.linnk.streaming.views.PlainXMLView;
import de.linnk.streaming.views.RWView;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URIImpl;
import de.mxro.utils.Utils;
import de.mxro.utils.background.Activity;
import de.mxro.utils.log.UserError;

public class PublisherDialog extends JDialog {

	
	public static Publisher editPublisher(Publisher publisher) {
		final PublisherDialog dialog = new PublisherDialog(null);
		dialog.setPublisher(publisher);
		de.mxro.utils.Utils.centerComponent(dialog, null);
		dialog.setModal(true);
		dialog.setVisible(true);
		UserError.singelton.log("PublisherDialog finished!", UserError.Priority.LOW);
		return dialog.getPublisher();
	}
	
	private static final long serialVersionUID = 1L;
	
	private Publisher publisher;  //  @jve:decl-index=0:
	private Folder template;
	private Folder destination;
	private IsUnchanged isUnchanged;
	
	private JPanel jContentPane = null;

	private JButton jButtonOkay = null;

	private JLabel jLabelDest = null;

	private JLabel jLabelDestination = null;

	private JButton jButtonSwitchDestination = null;

	private JLabel jLabelTempl = null;

	private JLabel jLabelTemplate = null;

	private JButton jButtonSetTemplate = null;

	private JLabel jLabelTtl = null;

	private JTextField jTextFieldTitle = null;

	private JButton jButtonPublish = null;

	private JButton jButtonAbort = null;

	private JCheckBox jCheckBoxOnlyChanged = null;

	private JLabel jLabelCaption = null;

	public Publisher getPublisher() {
		return this.publisher;
	}

	public void updatePublisher() {
		final PublishView view = new PublishView(new RWView(new PlainXMLView(), true, this.template, false));
		view.getXmlView().setPlaceholder(RWView.PlaceholderIDs.SITE_TITLE, this.jTextFieldTitle.getText());
		this.setPublisher( new Publisher(view, true, this.destination, ((NodeDocument) this.publisher.getDocument()),this.isUnchanged));
	}
	
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
		if (publisher != null) {
			this.jLabelDestination.setText( publisher.getDestination().getURI().toString() );
			this.jLabelTemplate.setText( (publisher.getView()).getXmlView().getTemplate().getURI().toString() );
			this.jTextFieldTitle.setText( (publisher.getView()).getXmlView().getPlaceholder(RWView.PlaceholderIDs.SITE_TITLE) );
			
			this.template = (publisher.getView()).getXmlView().getTemplate();
			if (this.template.getFileSystem() == null) {
				UserError.singelton.log(this, "setPublisher: correct template FileSystem ...", UserError.Priority.NORMAL);
				if (this.template.getURI().getFile().exists()) {
				this.template = FileSystemObject.newLocalRootFolder(this.template.getURI());
				}
			}
			
			this.destination = publisher.getDestination();
			if (this.destination.getFileSystem() == null) {
				UserError.singelton.log(this, "setPublisher: correct destination FileSystem ...", UserError.Priority.NORMAL);
				if (this.destination.getURI().getFile().exists()) {
				this.destination = FileSystemObject.newLocalRootFolder(this.destination.getURI());
				}
			}
			this.isUnchanged = publisher.getIsUnchanged();
			this.jCheckBoxOnlyChanged.setSelected(publisher.getIsUnchanged().isActive());
		}
	}




	
	/**
	 * @param owner
	 */
	public PublisherDialog(Frame owner) {
		super(owner);
		this.initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(499, 293);
		this.setContentPane(this.getJContentPane());
		this.setTitle("Publisher");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (this.jContentPane == null) {
			this.jLabelCaption = new JLabel();
			this.jLabelCaption.setBounds(new Rectangle(15, 0, 241, 31));
			this.jLabelCaption.setText("Publish Settings");
			this.jLabelCaption.setFont(LinnkFatClient.largeFont);
			this.jLabelTtl = new JLabel();
			this.jLabelTtl.setText("Title:");
			//this.jLabelTtl.setFont(Linnk.boldFont);
			this.jLabelTtl.setBounds(new Rectangle(15, 165, 106, 16));
			this.jLabelTemplate = new JLabel();
			this.jLabelTemplate.setText("not specified");
			// this.jLabelTemplate.setFont(Linnk.smallFont);
			this.jLabelTemplate.setBounds(new Rectangle(15, 120, 466, 16));
			this.jLabelTempl = new JLabel();
			this.jLabelTempl.setText("Template:");
			//this.jLabelTempl.setFont(Linnk.boldFont);
			this.jLabelTempl.setBounds(new Rectangle(15, 105, 151, 16));
			this.jLabelDestination = new JLabel();
			this.jLabelDestination.setText("not specified");
			//this.jLabelDestination.setFont(Linnk.smallFont);
			this.jLabelDestination.setBounds(new Rectangle(15, 60, 466, 16));
			this.jLabelDest = new JLabel();
			//this.jLabelDest.setFont(Linnk.boldFont);
			this.jLabelDest.setText("Destination:");
			this.jLabelDest.setBounds(new Rectangle(15, 45, 151, 16));
			this.jContentPane = new JPanel();

			
			this.jContentPane.setLayout(null);
			this.jContentPane.add(this.jLabelDest, null);
			this.jContentPane.add(this.jLabelDestination, null);
			this.jContentPane.add(this.jLabelTempl, null);
			this.jContentPane.add(this.jLabelTemplate, null);
			this.jContentPane.add(this.jLabelTtl, null);
			this.jContentPane.add(this.getJTextFieldTitle(), null);
			this.jContentPane.add(this.jLabelTemplate, null);
			this.jContentPane.add(this.jLabelDestination, null);
			this.jContentPane.add(this.jLabelDest, null);
			this.jContentPane.add(this.jLabelTtl, null);
			this.jContentPane.add(this.jLabelTempl, null);
			this.jContentPane.add(this.getJButtonPublish(), null);
			this.jContentPane.add(this.getJButtonAbort(), null);
			this.jContentPane.add(this.getJButtonOkay(), null);
			this.jContentPane.add(this.getJButtonSetTemplate(), null);
			this.jContentPane.add(this.getJButtonSwitchDestination(), null);
			this.jContentPane.add(this.jLabelTemplate, null);
			this.jContentPane.add(this.jLabelDestination, null);
			this.jContentPane.add(this.jLabelTtl, null);
			this.jContentPane.add(this.jLabelTempl, null);
			this.jContentPane.add(this.jLabelDest, null);
			this.jContentPane.add(this.getJCheckBoxOnlyChanged(), null);
			this.jContentPane.add(this.jLabelCaption, null);
		}
		return this.jContentPane;
	}

	/**
	 * This method initializes jButtonOkay	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOkay() {
		if (this.jButtonOkay == null) {
			this.jButtonOkay = new JButton();
			this.jButtonOkay.setText("Save Settings");
			this.jButtonOkay.setBounds(new Rectangle(225, 225, 135, 28));
			this.jButtonOkay.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PublisherDialog.this.updatePublisher();
					PublisherDialog.this.setVisible(false);
				}
			});
		}
		return this.jButtonOkay;
	}

	/**
	 * This method initializes jButtonSwitchDestination	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSwitchDestination() {
		if (this.jButtonSwitchDestination == null) {
			this.jButtonSwitchDestination = new JButton();
			this.jButtonSwitchDestination.setText("Set Destination");
			this.jButtonSwitchDestination.setBounds(new Rectangle(315, 75, 166, 16));
			this.jButtonSwitchDestination.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PublisherDialog.this.destination = Utils.selectLocalFolder(LinnkFatClient.application.getSettings().standardPublishDir, true);
					PublisherDialog.this.updatePublisher();
				}
			});
		}
		return this.jButtonSwitchDestination;
	}

	/**
	 * This method initializes jButtonSetTemplate	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSetTemplate() {
		if (this.jButtonSetTemplate == null) {
			this.jButtonSetTemplate = new JButton();
			this.jButtonSetTemplate.setText("Set Template");
			this.jButtonSetTemplate.setBounds(new Rectangle(315, 135, 166, 16));
			this.jButtonSetTemplate.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					final Folder newTemplate = Utils.selectLocalFolder(LinnkFatClient.application.getSettings().standardTemplateDir, false);
					if (newTemplate == null) return;
					if (newTemplate.getFile(URIImpl.create("Theme.plist")) == null) {
						de.mxro.utils.log.UserError.singelton.showError("Theme.plist must exist in selected directory.");
						return;
					}
					PublisherDialog.this.template = newTemplate;
					PublisherDialog.this.updatePublisher();
				}
			});
		}
		return this.jButtonSetTemplate;
	}

	/**
	 * This method initializes jTextFieldTitle	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldTitle() {
		if (this.jTextFieldTitle == null) {
			this.jTextFieldTitle = new JTextField();
			this.jTextFieldTitle.setBounds(new Rectangle(120, 165, 361, 20));
		}
		return this.jTextFieldTitle;
	}

	
	
	private class Task extends Thread implements Activity {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public void run() {
        	if (!PublisherDialog.this.getPublisher().publish()) {
				UserError.singelton.showError("Publishing failed!");
			}
        	LinnkFatClient.application.getProgress().stopProgress();
			PublisherDialog.this.setVisible(false);
        }

        

    }
	
	/**
	 * This method initializes jButtonPublish	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonPublish() {
		if (this.jButtonPublish == null) {
			this.jButtonPublish = new JButton();
			this.jButtonPublish.setBounds(new Rectangle(360, 225, 135, 28));
			this.jButtonPublish.setText("Publish Now");
			this.jButtonPublish.setDefaultCapable(true);
			this.jButtonPublish.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PublisherDialog.this.updatePublisher();
					
					LinnkFatClient.application.getBackgroundProcess().addActivity(new Task());
					LinnkFatClient.application.getProgress().initProgress("Publish documents");
					//SwingUtilities.invokeLater(new Task());
					PublisherDialog.this.setVisible(false);
					
				}
			});
		}
		return this.jButtonPublish;
	}

	/**
	 * This method initializes jButtonAbort	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAbort() {
		if (this.jButtonAbort == null) {
			this.jButtonAbort = new JButton();
			this.jButtonAbort.setBounds(new Rectangle(90, 225, 136, 28));
			this.jButtonAbort.setText("Cancel");
			this.jButtonAbort.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PublisherDialog.this.setVisible(false);
				}
			});
		}
		return this.jButtonAbort;
	}

	/**
	 * This method initializes jCheckBoxOnlyChanged	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBoxOnlyChanged() {
		if (this.jCheckBoxOnlyChanged == null) {
			this.jCheckBoxOnlyChanged = new JCheckBox();
			this.jCheckBoxOnlyChanged.setBounds(new Rectangle(15, 195, 466, 22));
			//jCheckBoxOnlyChanged.setFont(Linnk.smallFont);
			this.jCheckBoxOnlyChanged.setText("Process only changed documents");
			this.jCheckBoxOnlyChanged.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					
					if (e.getStateChange() == ItemEvent.SELECTED) {
						PublisherDialog.this.isUnchanged = new IsUnchanged(PublisherDialog.this.isUnchanged.getLastChanged(), true);
						System.out.println("PublisherDialog: Process only changed!");
					}
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						PublisherDialog.this.isUnchanged = new IsUnchanged(PublisherDialog.this.isUnchanged.getLastChanged(), false);
						System.out.println("PublisherDialog: Process all!");
					}
				}
			});
		}
		return this.jCheckBoxOnlyChanged;
	}

}  //  @jve:decl-index=0:visual-constraint="36,40"
