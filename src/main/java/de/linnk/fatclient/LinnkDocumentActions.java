package de.linnk.fatclient;

import de.linnk.fatclient.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import mx.gwtutils.MxroGWTUtils;
import net.iharder.dnd.FileDrop;
import de.linnk.DocumentBuilder;
import de.linnk.ExtendedItemBuilder;
import de.linnk.basispack.v05.DocumentProxy;
import de.linnk.basispack.v05.LinnkProxy;
import de.linnk.basispack.v05.LinnkProxyPanel;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.batch.Batch;
import de.linnk.batch.TransformationAction;
import de.linnk.domain.Document;
import de.linnk.domain.InsertItem;
import de.linnk.domain.Item;
import de.linnk.domain.ItemChange;
import de.linnk.domain.ProxyItem;
import de.linnk.domain.RemoveItem;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.TextItem;
import de.linnk.extpack.v03.HyperlinkProxy;
import de.linnk.extpack.v03.NeverPublishItem;
import de.linnk.extpack.v03.PublishItem;
import de.linnk.extpack.v03.PublishView;
import de.linnk.extpack.v03.PublisherDialog;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.ItemPanelContainer;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.fatclient.icons.Icons;
import de.linnk.fatclient.transform.AddDirectoryLinkAfterSelectedItem;
import de.linnk.fatclient.transform.DeleteSelectedItems;
import de.linnk.fatclient.transform.InsertItemAndSelect;
import de.linnk.fatclient.transform.InsertTextItemRelativeToSelectedItem;
import de.linnk.fatclient.transform.LinnkSelectedItem;
import de.linnk.gwt.LinnkGWTUtils;
import de.linnk.maintenance.Maintenance;
import de.linnk.netbeansguis.ImportHyperlinkDialog;
import de.linnk.netbeansguis.ImportHyperlinkDialogImpl;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.Publisher;
import de.linnk.streaming.views.PlainXMLView;
import de.linnk.streaming.views.RWView;
import de.linnk.transform.Transformation;
import de.linnk.utils.ItemList;
import de.linnk.utils.Items;
import de.mxro.filesystem.Folder;
import de.mxro.filesystem.ext.OpenObject;
import de.mxro.filesystem.ext.OpenTerminal;
import de.mxro.swing.actions.Actions;
import de.mxro.swing.actions.MyAction;
import de.mxro.swing.actions.MyActionKeyStrokeAlias;
import de.mxro.transferable.ClipboardFacade;
import de.mxro.utils.URI;
import de.mxro.utils.Utils;
import de.mxro.utils.background.Activity;
import de.mxro.utils.log.UserError;

public class LinnkDocumentActions extends Actions {
	private ItemsPanel itemsPanel;
	
	public final ItemsPanel getItemsPanel() {
		return this.itemsPanel;
	}

	public final void setItemsPanel(ItemsPanel itemsPanel) {
		this.itemsPanel = itemsPanel;
		//System.out.println("itemsPanle to "+itemsPanel);
	}
	
	public void addKeyboardShortcuts(JComponent comp) {
		de.mxro.utils.Utils.addKeyboardShortcuts(this.getActions(), comp);
	}
	
	public LinnkDocumentActions(WindowInterface windowInterface) {
		super(windowInterface);
		this.addAction(new AddHyperlinkAction());
		this.addAction(new CheckValidty());
		this.addAction(new RebuildAction());
		this.addAction(new CustomAction());
		this.addAction(new CopyAction());
		this.addAction(new CutAction());
		this.addAction(new DeleteAction());
		this.addAction(new LinnkItemAction());
		this.addAction(new MoveDownAction());
		this.addAction(new MoveUpAction());
		this.addAction(new NewDirectoryLinkAction());
		this.addAction(new NewTextItemAction());
		this.addAction(new OpenDirectoryAction());
		this.addAction(new OpenTerminalAction());
		this.addAction(new PasteAction());
		this.addAction(new SelectNextAction());
		this.addAction(new SelectPreviousAction());
		this.addAction(new ShowLinkPopupAction());
		this.addAction(new FollowLinkAction());
		this.addAction(new PublishAction());
		this.addAction(new ImportFileAction());
		this.addAction(new InsertAfterAction());
		this.addAction(new InsertBeforeAction());
		this.addAction(new ExpandItemAction());
		this.addAction(new ExpandOrCollapseItemAction());
		this.addAction(new LinnkOrFollowLinkAction());
		this.addAction(new BackAction());
		this.addAction(new NeverPublishAction());
		this.addAction(new NukeVersions());
		
		// Aliases
		this.addAction(new MyActionKeyStrokeAlias((MyAction) this.getAction(BackAction.class), 
				LinnkFatClient.application.getSettings().getStroke("back2")));
	}

	public final ExtendedItemBuilder getItemBuilder() {
		return new ExtendedItemBuilder(this.itemsPanel.getDocument(), LinnkFatClient.currentUser);
	}
	
	protected boolean addToDocumentAndPanel(Item item) {
		final ItemChange itemChange = ItemChange.newNewItem(
				item, 
				LinnkFatClient.currentUser);
		
		return this.itemsPanel.doChange(itemChange);
	}
	
	protected boolean removeItem(Item item) {
		final Item rootItem = this.itemsPanel.getDocument().getRootItem(item);
		final ItemChange itemChange = ItemChange.newRemoveItem(rootItem, LinnkFatClient.currentUser, ItemChange.Type.REVERSIBLE);
		
		return this.itemsPanel.doChange(itemChange);
	}
	
