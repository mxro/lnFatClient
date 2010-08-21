package de.linnk.fatclient.application;

import java.awt.Component;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import LinnkResources.Library;
import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.fatclient.LinnkMainWindow;
import de.linnk.fatclient.LinnkTab;
import de.linnk.fatclient.about.AboutDialog;
import de.linnk.fatclient.application.v02.Settings;
import de.linnk.streaming.LinnkXStream;
import de.linnk.streaming.views.HTMLView;
import de.linnk.streaming.views.PlainXMLView;
import de.linnk.streaming.views.RDFView;
import de.linnk.streaming.views.RWView;
import de.linnk.streaming.views.View;
import de.linnk.streaming.views.WithoutExtensionView;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.filesystem.ext.OpenObject;
import de.mxro.jmyspell.SpellCheck;
import de.mxro.utils.Mxro;
import de.mxro.utils.URIImpl;
import de.mxro.utils.application.Application;
import de.mxro.utils.background.Activity;
import de.mxro.utils.log.UserError;

public abstract class LinnkApplication extends Application {
	
	protected LinnkMainWindow lmw;
	
	private transient View defaultView;
	private transient View publishView;
	
	private Settings settings;
	public boolean firstRun;
	
	private boolean portableMode=false;
	private Folder portableFolder;
	
	private Folder linnkDocumentsFolder;
	
	private SpellCheck spellCheck;
	
	private Folder libraryFolder;
	
	protected abstract java.io.File getSystemLibraryFolder();
	
	public final void setLibraryFolder(Folder folder) {
		this.libraryFolder = folder;
	}
	
	public final Folder getLibraryFolder() {
		
		return this.libraryFolder;
	}
	
	public SpellCheck getSpellCheck() {
		if (this.spellCheck == null) {
			final Folder dictionariesFolder = this.getLibraryFolder().getFolder(URIImpl.create("Dictionaries"));
			if (dictionariesFolder == null) {
				UserError.singelton.log("LinnkApplication: Could not find Spell Checking dictionaries directory.", UserError.Priority.HIGH);
				return null;
			}
			this.spellCheck = new SpellCheck(dictionariesFolder);
		}
		return this.spellCheck;
	}
	
	public void startRealtimeSpellCheck(JTextComponent textComponent, Locale locale) {
		if (this.getSettings().isSpellCheckEnabled()) {
			Locale loc;
			if (this.getSettings().getSelectedLanguage().equals("Automatic")) {
				loc = locale;
			} else {
				if (this.getSettings().getSelectedLanguage().equals( "English" )) {
					loc = new Locale("EN", "en");
				} else
					if (this.getSettings().getSelectedLanguage().equals( "Deutsch" )) {
						loc = new Locale("DE", "de");
					} else
						loc = locale;
			}
			this.getSpellCheck().startRealtimeSpellCheck(textComponent, loc);
		}
	}
	
	
	
	public void stopRealtimeSpellCheck(JTextComponent textComponent) {
		if (this.getSettings().isSpellCheckEnabled()) {
			this.getSpellCheck().stopRealtimeSpellCheck(textComponent);
		}
	}
	
	public LinnkApplication() {
		super();
		
	}

	public final void editSettings() {
		this.settings = new SettingsDialog(this.lmw).editObject(this.settings);
	}
	
	public final void showAboutDialog() {
		final AboutDialog ab = new AboutDialog(null);
		de.mxro.utils.Utils.centerComponent(ab, null);
		ab.setModal(true);
		
		ab.setVisible(true);
	}
	
	public abstract void setPlatformSpecificSettings(Settings settings);
	
