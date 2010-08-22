package de.linnk.streaming.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.w3c.tidy.DOMElementImpl;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.linnk.Linnk;
import de.linnk.domain.Document;
import de.linnk.domain.TitleItem;
import de.linnk.streaming.DocumentStreamer;
import de.linnk.streaming.ItemsXSLBuilder;
import de.linnk.streaming.LinnkXStream;
import de.linnk.streaming.XMLUtils;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.utils.URIImpl;
import de.mxro.utils.Utils;
import de.mxro.utils.log.UserError;
import de.mxro.xml.CopyContentHandler;
import de.mxro.xml.MyContentHandler;
import de.mxro.xml.style.XSLBuilder;


public class RWView extends CompositeView {
	
	private final Folder rwTheme;
	private final boolean includeEditLink;
	
	private transient String RWTemplateHTML;
	private transient String[] files;
	private transient String htmlTemplate;
	
	private HashMap<String, String> placeholders;
	private final static String xmlSchemaNS = "http://www.w3.org/1999/XSL/Transform";
	
	public Folder getTemplate() {
		return this.rwTheme;
	}
	
	public static class PlaceholderIDs {
		public static String SITE_TITLE = "%site_title%";
		public static String TITLE = "%title%";
		public static String LOGO = "%logo%";
		public static String SITE_SLOGAN = "%site_slogan%";
		public static String BREADCRUMP = "%breadcrumb%";
		public static String TOOLBAR = "%toolbar%";
		public static String SIDEBAR = "%sidebar%";
		public static String PLUGIN_SIDEBAR = "%plugin_sidebar%";
		public static String SIDEBAR_TITLE = "%sidebar_title%";
		public static String FOOTER = "%footer%";
		public static String CONTENT = "%content%";
		public static String HEADER = "%header%";
		public static String STYLE_VARIATIONS = "%style_variations%";
		public static String USER_JAVASCRIPT = "%user_javascript%";
		public static String USER_STYLES = "%user_styles%";
	}
	
	public RWView(View priorView, boolean useBackground, final Folder rwTheme, final boolean includeEditLink) {
		super(priorView, useBackground);
		
		if (rwTheme == null)
			throw new IllegalArgumentException("rwTheme may not be null!");
		this.rwTheme = rwTheme;
		this.includeEditLink = includeEditLink;
		this.htmlTemplate = null;
		this.RWTemplateHTML = null;
		this.files = null;
		assert rwTheme != null : "RWView: Theme must exist!";
		this.placeholders = new HashMap<String, String>();
		this.placeholders.put(PlaceholderIDs.SITE_TITLE, null) ;
		this.placeholders.put(PlaceholderIDs.TITLE, null) ;
		this.placeholders.put(PlaceholderIDs.LOGO, null) ;
		this.placeholders.put(PlaceholderIDs.SITE_SLOGAN, null) ;
		this.placeholders.put(PlaceholderIDs.BREADCRUMP, null) ;
		this.placeholders.put(PlaceholderIDs.TOOLBAR, null) ;
		this.placeholders.put(PlaceholderIDs.SIDEBAR, null) ;
		this.placeholders.put(PlaceholderIDs.PLUGIN_SIDEBAR, null) ;
		this.placeholders.put(PlaceholderIDs.SIDEBAR_TITLE, null) ;
		this.placeholders.put(PlaceholderIDs.CONTENT, null);
		this.placeholders.put(PlaceholderIDs.FOOTER, null);;
		this.placeholders.put(PlaceholderIDs.HEADER, null);
		this.placeholders.put(PlaceholderIDs.STYLE_VARIATIONS, null);
		this.placeholders.put(PlaceholderIDs.USER_JAVASCRIPT, null);
		this.placeholders.put(PlaceholderIDs.USER_STYLES, null);
	}

	boolean readProperties() {
		if (this.files == null || this.RWTemplateHTML == null) {
			try {
				final de.mxro.filesystem.File theme_plist = this.rwTheme.getFile(new URIImpl(
						"Theme.plist"));
				if (theme_plist == null) {
					de.mxro.utils.log.UserError.singelton.log(this, "File Theme.plist must exist in Template: "+ this.rwTheme.getURI().getPath(), UserError.Priority.HIGH);
					return false;
				}
				try {
					this.RWTemplateHTML = Utils.getApplePropertyListStringKey(theme_plist.getInputStream(), "RWTemplateHTML");
					if ( this.RWTemplateHTML == null ) {
						de.mxro.utils.log.UserError.singelton.log(this, "File Theme.plist must define key RWTempleteHTML", UserError.Priority.HIGH);
						return false;
					}
					this.files = Utils.getApplePropertyListStringKeyArray(theme_plist.getInputStream(), "RWCopyFiles");
					return true;
				} catch (final IOException e) {
					de.mxro.utils.log.UserError.singelton.log(e);
					return false;
				}
				
			} catch (final URISyntaxException e) {
				de.mxro.utils.log.UserError.singelton.log(e);
				return false;
			}
		} 
		return true;
	}

