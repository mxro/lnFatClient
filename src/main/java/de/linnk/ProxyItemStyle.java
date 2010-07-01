package de.linnk;

import org.xml.sax.SAXException;

import de.linnk.domain.ProxyItem;
import de.linnk.style.ItemStyle;
import de.mxro.filesystem.Folder;
import de.mxro.xml.MyContentHandler;
import de.mxro.xml.style.XSLBuilder;

public abstract class ProxyItemStyle<PI extends ProxyItem> extends ItemStyle<ProxyItem> {

	/*@Override
	public void writeItemXML(MyContentHandler hd, Folder files, String path)
			throws SAXException {
		
		de.mxro.XMLUtils.writeApplyTemplatesElement(hd, "item");
	}*/

	
	
	
	@Override
	public boolean writeXSL(MyContentHandler hd, Folder files, String path) throws SAXException {
		super.writeXSL(hd, files, path);
		
		XSLBuilder builder = Linnk.S.getXSLBuilder(((ProxyItem) this.item).getItem()) ;
		if (builder != null) {
			builder.writeXSL(hd, files, "item");
			/*builder.writeXSL(hd, files, path+"[@class='"+Utils.resolveAlias(this.item.getItem().getClass())+"' "+
					"and id='"+this.item.getId()+"']"+"/item");*/
		}
		
		/*
		 * super.writeXSL(hd, files, path);
		if (((ProxyItem) this.item).getItem() instanceof Styled) {
			( ((Styled) ((ProxyItem) this.item).getItem()).getXSLBuilder()).writeXSL(hd, files, path+"[@class='"+Utils.resolveAlias(this.item.getClass())+"' "+
				"and id='"+this.item.getId()+"']"+"/item");
		}
		 */
		
		return true;
	}




	public ProxyItemStyle(PI item) {
		super(item);
	}
	
	

}
