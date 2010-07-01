package de.linnk.fatclient.application;


import java.io.File;

import de.linnk.fatclient.application.v02.Settings;
import de.mxro.utils.log.UserError;


public class MacOSXApplication extends LinnkApplication {
	
	
	@Override
	public java.io.File getSystemLibraryFolder() {
		
		final String userDir = System.getProperty("user.home");
		final String libraryFolderPath = userDir + "/Library/Application Support/";
		final java.io.File libraryFolderFile = new java.io.File(libraryFolderPath);
		if (!new java.io.File(libraryFolderPath).exists()) {
			UserError.singelton.log(this, "getSystemLibraryFolder: Cannot find Mac OSX application library folder '"+libraryFolderFile+"'", UserError.Priority.HIGH);
			return null;
		}
		
		return libraryFolderFile;
	}

	public MacOSXApplication() {
		super();
		
	}

	@Override
	public void register() {
		
		
	}
	
	@Override
	public void setPlatformSpecificSettings(Settings settings) {
		
	}

	@Override
	public File getSystemDesktop() {
		return new java.io.File(System.getProperty("user.home")+"/Desktop/");
	}

	@Override
	public File getSystemDocumentsDirectory() {
		final java.io.File docDir = new java.io.File(System.getProperty("user.home")+"/Documents/");
		if (!docDir.exists() && !docDir.mkdir()) {
			UserError.singelton.log("MacOSXApplication.getDocumentsDir: Couldn't create Folder: "+docDir, UserError.Priority.HIGH);
			return null;
		}
		return docDir;
	}
	
	
	
}
