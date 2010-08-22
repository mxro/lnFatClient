package de.linnk.streaming;

import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.linnk.basispack.v05.BasisPack;
import de.linnk.basispack.v05.NodeDocument;
import de.linnk.core.v04.Core;
import de.linnk.domain.SimpleLink;
import de.linnk.domain.TitleItem;
import de.linnk.domain.User;
import de.linnk.domain.Versions;
import de.linnk.extpack.v03.ExtPack;
import de.linnk.fatclient.application.v02.Application;
import de.mxro.filesystem.v01.IncludedFile;
import de.mxro.filesystem.v01.IncludedFileSystem;
import de.mxro.filesystem.v01.LocalFile;
import de.mxro.filesystem.v01.LocalFolder;
import de.mxro.filesystem.v01.LocalRootFolder;

public class LinnkXStream {
    
	protected XStream xstream;
	protected Updater updater;
	
	public static LinnkXStream singelton = new LinnkXStream();
	
	public String resolveAlias(Class<? extends Object> clazz) {
		return xstream.getMapper().serializedClass(clazz);
	}
	
	public Object fromXML(String s) {
		return this.updater.update(this.xstream.fromXML(s));
	}
	
	public Object fromXML(InputStream is) {
		return this.updater.update(this.xstream.fromXML(is));
	}
	
	public String toXML(Object o) {
		return this.xstream.toXML(o);
	}
	
	public void toXML(Object o, OutputStream os) {
		 this.xstream.toXML(o, os);
	}
	
	public boolean toStream(Object o, OutputStream stream) {
		this.xstream.toXML(o, stream);
		return true;
	}
	
	public void marshal(Object o, HierarchicalStreamWriter w) {
		this.xstream.marshal(o, w);
	}
	
	public Object unmarshal(HierarchicalStreamReader reader) {
		return this.updater.update(this.xstream.unmarshal(reader));
	}
	
	public LinnkXStream() {
		super();
		this.xstream = new XStream(new DomDriver("UTF-8"));
		this.updater = new Updater();
		
	//	de.mxro.xstream.filesystem.v01.XStreamStreamer.registerAllAnnotations(this.xstream);
		
		
		this.xstream.alias("de.mxro.URI", de.mxro.utils.URIImpl.class);
		this.xstream.alias("v01.uri", de.mxro.utils.URIImpl.class);
		this.xstream.alias("v01.localfolder", LocalFolder.class);
		this.xstream.alias("v01.localfile",  LocalFile.class);
		this.xstream.alias("v01.localrootfolder",  LocalRootFolder.class);
		this.xstream.alias("v01.includedfile",  IncludedFile.class);
		this.xstream.alias("v01.includedfilesystem", IncludedFileSystem.class);
		
		
		Annotations.configureAliases(this.xstream, KeyStrokes.Entry.class);
		
		BasisPack.registerAllAnnotations(this.xstream);
		ExtPack.registerAllAnnotations(this.xstream);
		Core.registerAnnotations(this.xstream);
		Application.registerAllAnnotations(this.xstream);
		
		Annotations.configureAliases(this.xstream, SimpleLink.class);
		
		Annotations.configureAliases(this.xstream, NodeDocument.class);
		Annotations.configureAliases(this.xstream, TitleItem.class);
		Annotations.configureAliases(this.xstream, User.class);
		
		Annotations.configureAliases(this.xstream, Versions.class);
	}
	
}
