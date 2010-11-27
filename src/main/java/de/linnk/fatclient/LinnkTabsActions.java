package de.linnk.fatclient;


import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.linnk.DocumentBuilder;
import de.linnk.Linnk;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.domain.Document;
import de.linnk.domain.LinnkConstants;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.DocumentPanelContainer;
import de.linnk.fatclient.fromnetbeans.SelectURIDialog;
import de.linnk.fatclient.icons.Icons;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.views.PlainXMLView;
import de.linnk.transform.ChangeURI;
import de.linnk.transform.Transformation;
import de.mxro.filesystem.FileSystem;
import de.mxro.filesystem.FileSystemAddressMapper;
import de.mxro.filesystem.ext.LocalFileSystem;
import de.mxro.filesystem.ext.OpenObject;
import de.mxro.filesystem.ext.VirtualRealAddressMapper;
import de.mxro.filesystem.v01.LocalRootFolder;
import de.mxro.swing.actions.Actions;
import de.mxro.swing.actions.MyAction;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

public class LinnkTabsActions extends Actions {
	protected LinnkTabs linnkTabs;
	
	public LinnkTabsActions(final LinnkTabs linnkTabs, WindowInterface wi) {
		super(wi);
		this.linnkTabs = linnkTabs;
		this.addAction(new NewDocumentAction(this.linnkTabs));
		this.addAction(new SaveDocumentAction(this.linnkTabs));
		this.addAction(new OpenDocumentAction(this.linnkTabs));
		this.addAction(new OpenInBrowserAction(this.linnkTabs));
		this.addAction(new PrintPreviewAction(this.linnkTabs));
		this.addAction(new CloseTabAction(this.linnkTabs));
		this.addAction(new GoBackAction(this.linnkTabs));
		this.addAction(new GoForwardAction(this.linnkTabs));
		this.addAction(new LevelUpAction(this.linnkTabs));
		this.addAction(new EditSettingsAction(this.linnkTabs));
		this.addAction(new ReportBugAction(this.linnkTabs));
		this.addAction(new HomepageAction(this.linnkTabs));
		this.addAction(new DocumentationAction(this.linnkTabs));
		this.addAction(new AboutAction(this.linnkTabs));
		this.addAction(new SelectDocumentURI(this.linnkTabs));
	}
	
	public class SelectDocumentURI extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		private void changeURI(NodeDocument doc, URI newURI) {
	 		URI folderURI = newURI.getFolder();
	 		// First change links of all children
	 		for (String uristr : doc.getChildNodes()) {
	 			URI uri = URIImpl.create(uristr);
	 			NodeDocument child = DocumentLoader.singelton.loadFromFile(doc.getFolder().getFile(uri));
	 			//	doc.loadDocumentFromSimpleLink(sl);
	 			if (child != null) {
	 				changeURI(child, folderURI.resolve(uri));
	 			}
	 		}
	 		new ChangeURI(newURI, Transformation.NONE).applyTransformation(doc);
			Linnk.S.saveDocument(doc, doc.getFolder(), new PlainXMLView());
	 		
	 	}
	 