	public final Settings getSettings() {
		
		
		if (this.settings == null) {
			if (this.getSettingsFile() == null) {
				de.mxro.utils.log.UserError.singelton.showError("Settings file cannot be loaded.");
				return null;
			}
			
			final LinnkXStream xstream = de.linnk.streaming.LinnkXStream.singelton;
			Object obj = null;
			try {
				final InputStream is = this.getSettingsFile().getInputStream();
				if (is != null) {
					obj = xstream.fromXML(is);
				}		
			} catch (final Exception e) {
				de.mxro.utils.log.UserError.singelton.log(e);
				obj = null;
			}
			// obj = null;
			
			if (obj instanceof Settings) {
				
				this.settings = (Settings) obj;
				if (!(this.settings.getVersion() < new Settings().getVersion()))
					return this.settings;
				else {
					de.mxro.utils.log.UserError.singelton.log("LinnkApplication.getSettings: Settings file is outdated and will be recreated ...", UserError.Priority.NORMAL);
				}
			} 
			
			de.mxro.utils.log.UserError.singelton.log("LinnkApplication.getSettings: Settings file will be created ...", UserError.Priority.NORMAL);
			this.settings = new Settings();
			this.settings.setApplication(this);
			this.settings.initialize();
			this.setPlatformSpecificSettings(this.settings);
			
		}
		return this.settings;
	}
	
	protected final void setLinnkDocumentsDirectory(Folder documentsDir) {
		this.linnkDocumentsFolder = documentsDir;
	}
	
	public final Folder getLinnkDocumentsDirectory() {
		return linnkDocumentsFolder;
		
	}
	
	public void firstRun() {
		
		
		installLibrary();
		initialiseUtilityAppFolder();
		
		OpenObject.newInstance("http://www.mxdt.de/davread/My_Applications/Linnk/Documentation/First_Steps/First_Steps.html").open();
		
		// create first database:
		if (this.getLinnkDocumentsDirectory() == null) {
			UserError.singelton.showError("Could not create directory for database.");
			return;
		}
		Folder docDir = this.getLinnkDocumentsDirectory();
		
		final File rootFile;
		/* first database has to be created */
		if (docDir.getFolder(URIImpl.create(LinnkFatClient.FIRST_DATABASE_NAME)) == null) {
			/*Folder dataDir = docDir.forceFolder(Linnk.FIRST_DATABASE_NAME);
			if (dataDir == null) {
				UserError.singelton.log(this, "Could not create sub directory: "+Linnk.FIRST_DATABASE_NAME, UserError.Priority.HIGH);
				UserError.singelton.showError("Could not create directory for database.");
				return;
			}*/
			final Folder modelDir = this.getLibraryFolder().getFolder(URIImpl.create("FirstDatabaseModel"));
			if (modelDir == null) {
				UserError.singelton.log(this, "Could not get model in folder: "+this.getLibraryFolder().getURI(), UserError.Priority.HIGH);
				UserError.singelton.showError("Could not create directory for database.");
				return;
			}
			Folder dataDir;
			
				dataDir = (Folder) docDir.importFile(modelDir.getFolder(URIImpl.create(LinnkFatClient.FIRST_DATABASE_NAME)).getURI(), false);
			
			
			if (dataDir == null) {
				UserError.singelton.showError("Could not copy model for database.");
				return;
			}
			
			rootFile = dataDir.getFile(URIImpl.create("index.xml"));
			if (rootFile == null) {
				UserError.singelton.showError("Corrupt model for database.");
				return;
			}
			
			
		} else 
		/* first database already exists */
		{
			rootFile = docDir.getFolder(URIImpl.create(LinnkFatClient.FIRST_DATABASE_NAME)).getFile((URIImpl.create("index.xml")));
			if (rootFile == null) {
				UserError.singelton.showError("Corrupt data in users dir.");
				return;
			}
		}
		
		this.lmw.getLinnkTabs().addTab(rootFile.makeLocal());
	}
	
	public final boolean installLibrary() {
		Folder destination = de.mxro.utils.Utils.getOwnerFolder(this.getLibraryFolder());
		UserError.singelton.log(this, "Install Library "+destination.getURI(), UserError.Priority.INFORMATION);
		return new Library().installTo(destination);
	}
	
	private final boolean libraryFilesExist() {
		
		return this.getLibraryFolder().getFolder(URIImpl.create("Templates")) != null;
		
	}
	
