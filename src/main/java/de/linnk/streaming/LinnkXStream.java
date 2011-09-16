package de.linnk.streaming;

import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDomDriver;

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
import de.mxro.shef.MxroEditorPane;

public class LinnkXStream {

	protected XStream xstream;
	protected Updater updater;

	public static LinnkXStream singelton = new LinnkXStream();

	public String resolveAlias(final Class<? extends Object> clazz) {
		return xstream.getMapper().serializedClass(clazz);
	}

	public Object fromXML(final String s) {
		return this.updater.update(this.xstream.fromXML(s));
	}

	public Object fromXML(final InputStream is) {
		return this.updater.update(this.xstream.fromXML(is));
	}

	public String toXML(final Object o) {
		return this.xstream.toXML(o);
	}

	public void toXML(final Object o, final OutputStream os) {
		this.xstream.toXML(o, os);
	}

	public boolean toStream(final Object o, final OutputStream stream) {
		this.xstream.toXML(o, stream);
		return true;
	}

	public void marshal(final Object o, final HierarchicalStreamWriter w) {
		this.xstream.marshal(o, w);
	}

	public Object unmarshal(final HierarchicalStreamReader reader) {
		return this.updater.update(this.xstream.unmarshal(reader));
	}

	public LinnkXStream() {
		super();
		final CompositeClassLoader cl = new CompositeClassLoader();
		cl.add(LinnkXStream.class.getClassLoader());
		cl.add(XStream.class.getClassLoader());
		cl.add(javax.xml.parsers.DocumentBuilderFactory.class.getClassLoader());
		cl.add(MxroEditorPane.class.getClassLoader());
		
		javax.xml.parsers.DocumentBuilderFactory.class.getClass();
		
		//final XStream temp = new XStream();
		
		this.xstream = new XStream(new Sun14ReflectionProvider(),//temp.getReflectionProvider(),
				new XppDomDriver(), cl);
		this.updater = new Updater();

		// de.mxro.xstream.filesystem.v01.XStreamStreamer.registerAllAnnotations(this.xstream);

		this.xstream.alias("de.mxro.URI", de.mxro.utils.URIImpl.class);
		this.xstream.alias("v01.uri", de.mxro.utils.URIImpl.class);
		this.xstream.alias("v01.localfolder", LocalFolder.class);
		this.xstream.alias("v01.localfile", LocalFile.class);
		this.xstream.alias("v01.localrootfolder", LocalRootFolder.class);
		this.xstream.alias("v01.includedfile", IncludedFile.class);
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