	public void importPlainText(String text) {
        final Item newItem = this.getItemBuilder().newItemFromPlainText(text);
		if (newItem == null) {
			UserError.singelton.log(this, "importPlainText: text "+text+" could not be imported", UserError.Priority.NORMAL);
			//UserError.singelton.showError("File could not be imported!");
			return;
		}
		new InsertItemAndSelect(newItem, InsertItem.Position.AFTER).transform(this.getItemsPanel());
    }
	
	public void importFile(java.io.File file) {
		assert file.exists() : "importFile failes: file does not exist";
		
		final Item newItem = this.getItemBuilder().newItemFromFile(file);
		if (newItem == null) {
			UserError.singelton.log(this, "importFile: file "+file.toString()+" could not be imported", UserError.Priority.NORMAL);
			UserError.singelton.showError("File could not be imported!");
			return;
		}
		new InsertItemAndSelect(newItem, InsertItem.Position.AFTER).transform(this.getItemsPanel());
		
	}

    private static class ImportFilesListener implements FileDrop.Listener {
		private final LinnkDocumentActions actions;

		public ImportFilesListener(final LinnkDocumentActions actions) {
			super();
			this.actions = actions;
		}

        public void filesDropped( java.io.File[] files )
        {   
           for (final java.io.File file : files) {
        	  this.actions.importFile(file);
           }
        } 
	}
	
	public FileDrop.Listener getFileDropListener() {
		  return new ImportFilesListener(this);
	}
	
	
	
	private static abstract class LinnkDocumentAction extends MyAction {

		@Override
		protected boolean mustBeEnabled() {
			return this.getItemsPanel() != null;
		}
		
		protected ItemsPanel getItemsPanel() {
			
			return ((LinnkDocumentActions) this.getActions()).getItemsPanel();
		}
		
		protected LinnkDocumentActions getLinnkDocumentActions() {
			return (LinnkDocumentActions) this.getActions();
		}
		
		
		public LinnkDocumentAction() {
			
		}
		
	}
	
	private static abstract class LinnkDocumentItemAction extends LinnkDocumentActions.LinnkDocumentAction {

		@Override
		protected boolean mustBeEnabled() {
			return super.mustBeEnabled() && this.getItemsPanel().getSelectedItem() != null;
		}
		
	}
	
	
	
	private static abstract class LinnkDocumentMultiItemAction extends LinnkDocumentActions.LinnkDocumentAction {

		@Override
		protected boolean mustBeEnabled() {
			return super.mustBeEnabled() && this.getItemsPanel().getSelectedItems().size() > 0;
		}
		
	}
	
	
	
    public static class NukeVersions extends LinnkDocumentAction {

		@Override
		protected void myActionPerformed(ActionEvent e) {
			String res = "";
			this.getItemsPanel().getDocument().nukeVersions();
			
//			LinnkFatClient.application.getProgress().initProgress("Nuke Versions");
//			final Vector<URI> docs = de.linnk.utils.Utils.getAllDocuments(this.getItemsPanel().getDocument());
//			//res = new Batch().doForAllDocuments(this.getItemsPanel().getDocument().getFolder(), docs, new SaveAll(Linnk.application.getDefaultView()), Linnk.application.getProgress());
//			
//			res = new Batch().doForAllDocuments(this.getItemsPanel().getDocument().getFolder(), docs, new TransformationAction(new Transformation(Transformation.NONE) { 
//				
//				@Override
//				public void applyTransformation(Document doc) {
//					doc.nukeVersions();
//					doc.touch();
//				}
//				
//			}), LinnkFatClient.application.getProgress());
//			
//			//System.out.println("complete!");
//			LinnkFatClient.application.getProgress().stopProgress();
		}
    	
		public NukeVersions() {
			super();
			
			this.putValue(Action.NAME, "Nuke Versions");
			this.putValue(Action.SHORT_DESCRIPTION, "Deletes previous versions for this document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		    
		}
    }
	
	private static class CustomAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			String res="";
			//Document doc = DocumentLoader.singelton.loadFromFile(URI.create("file:///Volumes/mirrored/synced/mxro.de/Studium/Vorlesungen/Unix/uebungen/uebungen.xml"));
			
			//doc = updateLinnkProxy.transform(doc);
			//doc.save();
			//res = new Batch().doForAllChildren(this.getItemsPanel().getDocument(), new TransformationAction(updateLinnkProxy));
			//Vector<String> processedDocs = new Vector<String>();
			// Map<URI, Integer> linkedTimes = new HashMap<URI, Integer>();
			//de.linnk.utils.Utils.getLinkedCount(this.getItemsPanel().getDocument(), processedDocs, linkedTimes);
			
			//Maintenance.checkDocumentProxies (this.getItemsPanel().getDocument().getFile());
			//res = new Batch().doForAllChildren(this.getItemsPanel().getDocument(), Batch.SAVE_ALL);
			//de.mxro.Utils.deleteAllEmptyFiles(this.getItemsPanel().getDocument().getFolder().getURI().getFile());
			final Vector<URI> docs = de.linnk.utils.Utils.getAllDocuments(this.getItemsPanel().getDocument());
			//res = new Batch().doForAllDocuments(this.getItemsPanel().getDocument().getFolder(), docs, new SaveAll(Linnk.application.getDefaultView()), Linnk.application.getProgress());
			LinnkFatClient.application.getProgress().initProgress("Custom Action");
			res = new Batch().doForAllDocuments(this.getItemsPanel().getDocument().getFolder(), docs, new TransformationAction(new Transformation(Transformation.NONE) { 
				
				@Override
				public void applyTransformation(Document doc) {
					doc.nukeVersions();
					doc.touch();
				}
				
			}), LinnkFatClient.application.getProgress());
			
