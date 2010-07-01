package de.linnk.basispack.v05;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.linnk.domain.Document;
import de.linnk.fatclient.document.ItemAction;
import de.linnk.fatclient.document.ItemActions;
import de.linnk.fatclient.document.itemspanel.ItemsPanel;
import de.linnk.streaming.DocumentLoader;
import de.mxro.filesystem.File;
import de.mxro.utils.log.UserError;

public class LinnkProxyActions extends ItemActions<LinnkProxyPanel> {
		
	
	public static class FollowLinkAction extends ItemAction<LinnkProxyPanel> {
		public static final long serialVersionUID = 1L;
		
	
		public void actionPerformed(ActionEvent arg0) {
			
			final LinnkProxy linnkProxy = this.getItemPanel().getItem();
			de.mxro.utils.log.UserError.singelton.log("LinnkProxyActions: Tries to follow link: "+linnkProxy.getLink().toURI().toString(), UserError.Priority.INFORMATION);
			de.mxro.utils.log.UserError.singelton.log("Link URI: "+de.mxro.filesystem.ext.LocalLink.fromURI( (linnkProxy.getLink().toURI()),
					((NodeDocument) linnkProxy.getDocument()).getFolder()), UserError.Priority.INFORMATION);
			
			// Load document
			
			// determine file
			File file = ((NodeDocument) linnkProxy.getDocument()).getFolder().getFile(linnkProxy.getLink().toURI());
			if (file == null) {
				de.mxro.utils.log.UserError.singelton.log(this, "Link URI '"+linnkProxy.getLink().toURI()+"' cannot be loaded\n" +
						" in Folder '"+((NodeDocument) linnkProxy.getDocument()).getFolder().getURI(), UserError.Priority.HIGH);
				return;
			}
			
			NodeDocument doc = DocumentLoader.singelton.loadFromFile(file);
			if (doc == null) {
				de.mxro.utils.log.UserError.singelton.log(this, "Document '"+file.getURI()+"' cannot be loaded\n" +
						" in Folder '"+((NodeDocument) linnkProxy.getDocument()).getFolder().getURI(), UserError.Priority.HIGH);
			}
			
			this.getItemPanel().getItemspanel().getDocumentPanel().getHolder().showDocument(doc);
//			this.getItemPanel().getItemspanel().getDocumentPanel().getHolder().showDocument(
//					(de.mxro.filesystem.LocalLink.fromURI( (linnkProxy.getLink().toURI()),
//						linnkProxy.getDocument().getFolder())));
		}
		
		public FollowLinkAction(final LinnkProxyPanel linnkProxyPanel) {
			super(linnkProxyPanel);
			
			this.putValue(Action.NAME, "Follow Link");
			this.putValue(Action.SHORT_DESCRIPTION, "Follows the link of selected linked item.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		   // this.putValue(Action.ACCELERATOR_KEY, Linnk.application.getSettings().getStroke("follow_link_proxy"));
		}
		
	}
	
	public static class ExpandOrCollapseAction extends ItemAction<LinnkProxyPanel> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (!this.getItemPanel().expandOrCollapse()) {
				de.mxro.utils.log.UserError.singelton.showError("Could not expand/collapse items.");
			}
		}

		public ExpandOrCollapseAction(LinnkProxyPanel item) {
			super(item);
			this.putValue(Action.NAME, "Expand/Collapse");
			this.putValue(Action.SHORT_DESCRIPTION, "Expands the linked document in this document.");
			//this.putValue(Action.ACCELERATOR_KEY, Linnk.application.getSettings().getStroke("expand_or_collapse"));
		}
		
	}
	
	
	public static class ExpandAction extends ItemAction<LinnkProxyPanel> {
		public static final long serialVersionUID = 1L;
		
		
		public void actionPerformed(ActionEvent arg0) {
			final LinnkProxy linnkProxy = this.getItemPanel().getItem();
			// de.mxro.UserError.singelton.log("LinnkProxyActions follow link: "+linnkProxy.getLink().toURI().toString());
			
			
				final Document doc = DocumentLoader.singelton.loadForDocument((NodeDocument) linnkProxy.getDocument(), linnkProxy.getLink().toURI());
				//linnkProxy.loadHoldedDocument();
	
			this.getItemPanel().setHoldedDocument(doc);
			final ItemsPanel ip = this.getItemPanel().getMyItemsPanel();
			ip.selectFirst();
			this.setEnabled(false);
			
			this.getActions().getAction(LinnkProxyActions.CollapseAction.class).setEnabled(true);
		}
		
		public ExpandAction(final LinnkProxyPanel linnkProxyPanel) {
			super(linnkProxyPanel);
			
			this.putValue(Action.NAME, "Expand");
			this.putValue(Action.SHORT_DESCRIPTION, "Expands the linked document in this document.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		   
		}
		
	}
	
	

	public static class CollapseAction extends ItemAction<LinnkProxyPanel> {
		public static final long serialVersionUID = 1L;
		

		public void actionPerformed(ActionEvent arg0) {
			
			this.getItemPanel().setHoldedDocument(null);
			this.getItemPanel().getItemspanel().selectItem(this.getItemPanel().getItem().getCompleteID());
			//this.linnkProxyPanel.revalidate();
			this.setEnabled(false);
			this.getActions().getAction(LinnkProxyActions.ExpandAction.class).setEnabled(true);
	
		}
		
		public CollapseAction(final LinnkProxyPanel linnkProxyPanel) {
			super(linnkProxyPanel);
			
			this.putValue(Action.NAME, "Collapse");
			this.putValue(Action.SHORT_DESCRIPTION, "Collapses linked document if it has been expanded before.");
			this.setEnabled(false);
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
		   // this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		    //        KeyEvent.VK_E, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
		}
		
	}
	

	

	public LinnkProxyActions(final LinnkProxyPanel linnkProxyPanel) {
		super(linnkProxyPanel);
		this.addAction(new FollowLinkAction(linnkProxyPanel));
		this.addAction(new ExpandAction(linnkProxyPanel));
		this.addAction(new CollapseAction(linnkProxyPanel));
		this.addAction(new ExpandOrCollapseAction(linnkProxyPanel));
	}
}
