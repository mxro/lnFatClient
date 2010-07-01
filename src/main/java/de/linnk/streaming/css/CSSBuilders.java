package de.linnk.streaming.css;

import java.io.IOException;

import de.mxro.xml.style.CSSBuilder;

public class CSSBuilders {
	public static CSSBuilders S = new CSSBuilders();
	
	public CSSBuilder getDocumentCSSBuilder() {
		final String cssFile = this.getClass().getSimpleName()+".css";
		// String packageDir = Utils.getPackageDirectory(this.getClass());
		
		try {
			return de.mxro.xml.style.FileCSSBuilder.fromInputStream( this.getClass().getResource(cssFile).openStream() );
		} catch (final IOException e) {
			de.mxro.utils.log.UserError.singelton.log(e);
			return null; 
		}
	}
}
