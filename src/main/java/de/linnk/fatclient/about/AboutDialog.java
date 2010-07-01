package de.linnk.fatclient.about;

import java.awt.Frame;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import de.linnk.fatclient.application.LinnkFatClient;
import de.mxro.utils.log.UserError;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JLabel jLabel1 = null;

	private JLabel jLabelVersion = null;

	private JLabel jLabel = null;

	private JScrollPane jScrollPane = null;

	private JTextPane jTextPane = null;

	private JButton jButtonOkay = null;

	private JButton jButton = null;

	/**
	 * @param owner
	 */
	public AboutDialog(Frame owner) {
		super(owner);
		this.initialize();
		//this.setModal(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(344, 333);
		this.setTitle("About");
		this.setContentPane(this.getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (this.jContentPane == null) {
			this.jLabel = new JLabel();
			this.jLabel.setBounds(new Rectangle(15, 75, 226, 16));
			this.jLabel.setText("Licence/ Acknowledgements:");
			this.jLabelVersion = new JLabel();
			this.jLabelVersion.setBounds(new Rectangle(15, 45, 136, 16));
			this.jLabelVersion.setText("Version "+LinnkFatClient.MAJOR_VERSION+"."+LinnkFatClient.MINOR_VERSION+"."+LinnkFatClient.VERSION_STEP+" "+LinnkFatClient.ADDITION);
			this.jLabel1 = new JLabel();
			this.jLabel1.setBounds(new Rectangle(15, 15, 61, 16));
			this.jLabel1.setText("Linnk");
			this.jContentPane = new JPanel();
			this.jContentPane.setLayout(null);
			this.jContentPane.add(this.jLabel1, null);
			this.jContentPane.add(this.jLabelVersion, null);
			this.jContentPane.add(this.jLabel, null);
			this.jContentPane.add(this.getJScrollPane(), null);
			this.jContentPane.add(this.getJButtonOkay(), null);
			this.jContentPane.add(this.getJButton(), null);
		}
		return this.jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (this.jScrollPane == null) {
			this.jScrollPane = new JScrollPane();
			this.jScrollPane.setBounds(new Rectangle(15, 105, 316, 151));
			this.jScrollPane.setViewportView(this.getJTextPane());
		}
		return this.jScrollPane;
	}

	private static String slurp (InputStream in) throws IOException {
	    final StringBuffer out = new StringBuffer();
	    final byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	}
	
	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getJTextPane() {
		if (this.jTextPane == null) {
			this.jTextPane = new JTextPane();
			this.jTextPane.setEditable(false);
			try {
				final String s = slurp(this.getClass().getResource("acknowledgements.txt").openStream());
				final String s2 = slurp(this.getClass().getResource("licence.txt").openStream());
				this.jTextPane.setText(s2+"\n"+s);
			} catch (final IOException e) {
				UserError.singelton.log(e);
			}
			
		}
		return this.jTextPane;
	}

	/**
	 * This method initializes jButtonOkay	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOkay() {
		if (this.jButtonOkay == null) {
			this.jButtonOkay = new JButton();
		}
		return this.jButtonOkay;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (this.jButton == null) {
			this.jButton = new JButton();
			this.jButton.setBounds(new Rectangle(255, 270, 75, 29));
			this.jButton.setText("Close");
			this.jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AboutDialog.this.setVisible(false);
				}
			});
		}
		return this.jButton;
	}

}  //  @jve:decl-index=0:visual-constraint="105,10"
