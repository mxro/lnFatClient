package de.linnk.fatclient.application;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Date;
import java.util.Vector;

import javax.swing.UIManager;

import de.linnk.Linnk;
import de.linnk.domain.User;
import de.mxro.utils.URIImpl;
import de.mxro.utils.Utils;
import de.mxro.utils.log.UserError;
import de.mxro.utils.log.impl.SwtUserError;

/**
 * the facade for lunching the fat client
 * 
 * 
 * @author mer
 *
 */
public class LinnkFatClient {
	public static Font largeFont;
	public static Font smallFont;
	public static Font textFont;
	public static Font boldFont;
	
	{
		
			UserError.singelton = new SwtUserError();
		
		
		final String[] fontNames= GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		final Vector<String> fontVector= new Vector<String>();
		for (final String s: fontNames) {
			fontVector.add(s);
		}
		
		if (fontVector.contains("Verdana")) {
			largeFont = new Font("Verdana", Font.BOLD, 16);
			smallFont = new Font("Verdana", Font.ITALIC, 14);
			textFont =new Font("Verdana", Font.PLAIN, 14);
			boldFont = new Font("Verdana", Font.BOLD, 12);
		} else {
			largeFont = new Font("sansserif", Font.BOLD, 16);
			smallFont = new Font("serif", Font.ITALIC, 14);
			textFont = new Font("serif", Font.PLAIN, 14);
			boldFont = new Font("serif", Font.BOLD, 12);
		}
	}
	/**
	 * set this to true when building release versions
	 */
	public static final boolean RELEASE = true;
	
	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 9;
	public static final int VERSION_STEP = 8;
	public static final String ADDITION = "Build 1";
	
	/**
	 * for some GUI issues
	 */
	public static final int PENDING_WAIT = 200;
	
	public static final String PORTABLE_SETTINGS_DIR_NAME = "LinnkSettings";
	
	public static final String PORTABLE_DOCUMENTS_FOLDER_NAME = "Databases";
	
	public static final String PERSISTENT_LIBRARY_FOLDER_NAME = "Linnk";
	
	public static final String DOCUMENT_FOLDER_NAME = "Linnk";
	
	public static final String LINNK_TITLE = "Linnk Network Composition Environment";
	
	public static final String FIRST_DATABASE_NAME = "Data";
	
	
	public static final String LINNK_LIBRARY_FOLDER_NAME = "LibraryFolder";
	
	
	// Html/RDF-Files are generated in another thread!
	public static boolean ASYNCHRON_SAVE = true;
	
	public static LinnkApplication application;
	private java.io.File executable;
	public static User dummyUser = Linnk.newUserInstance(URIImpl.create("http://www.linnk.de/semantic/linnkuser.rdf#linnkuser"));
	public static User currentUser = Linnk.newUserInstance(URIImpl.create("http://www.linnk.de/semantic/linnkuser.rdf#linnkuser"));
	
	{
		de.mxro.utils.log.UserError.singelton.triggerExceptions = !RELEASE;
	}
	public LinnkFatClient() {
		super();
		this.executable = null;
		
	}

	public static Date getTime() {
		return new Date();
	}
	
	
	public void open() {
		this.open(null);
	}
	
	public void quit() {
		if (application == null) {
			UserError.singelton.log(this, "quit: no application loaded!", UserError.Priority.HIGH);
			return;
		}
		application.quit();
	}
	
	public void editSettings() {
		if (application == null) {
			UserError.singelton.log(this, "editSettings: no application loaded!", UserError.Priority.HIGH);
			return;
		}
		application.editSettings();
	}
	
	public void showAboutDialog() {
		if (application == null) {
			UserError.singelton.log(this, "showAboutDialog: no application loaded!", UserError.Priority.HIGH);
			return;
		}
		application.showAboutDialog();
	}
	
	public void setExecutable(java.io.File file) {
		this.executable = file;
	}
	
	public void open(java.io.File file) {
		UserError.singelton.log(this, "open: called with file: "+file, UserError.Priority.INFORMATION);
		if (application == null) {
			if (Utils.getOperatingSystem() == Utils.WINDOWS) {
				try {
			      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    } catch(final Exception e) {
			      System.out.println("Error setting native LAF: " + e);
			    }
			}
			switch (Utils.getOperatingSystem()) {
			    case Utils.WINDOWS: application = new WindowsApplication(); break;
				case Utils.MACOSX: application = new MacOSXApplication(); break;
			
				default: application = new IndependentApplication(); break;
			}
		}
		application.setExecutable(this.executable);
		if (file == null) {
			application.open();
		}
		application.open(file);
	}
	
	/** 
	 * 
	 * to handle incoming files
	 * by default they will be handled by LinnkEasyEditor
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		    
		try {
//			 first tell SkinLF which theme to use
	        //Skin theSkinToUse = SkinLookAndFeel.loadThemePack("themepack.zip");
	        //SkinLookAndFeel.setSkin(theSkinToUse);

	        // finally set the Skin Look And Feel
	       // UIManager.setLookAndFeel(new SkinLookAndFeel());
			//UIManager.setLookAndFeel(new org.compiere.plaf.CompiereLookAndFeel());
			//new org.compiere.plaf.CompierePLAFEditor ();
			//UIManager.setLookAndFeel("org.gtk.java.swing.plaf.gtk");
			// UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
			//com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true, "mac");
			//com.birosoft.liquid.LiquidLookAndFeel.setPanelTransparency(false);
			//com.birosoft.liquid.LiquidLookAndFeel.setToolbarFlattedButtons(true);
			// com.birosoft.liquid.LiquidLookAndFeel.
			// com.birosoft.liquid.LiquidLookAndFeel.set
			 // UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel");
			 // UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
			// UIManager.setLookAndFeel(new SubstanceLookAndFeel());
			
	    } catch (final Exception e) {e.printStackTrace(); }
		final LinnkFatClient linnk = new LinnkFatClient();
		
		if (args == null || args.length == 0) {
			linnk.open(null);
		} else {
			linnk.open(new java.io.File(args[0]));
		}
		
		
	}
}