		@Override
		protected void myActionPerformed(ActionEvent e) {
			final DocumentPanelContainer holder = this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder();
			NodeDocument doc = holder.getDocumentPanel().getDocument();
			
			if (doc.getOwnerLink() != null) {
				JOptionPane.showMessageDialog(null,
					    "Please select a root document (Meaning no\n"+
					    "documents are linking to the document).", 
					    "Message", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			SelectURIDialog dialog = new SelectURIDialog();
			URI uri = dialog.selectURI(doc);
			if (uri == null) {
				return;
			}
			if (!uri.getFileName().equals("")) {
				uri = uri.getFolder();
			}
			uri = URIImpl.create(uri.toString()+doc.getFilename());
			
			changeURI(doc, uri);
			
			this.linnkTabs.removeActiveTab();
			
		}
		
		public SelectDocumentURI(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Select URI");
			this.putValue(Action.SHORT_DESCRIPTION, "Defines an unique identifier for this document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null;
		}
		
		
		
	}
	
	public class AboutAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			LinnkFatClient.application.showAboutDialog();
		}
		
		public AboutAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "About");
			this.putValue(Action.SHORT_DESCRIPTION, "Shows about dialog.");
		    //putValue(Action.SMALL_ICON, Icons.getLargeIcon("Web.png"));
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return true;
		}		
		
	}
	
	public class DocumentationAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			if (!OpenObject.newInstance("http://www.mxdt.de/davread/My_Applications/Linnk/Documentation/Documentation.html").open()) {
				UserError.singelton.showError("Could not connect to website.");
			}
		}
		
		public DocumentationAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Documentation");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens website with documentation.");
		    // putValue(Action.SMALL_ICON, Icons.getLargeIcon("Web.png"));
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return true;
		}		
		
	}
	
	public class HomepageAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			if (!OpenObject.newInstance("http://www.linnk.de/").open()) {
				UserError.singelton.showError("Could not connect to website.");
			}
		}
		
		public HomepageAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Homepage");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens official Linnk website.");
		    this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("Web.png"));
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return true;
		}		
		
	}
	
	public class ReportBugAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			if (!OpenObject.newInstance("http://www.mxro.de/contact/contact.php?subject=Bug%20Report&referring_to=Linnk").open()) {
				UserError.singelton.showError("Could not connect to website.");
			}
		}
		
		public ReportBugAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Report Bug");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens a website where you can report a bug.");
		    this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("Bug.png"));
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return true;
		}		
		
	}
	
	public class NewDocumentAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			final JFileChooser fc = new JFileChooser(new File("*.xml"));
			
			if (fc.showSaveDialog(this.linnkTabs)!=JFileChooser.APPROVE_OPTION)
				return;
			
			
			final String selectedFileAbsolutePath = fc.getSelectedFile().getAbsolutePath();
			
			final String selectedFileWithoutExtension = mx.gwtutils.MxroGWTUtils.removeExtension(selectedFileAbsolutePath);
			
			final String selectedFileWithDocumentExtension = selectedFileWithoutExtension+
			DocumentBuilder.documentExtension ;
			
			final java.io.File newfile = new java.io.File(
					selectedFileWithDocumentExtension);
			URIImpl folderURI;
			try {
				folderURI = URIImpl.fromFile(newfile).getFolder();
			} catch (URISyntaxException e1) {
				folderURI = null;
				UserError.singelton.log(e1);
			}
			
			URI newURI = URIImpl.create("http://www.linnk.de/"+newfile.getName());
			
			URI virtualFolder = newURI.getFolder();
			
			URI realFolder = URIImpl.fromJavaURI(newfile.toURI()).getFolder();
			
			FileSystem mapperSystem =new FileSystemAddressMapper(LocalFileSystem.singelton, new VirtualRealAddressMapper(virtualFolder, realFolder));
			
			NodeDocument newdoc = DocumentBuilder.newRootDocument(
					newURI, 
					LinnkFatClient.currentUser, 
					new LocalRootFolder(
							folderURI, mapperSystem
							)
					);
			