	private String loadTemplate() {
		assert this.RWTemplateHTML != null : "call readProperties before loadTemplate!";
		if (this.htmlTemplate == null) {
			try {
				this.htmlTemplate = Utils.fromInputStream(this.rwTheme.getFile(new URIImpl(this.RWTemplateHTML)).getInputStream());
				if (this.htmlTemplate == null) {
					de.mxro.utils.log.UserError.singelton.log(this, "File "+this.RWTemplateHTML+" must exist in folder "+this.rwTheme.getURI(), UserError.Priority.HIGH);
					return null;
				}
				return this.htmlTemplate;
			} catch (final URISyntaxException e) {
				de.mxro.utils.log.UserError.singelton.log(e);
				return null;
			} catch (final IOException e) {
				de.mxro.utils.log.UserError.singelton.log(e);
				return null;
			}
		} 
			
			return this.htmlTemplate;
	}
	
	public void setPlaceholder(String placeholder, String value) {
		this.placeholders.put(placeholder, value);
	}
	
	public String getPlaceholder(String placeholder) {
		return this.placeholders.get(placeholder);
	}
	
	protected String applyPlaceholder(String html, String placeholder, String value) {
		return  html.replaceAll(placeholder, value);
	}
	
	protected String applyPlaceholders(String html, Document root, Folder folder) {
		for (final String s : this.placeholders.keySet()) {
			if (this.placeholders.get(s) != null) {
				html = this.applyPlaceholder(html, s, this.placeholders.get(s));
			} else {
					String value = "";
					
					if (root.getItems(TitleItem.class).size() > 0) {
						final TitleItem ti = (TitleItem) root.getItems(TitleItem.class).firstElement();
						
						if ( s.equals( PlaceholderIDs.SITE_TITLE)) {
							value = "<xsl:value-of select=\"" +
								DocumentStreamer.enclosingNodeName+"/"+
								LinnkXStream.singelton.resolveAlias(root.getClass())+"/"
								+"item[@class='"+LinnkXStream.singelton.resolveAlias(ti.getClass())+"']/title"+
								"\" />";
						}
						
						if ( s.equals(  PlaceholderIDs.TITLE)) {
							value = "<xsl:value-of select=\"" +
								DocumentStreamer.enclosingNodeName+"/"+
								LinnkXStream.singelton.resolveAlias(root.getClass())+"/"
								+"item[@class='"+LinnkXStream.singelton.resolveAlias(ti.getClass())+"']/title"+
								"\" />";
						}
						
						if (  s.equals( PlaceholderIDs.SITE_SLOGAN)) {
							value = "<xsl:value-of select=\"" +
								DocumentStreamer.enclosingNodeName+"/"+
								LinnkXStream.singelton.resolveAlias(root.getClass())+"/"
								+"item[@class='"+LinnkXStream.singelton.resolveAlias(ti.getClass())+"']/title"+
								"\" />";
						}
					}
					
					if (s.equals( PlaceholderIDs.TOOLBAR )) {
							if (root.getOwnerLink() != null) {
								//System.out.println("owner_toolbar: "+root.getOwner());
								value = "<ul><li><a href='"+root.getOwnerLink()+"' >Level Up</a></li>";
								
							} else {
								value = "<ul><li></li>";
							}
							if (!this.includeEditLink) {
								value = value + "</ul>";
							} else {
								value = value + "<li><a href='"+Utils.removeExtension(root.getFilename())+de.linnk.domain.LinnkConstants.linnkExtension+
									"' type='application/linnk' >Edit Document</a></li></ul>";
							}
					}
							
					if (s.equals( PlaceholderIDs.CONTENT)) {
						value = "<xsl:apply-templates/>";
					}
					
					html = this.applyPlaceholder(html, s, value);
				}
					
		}
		
		html = html.substring(html.indexOf(">")+1);
		// remove remaining placeholders ...
		//this.htmlTemplate = this.htmlTemplate.replaceAll("%[^% ]%", "");
		return html;
	}
	
