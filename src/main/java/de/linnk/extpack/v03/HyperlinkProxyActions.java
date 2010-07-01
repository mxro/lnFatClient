package de.linnk.extpack.v03;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URISyntaxException;

import javax.swing.Action;
import javax.swing.KeyStroke;

import de.linnk.fatclient.document.ItemAction;
import de.linnk.fatclient.document.ItemActions;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.ext.OpenObject;
import de.mxro.filesystem.ext.OpenTerminal;
import de.mxro.utils.URI;
import de.mxro.utils.URIImpl;
import de.mxro.utils.log.UserError;

public class HyperlinkProxyActions extends ItemActions<HyperlinkProxyPanel> {
	private final HyperlinkProxyPanel hyperlinkProxyPanel;

	
	
	public HyperlinkProxyActions(final HyperlinkProxyPanel hyperlinkProxyPanel) {
		super(hyperlinkProxyPanel);
		this.hyperlinkProxyPanel = hyperlinkProxyPanel;
		this.addAction(new OpenLinkAction(hyperlinkProxyPanel));
		this.addAction(new OpenTerminalAction(hyperlinkProxyPanel));
		
	}
	
	public class OpenLinkAction extends ItemAction<HyperlinkProxyPanel> {
		public static final long serialVersionUID = 1L;
		
		protected final HyperlinkProxyPanel linnkProxyPanel;

		public void actionPerformed(ActionEvent arg0) {
			final HyperlinkProxy hyperlinkProxy = this.linnkProxyPanel.getItem();
			de.mxro.utils.log.UserError.singelton.log(this, "HyperlinkProxyActions open link: "+hyperlinkProxy.getHyperlink(), UserError.Priority.INFORMATION);
			
			
			
			final String hyperlink = hyperlinkProxy.getHyperlink();
			try {
				final URI uri = new URIImpl(hyperlink);
				// Workaround, check if a file exists
				FileSystemObject file = HyperlinkProxyActions.this.hyperlinkProxyPanel.getDocument().getFolder().get(uri);
				
				if (file != null) {
					OpenObject.newInstance(file.makeLocal().getAbsolutePath()).open();
					return;
				}
			} catch (final URISyntaxException e) {
				
			}
			OpenObject.newInstance(hyperlinkProxy.getHyperlink()).open();
			
		}
		
		public OpenLinkAction(final HyperlinkProxyPanel linnkProxyPanel) {
			super(linnkProxyPanel);
			this.linnkProxyPanel = linnkProxyPanel;
			this.putValue(Action.NAME, "Open Link");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens the link of selected hyperlink item.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		            KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
		}
		
	}
	
	
	
	public class OpenTerminalAction extends ItemAction<HyperlinkProxyPanel> {
		public static final long serialVersionUID = 1L;
		
		protected final HyperlinkProxyPanel linnkProxyPanel;

		public void actionPerformed(ActionEvent arg0) {
			final HyperlinkProxy hyperlinkProxy = this.linnkProxyPanel.getItem();
			// de.mxro.UserError.singelton.log("HyperlinkProxyActions open link: "+hyperlinkProxy.getHyperlink());
			final String hyperlink = hyperlinkProxy.getHyperlink();
			try {
				final URI uri = new URIImpl(hyperlink);
				if (!uri.isAbsolute()) {
					OpenTerminal.newInstance(HyperlinkProxyActions.this.hyperlinkProxyPanel.getDocument().getFolder().getFolder(uri).getURI().toString()).open();
					return;
				}
			} catch (final URISyntaxException e) {
				de.mxro.utils.log.UserError.singelton.log(e);
			}
			OpenTerminal.newInstance(hyperlinkProxy.getHyperlink()).open();
			
		}
		
		public OpenTerminalAction(final HyperlinkProxyPanel linnkProxyPanel) {
			super(linnkProxyPanel);
			this.linnkProxyPanel = linnkProxyPanel;
			this.putValue(Action.NAME, "Open Terminal");
			this.putValue(Action.SHORT_DESCRIPTION, "Opens a terminal for linked directory.");
		    //putValue(Action.LARGE_ICON_KEY, ...);
		    //putValue(Action.SMALL_ICON, ...);
		    //putValue(Action.MNEMONIC_KEY, ...);
		    //putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, ...);
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		            KeyEvent.VK_T, ActionEvent.ALT_MASK+ActionEvent.CTRL_MASK));
		}
		
	}
	
	
}
