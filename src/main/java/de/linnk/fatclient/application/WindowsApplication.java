package de.linnk.fatclient.application;

import java.awt.Color;
import java.io.File;
import java.net.URISyntaxException;

import de.linnk.fatclient.application.v02.Settings;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;


public class WindowsApplication extends LinnkApplication {

	
	@Override
	protected File getSystemLibraryFolder() {
        if (getUserHome() != null) return getUserHome();
		
		if (getUserDir() != null) return getUserDir(); 
		
		return null;
	}
	
	@Override
	public File getSystemDesktop() {
		return new java.io.File(System.getProperty("user.home")+"/Desktop/");
	}
	
	private java.io.File getUserDir() {
		return new java.io.File(System.getProperty("user.dir"));
	}
	
	private java.io.File getUserHome() {
		return new java.io.File(System.getProperty("user.home"));
	}
	
	private java.io.File getWindowsUserFolder() {
		if (getUserHome() != null) return getUserHome();
		
		if (getUserDir() != null) return getUserDir();
		
		return null;
	}
	
	private java.io.File getLibraryFolderInWindowsUserDir() {
		return new java.io.File(getWindowsUserFolder().getPath()+"/"+LinnkFatClient.PERSISTENT_LIBRARY_FOLDER_NAME);
	}
	
	private Folder getLinnkDirectory() {
		// if there is a folder LinnkSettings in the application dir
		// the settings will be loaded from there
		if (this.isPortableMode() || 
				this.standardPortableFolderExists() || 
				getWindowsUserFolder() == null ||
				(!getLibraryFolderInWindowsUserDir().exists() && !getLibraryFolderInWindowsUserDir().mkdirs())) { 
			UserError.singelton.log(this, "Linnk started in Portable Mode", UserError.Priority.INFORMATION);
			this.setPortableMode(true);
			return this.createStandardPortableFolder(); 
		}
	
		
		if (getLibraryFolderInWindowsUserDir().exists() || getLibraryFolderInWindowsUserDir().mkdirs()) {
			try {
				return FileSystemObject.newLocalRootFolder(URIImpl.fromFile(getLibraryFolderInWindowsUserDir()));
			} catch (URISyntaxException e) {
				UserError.singelton.log(e);
			}
		}
		
		UserError.singelton.log(this, "Settings Folder could not be created!", UserError.Priority.HIGH);
		return null;
	}
	
	
	
	@Override
	public void register() {
		/*if (this.getExecutable() != null) {
			if (!RegisterFileType.registerFileType("linnk", this.getExecutable().getAbsolutePath())) {
				UserError.singelton.log(this, "register: could not register file type", UserError.Priority.NORMAL);
			} else {
				UserError.singelton.log(this, "register: registred file type: linnk --> "+this.getExecutable().getAbsolutePath(), UserError.Priority.INFORMATION);
			}
		} else {
			UserError.singelton.log(this, "register: could not register file type - no executable specified.", UserError.Priority.LOW);
		} */
	}
	
	@Override
	public void setPlatformSpecificSettings(Settings settings) {
		settings.selectedColor = new Color(196, 217, 243);
		
		//settings.getStrokes().setStroke("back", "", KeyStroke.getKeyStroke(
	    //        KeyEvent.VK_LEFT,   ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));
	}


	@Override
	public File getSystemDocumentsDirectory() {
		javax.swing.JFileChooser fr = new javax.swing.JFileChooser();
		javax.swing.filechooser.FileSystemView fw = fr.getFileSystemView();
		return fw.getDefaultDirectory();
	}

	
	
	
	
}