	public LinnkMainWindow buildApplication() {
		/*this.firstRun = false;
		if (!this.libraryFilesExist()) {
			
			this.firstRun = true;
			
		}*/
		
		
		System.out.println(com.thoughtworks.xstream.io.HierarchicalStreamDriver.class.getName());
		initialiseFolders();
		
		
		
		this.lmw = new LinnkMainWindow();
		de.mxro.utils.Utils.centerComponent(this.lmw, null);
		this.lmw.setVisible(true);
        if (this.firstRun) {
			
			this.firstRun();
		}
		this.register();
		return this.lmw;
	}
	
	private final class CheckUserRoot implements Activity {
		private final NodeDocument doc;

		public CheckUserRoot(final NodeDocument doc) {
			super();
			this.doc = doc;
		}
		
		public void run() {
			final NodeDocument root = de.linnk.utils.Utils.getRootDocument(this.doc);
			if ( root != null )
			  LinnkApplication.this.getSettings().addUserRoot(root.getFile().makeLocal());
		}
	}
	
	public void checkUserRoot(NodeDocument doc) {
		this.getBackgroundProcess().addActivity(new CheckUserRoot(doc));
	}
	
	@Override
	public void open(java.io.File file) {
		//this.getSettings();
		if (this.lmw == null) {
			this.lmw = this.buildApplication();
		}
		if (file!=null) {
			de.mxro.utils.log.UserError.singelton.log("Application.open: "+file.getAbsolutePath(), UserError.Priority.LOW);
			final LinnkTab tab = this.lmw.getLinnkTabs().addTab(file);
			this.lmw.setVisible(true);
			// determine if you have to add new root
			if (tab == null) {
				UserError.singelton.showError("Could not open document: "+file);
			}
		};
	}
	
	public final File getSettingsFile() {
		return this.getLibraryFolder().forceFile("Linnk_Settings.xml");
	}
	
	@Override
	public void quit() {
		final LinnkXStream xstream = de.linnk.streaming.LinnkXStream.singelton;
		xstream.toXML(this.settings, this.getSettingsFile().getOutputStream());
		UserError.singelton.log("Settings file saved to "+this.getSettingsFile().getURI(), UserError.Priority.INFORMATION);
		//this.lmw.getLinnkTabs().deselectAll();
		
		for (final Component c: this.lmw.getLinnkTabs().getJTabbedPane().getComponents()) {
			if (c instanceof LinnkTab) {
				final LinnkTab tab = (LinnkTab) c;
				try {
					tab.getHolder().getDocumentPanel().getItemspanel().updateItems();
				} catch (final Exception e) {
					continue;
				}
				UserError.singelton.log("Check altered: "+tab.getHolder().getDocumentPanel().getDocument().getName(), UserError.Priority.INFORMATION);
				if (tab.getHolder().getDocumentPanel().getDocument().isAltered()) {
					
					final Object[] options = {"Yes",
		                    "No"};
					final int n = JOptionPane.showOptionDialog(null,
						"Do you want to save document "+tab.getHolder().getDocumentPanel().getDocument().getName(),
						"Save",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[0]);
					if (n==0) {
						Linnk.S.saveDocument(tab.getHolder().getDocumentPanel().getDocument());
						
					}
				}
			}
		}
		System.exit(0);
	}
	
	
	@Override
	public final void open() {
		this.open(null);
	}
	
	
	public abstract void register();
	
	public Folder getDefaultTemplate() {
		final Folder folder = this.getLibraryFolder().getFolder(URIImpl.create("Templates/Default/"));
		if (folder == null) {
			UserError.singelton.log("LinnkApplication: Default template could not be loaded: "+this.getLibraryFolder().getURI()+"Templates/Default", UserError.Priority.HIGH);
		}
		return folder;
	}
	
	
	
	public View getDefaultView() {
		if (this.defaultView != null)
			return this.defaultView;
		
		this.defaultView = new RDFView(
				             new HTMLView(
				               new RWView(
				                 new PlainXMLView() , false, this.getDefaultTemplate() ,LinnkFatClient.ASYNCHRON_SAVE),
				               LinnkFatClient.ASYNCHRON_SAVE), 
				             LinnkFatClient.ASYNCHRON_SAVE);
			// new StandaloneView(new RWView(this.getDefaultTemplate(), true), new RDFView(), );
		
		return this.defaultView;
	}

	public View getBrowseView() {
	
			
		return this.getDefaultView();
	}

