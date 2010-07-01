package de.linnk;


import de.linnk.basispack.v05.DefaultDocumentStyle;
import de.linnk.basispack.v05.DocumentProxy;
import de.linnk.basispack.v05.DocumentProxyPanel;
import de.linnk.basispack.v05.LinnkProxy;
import de.linnk.basispack.v05.LinnkProxyPanel;
import de.linnk.basispack.v05.LinnkProxyStyle;
import de.linnk.basispack.v05.NoStyleProxy;
import de.linnk.basispack.v05.NoStyleProxyPanel;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.basispack.v05.TextItemPanel;
import de.linnk.basispack.v05.TextItemStyle;
import de.linnk.basispack.v05.TitleItemStyle;
import de.linnk.domain.Copyable;
import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.LinnkConstants;
import de.linnk.domain.OwnerItem;
import de.linnk.domain.TextItem;
import de.linnk.domain.TitleItem;
import de.linnk.domain.User;
import de.linnk.extpack.v03.HyperlinkProxy;
import de.linnk.extpack.v03.HyperlinkProxyPanel;
import de.linnk.extpack.v03.HyperlinkProxyStyle;
import de.linnk.extpack.v03.JPEGPictureItem;
import de.linnk.extpack.v03.JPEGPictureItemPanel;
import de.linnk.extpack.v03.JPEGPictureItemStyle;
import de.linnk.extpack.v03.NeverPublishItem;
import de.linnk.extpack.v03.PublishItem;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.ItemPanelBuilder;
import de.linnk.streaming.LinnkXStream;
import de.linnk.streaming.views.LoadOnDemandDocument;
import de.linnk.streaming.views.View;
import de.linnk.style.NoItemStyle;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URI;
import de.mxro.xml.style.XSLBuilder;

/**
 * General global settings for Linnk 
 * @author mx
 *
 */
public class Linnk { 
	
	
	public final static Linnk S = new Linnk(); 

	
	public void registerEasyEditPanels(ItemPanelBuilder forBuilder) {
		forBuilder.registerContentItemPanel(LinnkProxy.class, LinnkProxyPanel.class);
		forBuilder.registerContentItemPanel(DocumentProxy.class, DocumentProxyPanel.class);	
		forBuilder.registerContentItemPanel(TextItem.class, TextItemPanel.class);
		forBuilder.registerContentItemPanel(TitleItem.class, de.linnk.basispack.v05.TitleItemPanel.class);
		forBuilder.registerContentItemPanel(NoStyleProxy.class, NoStyleProxyPanel.class);
		forBuilder.registerContentItemPanel(HyperlinkProxy.class, HyperlinkProxyPanel.class);
		forBuilder.registerContentItemPanel(JPEGPictureItem.class, JPEGPictureItemPanel.class);
	}
	
	public XSLBuilder getXSLBuilder(Document doc) {
		return new DefaultDocumentStyle(doc);
	}
	
	public XSLBuilder getXSLBuilder(Item i) {
		
		if (i instanceof DocumentProxy) return new LinnkProxyStyle((LinnkProxy) i);
		if (i instanceof LinnkProxy) return new LinnkProxyStyle((LinnkProxy) i);
		
		
		if (i instanceof TextItem) return new TextItemStyle((TextItem) i);
		if (i instanceof HyperlinkProxy) return new HyperlinkProxyStyle((HyperlinkProxy) i);
		if (i instanceof JPEGPictureItem) return new JPEGPictureItemStyle((JPEGPictureItem)i);
		if (i instanceof NeverPublishItem) return new NoItemStyle(i);
		
		if (i instanceof PublishItem) return new NoItemStyle(i);
		if (i instanceof OwnerItem) return new NoItemStyle(i);
		if (i instanceof NoStyleProxy) return new NoItemStyle(i);
		
		if (i instanceof TitleItem) return new TitleItemStyle(i);
		
		//if (i instanceof ProxyItem) return new ProxyItemStyle(i);
		
		return null;
	}
	
	
	public Linnk() {
		super();
		
	}
	
	public LoadOnDemandDocument saveDocument(Document doc, Folder folder, View view) {
		return view.writeView(doc, folder);
	}
	
	public LoadOnDemandDocument saveDocument(Document doc) {
		return saveDocument(doc, ((NodeDocument) doc).getFolder());
	}
	
	public LoadOnDemandDocument saveDocument(Document doc, Folder folder) {
		return saveDocument(doc, folder, LinnkFatClient.application.getDefaultView());
	}
	
	public final String itemToString(Item item) {
		if (item instanceof Copyable) {
			Document tempDocument;
			((Copyable) item).beforeToString();
			
			tempDocument = item.getDocument();
			item.setDocument(null);
			final String res = LinnkConstants.ITEMIDENTIFIER+LinnkXStream.singelton.toXML(item);
			item.setDocument(tempDocument);
			// System.out.println("copied: "+res);
			((Copyable) item).afterToString();
			
			return res;
		} 
		
		de.mxro.utils.log.UserError.singelton.log("Item Class "+item.getClass().getName()+" cannot be transformed into a string. It must implement interface Copyable.");
		return null;
		
	}

	public static User newUserInstance(final URI uri) {
		return new User(uri.toString());
	}
	
}