			//System.out.println("complete!");
			LinnkFatClient.application.getProgress().stopProgress();
			/*Vector<URI> docList = new Vector<URI>();
			for (String s: processedDocs) {
				docList.add(URI.create(s));
			}*/
			//Maintenance0607b.maintain(this.getItemsPanel().getDocument().getFile());
			//Maintenance.saveAll(this.getItemsPanel().getDocument().getFile(), Linnk.application.getDefaultView());
			//de.mxro.UserError.singelton.log(res);
			
		}
		
		public CustomAction() {
			super();
			
			this.putValue(Action.NAME, "Nuke Versions");
			this.putValue(Action.SHORT_DESCRIPTION, "Deletes all version information for this document and all of its children.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		    
		}
		
	}
	
	public AbstractAction getCustomAction() {
		return this.getAction(CustomAction.class);
	}
	
	
	
	private static class CheckValidty extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
	

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			de.mxro.utils.log.UserError.singelton.log("Checking for empty files:", UserError.Priority.INFORMATION);
			final Vector<String> emptyFiles = new Vector<String>();
			Maintenance.getEmptyFiles(this.getItemsPanel().getDocument().getFile().getOwner().getURI().getFile(), emptyFiles);
			
			de.mxro.utils.log.UserError.singelton.log("... there are "+emptyFiles.size()+" empty files.", UserError.Priority.INFORMATION);
			
			
			/*de.mxro.UserError.singelton.log("Checking all documents:", UserError.Priority.INFORMATION);
			Vector <URI> docs = de.linnk.utils.Utils.getAllDocuments(this.getItemsPanel().getDocument());
			de.mxro.UserError.singelton.log("... there are "+docs.size()+" documents.", UserError.Priority.INFORMATION);
			
			de.mxro.UserError.singelton.log("Checking all documents with documentproxies:", UserError.Priority.INFORMATION);
			Vector <URI> processedDocs = new Vector<URI>();
			new Batch().doForAllChildren(this.getItemsPanel().getDocument(), 
					new de.linnk.batch.Action() {

						@Override
						public String doOnDocument(Document doc) {
							return null;
						}
						
					}, DocumentFilter.ACCEPT_NONE, processedDocs);
			de.mxro.UserError.singelton.log("... there are "+processedDocs.size()+" documents linked with documentproxies!", UserError.Priority.INFORMATION);		*/
			
			
			de.mxro.utils.log.UserError.singelton.log("Validation check complete.", UserError.Priority.INFORMATION);
			JOptionPane.showMessageDialog(null, "Validation check complete!");
		}
		
		public CheckValidty( ) {
			super();
			 
			this.putValue(Action.NAME, "Check Validity");
			this.putValue(Action.SHORT_DESCRIPTION, "Performs validity check for database.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		    
		}
		
	}
	
	public AbstractAction getCheckValidty() {
		return this.getAction(CheckValidty.class);
	}
	
	private static class RebuildAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		
		private class Do implements Activity {

			public void run() {
				new Batch().doForAllDocuments(RebuildAction.this.getItemsPanel().getDocument().getFolder(), 
						de.linnk.utils.Utils.getAllDocuments(RebuildAction.this.getItemsPanel().getDocument()), 
						Batch.SAVE_ALL, LinnkFatClient.application.getProgress());
				LinnkFatClient.application.getProgress().stopProgress();
				de.mxro.utils.log.UserError.singelton.log("Documents rebuilded.", UserError.Priority.INFORMATION);
			}
			
		}

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			
			LinnkFatClient.application.getBackgroundProcess().addActivity(new Do());
			LinnkFatClient.application.getProgress().initProgress("Rebuild database");
			
			
			
			
		}
		
		public RebuildAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Rebuild database");
			this.putValue(Action.SHORT_DESCRIPTION, "Saves all documents.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		    
		}
		
	}
	
	public AbstractAction getRebuildAction() {
		return this.getAction(RebuildAction.class);
	}
	
	public static class ImportFileAction extends LinnkDocumentAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void myActionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser(new File("*.*"));
			fc.setMultiSelectionEnabled(true);
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (fc.showOpenDialog(this.getItemsPanel())!=JFileChooser.APPROVE_OPTION)
				return;
			
			for (final File f : fc.getSelectedFiles()) {
				if (this.getActions() instanceof LinnkDocumentActions) {
					((LinnkDocumentActions) this.getActions()).importFile(f);
				}
			}
		}
		
		public ImportFileAction() {
			super();
			
			this.putValue(Action.NAME, "Import File");
			this.putValue(Action.SHORT_DESCRIPTION, "Select files and import them to your database.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("fileimport.png"));
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		    
		}
		
	}

	
	public static class PublishAction extends LinnkDocumentAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void myActionPerformed(ActionEvent e) {
			
			// if already exists - edit it
			if (this.getItemsPanel().getDocument().getItems(PublishItem.class).size() > 0) {
				final PublishItem pi = (PublishItem) this.getItemsPanel().getDocument().getItems(PublishItem.class).firstElement();
				
				final Item modItem = new PublishItem(pi.getCreator(), pi.getId(), pi.getDocument(), PublisherDialog.editPublisher(pi.getPublisher()));
				final Item newItem = de.linnk.utils.Utils.replace(this.getItemsPanel().getDocument(),pi , modItem);
				final ItemChange ic = ItemChange.newModifyItem(newItem, this.getItemsPanel().getDocument().getRootItem(pi), LinnkFatClient.currentUser);
				if (! this.getItemsPanel().doChange(ic)) {
					UserError.singelton.showError("Could not update publish item!");
				}
				return;
			}
			
			// otherwise create it
			final PublishView defaultPublishView = new PublishView(new RWView(new PlainXMLView(), true, LinnkFatClient.application.getDefaultTemplate(), false));
			Folder defaultPublishFolder;
			/*try {
				defaultPublishFolder = FileSystemObject.newLocalRootFolder(URI.fromFile(Linnk.application.getSettings().standardPublishDir));
			} catch (final URISyntaxException e1) {
				UserError.singelton.showError("Invalid standard publish dir: "+Linnk.application.getSettings().standardPublishDir, e1);
				return;
			}*/
			
			defaultPublishFolder = Utils.selectLocalFolder(LinnkFatClient.application.getSettings().standardPublishDir, true);
			Publisher publisher = new Publisher(defaultPublishView, true, defaultPublishFolder, this.getLinnkDocumentActions().getItemsPanel().getDocument(), null);
			publisher = PublisherDialog.editPublisher(publisher);
			final Item newItem = this.getLinnkDocumentActions().getItemBuilder().newPublishItem(
					publisher
					);
			
			this.getLinnkDocumentActions().addToDocumentAndPanel(newItem);
		}
		
		public PublishAction() {
			super();
			
			this.putValue(Action.NAME, "Publish");
			this.putValue(Action.SHORT_DESCRIPTION, "Publishes current document and its children.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("webexport.png"));
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		    
		}
	}
	
	public static class NeverPublishAction extends LinnkDocumentAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void myActionPerformed(ActionEvent e) {
			// if this document has a publisher, delete it
			Item toDelete=null;
			for (Item item : this.getItemsPanel().getDocument().getItems(PublishItem.class)) {
				toDelete=item;
				
			}
			if (toDelete != null) {
				this.getItemsPanel().getDocument().doChange(new RemoveItem(LinnkFatClient.currentUser, ItemChange.Type.IMPLICIT, toDelete));
				UserError.singelton.log(this, "Deleted publish item", UserError.Priority.INFORMATION);
			}
			
			// if already exists - remove it
			final Vector<Item> items = this.getItemsPanel().getDocument().getItems(NeverPublishItem.class);
			if (items.size() > 0) {
				if (!this.getLinnkDocumentActions().removeItem(items.firstElement())) {
					UserError.singelton.showError("Could not remove NeverPublish-Item.");
				} else {
					JOptionPane.showMessageDialog(null, "This document may be published!");
				}
				return;
			}
			
			// otherwise create it
			
			final Item newItem = this.getLinnkDocumentActions().getItemBuilder().newNeverPublishItem();
			
			if (!this.getLinnkDocumentActions().addToDocumentAndPanel(newItem)) {
				UserError.singelton.showError("Could not insert NeverPublish-Item.");
			} else {
				JOptionPane.showMessageDialog(null, "This site will never be published!");
			}
		}
		
		public NeverPublishAction() {
			super();
			
			this.putValue(Action.NAME, "Never Publish");
			this.putValue(Action.SHORT_DESCRIPTION, "Assures that current document and its descandants will never be published.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			//putValue(Action.SMALL_ICON, Icons.getLargeIcon("webexport.png"));
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		    
		}
	}
	
	public static class NewTextItemAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {	
			new InsertTextItemRelativeToSelectedItem(InsertItem.Position.AFTER).transform(this.getItemsPanel());
			
			this.check();
		}
		
		public NewTextItemAction() {
			super();
			
			this.putValue(Action.NAME, "Add Text");
			this.putValue(Action.SHORT_DESCRIPTION, "Adds a new text item to the document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("New_Text.png"));
			
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("new_textitem"));
		    
		}
		
	}
	
	public AbstractAction getNewTextItemAction() {
		return this.getAction(NewTextItemAction.class);
	}
	
	
	
	private static class NewDirectoryLinkAction extends LinnkDocumentItemAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			final String s = (String)JOptionPane.showInputDialog(
                    this.getItemsPanel(),
                    "Give name of new directory.",
                    "Choose name",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
			
			if ( s.equals("")) {
				UserError.singelton.showError("Please give a name!");
				return;
			}
			
			final String newName = s.length() > 40 ? s.substring(0, 40) : s; 
			final String folderName = this.getItemsPanel().getDocument().getFolder().getUniqueFileName(MxroGWTUtils.getSimpleName(newName));
			
			if (!new AddDirectoryLinkAfterSelectedItem(folderName).transform(this.getItemsPanel()) ) {
				UserError.singelton.showError("Could not insert directory!");
			}
		}
		
		public NewDirectoryLinkAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Add directory");
			this.putValue(Action.SHORT_DESCRIPTION, "Adds link to new directory.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		    // putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 1);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("add_directory"));
		    
		    
		}
		
	}
	
	public AbstractAction getNewDirectoryLinkAction() {
		return this.getAction(NewDirectoryLinkAction.class);
	}
	
	private static class ShowLinkPopupAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			final JPopupMenu popup = new JPopupMenu();
			popup.add(this.getLinnkDocumentActions().getAddHyperlinkAction());
			JComponent invoker=null;
			if (e.getSource() instanceof JComponent) {
				invoker = (JComponent) e.getSource();
			}
			if (invoker != null) {
				popup.show(invoker, invoker.getX(), invoker.getY());
			}
		}
		
		public ShowLinkPopupAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Link (ext.)");
			this.putValue(Action.SHORT_DESCRIPTION, "Posibility to link furthor resources to the document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		}
		
	}
	
	public AbstractAction getShowLinkPopupAction() {
		return this.getAction( ShowLinkPopupAction.class);
	}
	
	private static class AddHyperlinkAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			ImportHyperlinkDialog dialog = new ImportHyperlinkDialogImpl(null, true);
		
			dialog.showDialog();
			
			final String s = dialog.getHyperlink();
			/*final String s = (String)JOptionPane.showInputDialog(
                    this.getItemsPanel(),
                    "Type desintation address of your new hyperlink.\n"+
                    "(url like 'http://www.google.de', file like 'C:\\temp.txt', etc.)",
                    "Specify link target",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "http://");*/
			if (s!=null) {
				this.getItemsPanel().deselectAll();
				
				final Item textItem =   
					this.getLinnkDocumentActions().getItemBuilder().newTextItem(dialog.getDescirption());
				final HyperlinkProxy hyperlinkProxy = new HyperlinkProxy(LinnkFatClient.currentUser,
						this.getItemsPanel().getDocument().getUniqueItemName("HyperlinkProxy"),
						this.getItemsPanel().getDocument(),
						textItem,
						dialog.getHyperlink());
				
				this.getItemsPanel().doChange(
						ItemChange.newNewItem(
								hyperlinkProxy, 
								LinnkFatClient.currentUser));
				//getItemsPanel().selectItem(hyperlinkProxy);
				
			}
		}
		
		public AddHyperlinkAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Add Hyperlink");
			this.putValue(Action.SHORT_DESCRIPTION, "Adds Hyperlink to current document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("webarchiver.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			 this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("add_hyperlink"));
		}
		
	}
	
	public AbstractAction getAddHyperlinkAction() {
		return this.getAction( AddHyperlinkAction.class);
	}
	
	
	private static class LinnkItemAction extends LinnkDocumentItemAction {
		public static final long serialVersionUID = 1L;
	
	
		@Override
		protected boolean mustBeEnabled() {
			return super.mustBeEnabled() && this.getItemsPanel().getSelectedItem() instanceof TextItem;
		}

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
				
				if (! new LinnkSelectedItem().transform(this.getItemsPanel())) {
					
				}
			
		}
		
		public LinnkItemAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Linnk!");
			this.putValue(Action.SHORT_DESCRIPTION, "Links selected item with new document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("Linnk.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("linnk"));
		}
		
	}
	
	public AbstractAction getLinnkItemAction() {
		return this.getAction( LinnkItemAction.class);
	}
	
	private static class FollowLinkAction extends LinnkDocumentItemAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean mustBeEnabled() {
			return super.mustBeEnabled() && 
				this.getItemsPanel().getSelectedItem() instanceof ProxyItem && (
					LinnkGWTUtils.getProxies(LinnkProxy.class, ((ProxyItem) this.getItemsPanel().getSelectedItem())).size() > 0 ||
					LinnkGWTUtils.getProxies(DocumentProxy.class, ((ProxyItem) this.getItemsPanel().getSelectedItem())).size() > 0);
		}

		@Override
		public void myActionPerformed(ActionEvent arg0) {
			final Vector<LinnkProxy> proxies =  LinnkGWTUtils.getProxies(LinnkProxy.class, ((ProxyItem) this.getItemsPanel().getSelectedItem()));
			final LinnkProxy linnkProxy;
			if (proxies.size() > 0) {
				linnkProxy = (LinnkProxy) proxies.firstElement();
			} else
			{
				
				linnkProxy = (LinnkProxy) LinnkGWTUtils.getProxies(DocumentProxy.class, ((ProxyItem) this.getItemsPanel().getSelectedItem())).firstElement();
			}
			de.mxro.utils.log.UserError.singelton.log("LinnkProxyActions: Tries to follow link: "+linnkProxy.getLink().toURI().toString());
			
			//DocumentPanelContainer container = this.getItemsPanel().getDocumentPanpel().getHolder();
			NodeDocument doc = DocumentLoader.singelton.loadForDocument(((NodeDocument) linnkProxy.getDocument()), linnkProxy.getLink().toURI());
			this.getItemsPanel().getDocumentPanel().getHolder().showDocument(
					
						doc);
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						Thread.sleep(100);
					} catch (final InterruptedException e) { }
					FollowLinkAction.this.getItemsPanel().selectFirst();
				}
			});
			
		}

		public FollowLinkAction() {
			super();
			
			this.putValue(Action.NAME, "Follow Link");
			this.putValue(Action.SHORT_DESCRIPTION, "Follows the link of selected linked item.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("line_arrow_end.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("follow_link"));
		}
	}
	
	public AbstractAction getFollowLinkAction() {
		return this.getAction(FollowLinkAction.class);
	}
	
	private static class LinnkOrFollowLinkAction extends LinnkDocumentItemAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean mustBeEnabled() {
			return super.mustBeEnabled() ;
		}

		@Override
		public void myActionPerformed(ActionEvent arg0) {
			if (!(this.getItemsPanel().getSelectedItem() instanceof ProxyItem)) {
				this.getActions().getAction(LinnkItemAction.class).actionPerformed(arg0);
				return;
			}
			
			final Vector<LinnkProxy> proxies =  LinnkGWTUtils.getProxies(LinnkProxy.class, ((ProxyItem) this.getItemsPanel().getSelectedItem()));
			final LinnkProxy linnkProxy;
			if (proxies.size() > 0) {
				linnkProxy = (LinnkProxy) proxies.firstElement();
			} else
			{
				linnkProxy = (LinnkProxy) LinnkGWTUtils.getProxies(DocumentProxy.class, ((ProxyItem) this.getItemsPanel().getSelectedItem())).firstElement();
			}
			if (linnkProxy == null) {
				this.getActions().getAction(LinnkItemAction.class).actionPerformed(arg0);
				return;
			} else {
				this.getActions().getAction(FollowLinkAction.class).actionPerformed(arg0);
				return;
			}
			
		}

		public LinnkOrFollowLinkAction() {
			super();
			
			this.putValue(Action.NAME, "Linnk/Follow Link");
			this.putValue(Action.SHORT_DESCRIPTION, "Creates new link for item or follows the link.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("line_arrow_end.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("linnk_or_follow_link"));
		}
	}
	
	public AbstractAction getLinnkOrFollowLinkAction() {
		return this.getAction(LinnkOrFollowLinkAction.class);
	}
	
	
	private static class ExpandItemAction extends LinnkDocumentItemAction {
		public static final long serialVersionUID = 1L;
		
		public class SelectFirstItem implements Runnable {
			private final ItemPanelContainer container;
			public void run() {
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
				}
				((LinnkProxyPanel) this.container.getItemPanel()).getMyItemsPanel().selectFirst();
			}
			public SelectFirstItem(final ItemPanelContainer container) {
				super();
				this.container = container;
			}	
			
		}
		
	
		@Override
		protected boolean mustBeEnabled() {
			return super.mustBeEnabled() && this.getItemsPanel().getSelectedItem() instanceof TextItem;
		}

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
				
				
			
				this.getItemsPanel().updateItems();
				final Item selected = this.getItemsPanel().getSelectedItem();
				
				final SimpleLink simpleLink = DocumentBuilder.instance.createDocumentForItem(selected, LinnkFatClient.currentUser);
				
				this.getItemsPanel().deselectAll();
				
				//de.mxro.UserError.singelton.log("simpleLink created: "+simpleLink.link);
				//XMLView xmlview = new XMLView();
				if (simpleLink != null) {
					Item newItem;
					
					newItem = this.getLinnkDocumentActions().getItemBuilder().newDocumentProxy(selected, simpleLink, true);
					final ItemsPanel itemsPanel = this.getItemsPanel();
					
					final ItemChange modifyItem = ItemChange.newModifyItem(newItem, selected, LinnkFatClient.currentUser);
					if (!this.getItemsPanel().doChange(modifyItem)) {
						de.mxro.utils.log.UserError.singelton.log("ExpandItemAction: Could not insert link!", UserError.Priority.HIGH);
						de.mxro.utils.log.UserError.singelton.showError("Could not insert link!");
						return;
					}
					
					final ItemPanelContainer container = itemsPanel.getItemPanelFor(newItem);
					if (!(container.getItemPanel() instanceof LinnkProxyPanel)) {
						de.mxro.utils.log.UserError.singelton.log("ExpandItemAction: new Item not found!", UserError.Priority.HIGH);
						return;
					}
					
					SwingUtilities.invokeLater(new SelectFirstItem(container));
				} else {
					de.mxro.utils.log.UserError.singelton.showError("Document could not be created.");
				}
				
			
		}
		
		public ExpandItemAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Expand");
			this.putValue(Action.SHORT_DESCRIPTION, "Expands this item to add child items");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("Expand.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //this.putValue(Action.ACCELERATOR_KEY, Linnk.application.getSettings().getStroke("expand"));
		}
		
	}
	
	public AbstractAction getExpandItemAction() {
		return this.getAction( ExpandItemAction.class);
	}
	
	
	private static class ExpandOrCollapseItemAction extends LinnkDocumentItemAction {
		public static final long serialVersionUID = 1L;
	
	
		@Override
		protected boolean mustBeEnabled() {
			return super.mustBeEnabled();
		}

		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
							
			this.getItemsPanel().updateItems();
			final Item selected = this.getItemsPanel().getSelectedItem();
			
			if (LinnkGWTUtils.getProxies(LinnkProxy.class, selected).size() > 0) {
				
				new LinnkProxyPanel.ExpandOrCollapseTransformation().transform(this.getItemsPanel(), this.getItemsPanel().getSelectedItemPanel());
				
				return;
			}
			
			this.getActions().getAction(ExpandItemAction.class).actionPerformed(e);
			return;	
			
		}
		
		public ExpandOrCollapseItemAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Expand/Collapse");
			this.putValue(Action.SHORT_DESCRIPTION, "Expands/collapses this item");
		    //putValue(Action.LARGE_ICON_KEY, ...);
			//this.putValue(Action.SMALL_ICON, Icons.getLargeIcon("attach.png"));
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("expand_or_collapse"));
		}
		
	}
	
	public AbstractAction getExpandOrCollapseItemAction() {
		return this.getAction( ExpandOrCollapseItemAction.class);
	}
	
	private static class PasteAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			if (this.getItemsPanel() != null) {
				// Insert pictures etc.
				final Item newItem =   
					this.getLinnkDocumentActions().getItemBuilder().newItemFromClipboard();
				
				
				if (newItem == null) {
					if (ClipboardFacade.getText() != null ) {
						// Insert cut/copied items
						final ItemList list = ItemList.fromString(ClipboardFacade.getText(), this.getItemsPanel().getDocument());
						if (list != null) {
							for (final Item i : list.getItems()) {
								i.setId(this.getItemsPanel().getDocument().getUniqueItemName(i.getId()));
								this.getLinnkDocumentActions().addToDocumentAndPanel(i);
							}
							return;
						}
						
						// Insert string
						
							new InsertItemAndSelect(this.getLinnkDocumentActions().getItemBuilder().newTextItem(ClipboardFacade.getText()),
									  InsertItem.Position.AFTER).transform(this.getItemsPanel());
						
						
					} 
				}
				
				if (newItem != null) {
					this.getLinnkDocumentActions().addToDocumentAndPanel(newItem);
				}
			}
		}
		
		public PasteAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Paste");
			this.putValue(Action.SHORT_DESCRIPTION, "Pastes Clipboard");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("paste_item"));
		}
		
	}
	
	public AbstractAction getPasteAction() {
		return this.getAction( PasteAction.class);
	}
	
	private static class CopyAction extends LinnkDocumentMultiItemAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
				final ItemList list = new ItemList();
				final Items selected = this.getItemsPanel().getSelectedItems();
				for (final Item i : selected) {
					// Document Proxies have to stay unique
					// so when copied they are transformed into LinnkProxies
					if (i instanceof DocumentProxy) {
						final ExtendedItemBuilder ib = new ExtendedItemBuilder(this.getItemsPanel().getDocument(), LinnkFatClient.currentUser);
						final Item toCopyItem = ib.newLinnkProxy(((LinnkProxy) i).getItem(), ((LinnkProxy) i).getLink());
						list.getItems().add(toCopyItem);
					} else {
						list.getItems().add(i);
					}
				}
				
				ClipboardFacade.setText(list.toString());
				
		}
		
		public CopyAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Copy");
			this.putValue(Action.SHORT_DESCRIPTION, "Copys selected item to clipboard.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("copy_item"));
		}
		
	}
	
	public AbstractAction getCopyAction() {
		return this.getAction( CopyAction.class);
	}
	
	private static class CutAction extends LinnkDocumentMultiItemAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
				final ItemList list = new ItemList();
				for (final Item i: this.getItemsPanel().getSelectedItems()) {
					for (final Item ii : this.getItemsPanel().getDocument().getDependendOn(i)) {
						list.getItems().add(ii);
					}
					list.getItems().add(i);
				}
				ClipboardFacade.setText(list.toString());
				this.getLinnkDocumentActions().getDeleteAction().actionPerformed(e);
			
		}
		
		public CutAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Cut");
			this.putValue(Action.SHORT_DESCRIPTION, "Deletes selected item and copies it to clipboard.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("cut_item"));
		}
		
	}
	
	public AbstractAction getCutAction() {
		return this.getAction( CutAction.class);
	}
	
	private static class MoveUpAction extends LinnkDocumentItemAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			this.getItemsPanel().moveUp();
			this.getItemsPanel().revalidate();
		}
		
		public MoveUpAction( ) {
			super();
			 
			this.putValue(Action.NAME, "MoveUp");
			this.putValue(Action.SHORT_DESCRIPTION, "Moves currently selected item up one position.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("move_up"));
		}
		
	}
	
	public AbstractAction getMoveUpAction() {
		return this.getAction( MoveUpAction.class);
	}
	
	private static class MoveDownAction extends LinnkDocumentItemAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			this.getItemsPanel().moveDown();
			this.getItemsPanel().revalidate();
			
		}
		
		public MoveDownAction( ) {
			super();
			 
			this.putValue(Action.NAME, "MoveDown");
			this.putValue(Action.SHORT_DESCRIPTION, "Moves currently selected item down one position.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("move_down"));
		}
		
	}
	
	public AbstractAction getMoveDownAction() {
		return this.getAction( MoveDownAction.class);
	}
	
	private static class DeleteAction extends LinnkDocumentMultiItemAction {
		public static final long serialVersionUID = 1L;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			new DeleteSelectedItems().transform(this.getItemsPanel());
		}
		
		public DeleteAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Delete");
			this.putValue(Action.SHORT_DESCRIPTION, "Deletes selected Items");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("delete_item"));
		}
		
	}
	
	public AbstractAction getDeleteAction() {
		return this.getAction( DeleteAction.class );
	}
	
	/*public ActionListener newDocumentLinkToEmptyDocument = new java.awt.event.ActionListener() {
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			if (itemsPanel != null) {
				String newName = JOptionPane.showInputDialog("Please enter name of new Document:");
				//String simpleName = Document.getSimpleName(newName);
				
				SimpleLink newdoc = DocumentBuilder.instance.createEmptyChildDocument(itemsPanel.getDocument(), User.currentUser, newName);
				
				if (newdoc == null) {
					de.mxro.UserError.singelton.showError("Cannot create document!");
					return;
				}
				
				Item newItem = Item.newDocumentItem(User.currentUser, 
						itemsPanel.getDocument().getUniqueItemName("DocumentLink"), 
						itemsPanel.getDocument(), 
						newName,
						newdoc);
				itemsPanel.getDocument().doChange(
					ItemChange.newNewItem(
							newItem, 
							User.currentUser));
				itemsPanel.addItem(newItem);
				itemsPanel.revalidate();
			}
		}
	};*/
	
	private static class SelectNextAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			final Item selectedItem = this.getItemsPanel().getSelectedItem();
			
			if (selectedItem != null) {
				// select first of own items
				if (this.getItemsPanel().getSelectedItemPanel() instanceof LinnkProxyPanel) {
					final LinnkProxyPanel lp = (LinnkProxyPanel) this.getItemsPanel().getSelectedItemPanel();
					if (lp.getMyItemsPanel() != null) {
						lp.getMyItemsPanel().selectFirst();
						return;
					}
				}
				
				if (!this.getItemsPanel().selectNext()) {
					if (this.getItemsPanel().getOwnerItem() == null) {
						this.getLinnkDocumentActions().getNewTextItemAction().actionPerformed(e);
						return;
					}
					
					final ItemsPanel owner = this.getItemsPanel().getOwnerItem().getItemspanel();
					final ItemsPanel itemsPanel = this.getItemsPanel();
					if (!owner.selectNext(this.getItemsPanel().getOwnerItem().getItem())) {
						owner.select();
						
						this.getLinnkDocumentActions().getNewTextItemAction().actionPerformed(e);
						
						return;
					}
					itemsPanel.revalidate();
					return;
				}
			} else {
				
				this.getLinnkDocumentActions().getNewTextItemAction().actionPerformed(e);
			}
		}
		
		public SelectNextAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Select Next");
			this.putValue(Action.SHORT_DESCRIPTION, "Selects next item.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("select_next"));
		}
		
	}
	
	public AbstractAction getSelectNextAction() {
		return this.getAction( SelectNextAction.class);
	}
	
	
	private static class SelectPreviousAction extends LinnkDocumentItemAction {
		public static final long serialVersionUID = 1L;

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			// de.mxro.UserError.singelton.log("selectedItem "+selectedItem.toString());
			if (!this.getItemsPanel().selectPrevious()) {
				
				if (this.getItemsPanel().getOwnerItem() != null) {
					this.getItemsPanel().getOwnerItem().getItemspanel().selectItem(this.getItemsPanel().getOwnerItem().getItem().getCompleteID());
					return;
				}
				
				this.getItemsPanel().selectFirst();
			}
		}
		
		public SelectPreviousAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Select Previous");
			this.putValue(Action.SHORT_DESCRIPTION, "Selects previous item.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("select_previous"));
		}
		
	}
	
	public AbstractAction getSelectPreviousAction() {
		return this.getAction( SelectPreviousAction.class);
	}
	
	private static class BackAction extends LinnkDocumentItemAction {
		public static final long serialVersionUID = 1L;

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			if (this.getItemsPanel().getOwnerItem() != null) {
				this.getItemsPanel().getOwnerItem().getItemspanel().selectItem(this.getItemsPanel().getOwnerItem().getItem().getCompleteID());
			}
			
			this.getItemsPanel().getDocumentPanel().getHolder().getWindowInterface().getLinnkTabsActions().getAction(LinnkTabsActions.GoBackAction.class).actionPerformed(e);
		}
		
		public BackAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Back");
			this.putValue(Action.SHORT_DESCRIPTION, "Selects superordinate item or opens superordiante document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("back"));
		}
		
	}
	
	public AbstractAction getBackAction() {
		return this.getAction( BackAction.class);
	}
	
	
	private static class InsertAfterAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			new InsertTextItemRelativeToSelectedItem(InsertItem.Position.AFTER).transform(this.getItemsPanel());
			
		}
		
		public InsertAfterAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Insert After");
			this.putValue(Action.SHORT_DESCRIPTION, "Inserts a new text item after the selected item.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("insert_after"));
		}
		
	}
	
	public AbstractAction getInsertAfterAction() {
		return this.getAction( InsertAfterAction.class);
	}
	
	private static class InsertBeforeAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			new InsertTextItemRelativeToSelectedItem(InsertItem.Position.BEFORE).transform(this.getItemsPanel());
		}
		
		public InsertBeforeAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Insert Before");
			this.putValue(Action.SHORT_DESCRIPTION, "Inserts a new text item before the selected item.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("insert_before"));
		}
		
	}
	
	public AbstractAction getInsertBeforeAction() {
		return this.getAction( InsertBeforeAction.class);
	}
	
	
	private static class OpenTerminalAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		
		

		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {			
			
			Folder folder = this.getItemsPanel().getDocument().getFolder();
			
			if (!folder.isLocal()) {
				UserError.singelton.log(this, "Folder '"+folder.getURI()+"' is not available locally.", UserError.Priority.INFORMATION);
				UserError.singelton.showError("The document is not availalble locally. Terminal cannot be opened.");
				return;
			}
			
			OpenTerminal.newInstance(folder.makeLocal().getAbsolutePath()).open();
		}
		
		public OpenTerminalAction( ) {
			super();
			 
			this.putValue(Action.NAME, "Open Terminal");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens Terminal for current documents directory.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("open_terminal"));
		}
		
	}
	
	public AbstractAction getOpenTerminalAction() {
		return this.getAction( OpenTerminalAction.class);
	}
	
	private static class OpenDirectoryAction extends LinnkDocumentAction {
		public static final long serialVersionUID = 1L;
		
		@Override
		public void myActionPerformed(java.awt.event.ActionEvent e) {
			
			Folder folder = this.getItemsPanel().getDocument().getFolder();
			
			if (!folder.isLocal()) {
				UserError.singelton.log(this, "Folder '"+folder.getURI()+"' is not available locally.", UserError.Priority.INFORMATION);
				UserError.singelton.showError("The document is not availalble locally. Directory cannot be opened.");
				return;
			}
			
			OpenObject.newInstance(folder.makeLocal().getAbsolutePath()).open();
				
		}
		
		public OpenDirectoryAction() {
			super();
			
			this.putValue(Action.NAME, "Open Directory");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens directory of current documnet.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    this.putValue(Action.ACCELERATOR_KEY, LinnkFatClient.application.getSettings().getStroke("open_directory"));
		}
		
	}
	
	public AbstractAction getOpenDirectoryAction() {
		return this.getAction( OpenDirectoryAction.class);
	}
	
	
	
}