	private String setPathes(String html) {
		int idx;
		while ((idx=html.indexOf("%pathto("))!=-1) {
			html = html.replaceFirst("%pathto\\(", "");
			final int idx2 = html.indexOf("%", idx);
			html = html.substring(0, idx2-1) + html.substring(idx2+1);
		}
		return html;
	}
	
	private boolean copyFiles(Folder dest) {
		boolean res=true;
		try {
			for (final String s : this.files) {
				final FileSystemObject object = this.rwTheme.get(new URIImpl(s));
				if (object == null) {
					UserError.singelton.log(this, "copyFiles: Could not find file: "+s+" in "+this.rwTheme.getURI(), UserError.Priority.NORMAL);
					continue;
				}
				res = res & dest.importObject(object)!=null; 
			}
			return res;
		} catch (final IOException e) {
			de.mxro.utils.log.UserError.singelton.log(e);
			return false;
		} catch (final URISyntaxException e) {
			de.mxro.utils.log.UserError.singelton.log(e);
			return false;
		}
	}
	
	public final File writeXSL(Document root, File toFile, Folder folder) {
		File xslFile;
		
		xslFile = toFile;
			
		
		if (xslFile != null) {
			try {	
				final OutputStream os = xslFile.getOutputStream();
				
				final MyContentHandler hd = MyContentHandler.createXMLOutput(os); 
				/*StreamResult streamResult = new StreamResult(os);
				SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			     tf.setFeature("http://xml.org/sax/features/validation", false);
				
				
				MyContentHandler hd = tf.newMyContentHandler();
				hd.setResult(streamResult);
				
				Transformer serializer = hd.getTransformer();
				serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
				 serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
				serializer.setOutputProperty(OutputKeys.INDENT,"yes");*/
				
				
				
				
				hd.startDocument();
				final AttributesImpl atts = new AttributesImpl();
				atts.addAttribute("", "", "version", "CDATA", "1.0");
				atts.addAttribute("", "", "xmlns:xsl", "CDATA", "http://www.w3.org/1999/XSL/Transform");
				hd.startElement(xmlSchemaNS, "", "xsl:stylesheet", atts);
				
				this.writeXSLContent(root, folder, hd, os);
			    
				hd.endElement(xmlSchemaNS, "xmlns:xsl", "xsl:stylesheet");
				hd.endDocument();
				
				// de.mxro.UserError.singelton.log("XMLFile.writeXSL: File: "+xslFile.getURI().toString(), UserError.Priority.LOW);
				return xslFile;
				
			}
			
			catch (final SAXException e) {
				UserError.singelton.showError("Error while writing xml view xsl file!", e);
				
			}
			catch (final UnsupportedEncodingException e) {
				de.mxro.utils.log.UserError.singelton.log(e);
			}
		}
		return null;
		
	}
	
//public void writeHeader(Document root, MyContentHandler hd, Folder folder) throws SAXException {
//		
//		hd.startElement("", "", "header", null);
//		hd.startElement("", "", "title", null);
//				
//		if (root.getItems(TitleItem.class).size() > 0) {
//			final TitleItem ti = (TitleItem) root.getItems(TitleItem.class).firstElement();
//			Utils.assertField("title", ti.getClass());
//			Utils.writeValueOfElement(hd, 
//					DocumentStreamer.enclosingNodeName+"/"+
//					de.mxro.xstream.Utils.resolveAlias(root.getClass())+"/"
//					+"item[@class='"+de.mxro.xstream.Utils.resolveAlias(ti.getClass())+"']/title");
//		}
//		
//		hd.endElement("", "", "title");
//		
//		final AttributesImpl styleatts = new AttributesImpl();
//		styleatts.addAttribute("", "", "type", "CDATA", "text/css");
//		hd.startElement("", "", "style", styleatts);
//		final String css = ((Styled) root).getCSSBuilder().generateCSS(folder);
//		hd.characters(css.toCharArray(), 0, css.length());
//		hd.endElement("", "", "style");
//		
//		hd.endElement("", "", "header");
//		
//		
//	}
//	
//	protected void writeXSLContent(Document root, Folder folder, MyContentHandler hd, OutputStream os) throws SAXException {
//		Utils.startXSLTemplateElement(hd, "/");
//		hd.startElement("", "", "html", null);
//		this.writeHeader(root, hd, folder);
//		final AttributesImpl bodyatts = new AttributesImpl();
//		bodyatts.addAttribute("", "", "class", "CDATA", bodyClass);
//		hd.startElement("", "", "body", bodyatts);
//		
//		final AttributesImpl editThisAtts = new AttributesImpl();
//		editThisAtts.addAttribute("", "", "href", "CDATA", Utils.removeExtension(root.getFilename())+de.linnk.application.Linnk.linnkExtension);
//		editThisAtts.addAttribute("", "", "type", "CDATA", "application/linnk" );
//		hd.startElement("", "", "a", editThisAtts);
//		Utils.writeCharacters(hd, "Edit Document");
//		hd.endElement("", "", "a");
//		Utils.writeApplyTemplatesElement(hd);
//		hd.endElement("", "", "body");
//		hd.endElement("", "", "html");
//		Utils.endXSLTemplateElement(hd);
//		
//		if (root instanceof Styled) {
//			final XSLBuilder builder = ((Styled) root).getXSLBuilder();
//			builder.writeXSL(hd, folder, "item");
//		}
//		
//		if (root.getItems().size() > 0) {
//			new ItemsXSLBuilder(folder, hd, root.getItems()).build();
//		}
//	}
//	
	