//			if (newdoc == null) {
//				de.mxro.UserError.singelton.showError("Document could not be created!");
//			}
//			newdoc.save();
////			final Document newdoc = Linnk.createDocumentAndXMLView(
////					de.mxro.Utils.removeExtension(newfile.getName()), User.currentUser, newfile); 
//			
//			newdoc = DocumentLoader.singelton.loadFromFile(newdoc.getFile());
//			if (newdoc == null) {
//				UserError.singelton.log(this, "Could not load doucment from file '"+newfile.toString(), UserError.Priority.HIGH);
//				UserError.singelton.showError("New Document could not be loaded.");
//			}
			
				
			this.linnkTabs.addTab(newdoc);
				
				
			
		}
		
		public NewDocumentAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "New Database");
			this.putValue(Action.SHORT_DESCRIPTION, "Creates a new database.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return true;
		}		
		
	}
	
	public class SaveDocumentAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			final DocumentPanelContainer holder = this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder();
			if (holder != null) {
				holder.saveDocument();
			}
		}
		
		public SaveDocumentAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Save");
			this.putValue(Action.SHORT_DESCRIPTION, "Saves currently opened document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("filesave.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null;
		}
		
		
		
	}
	
	public static class OpenDocumentAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		private File file;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			LinnkTab tab;
			if (this.file == null) {
				final JFileChooser fc = new JFileChooser(new File("*.xml"));
				
				if (fc.showOpenDialog(this.linnkTabs)!=JFileChooser.APPROVE_OPTION)
					return;
				
				tab = this.linnkTabs.addTab(fc.getSelectedFile().getAbsoluteFile());
			} else {
				tab = this.linnkTabs.addTab(this.file);
			}
			LinnkFatClient.application.checkUserRoot(tab.getHolder().getDocumentPanel().getDocument());
			//DocumentPanelHolder holder = new DocumentPanelHolder();
			
			//holder.showDocument(fc.getSelectedFile().getAbsoluteFile());
			
			
			
			// holder.saveDocument();
			
				//holder.showDocument(de.mxro.filesystem.LocalLink.fromURI(de.mxro.filesystem.URI.fromFile(fc.getSelectedFile())));
			//LinnkEasyEditor.newEditor(fc.getSelectedFile().getAbsolutePath());
			
		}
		
		/**
		 * action will always open specified file
		 * 
		 * @param linnkTabs
		 * @param file
		 */
		public OpenDocumentAction(LinnkTabs linnkTabs, File file) {
			this(linnkTabs);
			this.file = file;
			if (file == null) {
				UserError.singelton.log(this, "Passed file was null.", UserError.Priority.HIGH);
				return;
			}
			final Document doc = DocumentLoader.singelton.loadFromJavaFile(file);
			if (!(doc instanceof NodeDocument)) {
				UserError.singelton.log(this, "destination document is not correct.", UserError.Priority.HIGH);
				return;
			}
			this.putValue(Action.NAME, ((NodeDocument) doc).getTitle());
			this.putValue(Action.SHORT_DESCRIPTION, "Opens document '"+((NodeDocument) doc).getTitle()+"'");
		}
		
		public OpenDocumentAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			
			this.putValue(Action.NAME, "Open");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens a document file.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return true;
		}
		
		
	}
	
	public class OpenInBrowserAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			final DocumentPanelContainer holder = this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder();
			if (holder != null) {
				holder.saveDocument();
				// de.mxro.UserError.singelton.log("openInBrowser");
				de.mxro.filesystem.File xmlFile = holder.getDocumentPanel().getDocument().getFile();
				
				final String htmlFileName = mx.gwtutils.MxroGWTUtils.removeExtension(xmlFile.getName())+LinnkConstants.htmlExtension;
				de.mxro.filesystem.File htmlFile = xmlFile.getOwner().getFile(URIImpl.create(htmlFileName));
				if (htmlFile == null) {
					UserError.singelton.log(this, "html file does not exist: '"+htmlFileName+"'\n " +
							"in Folder "+xmlFile.getOwner().getURI(), UserError.Priority.HIGH);
					UserError.singelton.showError("HTML version does not exist.");
					return;
				}
				if (!htmlFile.isLocal()) {
					UserError.singelton.log(this, "Document not locally availalbe "+htmlFile.getURI().toString(), UserError.Priority.INFORMATION);
					UserError.singelton.showError("No local document.");
					return;
				}
				UserError.singelton.log(this, "Open in Browser: "+htmlFile.getURI().toString(), UserError.Priority.INFORMATION);
				de.mxro.filesystem.ext.OpenObject.newInstance(htmlFile.makeLocal().toString()).open();
				//this.linnkTabs.removeActiveTab();
				
			}
		}
		
		public OpenInBrowserAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Open in Browser");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens currently selected document in your standard browser.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("mozilla.png"));
			
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			
			return this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null;
		}
		
	}
	
	public class PrintPreviewAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			/*try {
				DocumentPanelContainer holder = this.linnkTabs.getActiveDocumentContainer();
				if (holder != null) {
					new java.io.File("temp").mkdir();
					//System.out.println(theme.getFile(new URI("Theme.plist")).getName());
					new Publisher(
							new de.linnk.streaming.StandaloneView(
									new de.linnk.streaming.RWView(Linnk.application.getDefaultTemplate(), true)
									),true).publish(
											holder.getDocumentPanel().getDocument(), 
											FileSystemObject.newLocalRootFolder(URI.fromFile(new java.io.File("temp").getAbsoluteFile())));
					
					// de.mxro.UserError.singelton.log("PrintPreview");
					de.mxro.filesystem.OpenObject.newInstance(new java.io.File(new java.io.File("temp").getAbsolutePath()+"/"+Utils.removeExtension(holder.getDocumentPanel().getDocument().getFile().getName())+".html" ).getAbsolutePath()).open();
					
				}
			} catch (URISyntaxException e1) {
				de.mxro.UserError.singelton.log(e1);
			}*/
		}
		
		public PrintPreviewAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Print Preview");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens currently selected document in your standard browser.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null;
		}
		
		
	}

	
	public class CloseTabAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			if (this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder() != null) {
				final Object[] options = {"Yes",
                "No"};
				final int n = JOptionPane.showOptionDialog(null,
					"Do you want to save document "+this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getDocument().getName(),
					"Save",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[0]);
				if (n==0) {
					this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder().saveDocument();
				}
				//this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder().saveDocument();
				
			}
			this.linnkTabs.removeActiveTab();
			
		}
		
		public CloseTabAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Close Tab");
			this.putValue(Action.SHORT_DESCRIPTION, "Closes current Tab");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null;
		}
		
		
	}
	
	public class GoBackAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			if (this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null) {
				this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder().getNavigation().performBack();
				
			}
		}
		
		public GoBackAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Back");
			this.putValue(Action.SHORT_DESCRIPTION, "Shows previously displayed document");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("1leftarrow.png"));
			
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			// this.putValue(Action.ACCELERATOR_KEY, Linnk.application.getSettings().getStroke("go_back"));
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			if (this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null)
				return this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder().getNavigation().backPossible();
			return false;
		}
		
		
	}
	
	public class GoForwardAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			if (this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null) {
				this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder().getNavigation().performForward();
				
			}
		}
		
		
		public GoForwardAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Forward");
			this.putValue(Action.SHORT_DESCRIPTION, "Shows previously displayed document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("1rightarrow.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			if (this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null)
				return this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder().getNavigation().forwardPossible();
			return false;
		}
		
		
	}
	
	public class LevelUpAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder().getNavigation().perfomLevelUp();
				
			
		}
		
		public LevelUpAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Level Up");
			this.putValue(Action.SHORT_DESCRIPTION, "Shows the document above the current document");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("1uparrow.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			if (this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null)
				return this.linnkTabs.getWindowInterface().getActiveItemsPanel().getDocumentPanel().getHolder().getNavigation().levelUpPossible();
			return false;
		}
		
		
	}
	
	public class EditSettingsAction extends MyAction {
		public static final long serialVersionUID = 1L;
		
		public final LinnkTabs linnkTabs;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			LinnkFatClient.application.editSettings();
		}
		
		public EditSettingsAction(LinnkTabs linnkTabs) {
			super();
			this.linnkTabs	= linnkTabs;
			this.putValue(Action.NAME, "Preferences");
			this.putValue(Action.SHORT_DESCRIPTION, "Edit preferences.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			//putValue(Action.SMALL_ICON, Icons.getLargeIcon("filesave.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		    
		}

		@Override
		protected boolean mustBeEnabled() {
			return this.linnkTabs.getWindowInterface().getActiveItemsPanel() != null;
		}
		
		
		
	}
	
}
