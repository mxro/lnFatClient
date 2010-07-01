package de.linnk.semantic;

import thewebsemantic.Namespace;
import thewebsemantic.vocabulary.Sioc;

@Namespace("http://www.linnk.de/semantic/basic.rdf#")
public interface Linnk extends Sioc {
	public interface Document extends Linnk {};
}
