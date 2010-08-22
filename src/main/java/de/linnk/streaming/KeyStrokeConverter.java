package de.linnk.streaming;

import javax.swing.KeyStroke;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class KeyStrokeConverter implements Converter {

	public void marshal(Object arg0, HierarchicalStreamWriter arg1,
			MarshallingContext arg2) {
		if (!(arg0 instanceof KeyStroke))
			return;
		final KeyStroke stroke = (KeyStroke) arg0;
		arg1.setValue(stroke.toString());
	}

	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		return KeyStroke.getKeyStroke(arg0.getValue());
	}

	public boolean canConvert(Class arg0) {
		return arg0.equals(KeyStroke.class);
	}

}
