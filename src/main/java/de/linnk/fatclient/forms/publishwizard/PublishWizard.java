/*
 * PublishWizard.java
 *
 * Created on 3. Oktober 2007, 21:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.linnk.fatclient.forms.publishwizard;

import java.awt.Rectangle;

import javax.swing.JOptionPane;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;

/**
 *
 * @author mer
 */
public class PublishWizard {
    
    /** Creates a new instance of PublishWizard */
    public PublishWizard() {
    }
    
    public static void main(String[] args) {
        final Class[] pages = new Class[] {
            PublishGeneralPage.class
        };
        
         //Use the utility method to compose a Wizard
        final Wizard wizard = WizardPage.createWizard(pages, 
                new WizardPage.WizardResultProducer() {
                  public boolean cancel(java.util.Map settings) {
                   boolean dialogShouldClose = JOptionPane.showConfirmDialog (null, 
                    "Abort publishing?") == JOptionPane.OK_OPTION;
                     return dialogShouldClose;
                  }
            
                  public java.lang.Object finish(java.util.Map wizardData) {
                      System.out.println("finished!");
                      for ( Object o : wizardData.keySet()) {
                          System.out.println(o.toString());
                      }
                      return null;
                  }
           });
        
        //And show it onscreen
        final Rectangle r = new Rectangle(0,0,640,480);
        final java.util.Map initialProperties = null;//new HashMap<String, Object>();
        WizardDisplayer.showWizard (wizard, r, null, initialProperties);
    }
}
