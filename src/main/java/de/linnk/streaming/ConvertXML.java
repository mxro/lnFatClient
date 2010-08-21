package de.linnk.streaming;

import java.io.ByteArrayInputStream;

import org.w3c.tidy.Tidy;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.mxro.xstream.XMLUtils;

public class ConvertXML implements Converter {
	
	private static Tidy tidy;
	{
		tidy = new Tidy();
		tidy.setMakeClean(true); 
		tidy.setXmlTags(true); 
		tidy.setXHTML(true);
		tidy.setXmlOut(true);
		tidy.setQuiet(true);
		tidy.setOnlyErrors(true);
		
	}
	
	public void marshal(Object arg0, HierarchicalStreamWriter arg1,
			MarshallingContext arg2) {
		String text = ((String) arg0);
		text = text.replaceAll("<br>", "<br/>"); // tidy cannot convert this correctly ...
		
		final ByteArrayInputStream is = new ByteArrayInputStream(text.getBytes());
		final org.w3c.dom.Document doc = tidy.parseDOM(is, null);
		
		de.mxro.xstream.XMLUtils.insertNode(arg1, doc, true);

	}

	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
//		String value = arg0.getValue();
		return "";
	}

	public boolean canConvert(Class arg0) {
		return arg0.equals(String.class);
	}

}
