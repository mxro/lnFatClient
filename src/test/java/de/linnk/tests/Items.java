package de.linnk.tests;



import de.mxro.string.filter.Filter;


public class Items {
	
	private final static Filter TEXT_ONLY = /*new Xslt(
		"<xsl:template match='/'><xsl:value-of select='*' /></xsl:template>\n",*/
		Filter.regExReplace("\\<.*?>|\n|^ *", "", Filter.identity);
	
	
	public void textItemTextOnly() {
		
		String html2 = "<body>\n\nhere is text<b/>und here too</body>";
		String html = "<html>\n  <head>\n      </head>\n  <body>\n    Max<br>\n  </body>\n</html>\n\n";
		System.out.println(html);
		System.out.println("==");
		System.out.println(TEXT_ONLY.perform(html));
		
	}
}
