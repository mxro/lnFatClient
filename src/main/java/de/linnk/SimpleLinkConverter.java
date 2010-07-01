package de.linnk;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.linnk.domain.SimpleLink;


public class SimpleLinkConverter implements Converter {

	public void marshal(Object arg0, HierarchicalStreamWriter arg1,
			MarshallingContext arg2) {
		arg1.setValue(((SimpleLink) arg0 ).link);

	}

	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		return new SimpleLink(arg0.getValue());
	}

	public boolean canConvert(Class arg0) {
		return arg0.equals(SimpleLink.class);
	}

}
