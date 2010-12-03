package de.linnk.fatclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JPopupMenu.Separator;

import net.iharder.dnd.FileDrop;
import de.linnk.fatclient.application.LinnkFatClient;
import de.mxro.image.ToolBarButton;
import de.mxro.swing.JMyPanel;
import de.mxro.swing.MenuItem;
import de.mxro.utils.background.Activity;



public class LinnkMainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jPanelMainToolbar = null;

	private JPanel jPanelBrowser = null;

	private JPanel jPanelLeft = null;

	private JPanel jPanelRight = null;

	private LinnkTabs linnkTabs;

	private JToolBar jToolBar = null;

	private JMenuBar mainMenu = null;
	
	private JMenu fileMenu = null;
	
	private WindowInterface windowInterface;

	private de.mxro.swing.ToolBar jToolBarDocument = null;

	private JMenu jMenuEdit = null;

	private JMenu jMenuNavigation = null;

	private JMenu jMenuInsert = null;

	private JMenu jMenuLinnk = null;
	
	private JMenu jMenuHelp = null;

	private class CloseAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent arg0) {
			
			LinnkFatClient.application.quit();
		}
		
		public CloseAction() {
			super();
			this.putValue(Action.NAME, "Close");
			this.putValue(Action.SHORT_DESCRIPTION, "Closes this window.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		    //putValue(Action.ACCELERATOR_KEY, ...);
		}
		
	}
	
	private class AddUserRoots implements Activity {

		private final JMenu toMenu;
		private final LinnkTabsActions acts;
		
		public void run() {
			for (final java.io.File file : LinnkFatClient.application.getSettings().getUserRoots()) {
				this.toMenu.add(new LinnkTabsActions.OpenDocumentAction(this.acts.linnkTabs, file));
			}
		}

		public AddUserRoots(final JMenu toMenu, final LinnkTabsActions acts) {
			super();
			this.toMenu = toMenu;
			this.acts = acts;
		}

		
		
		
		
	}
	
	public  void buildFileMenu(JMenu dest, LinnkTabsActions acts, LinnkDocumentActions docActs) {
		
		dest.add(new MenuItem(acts.getAction(LinnkTabsActions.NewDocumentAction.class)));
		dest.add(new MenuItem(acts.getAction(LinnkTabsActions.SaveDocumentAction.class)));
		dest.add(new MenuItem(acts.getAction(LinnkTabsActions.OpenDocumentAction.class)));
		final JMenu knownRootDocuments = new JMenu("Root Documents");
		
		dest.add(knownRootDocuments);
		LinnkFatClient.application.getBackgroundProcess().addActivity(new AddUserRoots(knownRootDocuments, acts));
		dest.add(new Separator());
		dest.add(new MenuItem(acts.getAction(LinnkTabsActions.CloseTabAction.class)));
		dest.add(new MenuItem(acts.getAction(LinnkTabsActions.OpenInBrowserAction.class)));
		dest.add(new MenuItem(docActs.getOpenDirectoryAction()));
		dest.add(new MenuItem(docActs.getOpenTerminalAction()));
		dest.add(new Separator());
		if (!LinnkFatClient.RELEASE) {
			dest.add(new MenuItem(docActs.getCustomAction()));
		}
		dest.add(new MenuItem(docActs.getCheckValidty()));
		dest.add(new MenuItem(docActs.getRebuildAction()));
		dest.add(new MenuItem(docActs.getAction(LinnkDocumentActions.NukeVersions.class)));
		dest.add(new Separator());
		dest.add(new MenuItem(acts.getAction(LinnkTabsActions.EditSettingsAction.class)));
		dest.add(new MenuItem(new CloseAction()));
	}
	
	public static void buildEditMenu(JMenu dest, LinnkDocumentActions docActs) {
		dest.add(new MenuItem(docActs.getCopyAction()));
		dest.add(new MenuItem(docActs.getCutAction()));
		dest.add(new MenuItem(docActs.getPasteAction()));
		dest.add(new Separator());
		dest.add(new MenuItem(docActs.getDeleteAction()));
		dest.add(new MenuItem(docActs.getMoveUpAction()));
		dest.add(new MenuItem(docActs.getMoveDownAction()));
		//dest.add(new Separator());
		// Undo Redo
	}
	
	public static void buildNavigationMenu(JMenu dest, LinnkDocumentActions acts, LinnkTabsActions linnkActs) {
		dest.add(new MenuItem(linnkActs.getAction(LinnkTabsActions.GoBackAction.class)));
		dest.add(new MenuItem(linnkActs.getAction(LinnkTabsActions.GoForwardAction.class)));
		dest.add(new MenuItem(linnkActs.getAction(LinnkTabsActions.LevelUpAction.class)));
		dest.add(new Separator());
		dest.add(new MenuItem(acts.getSelectNextAction()));
		dest.add(new MenuItem(acts.getSelectPreviousAction()));
	}
	
	public static void buildInsertMenu(JMenu dest, LinnkDocumentActions acts) {
		dest.add(new MenuItem(acts.getNewTextItemAction()));
		dest.add(new MenuItem(acts.getAddHyperlinkAction()));
		dest.add(new MenuItem(acts.getNewDirectoryLinkAction()));
		// import Action ...
	}
	
	public static void buildLinnkMenu(JMenu dest, LinnkDocumentActions acts, LinnkTabsActions linnkActs) {
		dest.add(new MenuItem(acts.getLinnkItemAction()));
		dest.add(new MenuItem(acts.getFollowLinkAction()));
		dest.add(new MenuItem(acts.getAction(LinnkDocumentActions.PublishAction.class)));
		dest.add(new MenuItem(acts.getAction(LinnkDocumentActions.NeverPublishAction.class)));
		dest.add(new MenuItem(linnkActs.getAction(LinnkTabsActions.SelectDocumentURI.class)));
	}
	
	public static void buildHelpMenu(JMenu dest, LinnkDocumentActions acts, LinnkTabsActions linnkActs) {
		dest.add(new MenuItem(linnkActs.getAction(LinnkTabsActions.ReportBugAction.class)));
		dest.add(new MenuItem(linnkActs.getAction(LinnkTabsActions.HomepageAction.class)));
		dest.add(new MenuItem(linnkActs.getAction(LinnkTabsActions.DocumentationAction.class)));
		dest.add(new Separator());
		dest.add(new MenuItem(linnkActs.getAction(LinnkTabsActions.AboutAction.class)));
	}
	
	public static void buildJToolbarDocument(JToolBar dest, LinnkDocumentActions acts, LinnkTabsActions linnkActs) {
		
		//dest.add(new javax.swing.JToolBar.Separator());
		dest.add(new ToolBarButton(linnkActs.getAction(LinnkTabsActions.GoBackAction.class)));
		dest.add(new ToolBarButton(linnkActs.getAction(LinnkTabsActions.GoForwardAction.class)));
		dest.add(new ToolBarButton(linnkActs.getAction(LinnkTabsActions.LevelUpAction.class)));
		dest.add(new ToolBarButton(acts.getFollowLinkAction()));
		dest.add(new ToolBarButton(linnkActs.getAction(LinnkTabsActions.OpenInBrowserAction.class)));
		dest.add(new javax.swing.JToolBar.Separator());
		dest.add(new ToolBarButton(acts.getLinnkItemAction()));
		dest.add(new ToolBarButton(acts.getNewTextItemAction()));
		dest.add(new ToolBarButton(acts.getAction(LinnkDocumentActions.ImportFileAction.class)));
		dest.add(new ToolBarButton(acts.getAddHyperlinkAction()));
		dest.add(new javax.swing.JToolBar.Separator());
		dest.add(new ToolBarButton(acts.getAction(LinnkDocumentActions.PublishAction.class)));
		
		dest.add(new javax.swing.JToolBar.Separator());
		dest.add(new ToolBarButton(linnkActs.getAction(LinnkTabsActions.ReportBugAction.class)));
		dest.add(new ToolBarButton(linnkActs.getAction(LinnkTabsActions.HomepageAction.class)));
	}
	
	/**
	 * This is the default constructor
	 */
	public LinnkMainWindow() {
		super();
		this.initialize();
		this.windowInterface = new WindowInterface();
		
		
		this.linnkTabs = new LinnkTabs(this.windowInterface);
		//this.windowInterface.setLinnkTabs(this.linnkTabs);
		
		final LinnkDocumentActions actions = new LinnkDocumentActions(this.windowInterface);
		final LinnkTabsActions op = new LinnkTabsActions(this.linnkTabs, this.windowInterface);
		
		this.windowInterface.setLinnkTabsActions(op);
		
		this.windowInterface.setLinnkDocumentActions(actions);
		this.windowInterface.addActions(actions);
		
		
		this.buildFileMenu(this.getFileMenu(), op, actions);
		buildEditMenu(this.getJMenuEdit(), actions);
		buildNavigationMenu(this.getJMenuNavigation(), actions, op);
		buildInsertMenu(this.getJMenuInsert(), actions);
		buildLinnkMenu(this.getJMenuLinnk(), actions, op);
		buildHelpMenu(this.getJMenuHelp(), actions, op);
		
		buildJToolbarDocument(this.jToolBarDocument, actions, op);
		
		this.setJMenuBar(this.getMainMenu());
		this.getJMyPanelRight().add(this.linnkTabs);
		
		
		this.addWindowListener(new WindowListener () {

			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowClosing(WindowEvent arg0) {
				new CloseAction().actionPerformed(null);
			}

			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		this.windowInterface.addActions(op);
		
		for (final AbstractAction a : op.getActions()) {
			this.getJToolBar().add(a);
		}
		
		
		
//		final JMyPanel dropPanel = new JMyPanel();
//		dropPanel.setLayout(new BoxLayout(dropPanel, BoxLayout.Y_AXIS));
//		dropPanel.setMinimumSize(new Dimension(200,0));
//		final JLabel dropLabel = new JLabel("--- drop files here ---");
//		dropPanel.add(dropLabel);
		
		//this.getJToolBar().add(dropPanel);
		//this.rootPane.add(dropPanel);
		
		
		
		this.windowInterface.check();
		
		//this.windowInterface.checkEnabled();
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(860, 650);
		this.setJMenuBar(this.getMainMenu());
		this.setLocation(new Point(300,200));
		this.setContentPane(this.getJContentPane());
		this.setTitle(LinnkFatClient.LINNK_TITLE);
		
	}
	
	public JMenu getFileMenu() {
		if (this.fileMenu == null) {
			this.fileMenu = new JMenu("File");
		}
		return this.fileMenu;
	}
	
	public JMenuBar getMainMenu() {
		if (this.mainMenu == null) {
			this.mainMenu = new JMenuBar();
			
			this.mainMenu.add(this.getFileMenu());
			this.mainMenu.add(this.getJMenuEdit());
			this.mainMenu.add(this.getJMenuNavigation());
			this.mainMenu.add(this.getJMenuInsert());
			this.mainMenu.add(this.getJMenuLinnk());
			this.mainMenu.add(this.getJMenuHelp());
			
		}
		return this.mainMenu;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JMyPanel
	 */
	private JPanel getJContentPane() {
		if (this.jContentPane == null) {
			this.jContentPane = new JPanel();
			//this.jContentPane.setLayout(new BoxLayout(this.getJContentPane(), BoxLayout.Y_AXIS));
			this.jContentPane.setLayout(new BorderLayout());
			this.jContentPane.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jContentPane.add(this.getJMyPanelMainToolbar(), BorderLayout.NORTH);
			this.jContentPane.add(this.getJMyPanelBrowser(), BorderLayout.CENTER);
			
		}
		return this.jContentPane;
	}

	
	/**
	 * This method initializes jPanelMainToolbar	
	 * 	
	 * @return javax.swing.JMyPanel	
	 */
	private JPanel getJMyPanelMainToolbar() {
		if (this.jPanelMainToolbar == null) {
			this.jPanelMainToolbar = new JPanel();
			//jPanelMainToolbar.setMinimumSize(new Dimension(Short.MAX_VALUE, 70));
			//jPanelMainToolbar.setPreferredSize(new Dimension(Short.MAX_VALUE, 70));
			//this.jPanelMainToolbar.setLayout(new BoxLayout(getJMyPanelMainToolbar(), BoxLayout.X_AXIS));
			this.jPanelMainToolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
			//this.jPanelMainToolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
			//jPanelMainToolbar.add(getJToolBar(), null);
			this.jPanelMainToolbar.add(this.getJToolBarDocument(),null);
			
			
		}
		return this.jPanelMainToolbar;
	}

	/**
	 * This method initializes jPanelBrowser	
	 * 	
	 * @return javax.swing.JMyPanel	
	 */
	private JPanel getJMyPanelBrowser() {
		if (this.jPanelBrowser == null) {
			this.jPanelBrowser = new JPanel();
			this.jPanelBrowser.setLayout(new BoxLayout(this.getJMyPanelBrowser(), BoxLayout.X_AXIS));
			this.jPanelBrowser.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.jPanelBrowser.add(this.getJMyPanelLeft(), null);
			this.jPanelBrowser.add(this.getJMyPanelRight(), null);
		}
		return this.jPanelBrowser;
	}

	/**
	 * This method initializes jPanelLeft	
	 * 	
	 * @return javax.swing.JMyPanel	
	 */
	private JPanel getJMyPanelLeft() {
		if (this.jPanelLeft == null) {
			this.jPanelLeft = new JPanel();
			this.jPanelLeft.setLayout(new BoxLayout(this.getJMyPanelLeft(), BoxLayout.Y_AXIS));
		}
		return this.jPanelLeft;
	}

	/**
	 * This method initializes jPanelRight	
	 * 	
	 * @return javax.swing.JMyPanel	
	 */
	private JPanel getJMyPanelRight() {
		if (this.jPanelRight == null) {
			this.jPanelRight = new JPanel();
			this.jPanelRight.setLayout(new BoxLayout(this.getJMyPanelRight(), BoxLayout.Y_AXIS));
		}
		return this.jPanelRight;
	}

	public LinnkTabs getLinnkTabs() {
		return this.linnkTabs;
	}

	/**
	 * This method initializes jToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJToolBar() {
		if (this.jToolBar == null) {
			this.jToolBar = new de.mxro.swing.ToolBar();
			
		}
		return this.jToolBar;
	}

	/**
	 * This method initializes jToolBarDocument	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJToolBarDocument() {
		if (this.jToolBarDocument == null) {
			this.jToolBarDocument = new de.mxro.swing.ToolBar();
			
			//jToolBarDocument.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return this.jToolBarDocument;
	}

	/**
	 * This method initializes jMenuEdit	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuEdit() {
		if (this.jMenuEdit == null) {
			this.jMenuEdit = new JMenu();
			this.jMenuEdit.setText("Edit");
		}
		return this.jMenuEdit;
	}

	/**
	 * This method initializes jMenuNavigation	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuNavigation() {
		if (this.jMenuNavigation == null) {
			this.jMenuNavigation = new JMenu();
			this.jMenuNavigation.setText("Navigation");
		}
		return this.jMenuNavigation;
	}

	/**
	 * This method initializes jMenuInsert	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuInsert() {
		if (this.jMenuInsert == null) {
			this.jMenuInsert = new JMenu();
			this.jMenuInsert.setText("Insert");
		}
		return this.jMenuInsert;
	}

	/**
	 * This method initializes jMenuLinnk	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuLinnk() {
		if (this.jMenuLinnk == null) {
			this.jMenuLinnk = new JMenu();
			this.jMenuLinnk.setText("Linnk");
		}
		return this.jMenuLinnk;
	}
	
	/**
	 * This method initializes jMenuHelp	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuHelp() {
		if (this.jMenuHelp == null) {
			this.jMenuHelp = new JMenu();
			this.jMenuHelp.setText("Help");
		}
		return this.jMenuHelp;
	}

	

}  //  @jve:decl-index=0:visual-constraint="33,7"
