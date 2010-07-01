/*
 * PublishGeneralPage.java
 *
 * Created on 3. Oktober 2007, 14:23
 */

package de.linnk.fatclient.forms.publishwizard;

import java.awt.Component;

import org.netbeans.spi.wizard.WizardPage;

/**
 *
 * @author  mer
 */
public class PublishGeneralPage extends WizardPage {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String getDescription() {
        return "Settings for publishing";
    }
    
 
    /**
     * callend when page should be displayed
     */
    @Override
	protected void renderingPage() {
       
    }
    
    @Override
	protected String validateContents (Component component, Object o) {
        return null;
    }
    
    /** Creates new form PublishGeneralPage */
    public PublishGeneralPage() {
        this.initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Erzeugter Quelltext ">//GEN-BEGIN:initComponents
    private void initComponents() {
        this.jPanel1 = new javax.swing.JPanel();
        this.jLabel1 = new javax.swing.JLabel();
        this.jTextField1 = new javax.swing.JTextField();
        this.jPanel2 = new javax.swing.JPanel();
        this.jLabel2 = new javax.swing.JLabel();
        this.jTextField2 = new javax.swing.JTextField();
        this.jPanel4 = new javax.swing.JPanel();
        this.jButton1 = new javax.swing.JButton();

        this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        this.jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        this.jPanel1.setAlignmentX(0.0F);
        this.jPanel1.setMaximumSize(new java.awt.Dimension(32767, 40));
        this.jPanel1.setOpaque(false);
        this.jPanel1.setPreferredSize(new java.awt.Dimension(100, 40));
        this.jLabel1.setText("Title:");
        this.jLabel1.setMinimumSize(new java.awt.Dimension(32, 16));
        this.jLabel1.setPreferredSize(new java.awt.Dimension(160, 16));
        this.jPanel1.add(this.jLabel1);

        this.jTextField1.setPreferredSize(new java.awt.Dimension(180, 22));
        this.jPanel1.add(this.jTextField1);

        this.add(this.jPanel1);

        this.jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        this.jPanel2.setAlignmentX(0.0F);
        this.jPanel2.setMaximumSize(new java.awt.Dimension(32767, 80));
        this.jPanel2.setPreferredSize(new java.awt.Dimension(435, 80));
        this.jLabel2.setText("Destination:");
        this.jLabel2.setPreferredSize(new java.awt.Dimension(160, 16));
        this.jPanel2.add(this.jLabel2);

        this.jTextField2.setPreferredSize(new java.awt.Dimension(260, 22));
        this.jPanel2.add(this.jTextField2);

        this.jPanel4.setMinimumSize(new java.awt.Dimension(200, 10));
        this.jPanel4.setPreferredSize(new java.awt.Dimension(340, 10));
        final org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(this.jPanel4);
        this.jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 340, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 10, Short.MAX_VALUE)
        );
        this.jPanel2.add(this.jPanel4);

        this.jButton1.setText("Choose");
        this.jPanel2.add(this.jButton1);

        this.add(this.jPanel2);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variablendeklaration - nicht modifizieren//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // Ende der Variablendeklaration//GEN-END:variables
    
}