	protected void writeXSLContent(Document root, Folder folder, MyContentHandler hd, OutputStream os) throws SAXException {
		
		if (!this.readProperties()) {
			UserError.singelton.log(this, "Could not load Template!", UserError.Priority.HIGH);
			return;
		}
		
		
		String html = this.loadTemplate();
		
		if (html == null) return;
		
		
		html = this.applyPlaceholders(html, root, folder) ;
		
		html = this.setPathes(html);
	
		this.copyFiles(folder);
		
		XMLUtils.startXSLTemplateElement(hd, "/");
		
		try {
			// TODO THIS IS TOO SLOW !!!
			
			final CopyContentHandler handler = new CopyContentHandler(hd);
			
			final XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setFeature("http://xml.org/sax/features/validation", false);
			reader.setContentHandler(handler);
			reader.setFeature("http://xml.org/sax/features/namespaces", false);
			
			reader.setFeature("http://apache.org/xml/features/allow-java-encodings", false);
			final ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
			// de.mxro.UserError.singelton.log(htmlTemplate);
			final Tidy tidy = new Tidy();
			tidy.setMakeClean(true); 
			tidy.setXmlTags(true); 
			final org.w3c.dom.Document doc = tidy.parseDOM(is, null);
			// reader.parse(new InputSource(is));
			//Source source = new DOMSource(doc);
			//InputSource = new DOMInputSource(doc);
			//SAXResult res = new SAXResult(handler);
			//Transformer xformer = TransformerFactory.newInstance().newTransformer();
			// xformer.transform(source, res);
			final XMLSerializer serializer = new XMLSerializer();
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			serializer.setOutputByteStream(bos);
            serializer.serialize(doc);
          //  System.out.println("Namespace: "+doc.getFirstChild().getNamespaceURI());
          //  System.out.println("Namespace: "+doc.getNamespaceURI());
          //  System.out.println("Namespace: "+((DOMElementImpl) doc.getElementsByTagName("html").item(0)).getAttribute("xmlns"));
            hd.setDefaultNamespace(((DOMElementImpl) doc.getElementsByTagName("html").item(0)).getAttribute("xmlns"));
            /*AttVal attVal = new AttVal();
            attVal.attribute = "xmlns";
            ((DOMElementImpl)  doc.getElementsByTagName("html").item(0)).removeAttribute("xmlns");*/
            reader.parse(new InputSource(new ByteArrayInputStream(bos.toByteArray())));
            
            
			//HierarchicalStreamWriter writer = new DomDriver().createWriter(os);
			
			//hd.parse(new InputSource(is));
			
		} catch (final Exception e) {
			UserError.singelton.log(e);
			
		}
		
		XMLUtils.endXSLTemplateElement(hd);
        
		XSLBuilder builder = Linnk.S.getXSLBuilder(root);
		
		
		
		if (builder != null) {
			
			builder.writeXSL(hd, folder, "item");
		}
		
		if (root.getItems().size() > 0) {
			final ItemsXSLBuilder itemBuilder = new ItemsXSLBuilder(folder, hd, root.getItems());
			itemBuilder.build();
		}
	}

	@Override
	public boolean stepWriteView(Document toSave, Folder destinationFolder,
			LoadOnDemandDocument newDocumentInDestnationFolder) {
		
	
		final File xslFile = destinationFolder.forceFile(Utils.removeExtension(toSave.getFilename())+de.linnk.domain.LinnkConstants.xslExtension);
			
		return this.writeXSL(toSave, xslFile, destinationFolder) != null;
	}

	

}