	public View getPublishView() {
		if (this.publishView == null) {
			this.publishView = 	
			  new WithoutExtensionView(
				new WithoutExtensionView( 
				   new RDFView(
		             new HTMLView(
				       new RWView(
				         new PlainXMLView() , false, this.getDefaultTemplate() ,LinnkFatClient.ASYNCHRON_SAVE),
				       LinnkFatClient.ASYNCHRON_SAVE), 
				     LinnkFatClient.ASYNCHRON_SAVE),
				    ".xsl"),
				  ".xml");
		}
			
		return this.publishView;
	}

	

	public boolean isPortableMode() {
		return portableMode;
	}
	
	protected void initialisePortableFolders() {
		this.setPortableMode(true);
		if (portableFolder == null) {
			try {
			    Folder applicationDirectory = FileSystemObject.newLocalRootFolder(URIImpl.fromFile(new java.io.File("").getAbsoluteFile()));
			    
			    Folder portableFolder = applicationDirectory.forceFolder(LinnkFatClient.PORTABLE_SETTINGS_DIR_NAME);
			    
				this.setPortableFolder(portableFolder);
			} catch (URISyntaxException e) {
				UserError.singelton.log(this, "initialisePortableFolders: Cannot create portable folder in '"+new java.io.File("")+"'", UserError.Priority.HIGH);
			}
			
		}
		
		this.firstRun = this.getPortableFolder().get(URIImpl.create(LinnkFatClient.LINNK_LIBRARY_FOLDER_NAME)) == null;
		
		Folder libFolder = this.getPortableFolder().forceFolder(LinnkFatClient.LINNK_LIBRARY_FOLDER_NAME);
		UserError.singelton.log(this, "initialisePortableFolders: Library Folder: '"+libFolder.getURI().toString()+"'", UserError.Priority.INFORMATION);
		this.setLibraryFolder(libFolder);
		
   
		this.setLinnkDocumentsDirectory(this.getLibraryFolder().forceFolder(LinnkFatClient.PORTABLE_DOCUMENTS_FOLDER_NAME));
		
		UserError.singelton.log(this, "initialisePortableFolders: Documents Folder '"+this.getLinnkDocumentsDirectory()+"'", UserError.Priority.INFORMATION);
		
	}
	
	protected void initialisePersistentFolders() {
		Folder sytemLibraryFolder = null;
		try {
			sytemLibraryFolder = FileSystemObject.newLocalRootFolder(URIImpl.fromFile(this.getSystemLibraryFolder()));
		} catch (URISyntaxException e) {
			UserError.singelton.log(this, "initialisePersistentFolders: Cannot find system library folder '"+this.getSystemLibraryFolder()+"'", UserError.Priority.HIGH);
			initialisePortableFolders(); return;
		}
		
		this.firstRun = sytemLibraryFolder.get(URIImpl.create(LinnkFatClient.PERSISTENT_LIBRARY_FOLDER_NAME)) == null;
		
		Folder libFolder = sytemLibraryFolder.forceFolder(LinnkFatClient.PERSISTENT_LIBRARY_FOLDER_NAME);
		
		if (libFolder == null) {
			UserError.singelton.log(this, "initialisoePersistentFolders: Could not create library folder in '"+sytemLibraryFolder.getURI()+"'", UserError.Priority.NORMAL);
			initialisePortableFolders(); return;
		}
		
		Folder linnkLibFolder = libFolder.forceFolder(LinnkFatClient.LINNK_LIBRARY_FOLDER_NAME);
		
		this.setLibraryFolder(linnkLibFolder);
		
		UserError.singelton.log(this, "initialisePersistentFolders: Library Folder: '"+libraryFolder.getURI().toString()+"'", UserError.Priority.INFORMATION);
	
		if (this.getSystemDocumentsDirectory() == null || !this.getSystemDocumentsDirectory().exists()) {
			try {
				this.setLinnkDocumentsDirectory( FileSystemObject.newLocalRootFolder(URIImpl.fromFile(this.getSystemDocumentsDirectory())).forceFolder(LinnkFatClient.DOCUMENT_FOLDER_NAME));
			} catch (URISyntaxException e) {
				UserError.singelton.log(e);
			}
		}
		
		UserError.singelton.log(this, "initialisePersistentFolders: Documents Folder '"+this.getLinnkDocumentsDirectory()+"'", UserError.Priority.INFORMATION);
		
	}
	
