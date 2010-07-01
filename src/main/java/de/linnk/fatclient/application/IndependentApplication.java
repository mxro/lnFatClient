package de.linnk.fatclient.application;

import java.io.File;
import java.net.URISyntaxException;

import de.linnk.fatclient.application.v02.Settings;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

public class IndependentApplication extends LinnkApplication {

	
	
	@Override
	public java.io.File getSystemLibraryFolder() {
		
	
			return new java.io.File(LinnkFatClient.PORTABLE_SETTINGS_DIR_NAME).getAbsoluteFile();
		

		
	}

	@Override
	public void register() {
		
	}

	@Override
	public void setPlatformSpecificSettings(Settings settings) {
		
	}

	@Override
	public File getSystemDesktop() {
		return new java.io.File(System.getProperty("user.home"));
	}

	@Override
	public File getSystemDocumentsDirectory() {
		try {
			return FileSystemObject.newLocalRootFolder(URIImpl.fromFile(new java.io.File("").getAbsoluteFile())).forceFolder("Databases").getURI().getFile();
		} catch (final URISyntaxException e) {
			UserError.singelton.log(e);
			return null;
		}
	}
	
	
	
}
