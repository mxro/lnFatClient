package de.linnk.fatclient.application.v02;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.KeyStroke;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.linnk.fatclient.application.LinnkApplication;
import de.linnk.fatclient.application.LinnkFatClient;
import de.mxro.utils.URIImpl;
import de.mxro.xstream.definitions.KeyStrokes;


@XStreamAlias("v02.settings")
public class Settings {
	public java.io.File standardPublishDir;
	public java.io.File standardTemplateDir;
	public int version;
	public Color selectedColor;
	
	private Vector<java.io.File> userRoots; 
	
	private KeyStrokes keyStrokes;
	private boolean spellCheckEnabled=false;
	private String selectedLanguage="Automatic";
	public boolean xmlDeclarationForHTMLOutput = false;
	
	public java.io.File firstDatabase;
	
	public void addUserRoot(java.io.File file) {
		if (!this.userRoots.contains(file)) {
			this.userRoots.add(file);
		}
	}
	
	private transient LinnkApplication application;
	
	/**
	 * remove nonexisting files
	 *
	 */
	private void checkUserRoots() {
		final Vector<java.io.File> toDelete = new Vector<java.io.File>();
		for (final java.io.File file : this.userRoots) {
			if (!file.exists()) {
				toDelete.add(file);
			}
				
		}
		
		for (final java.io.File file : toDelete) {
			this.userRoots.remove(file);
		}
	}
	
	public Vector<java.io.File> getUserRoots() {
		this.checkUserRoots();
		return this.userRoots;
	}
	
	
	
	public LinnkApplication getApplication() {
		return this.application;
	}
	

	public KeyStroke getStroke(String id) {
		return this.getStrokes().getStroke(id);
	}

	public KeyStrokes getStrokes() {
		return this.keyStrokes;
	}
	
	public void setApplication(LinnkApplication application) {
		this.application = application;
	}

	public static void addKeyStrokes(KeyStrokes strokes) {
        // Linnk.application.getSettings().getStroke("add_hyperlink")
		
		// ItemsPanel
		strokes.addStroke("new_textitem", "", KeyStroke.getKeyStroke(
				KeyEvent.VK_T, ActionEvent.ALT_MASK));
	    strokes.addStroke("add_directory", "", KeyStroke.getKeyStroke(
	    		KeyEvent.VK_D, ActionEvent.ALT_MASK));
	    strokes.addStroke("add_hyperlink", "", KeyStroke.getKeyStroke(
	    		KeyEvent.VK_H, ActionEvent.ALT_MASK));
	   
	    strokes.addStroke("linnk", "", KeyStroke.getKeyStroke(
	    		KeyEvent.VK_N, ActionEvent.ALT_MASK));
	    strokes.addStroke("linnk_or_follow_link", "", KeyStroke.getKeyStroke(
	    		KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
	    strokes.addStroke("expand_or_collapse", "", KeyStroke.getKeyStroke(
	    		KeyEvent.VK_LEFT, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
	    //strokes.addStroke("expand", "", KeyStroke.getKeyStroke(
	    //		KeyEvent.VK_E, ActionEvent.ALT_MASK));
	    strokes.addStroke("follow_link", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_RIGHT, ActionEvent.CTRL_MASK));
	    
	    strokes.addStroke("paste_item", "", KeyStroke.getKeyStroke(
	    		KeyEvent.VK_P, ActionEvent.ALT_MASK + ActionEvent.CTRL_MASK));
	    strokes.addStroke("copy_item", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_C, ActionEvent.ALT_MASK + ActionEvent.CTRL_MASK));
	    strokes.addStroke("cut_item", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_X, ActionEvent.ALT_MASK + ActionEvent.CTRL_MASK));
	    strokes.addStroke("delete_item", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_DELETE, ActionEvent.ALT_MASK));
	    
	    strokes.addStroke("select_next", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_DOWN, ActionEvent.CTRL_MASK));
	    strokes.addStroke("select_previous", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_UP,   ActionEvent.CTRL_MASK));
	    strokes.addStroke("back", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_LEFT,   ActionEvent.CTRL_MASK));
	    strokes.addStroke("back2", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_MINUS,   ActionEvent.CTRL_MASK));
	    strokes.addStroke("move_up", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_UP, ActionEvent.ALT_MASK + ActionEvent.CTRL_MASK));
	    strokes.addStroke("move_down", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_DOWN, ActionEvent.ALT_MASK + ActionEvent.CTRL_MASK));
	    strokes.addStroke("insert_after", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_DOWN, ActionEvent.ALT_MASK));
	    strokes.addStroke("insert_before", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_UP, ActionEvent.ALT_MASK));
	    
	    strokes.addStroke("open_terminal", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_T,  ActionEvent.CTRL_MASK));
	    strokes.addStroke("open_directory", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_D,  ActionEvent.CTRL_MASK));
	    
	    // LinnkTabs
	    //strokes.addStroke("go_back", "", KeyStroke.getKeyStroke(
	    //        KeyEvent.VK_LEFT, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
	    
	    // LinnkProxy
	    //strokes.addStroke("follow_link_proxy", "", KeyStroke.getKeyStroke(
	    //        KeyEvent.VK_F, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
	    //strokes.addStroke("expand_or_collapse", "", KeyStroke.getKeyStroke(
	    //        KeyEvent.VK_0, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
	    
	    /*
	    strokes.addStroke("follow_link_proxy", "", KeyStroke.getKeyStroke(
	            KeyEvent.VK_F, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));*/
	}
	
	
	
	public boolean isSpellCheckEnabled() {
		return this.spellCheckEnabled;
	}

	public void setSpellCheckEnabled(boolean spellCheckEnabled) {
		this.spellCheckEnabled = spellCheckEnabled;
	}
	
	public String getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public int getVersion() {
		return this.version;
	}
	
	public void initialize() {
		this.userRoots = new Vector<java.io.File>();
		
		
	
		this.standardPublishDir = LinnkFatClient.application.getSystemDesktop();
		
		
		this.keyStrokes = new KeyStrokes();
		addKeyStrokes(this.keyStrokes);
		
		this.spellCheckEnabled = false;
		
		// colors
		// hell: new Color(227, 237, 249);
		// dunkler: new Color(205, 222, 244);
		this.selectedColor = new Color(227, 237, 249);
		
		this.getApplication().getLibraryFolder().createFolder("Templates");
		this.standardTemplateDir = this.getApplication().getLibraryFolder().getFolder(URIImpl.create("Templates/")).getURI().getFile();
		
	}

	public Settings() {
		super();
		this.version = 006;
	}
	
	
	
}