	protected void initialiseUtilityAppFolder() {
		Folder utilityAppsFolder = this.getLibraryFolder().getFolder(URIImpl.create("UtilityApps"));
		if (utilityAppsFolder == null) {
			UserError.singelton.log(this, "Could not find utility Apps Folder in '"+this.getLibraryFolder().getURI()+"'", UserError.Priority.NORMAL);
		} else {
		  UserError.singelton.log(this, "initialiseFolders: Utility Apps Folder '"+utilityAppsFolder.getURI()+"'", UserError.Priority.INFORMATION);
		}
		
		Mxro.setUtilityAppsFolder(utilityAppsFolder);
	}
	
	protected void initialiseFolders() {
		// get System Directory Folders
		java.io.File systemsDesktop = this.getSystemDesktop();
		UserError.singelton.log(this, "initialiseFolders: System Desktop path: '"+systemsDesktop+"'", UserError.Priority.INFORMATION);
		
		java.io.File systemDocumentsDirectory = this.getSystemDocumentsDirectory();
		UserError.singelton.log(this, "initialiseFolders: System Documents Directory: '"+systemDocumentsDirectory+"'", UserError.Priority.INFORMATION);
		
		java.io.File systemsLibraryFolder = this.getSystemLibraryFolder();
		UserError.singelton.log(this, "initialiseFolders: Systems Library Folder '"+systemsLibraryFolder+"'", UserError.Priority.INFORMATION);
		
		// ---
		// check if settings folder exists in program directory
		// if run in portable mode
		// --
		
		java.io.File localSettingsFolder = new java.io.File(LinnkFatClient.PORTABLE_SETTINGS_DIR_NAME).getAbsoluteFile();
		if (localSettingsFolder.exists()) {
			this.setPortableMode(true);
		}
		
		if (this.isPortableMode()) {
			initialisePortableFolders();
			
		} else {
			initialisePersistentFolders();
		}
		
		 Folder libraryFolder = this.getLibraryFolder();
			
		 UserError.singelton.log(this, "initialiseFolders: Preferred System Library Folder: '"+this.getLibraryFolder().getURI()+"'", UserError.Priority.INFORMATION);
			
		if (this.getLibraryFolder() == null) {
		  UserError.singelton.log(this, "Could not find library folder at preferred location.", UserError.Priority.NORMAL);
		}
			
		
		UserError.singelton.log(this, "initialiseFolders: Linnk Library Folder '"+libraryFolder.getURI()+"'", UserError.Priority.INFORMATION);
		
		initialiseUtilityAppFolder();
		
		
	}

	public void setPortableMode(boolean portableMode) {
		this.portableMode = portableMode;
	}

	
	private java.io.File standardPortableSettingsFolder() {
		return new java.io.File(LinnkFatClient.PORTABLE_SETTINGS_DIR_NAME).getAbsoluteFile();
	}
	
	public Folder createStandardPortableFolder() {
		if (portableFolder == null) {
			if (!standardPortableSettingsFolder().exists() && !standardPortableSettingsFolder().mkdirs()) { 
				UserError.singelton.log(this, "Portable Folder '"+standardPortableSettingsFolder().getAbsolutePath()+"' cannot be created.", UserError.Priority.NORMAL);
				
				return null; 
			}
			try {
				this.portableFolder = FileSystemObject.newLocalRootFolder(URIImpl.fromFile(standardPortableSettingsFolder()));
				return this.portableFolder;
			} catch (URISyntaxException e) {
				UserError.singelton.log(e);
			}
		}
		return portableFolder;
	}

	public boolean standardPortableFolderExists() {
		return standardPortableSettingsFolder().exists();
	}

	public Folder getPortableFolder() {
		return portableFolder;
	}
	
	public void setPortableFolder(Folder portableFolder) {
		this.portableFolder = portableFolder;
	}

	
	
	
}
